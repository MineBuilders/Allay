package org.allaymc.server.world.chunk;

import lombok.experimental.Delegate;
import lombok.extern.slf4j.Slf4j;
import org.allaymc.api.block.type.BlockState;
import org.allaymc.api.world.biome.BiomeType;
import org.allaymc.api.world.chunk.Chunk;
import org.allaymc.api.world.chunk.ChunkSection;
import org.allaymc.api.world.chunk.OperationType;
import org.allaymc.api.world.chunk.UnsafeChunk;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Consumer;

/**
 * @author Cool_Loong | daoge_cmd
 */
@Slf4j
public class AllayChunk implements Chunk {

    @Delegate(types = UnsafeChunk.class)
    protected final AllayUnsafeChunk unsafeChunk;

    protected final ChunkSectionLocks blockLocks;
    protected final ChunkSectionLocks biomeLocks;

    AllayChunk(AllayUnsafeChunk unsafeChunk) {
        this.unsafeChunk = unsafeChunk;

        // Init locks
        var dimensionInfo = unsafeChunk.getDimensionInfo();
        this.blockLocks = new ChunkSectionLocks(dimensionInfo);
        this.biomeLocks = new ChunkSectionLocks(dimensionInfo);
    }

    @Override
    public short getHeight(int x, int z) {
        return unsafeChunk.getHeight(x, z);
    }

    @Override
    public BlockState getBlockState(int x, int y, int z, int layer) {
        var sectionY = y >> 4;
        blockLocks.lockReadLockAt(sectionY);
        try {
            return unsafeChunk.getBlockState(x, y, z, layer);
        } finally {
            blockLocks.unlockReadLockAt(sectionY);
        }
    }

    @Override
    public void setBlockState(int x, int y, int z, BlockState blockState, int layer, boolean send) {
        var sectionY = y >> 4;
        blockLocks.lockWriteLockAt(sectionY);
        try {
            unsafeChunk.setBlockState(x, y, z, blockState, layer, send);
        } finally {
            blockLocks.unlockWriteLockAt(sectionY);
        }
    }

    @Override
    public BiomeType getBiome(int x, int y, int z) {
        var sectionY = y >> 4;
        biomeLocks.lockReadLockAt(sectionY);
        try {
            return unsafeChunk.getBiome(x, y, z);
        } finally {
            biomeLocks.unlockReadLockAt(sectionY);
        }
    }

    @Override
    public void setBiome(int x, int y, int z, BiomeType biomeType) {
        var sectionY = y >> 4;
        biomeLocks.lockWriteLockAt(sectionY);
        try {
            unsafeChunk.setBiome(x, y, z, biomeType);
        } finally {
            biomeLocks.unlockWriteLockAt(sectionY);
        }
    }

    @Override
    public void applyOperation(Consumer<UnsafeChunk> operation, OperationType block, OperationType biome) {
        tryLockAllSections(block, blockLocks);
        tryLockAllSections(biome, biomeLocks);
        try {
            operation.accept(unsafeChunk);
        } finally {
            tryUnlockAllSections(block, blockLocks);
            tryUnlockAllSections(biome, biomeLocks);
        }
    }

    @Override
    public void applyOperationInSection(int sectionY, Consumer<ChunkSection> operation, OperationType block, OperationType biome) {
        tryLockSection(sectionY, block, blockLocks);
        tryLockSection(sectionY, biome, biomeLocks);
        try {
            operation.accept(unsafeChunk.getSection(sectionY));
        } finally {
            tryUnlockSection(sectionY, block, blockLocks);
            tryUnlockSection(sectionY, biome, biomeLocks);
        }
    }

    protected void tryLockAllSections(OperationType operationType, ChunkSectionLocks locks) {
        switch (operationType) {
            case READ -> locks.lockAllReadLocks();
            case WRITE -> locks.lockAllWriteLocks();
        }
    }

    protected void tryLockSection(int sectionY, OperationType operationType, ChunkSectionLocks locks) {
        switch (operationType) {
            case READ -> locks.lockReadLockAt(sectionY);
            case WRITE -> locks.lockWriteLockAt(sectionY);
        }
    }

    protected void tryUnlockAllSections(OperationType operationType, ChunkSectionLocks locks) {
        switch (operationType) {
            case READ -> locks.unlockAllReadLocks();
            case WRITE -> locks.unlockAllWriteLocks();
        }
    }

    protected void tryUnlockSection(int sectionY, OperationType operationType, ChunkSectionLocks locks) {
        switch (operationType) {
            case READ -> locks.unlockReadLockAt(sectionY);
            case WRITE -> locks.unlockWriteLockAt(sectionY);
        }
    }

    protected void tryLock(OperationType operationType, ReadWriteLock lock) {
        switch (operationType) {
            case READ -> lock.readLock().lock();
            case WRITE -> lock.writeLock().lock();
        }
    }

    protected void tryUnlock(OperationType operationType, ReadWriteLock lock) {
        switch (operationType) {
            case READ -> lock.readLock().unlock();
            case WRITE -> lock.writeLock().unlock();
        }
    }

    @Override
    public UnsafeChunk toUnsafeChunk() {
        return unsafeChunk;
    }
}
