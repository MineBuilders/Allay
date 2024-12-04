package org.allaymc.server.world.storage;

import io.netty.buffer.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.allaymc.api.block.type.BlockState;
import org.allaymc.api.block.type.BlockTypes;
import org.allaymc.api.blockentity.BlockEntity;
import org.allaymc.api.entity.Entity;
import org.allaymc.api.entity.interfaces.EntityPlayer;
import org.allaymc.api.registry.Registries;
import org.allaymc.api.utils.HashUtils;
import org.allaymc.api.utils.Utils;
import org.allaymc.api.world.DimensionInfo;
import org.allaymc.api.world.biome.BiomeId;
import org.allaymc.api.world.biome.BiomeType;
import org.allaymc.api.world.chunk.UnsafeChunk;
import org.allaymc.api.world.storage.WorldStorageException;
import org.allaymc.server.utils.PaletteUtils;
import org.allaymc.server.world.HeightMap;
import org.allaymc.server.world.chunk.AllayUnsafeChunk;
import org.allaymc.server.world.chunk.ChunkSection;
import org.allaymc.server.world.palette.Palette;
import org.allaymc.server.world.palette.PaletteException;
import org.allaymc.updater.block.BlockStateUpdaters;
import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NBTOutputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtUtils;
import org.cloudburstmc.nbt.util.stream.LittleEndianDataInputStream;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.WriteBatch;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

