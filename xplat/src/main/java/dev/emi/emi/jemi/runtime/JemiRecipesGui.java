package dev.emi.emi.jemi.runtime;

import java.util.List;
import java.util.Optional;

import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.jemi.JemiUtil;
import dev.emi.emi.screen.RecipeScreen;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class JemiRecipesGui implements IRecipesGui {

	@Override
	public <V> void show(IFocus<V> focus) {
		show(List.of(focus));
	}

	@Override
	public void show(List<IFocus<?>> focuses) {
		for (IFocus<?> focus : focuses) {
			EmiStack stack = JemiUtil.getStack(focus.getTypedValue());
			if (!stack.isEmpty()) {
				RecipeIngredientRole role = focus.getRole();
				if (role == RecipeIngredientRole.OUTPUT) {
					EmiApi.displayRecipes(stack);
				} else {
					EmiApi.displayUses(stack);
				}
			}
		}
	}

	@Override
	public void showTypes(List<RecipeType<?>> recipeTypes) {
		for (RecipeType<?> type : recipeTypes) {
			for (EmiRecipeCategory category : EmiApi.getRecipeManager().getCategories()) {
				if (category.getId().equals(type.getUid())) {
					EmiApi.displayRecipeCategory(category);
				}
			}
		}
	}

	@Override
	public <T> T getIngredientUnderMouse(IIngredientType<T> ingredientType) {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.currentScreen instanceof RecipeScreen screen) {
			EmiIngredient stack = screen.getHoveredStack();
			if (!stack.isEmpty()) {
				Optional<ITypedIngredient<?>> opt = JemiUtil.getTyped(stack.getEmiStacks().get(0));
				if (opt.isPresent()) {
					return opt.get().getIngredient(ingredientType).orElse(null);
				}
			}
		}
		return null;
	}

	@Override
	public void showCategories(List<Identifier> recipeCategoryUids) {
		for (Identifier uid : recipeCategoryUids) {
			for (EmiRecipeCategory category : EmiApi.getRecipeManager().getCategories()) {
				if (category.getId().equals(uid)) {
					EmiApi.displayRecipeCategory(category);
				}
			}
		}
	}
}
