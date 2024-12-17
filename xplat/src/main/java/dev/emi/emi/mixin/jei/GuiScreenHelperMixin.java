package dev.emi.emi.mixin.jei;

import dev.emi.emi.jemi.ScreenHelper;
import mezz.jei.common.gui.GuiContainerHandlers;
import mezz.jei.common.gui.GuiScreenHelper;
import mezz.jei.common.ingredients.RegisteredIngredients;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(GuiScreenHelper.class)
public class GuiScreenHelperMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    public void init(RegisteredIngredients registeredIngredients, List globalGuiHandlers, GuiContainerHandlers guiContainerHandlers, Map ghostIngredientHandlers, Map guiScreenHandlers, CallbackInfo ci) {
        ScreenHelper.INSTANCE = (GuiScreenHelper) (Object) this;
    }
}
