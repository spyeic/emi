package dev.emi.emi.mixin.jei.accessor;

import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.common.transfer.BasicRecipeTransferHandler;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BasicRecipeTransferHandler.class)
public interface BasicRecipeTransferHandlerAccessor<C extends ScreenHandler, R> {
    @Accessor("transferInfo")
    IRecipeTransferInfo<C, R> getTransferInfo();
}
