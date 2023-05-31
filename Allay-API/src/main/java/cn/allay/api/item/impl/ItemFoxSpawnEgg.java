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
public interface ItemFoxSpawnEgg extends ItemStack {
    ItemType<ItemFoxSpawnEgg> TYPE = ItemTypeBuilder
            .builder(ItemFoxSpawnEgg.class)
            .vanillaItem(VanillaItemId.FOX_SPAWN_EGG, true)
            .addBasicComponents()
            .build().register(ItemTypeRegistry.getRegistry());
}
