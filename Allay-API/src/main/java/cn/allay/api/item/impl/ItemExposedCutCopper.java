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
public interface ItemExposedCutCopper extends ItemStack {
    ItemType<ItemExposedCutCopper> TYPE = ItemTypeBuilder
            .builder(ItemExposedCutCopper.class)
            .vanillaItem(VanillaItemId.EXPOSED_CUT_COPPER, true)
            .addBasicComponents()
            .build().register(ItemTypeRegistry.getRegistry());
}
