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
public interface BlockCalibratedSculkSensor extends Block {
    BlockType<BlockCalibratedSculkSensor> TYPE = BlockTypeBuilder
            .builder(BlockCalibratedSculkSensor.class)
            .vanillaBlock(VanillaBlockId.CALIBRATED_SCULK_SENSOR, true)
            .withProperties(VanillaBlockPropertyTypes.DIRECTION,
                    VanillaBlockPropertyTypes.POWERED_BIT)
            .addBasicComponents()
            .build().register(BlockTypeRegistry.getRegistry());
}
