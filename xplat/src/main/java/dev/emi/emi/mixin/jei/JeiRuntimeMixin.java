package dev.emi.emi.mixin.jei;

import dev.emi.emi.jemi.runtime.JemiBookmarkOverlay;
import dev.emi.emi.jemi.runtime.JemiIngredientFilter;
import dev.emi.emi.jemi.runtime.JemiIngredientListOverlay;
import dev.emi.emi.jemi.runtime.JemiRecipesGui;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.runtime.*;
import mezz.jei.common.ingredients.RegisteredIngredients;
import mezz.jei.common.runtime.JeiRuntime;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JeiRuntime.class)
public class JeiRuntimeMixin {
    @Mutable
    @Shadow @Final private IBookmarkOverlay bookmarkOverlay;

    @Mutable
    @Shadow @Final private IIngredientListOverlay ingredientListOverlay;

    @Mutable
    @Shadow @Final private IRecipesGui recipesGui;

    @Mutable
    @Shadow @Final private IIngredientFilter ingredientFilter;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void setRuntimeRegistration(IRecipeManager recipeManager, IIngredientListOverlay ingredientListOverlay, IBookmarkOverlay bookmarkOverlay, IRecipesGui recipesGui, IIngredientFilter ingredientFilter, RegisteredIngredients registeredIngredients, IIngredientManager ingredientManager, IIngredientVisibility ingredientVisibility, IJeiHelpers jeiHelpers, CallbackInfo ci) {
        this.ingredientListOverlay = new JemiIngredientListOverlay();
        this.bookmarkOverlay = new JemiBookmarkOverlay();
        this.recipesGui = new JemiRecipesGui();
        this.ingredientFilter = new JemiIngredientFilter();
    }
}
