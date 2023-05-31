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
public interface ItemDarkOakTrapdoor extends ItemStack {
    ItemType<ItemDarkOakTrapdoor> TYPE = ItemTypeBuilder
            .builder(ItemDarkOakTrapdoor.class)
            .vanillaItem(VanillaItemId.DARK_OAK_TRAPDOOR, true)
            .addBasicComponents()
            .build().register(ItemTypeRegistry.getRegistry());
}
