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
public interface BlockElement70 extends Block {
    BlockType<BlockElement70> TYPE = BlockTypeBuilder
            .builder(BlockElement70.class)
            .vanillaBlock(VanillaBlockId.ELEMENT_70, true)
            .addBasicComponents()
            .build().register(BlockTypeRegistry.getRegistry());
}
