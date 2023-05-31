package cn.allay.api.item.impl;

import cn.allay.api.data.VanillaItemId;
import cn.allay.api.item.ItemStack;
import cn.allay.api.item.type.ItemType;
import cn.allay.api.item.type.ItemTypeBuilder;
import cn.allay.api.item.type.ItemTypeRegistry;

/**
 * Author: daoge_cmd <br>
 * Allay Project <br>
 */
public interface ItemUndyedShulkerBox extends ItemStack {
    ItemType<ItemUndyedShulkerBox> TYPE = ItemTypeBuilder
            .builder(ItemUndyedShulkerBox.class)
            .vanillaItem(VanillaItemId.UNDYED_SHULKER_BOX, true)
            .addBasicComponents()
            .build().register(ItemTypeRegistry.getRegistry());
}
