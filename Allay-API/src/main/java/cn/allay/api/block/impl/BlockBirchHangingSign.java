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
public interface BlockBirchHangingSign extends Block {
    BlockType<BlockBirchHangingSign> TYPE = BlockTypeBuilder
            .builder(BlockBirchHangingSign.class)
            .vanillaBlock(VanillaBlockId.BIRCH_HANGING_SIGN, true)
            .withProperties(VanillaBlockPropertyTypes.ATTACHED_BIT,
                    VanillaBlockPropertyTypes.FACING_DIRECTION,
                    VanillaBlockPropertyTypes.GROUND_SIGN_DIRECTION,
                    VanillaBlockPropertyTypes.HANGING)
            .addBasicComponents()
            .build().register(BlockTypeRegistry.getRegistry());
}
