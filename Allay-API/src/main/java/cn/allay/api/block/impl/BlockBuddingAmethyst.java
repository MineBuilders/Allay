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
public interface BlockBuddingAmethyst extends Block {
    BlockType<BlockBuddingAmethyst> TYPE = BlockTypeBuilder
            .builder(BlockBuddingAmethyst.class)
            .vanillaBlock(VanillaBlockId.BUDDING_AMETHYST, true)
            .addBasicComponents()
            .build().register(BlockTypeRegistry.getRegistry());
}
