package dev.emi.emi.mixin.jei.accessor;

import mezz.jei.common.deprecated.gui.recipes.layout.RecipeLayoutLegacyAdapter;
import mezz.jei.common.gui.recipes.layout.IRecipeLayoutInternal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeLayoutLegacyAdapter.class)
public interface RecipeLayoutLegacyAdapterAccessor<R> {
    @Accessor("recipeLayout")
    IRecipeLayoutInternal<R> getRecipeLayout();
}
