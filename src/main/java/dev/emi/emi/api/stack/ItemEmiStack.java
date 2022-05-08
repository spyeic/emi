package dev.emi.emi.api.stack;

import java.util.List;

import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.screen.FakeScreen;
import dev.emi.emi.screen.StackBatcher.Batchable;
import dev.emi.emi.screen.tooltip.RemainderTooltipComponent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemEmiStack extends EmiStack implements Batchable {
	private final ItemStackEntry entry;
	public final ItemStack stack;
	private boolean unbatchable;

	public ItemEmiStack(ItemStack stack) {
		this(stack, stack.getCount());
	}

	public ItemEmiStack(ItemStack stack, int amount) {
		this.stack = stack.copy();
		this.stack.setCount(1);
		entry = new ItemStackEntry(this.stack);
		this.amount = amount;
	}

	@Override
	public ItemStack getItemStack() {
		return stack;
	}

	@Override
	public EmiStack copy() {
		EmiStack e = new ItemEmiStack(stack.copy());
		e.setRemainder(getRemainder().copy());
		e.comparison = comparison;
		return e;
	}

	@Override
	public boolean isEmpty() {
		return stack.isEmpty();
	}

	@Override
	public NbtCompound getNbt() {
		return stack.getNbt();
	}

	@Override
	public Object getKey() {
		return stack.getItem();
	}

	@Override
	public Entry<?> getEntry() {
		return entry;
	}

	@Override
	public Identifier getId() {
		return Registry.ITEM.getId(stack.getItem());
	}

	@Override
	public void renderIcon(MatrixStack matrices, int x, int y, float delta) {
		MinecraftClient client = MinecraftClient.getInstance();
		client.getItemRenderer().renderInGui(stack, x, y);
		String count = "";
		if (amount != 1) {
			count += amount;
		}
		client.getItemRenderer().renderGuiItemOverlay(client.textRenderer, stack, x, y, count);
	}

	@Override
	public void renderOverlay(MatrixStack matrices, int x, int y, float delta) {
		EmiRenderHelper.renderRemainder(this, matrices, x, y);
	}
	
	@Override
	public boolean isSideLit() {
		return MinecraftClient.getInstance().getItemRenderer().getModel(stack, null, null, 0).isSideLit();
	}
	
	@Override
	public boolean isUnbatchable() {
		return unbatchable || stack.hasGlint() || MinecraftClient.getInstance().getItemRenderer().getModel(stack, null, null, 0).isBuiltin();
	}
	
	@Override
	public void setUnbatchable() {
		this.unbatchable = true;
	}
	
	@Override
	public void renderForBatch(VertexConsumerProvider vcp, MatrixStack matrices, int x, int y, int z, float delta) {
		ItemRenderer ir = MinecraftClient.getInstance().getItemRenderer();
		BakedModel model = ir.getModel(stack, null, null, 0);
		matrices.push();
		try {
			matrices.translate(x, y, 100.0f + z + (model.hasDepth() ? 50 : 0));
			matrices.translate(8.0, 8.0, 0.0);
			matrices.scale(16.0f, 16.0f, 16.0f);
			ir.renderItem(stack, ModelTransformation.Mode.GUI, false, matrices, vcp, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, model);
		} finally {
			matrices.pop();
		}
	}

	@Override
	public List<Text> getTooltipText() {
		return FakeScreen.INSTANCE.getTooltipFromItem(stack);
	}

	@Override
	public List<TooltipComponent> getTooltip() {
		if (!stack.isEmpty()) {
			List<TooltipComponent> list = FakeScreen.INSTANCE.getTooltipComponentListFromItem(stack);
			String namespace = Registry.ITEM.getId(stack.getItem()).getNamespace();
			String mod = EmiUtil.getModName(namespace);
			list.add(TooltipComponent.of(new LiteralText(mod).formatted(Formatting.BLUE, Formatting.ITALIC).asOrderedText()));
			if (!getRemainder().isEmpty()) {
				list.add(new RemainderTooltipComponent(this));
			}
			return list;
		} else {
			return List.of();
		}
	}

	@Override
	public Text getName() {
		return stack.getName();
	}

	public static class ItemStackEntry extends Entry<ItemStack> {

		public ItemStackEntry(ItemStack stack) {
			super(stack);
		}

		@Override
		Class<ItemStack> getType() {
			return ItemStack.class;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof ItemStackEntry e && getValue().isItemEqual(e.getValue());
		}
	}
}