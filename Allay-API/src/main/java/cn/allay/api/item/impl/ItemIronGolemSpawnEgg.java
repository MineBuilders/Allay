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
public interface ItemIronGolemSpawnEgg extends ItemStack {
    ItemType<ItemIronGolemSpawnEgg> TYPE = ItemTypeBuilder
            .builder(ItemIronGolemSpawnEgg.class)
            .vanillaItem(VanillaItemId.IRON_GOLEM_SPAWN_EGG, true)
            .addBasicComponents()
            .build().register(ItemTypeRegistry.getRegistry());
}
