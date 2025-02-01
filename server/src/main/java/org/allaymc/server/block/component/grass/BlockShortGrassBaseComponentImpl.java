package org.allaymc.server.block.component.grass;

import org.allaymc.api.block.BlockBehavior;
import org.allaymc.api.block.data.BlockFace;
import org.allaymc.api.block.dto.BlockStateWithPos;
import org.allaymc.api.block.type.BlockType;
import org.allaymc.api.entity.Entity;
import org.allaymc.api.item.ItemStack;
import org.allaymc.api.item.interfaces.ItemAirStack;
import org.allaymc.api.item.type.ItemTypes;
import org.allaymc.server.block.component.BlockBaseComponentImpl;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.allaymc.api.block.type.BlockTypes.*;

/**
 * @author daoge_cmd
 */
public class BlockShortGrassBaseComponentImpl extends BlockBaseComponentImpl {
    public BlockShortGrassBaseComponentImpl(BlockType<? extends BlockBehavior> blockType) {
        super(blockType);
    }

    @Override
    public void onNeighborUpdate(BlockStateWithPos current, BlockStateWithPos neighbor, BlockFace face) {
        super.onNeighborUpdate(current, neighbor, face);

        if (face != BlockFace.DOWN) {
            return;
        }

        if (!canPlaceOn(neighbor.blockState().getBlockType())) {
            current.pos().dimension().breakBlock(current.pos());
        }
    }

    protected boolean canPlaceOn(BlockType<?> blockType) {
        return blockType == GRASS_BLOCK ||
               blockType == MYCELIUM ||
               blockType == PODZOL ||
               blockType == DIRT ||
               blockType == DIRT_WITH_ROOTS ||
               blockType == FARMLAND ||
               blockType == MUD ||
               blockType == MUDDY_MANGROVE_ROOTS ||
               blockType == MOSS_BLOCK;
    }

    @Override
    public Set<ItemStack> getDrops(BlockStateWithPos blockState, ItemStack usedItem, Entity entity) {
        var rand = ThreadLocalRandom.current();
        if (rand.nextInt(8) == 0) {
            return Set.of(ItemTypes.WHEAT_SEEDS.createItemStack(1));
        }
        return Set.of(ItemAirStack.AIR_STACK);
    }
}
