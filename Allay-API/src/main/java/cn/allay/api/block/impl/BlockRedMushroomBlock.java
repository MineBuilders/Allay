package cn.allay.api.block.impl;

import cn.allay.api.block.Block;
import cn.allay.api.data.VanillaBlockId;
import cn.allay.api.data.VanillaBlockPropertyTypes;
import cn.allay.api.block.type.BlockType;
import cn.allay.api.block.type.BlockTypeBuilder;
import cn.allay.api.block.type.BlockTypeRegistry;

/**
 * Author: daoge_cmd <br>
 * Allay Project <br>
 */
public interface BlockRedMushroomBlock extends Block {
    BlockType<BlockRedMushroomBlock> TYPE = BlockTypeBuilder
            .builder(BlockRedMushroomBlock.class)
            .vanillaBlock(VanillaBlockId.RED_MUSHROOM_BLOCK, true)
            .withProperties(VanillaBlockPropertyTypes.HUGE_MUSHROOM_BITS)
            .addBasicComponents()
            .build().register(BlockTypeRegistry.getRegistry());
}
