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
public interface BlockRedMushroom extends Block {
    BlockType<BlockRedMushroom> TYPE = BlockTypeBuilder
            .builder(BlockRedMushroom.class)
            .vanillaBlock(VanillaBlockId.RED_MUSHROOM, true)
            .addBasicComponents()
            .build().register(BlockTypeRegistry.getRegistry());
}
