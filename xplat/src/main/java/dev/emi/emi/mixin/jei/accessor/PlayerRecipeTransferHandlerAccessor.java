package dev.emi.emi.mixin.jei.accessor;

import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.common.transfer.PlayerRecipeTransferHandler;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.screen.PlayerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerRecipeTransferHandler.class)
public interface PlayerRecipeTransferHandlerAccessor {
    @Accessor("handler")
    IRecipeTransferHandler<PlayerScreenHandler, CraftingRecipe> getRecipeTransferHandler();
}
