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
public interface ItemDarkOakHangingSign extends ItemStack {
    ItemType<ItemDarkOakHangingSign> TYPE = ItemTypeBuilder
            .builder(ItemDarkOakHangingSign.class)
            .vanillaItem(VanillaItemId.DARK_OAK_HANGING_SIGN, true)
            .addBasicComponents()
            .build().register(ItemTypeRegistry.getRegistry());
}