/**
 * @author Cool_Loong
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LevelDBChunkSerializer {
    public static final LevelDBChunkSerializer INSTANCE = new LevelDBChunkSerializer();

    public void serialize(WriteBatch writeBatch, AllayUnsafeChunk chunk) {
        serializeBlock(writeBatch, chunk);
        serializeHeightAndBiome(writeBatch, chunk);
        serializeEntityAndBlockEntity(writeBatch, chunk);
    }

    public void deserialize(DB db, AllayUnsafeChunk.Builder builder) {
        deserializeBlock(db, builder);
        deserializeHeightAndBiome(db, builder);
        deserializeEntityAndBlockEntity(db, builder);
    }

    private void serializeBlock(WriteBatch writeBatch, AllayUnsafeChunk chunk) {
        for (int ySection = chunk.getDimensionInfo().minSectionY(); ySection <= chunk.getDimensionInfo().maxSectionY(); ySection++) {
            ChunkSection section = chunk.getSection(ySection);
            ByteBuf buffer = ByteBufAllocator.DEFAULT.ioBuffer();
            try {
                buffer.writeByte(ChunkSection.CHUNK_SECTION_VERSION);
                buffer.writeByte(ChunkSection.LAYER_COUNT);
                buffer.writeByte(ySection);
                for (int i = 0; i < ChunkSection.LAYER_COUNT; i++) {
                    section.blockLayers()[i].writeToStoragePersistent(buffer, BlockState::getBlockStateTag);
                }
                writeBatch.put(LevelDBKey.CHUNK_SECTION_PREFIX.getKey(chunk.getX(), chunk.getZ(), ySection, chunk.getDimensionInfo()), Utils.convertByteBuf2Array(buffer));
            } finally {
                buffer.release();
            }
        }
    }

    private void deserializeBlock(DB db, AllayUnsafeChunk.Builder builder) {
        DimensionInfo dimensionInfo = builder.getDimensionInfo();
        ChunkSection[] sections = new ChunkSection[dimensionInfo.chunkSectionCount()];
        var minSectionY = dimensionInfo.minSectionY();
        for (int ySection = minSectionY; ySection <= dimensionInfo.maxSectionY(); ySection++) {
            byte[] bytes = db.get(LevelDBKey.CHUNK_SECTION_PREFIX.getKey(builder.getChunkX(), builder.getChunkZ(), ySection, dimensionInfo));
            if (bytes == null) continue;
            ByteBuf byteBuf = ByteBufAllocator.DEFAULT.ioBuffer(bytes.length);
            try {
                byteBuf.writeBytes(bytes);
                byte subChunkVersion = byteBuf.readByte();
                int layers = 2;
                switch (subChunkVersion) {
                    case 9, 8:
                        // Layers
                        layers = byteBuf.readByte();
                        if (subChunkVersion == 9) {
                            // Extra section y value in version 9
                            byteBuf.readByte();
                        }
                    case 1:
                        ChunkSection section;
                        if (layers <= ChunkSection.LAYER_COUNT) {
                            // This is the normal situation where the chunk section is loaded correctly,
                            // and we use the single-arg constructor of ChunkSection directly to avoid
                            // using Arrays.fill(), which will be slower
                            section = new ChunkSection((byte) ySection);
                        } else {
                            // Currently only two layers are used in minecraft, so that might mean this chunk is corrupted
                            // However we can still load it c:
                            log.warn("Loading chunk section ({}, {}, {}) with {} layers, which might mean that this chunk is corrupted!", builder.getChunkX(), ySection, builder.getChunkZ(), layers);
                            @SuppressWarnings("rawtypes") Palette[] palettes = new Palette[layers];
                            Arrays.fill(palettes, new Palette<>(BlockTypes.AIR.getDefaultState()));
                            section = new ChunkSection((byte) ySection, palettes);
                        }
                        for (int layer = 0; layer < layers; layer++) {
                            section.blockLayers()[layer].readFromStoragePersistent(byteBuf, LevelDBChunkSerializer::fastBlockStateDeserializer);
                        }
                        sections[ySection - minSectionY] = section;
                        break;
                }
            } finally {
                byteBuf.release();
            }
        }
        builder.sections(fillNullSections(sections, dimensionInfo));
    }

    private static BlockState fastBlockStateDeserializer(ByteBuf buffer) {
        // Get block state hash
        int blockStateHash;
        try (var bufInputStream = new ByteBufInputStream(buffer);
             var input = new LittleEndianDataInputStream(bufInputStream);
             var nbtInputStream = new NBTInputStream(input)) {
            blockStateHash = PaletteUtils.fastReadBlockStateHash(input, buffer);
            if (blockStateHash == PaletteUtils.HASH_NOT_LATEST) {
                var oldNbtMap = (NbtMap) nbtInputStream.readTag();
                var newNbtMap = BlockStateUpdaters.updateBlockState(oldNbtMap, BlockStateUpdaters.LATEST_VERSION);
                // Make sure that tree map is used
                // If the map inside states nbt is not tree map
                // the block state hash will be wrong!
                var states = new TreeMap<>(newNbtMap.getCompound("states"));
                // To calculate the hash of the block state
                // "name" field must be in the first place
                var tag = NbtMap.builder()
                        .putString("name", newNbtMap.getString("name"))
                        .putCompound("states", NbtMap.fromMap(states))
                        .build();
                blockStateHash = HashUtils.fnv1a_32_nbt(tag);
            }
        } catch (IOException e) {
            throw new PaletteException(e);
        }

        // Get block state by hash
        BlockState blockState = Registries.BLOCK_STATE_PALETTE.get(blockStateHash);
        if (blockState != null) {
            return blockState;
        }

        log.error("Find unknown block state hash {} while loading chunk section", blockStateHash);
        return BlockTypes.UNKNOWN.getDefaultState();
    }

    private static ChunkSection[] fillNullSections(ChunkSection[] sections, DimensionInfo dimensionInfo) {
        for (int i = 0; i < sections.length; i++) {
            if (sections[i] == null) {
                sections[i] = new ChunkSection((byte) (i + dimensionInfo.minSectionY()));
            }
        }
        return sections;
    }

    /*
     * Bedrock Edition 3d-data saves the height map starting from index 0, so adjustments are made here to accommodate the world's minimum height. For details, see:
     * See https://github.com/bedrock-dev/bedrock-level/blob/main/src/include/data_3d.h#L115
     */

    private void serializeHeightAndBiome(WriteBatch writeBatch, AllayUnsafeChunk chunk) {
        ByteBuf heightAndBiomesBuffer = ByteBufAllocator.DEFAULT.ioBuffer();
        try {
            // Serialize height map
            for (short height : chunk.getHeightMap().getHeights()) {
                heightAndBiomesBuffer.writeShortLE(height - chunk.getDimensionInfo().minHeight());
            }
            // Serialize biome
            Palette<BiomeType> lastPalette = null;
            for (int y = chunk.getDimensionInfo().minSectionY(); y <= chunk.getDimensionInfo().maxSectionY(); y++) {
                ChunkSection section = chunk.getSection(y);
                section.biomes().writeToStorageRuntime(heightAndBiomesBuffer, BiomeType::getId, lastPalette);
                lastPalette = section.biomes();
            }
            writeBatch.put(LevelDBKey.DATA_3D.getKey(chunk.getX(), chunk.getZ(), chunk.getDimensionInfo()), Utils.convertByteBuf2Array(heightAndBiomesBuffer));
        } finally {
            heightAndBiomesBuffer.release();
        }
    }

    private void deserializeHeightAndBiome(DB db, AllayUnsafeChunk.Builder builder) {
        ByteBuf heightAndBiomesBuffer = null;
        try {
            // Try load data_3d
            byte[] bytes = db.get(LevelDBKey.DATA_3D.getKey(builder.getChunkX(), builder.getChunkZ(), builder.getDimensionInfo()));
            if (bytes != null) {
                // Height map
                heightAndBiomesBuffer = Unpooled.wrappedBuffer(bytes);
                short[] heights = new short[256];
                for (int i = 0; i < 256; i++) {
                    heights[i] = (short) (heightAndBiomesBuffer.readUnsignedShortLE() + builder.getDimensionInfo().minHeight());
                }
                builder.heightMap(new HeightMap(heights));

                // Biomes
                Palette<BiomeType> lastPalette = null;
                var minSectionY = builder.getDimensionInfo().minSectionY();
                for (int y = minSectionY; y <= builder.getDimensionInfo().maxSectionY(); y++) {
                    ChunkSection section = builder.getSections()[y - minSectionY];
                    if (section == null) continue;
                    section.biomes().readFromStorageRuntime(heightAndBiomesBuffer, this::getBiomeByIdNonNull, lastPalette);
                    lastPalette = section.biomes();
                }

                return;
            }

            // Try load data_2d if data_3d is not found
            byte[] bytes2D = db.get(LevelDBKey.DATA_2D.getKey(builder.getChunkX(), builder.getChunkZ(), builder.getDimensionInfo()));
            if (bytes2D == null) return;
            heightAndBiomesBuffer = Unpooled.wrappedBuffer(bytes2D);
            short[] heights = new short[256];
            for (int i = 0; i < 256; i++) {
                heights[i] = heightAndBiomesBuffer.readShortLE();
            }
            builder.heightMap(new HeightMap(heights));
            byte[] biomes = new byte[256];
            heightAndBiomesBuffer.readBytes(biomes);

            var minSectionY = builder.getDimensionInfo().minSectionY();
            for (int y = minSectionY; y <= builder.getDimensionInfo().maxSectionY(); y++) {
                ChunkSection section = builder.getSections()[y - minSectionY];
                if (section == null) continue;
                final Palette<BiomeType> biomePalette = section.biomes();
                for (int x = 0; x < 16; x++) {
                    for (int z = 0; z < 16; z++) {
                        for (int sy = 0; sy < 16; sy++) {
                            biomePalette.set(UnsafeChunk.index(x, sy, z), getBiomeByIdNonNull(biomes[x + 16 * z]));
                        }
                    }
                }
            }
        } finally {
            if (heightAndBiomesBuffer != null) {
                heightAndBiomesBuffer.release();
            }
        }
    }

    private void serializeEntityAndBlockEntity(WriteBatch writeBatch, AllayUnsafeChunk chunk) {
        // Write blockEntities
        Collection<BlockEntity> blockEntities = chunk.getBlockEntities().values();
        ByteBuf tileBuffer = ByteBufAllocator.DEFAULT.ioBuffer();
        try (var bufStream = new ByteBufOutputStream(tileBuffer)) {
            byte[] key = LevelDBKey.BLOCK_ENTITIES.getKey(chunk.getX(), chunk.getZ(), chunk.getDimensionInfo());
            if (blockEntities.isEmpty()) {
                writeBatch.delete(key);
            } else {
                for (BlockEntity blockEntity : blockEntities) {
                    NBTOutputStream writerLE = NbtUtils.createWriterLE(bufStream);
                    writerLE.writeTag(blockEntity.saveNBT());
                }
                writeBatch.put(key, Utils.convertByteBuf2Array(tileBuffer));
            }
        } catch (IOException e) {
            throw new WorldStorageException(e);
        } finally {
            tileBuffer.release();
        }

        // Write entities
        Collection<Entity> entities = chunk.getEntities().values();
        ByteBuf entityBuffer = ByteBufAllocator.DEFAULT.ioBuffer();
        try (var bufStream = new ByteBufOutputStream(entityBuffer)) {
            byte[] key = LevelDBKey.ENTITIES.getKey(chunk.getX(), chunk.getZ(), chunk.getDimensionInfo());
            if (entities.isEmpty()) {
                writeBatch.delete(key);
                return;
            }
            for (Entity e : entities) {
                // Player entity won't be saved to chunk
                // As we will save player data through player storage
                if (e instanceof EntityPlayer) continue;
                NBTOutputStream writerLE = NbtUtils.createWriterLE(bufStream);
                writerLE.writeTag(e.saveNBT());
            }
            writeBatch.put(key, Utils.convertByteBuf2Array(entityBuffer));
        } catch (IOException e) {
            throw new WorldStorageException(e);
        } finally {
            entityBuffer.release();
        }
    }

    private void deserializeEntityAndBlockEntity(DB db, AllayUnsafeChunk.Builder builder) {
        DimensionInfo dimensionInfo = builder.getDimensionInfo();
        byte[] tileBytes = db.get(LevelDBKey.BLOCK_ENTITIES.getKey(builder.getChunkX(), builder.getChunkZ(), dimensionInfo));
        if (tileBytes != null) {
            builder.blockEntities(deserializeNbtTagsFromBytes(tileBytes));
        }
        byte[] key = LevelDBKey.ENTITIES.getKey(builder.getChunkX(), builder.getChunkZ(), dimensionInfo);
        byte[] entityBytes = db.get(key);
        if (entityBytes != null) {
            builder.entities(deserializeNbtTagsFromBytes(entityBytes));
        }
    }

    private List<NbtMap> deserializeNbtTagsFromBytes(byte[] bytes) {
        List<NbtMap> tags = new ArrayList<>();
        try (BufferedInputStream stream = new BufferedInputStream(new ByteArrayInputStream(bytes))) {
            while (stream.available() > 0) {
                tags.add((NbtMap) NbtUtils.createReaderLE(stream).readTag());
            }
        } catch (IOException e) {
            throw new WorldStorageException(e);
        }
        return tags;
    }

    private BiomeType getBiomeByIdNonNull(int id) {
        try {
            return BiomeId.fromId(id);
        } catch (ArrayIndexOutOfBoundsException e) {
            log.warn("Unknown biome id: {}", id);
            return BiomeId.PLAINS;
        }
    }
}
