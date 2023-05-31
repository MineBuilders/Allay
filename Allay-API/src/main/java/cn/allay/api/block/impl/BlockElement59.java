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
public interface BlockElement59 extends Block {
    BlockType<BlockElement59> TYPE = BlockTypeBuilder
            .builder(BlockElement59.class)
            .vanillaBlock(VanillaBlockId.ELEMENT_59, true)
            .addBasicComponents()
            .build().register(BlockTypeRegistry.getRegistry());
}
