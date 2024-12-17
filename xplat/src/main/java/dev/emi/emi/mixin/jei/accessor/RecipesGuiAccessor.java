package dev.emi.emi.mixin.jei.accessor;

import mezz.jei.common.gui.recipes.RecipesGui;
import mezz.jei.common.recipes.RecipeTransferManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipesGui.class)
public interface RecipesGuiAccessor {
    @Accessor("recipeTransferManager")
    RecipeTransferManager getRecipeTransferManager();
}
