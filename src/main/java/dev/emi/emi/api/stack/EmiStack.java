package dev.emi.emi.api.stack;

import java.util.List;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import dev.emi.emi.EmiComparisonDefaults;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * An abstract representation of an object in EMI.
 * Can be an item, a fluid, or something else.
 */
public abstract class EmiStack implements EmiIngredient {
	public static final EmiStack EMPTY = new EmptyEmiStack();
	private EmiStack remainder = EMPTY;
	protected Comparison comparison = Comparison.DEFAULT_COMPARISON;
	protected int amount = 1;

	@Override
	public List<EmiStack> getEmiStacks() {
		return List.of(this);
	}

	public EmiStack getRemainder() {
		return remainder;
	}

	public EmiStack setRemainder(EmiStack stack) {
		if (stack == this) {
			stack = stack.copy();
		}
		remainder = stack;
		return this;
	}

	public EmiStack comparison(Function<Comparison, Comparison> comparison) {
		this.comparison = comparison.apply(this.comparison);
		return this;
	}

	public abstract EmiStack copy();

	public abstract boolean isEmpty();

	public int getAmount() {
		return amount;
	}
	
	public EmiStack setAmount(int amount) {
		this.amount = amount;
		return this;
	}

	public abstract NbtCompound getNbt();

	public boolean hasNbt() {
		return getNbt() != null;
	}

	public abstract Object getKey();

	public abstract Entry<?> getEntry();

	public abstract Identifier getId();

	@SuppressWarnings("unchecked")
	public <T> @Nullable Entry<T> getEntryOfType(Class<T> clazz) {
		Entry<?> entry = getEntry();
		if (entry.getType() == clazz) {
			return (Entry<T>) entry;
		}
		return null;
	}

	public ItemStack getItemStack() {
		return ItemStack.EMPTY;
	}

	public boolean isEqual(EmiStack stack) {
		boolean amount = comparison.amount.orElseGet(() -> EmiComparisonDefaults.get(getKey()).amount.orElse(false))
			|| stack.comparison.amount.orElseGet(() -> EmiComparisonDefaults.get(stack.getKey()).amount.orElse(false));
		boolean nbt = comparison.nbt.orElseGet(() -> EmiComparisonDefaults.get(getKey()).nbt.orElse(false))
			|| stack.comparison.nbt.orElseGet(() -> EmiComparisonDefaults.get(stack.getKey()).nbt.orElse(false));
		if (!getEntry().equals(stack.getEntry())) {
			return false;
		}
		if (nbt && (hasNbt() != stack.hasNbt() || (hasNbt() && !getNbt().equals(stack.getNbt())))) {
			return false;
		}
		if (amount && getAmount() != stack.getAmount()) {
			return false;
		}
		return true;
	}

	@Override
	public void render(MatrixStack matrices, int x, int y, float delta) {
		renderIcon(matrices, x, y, delta);
		renderOverlay(matrices, x, y, delta);
	}

	public abstract void renderIcon(MatrixStack matrices, int x, int y, float delta);

	public abstract void renderOverlay(MatrixStack matrices, int x, int y, float delta);

	public abstract List<Text> getTooltipText();

	public abstract List<TooltipComponent> getTooltip();

	public abstract Text getName();

	@Override
	public boolean equals(Object obj) {
		return obj instanceof EmiStack stack && this.isEqual(stack);
	}

	@Override
	public int hashCode() {
		return getKey().hashCode();
	}

	public static EmiStack of(ItemStack stack) {
		return new ItemEmiStack(stack);
	}

	public static EmiStack of(ItemConvertible item) {
		return of(new ItemStack(item));
	}

	public static EmiStack of(FluidVariant fluid) {
		return new FluidEmiStack(fluid);
	}

	public static EmiStack of(FluidVariant fluid, int amount) {
		return new FluidEmiStack(fluid, amount);
	}

	public static EmiStack of(Fluid fluid) {
		return of(FluidVariant.of(fluid));
	}

	public static EmiStack of(Fluid fluid, int amount) {
		return of(FluidVariant.of(fluid), amount);
	}

	public static abstract class Entry<T> {
		private final T value;

		public Entry(T value) {
			this.value = value;
		}

		public T getValue() {
			return value;
		}

		abstract Class<T> getType();
	}
}