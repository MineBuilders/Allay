package cn.allay.api.block.impl;

import cn.allay.api.block.Block;
import cn.allay.api.block.type.BlockType;
import cn.allay.api.block.type.BlockTypeBuilder;
import cn.allay.api.block.type.BlockTypeRegistry;
import cn.allay.api.data.VanillaBlockId;
import cn.allay.api.data.VanillaBlockPropertyTypes;

/**
 * Author: daoge_cmd <br>
 * Allay Project <br>
 */
public interface BlockDecoratedPot extends Block {
    BlockType<BlockDecoratedPot> TYPE = BlockTypeBuilder
            .builder(BlockDecoratedPot.class)
            .vanillaBlock(VanillaBlockId.DECORATED_POT, true)
            .withProperties(VanillaBlockPropertyTypes.DIRECTION)
            .addBasicComponents()
            .build().register(BlockTypeRegistry.getRegistry());
}
