package cn.allay.api.block.impl;

import cn.allay.api.block.Block;
import cn.allay.api.block.type.BlockType;
import cn.allay.api.block.type.BlockTypeBuilder;
import cn.allay.api.block.type.BlockTypeRegistry;
import cn.allay.api.data.VanillaBlockId;

/**
 * Author: daoge_cmd <br>
 * Allay Project <br>
 */
public interface BlockRawIronBlock extends Block {
    BlockType<BlockRawIronBlock> TYPE = BlockTypeBuilder
            .builder(BlockRawIronBlock.class)
            .vanillaBlock(VanillaBlockId.RAW_IRON_BLOCK, true)
            .addBasicComponents()
            .build().register(BlockTypeRegistry.getRegistry());
}
