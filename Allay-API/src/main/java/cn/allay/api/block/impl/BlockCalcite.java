package cn.allay.api.block.impl;

import cn.allay.api.block.Block;
import cn.allay.api.data.VanillaBlockId;
import cn.allay.api.block.type.BlockType;
import cn.allay.api.block.type.BlockTypeBuilder;
import cn.allay.api.block.type.BlockTypeRegistry;

/**
 * Author: daoge_cmd <br>
 * Allay Project <br>
 */
public interface BlockCalcite extends Block {
    BlockType<BlockCalcite> TYPE = BlockTypeBuilder
            .builder(BlockCalcite.class)
            .vanillaBlock(VanillaBlockId.CALCITE, true)
            .addBasicComponents()
            .build().register(BlockTypeRegistry.getRegistry());
}
