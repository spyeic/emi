package dev.emi.emi.jemi;

import dev.emi.emi.mixin.jei.accessor.IngredientManagerAccessor;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.common.gui.GuiScreenHelper;
import mezz.jei.common.ingredients.TypedIngredient;
import mezz.jei.common.input.ClickedIngredient;
import mezz.jei.common.input.IClickedIngredient;
import mezz.jei.common.platform.IPlatformScreenHelper;
import mezz.jei.common.platform.Services;
import mezz.jei.common.util.ImmutableRect2i;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

import java.util.Optional;
import java.util.stream.Stream;

public class ScreenHelper {
    public static GuiScreenHelper INSTANCE;

    public static Stream<IClickedIngredient<?>> getClickableIngredientUnderMouse(Screen screen, double mouseX, double mouseY) {
        return Stream.concat(
                INSTANCE.getPluginsIngredientUnderMouse(null, mouseX, mouseY),
                getSlotIngredientUnderMouse(screen).stream()
        );
    }

    private static Optional<IClickedIngredient<?>> getClickedIngredient(Slot slot, HandledScreen<?> guiContainer) {
        ItemStack stack = slot.getStack();
        return TypedIngredient.create(((IngredientManagerAccessor) JemiPlugin.runtime.getIngredientManager()).getRegisteredIngredients(), VanillaTypes.ITEM_STACK, stack)
                .map(typedIngredient -> {
                    IPlatformScreenHelper screenHelper = Services.PLATFORM.getScreenHelper();
                    ImmutableRect2i slotArea = new ImmutableRect2i(
                            screenHelper.getGuiLeft(guiContainer) + slot.x,
                            screenHelper.getGuiTop(guiContainer) + slot.y,
                            16,
                            16
                    );
                    return new ClickedIngredient<>(typedIngredient, slotArea, false, false);
                });
    }

    private static Optional<IClickedIngredient<?>> getSlotIngredientUnderMouse(Screen guiScreen) {
        if (!(guiScreen instanceof HandledScreen<?> guiContainer)) {
            return Optional.empty();
        }
        IPlatformScreenHelper screenHelper = Services.PLATFORM.getScreenHelper();
        Slot slot = screenHelper.getSlotUnderMouse(guiContainer);
        if (slot == null) {
            return Optional.empty();
        }
        return getClickedIngredient(slot, guiContainer);
    }
}