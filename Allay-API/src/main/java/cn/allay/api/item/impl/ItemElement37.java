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
public interface ItemElement37 extends ItemStack {
    ItemType<ItemElement37> TYPE = ItemTypeBuilder
            .builder(ItemElement37.class)
            .vanillaItem(VanillaItemId.ELEMENT_37, true)
            .addBasicComponents()
            .build().register(ItemTypeRegistry.getRegistry());
}
