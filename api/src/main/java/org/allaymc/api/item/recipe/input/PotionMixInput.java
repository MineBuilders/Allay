package org.allaymc.api.item.recipe.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.allaymc.api.item.ItemStack;

/**
 * @author IWareQ
 */
@Getter
@AllArgsConstructor
public class PotionMixInput implements Input {
    private final ItemStack ingredient;
    private final ItemStack reagent;
}
