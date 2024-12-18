package dev.emi.emi.mixin.jei;

import com.google.common.collect.ImmutableTable;
import mezz.jei.common.recipes.RecipeTransferManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RecipeTransferManager.class)
public class RecipeTransferManagerMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    public void getRecipeTransferManager(ImmutableTable recipeTransferHandlers, CallbackInfo ci) {
        dev.emi.emi.jemi.RecipeTransferManager.INSTANCE = (mezz.jei.common.recipes.RecipeTransferManager) (Object) this;
    }
}
