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
public interface ItemGhastTear extends ItemStack {
    ItemType<ItemGhastTear> TYPE = ItemTypeBuilder
            .builder(ItemGhastTear.class)
            .vanillaItem(VanillaItemId.GHAST_TEAR, true)
            .addBasicComponents()
            .build().register(ItemTypeRegistry.getRegistry());
}
