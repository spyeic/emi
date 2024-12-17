package dev.emi.emi.platform.forge;

import java.util.Arrays;

import com.mojang.blaze3d.systems.RenderSystem;

import dev.emi.emi.EmiPort;
import dev.emi.emi.data.EmiData;
import dev.emi.emi.network.EmiNetwork;
import dev.emi.emi.platform.EmiClient;
import dev.emi.emi.registry.EmiTags;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.runtime.EmiReloadManager;
import dev.emi.emi.screen.ConfigScreen;
import dev.emi.emi.screen.EmiScreenBase;
import dev.emi.emi.screen.EmiScreenManager;
import dev.emi.emi.screen.StackBatcher;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = "emi", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EmiClientForge {
	
	@SubscribeEvent
	public static void clientInit(FMLClientSetupEvent event) {
		StackBatcher.EXTRA_RENDER_LAYERS.addAll(Arrays.stream(ForgeRenderTypes.values()).map(f -> f.get()).toList());
		EmiClient.init();
		EmiNetwork.initClient(packet -> EmiPacketHandler.CHANNEL.sendToServer(packet));
		MinecraftForge.EVENT_BUS.addListener(EmiClientForge::recipesReloaded);
		MinecraftForge.EVENT_BUS.addListener(EmiClientForge::tagsReloaded);
		MinecraftForge.EVENT_BUS.addListener(EmiClientForge::renderScreenForeground);
		MinecraftForge.EVENT_BUS.addListener(EmiClientForge::postRenderScreen);
		ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class,
			() -> new ConfigGuiHandler.ConfigGuiFactory((client, last) -> new ConfigScreen(last)));
	}

	@SubscribeEvent
	public static void registerAdditionalModels(ModelRegistryEvent event) {
		MinecraftClient client = MinecraftClient.getInstance();
		EmiTags.registerTagModels(client.getResourceManager(), ForgeModelBakery::addSpecialModel);
	}

	@SubscribeEvent
	public static void registerResourceReloaders(RegisterClientReloadListenersEvent event) {
		EmiData.init(event::registerReloadListener);
	}

	public static void recipesReloaded(RecipesUpdatedEvent event) {
		EmiReloadManager.reloadRecipes();
	}

	public static void tagsReloaded(TagsUpdatedEvent event) {
		EmiReloadManager.reloadTags();
	}

	public static void renderScreenForeground(ContainerScreenEvent.DrawForeground event) {
		EmiDrawContext context = EmiDrawContext.wrap(event.getPoseStack());
		HandledScreen<?> screen = event.getContainerScreen();
		EmiScreenBase base = EmiScreenBase.of(screen);
		if (base != null) {
			MinecraftClient client = MinecraftClient.getInstance();
			MatrixStack viewStack = RenderSystem.getModelViewStack();
			viewStack.push();
			viewStack.translate(-screen.getGuiLeft(), -screen.getGuiTop(), 0.0);
			RenderSystem.applyModelViewMatrix();
			EmiPort.setPositionTexShader();
			EmiScreenManager.render(context, event.getMouseX(), event.getMouseY(), client.getTickDelta());
			viewStack.pop();
			RenderSystem.applyModelViewMatrix();
		}
	}

	public static void postRenderScreen(ScreenEvent.DrawScreenEvent.Post event) {
		EmiDrawContext context = EmiDrawContext.wrap(event.getPoseStack());
		Screen screen = event.getScreen();
		if (!(screen instanceof HandledScreen<?>)) {
			return;
		}
		EmiScreenBase base = EmiScreenBase.of(screen);
		if (base != null) {
			MinecraftClient client = MinecraftClient.getInstance();
			context.push();
			EmiPort.setPositionTexShader();
			EmiScreenManager.drawForeground(context, event.getMouseX(), event.getMouseY(), client.getTickDelta());
			context.pop();
		}
	}
}
