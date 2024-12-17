package dev.emi.emi.mixin.jei.accessor;

import mezz.jei.common.ingredients.IngredientManager;
import mezz.jei.common.ingredients.RegisteredIngredients;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(IngredientManager.class)
public interface IngredientManagerAccessor {
    @Accessor("registeredIngredients")
    RegisteredIngredients getRegisteredIngredients();
}
