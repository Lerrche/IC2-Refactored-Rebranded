package ic2.advsolar.client;

import ic2.advsolar.AdvancedSolarPanelsMod;
import ic2.advsolar.client.gui.MolecularTransformerScreen;
import ic2.advsolar.client.gui.QuantumGeneratorScreen;
import ic2.advsolar.client.gui.SolarPanelScreen;
import ic2.advsolar.client.render.MolecularTransformerRenderer;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

public class AdvancedSolarPanelsClient {
  public static void registerScreens(RegisterMenuScreensEvent event) {
    event.register(AdvancedSolarPanelsMod.SOLAR_PANEL_MENU.get(), SolarPanelScreen::new);
    event.register(
        AdvancedSolarPanelsMod.QUANTUM_GENERATOR_MENU.get(), QuantumGeneratorScreen::new);
    event.register(
        AdvancedSolarPanelsMod.MOLECULAR_TRANSFORMER_MENU.get(), MolecularTransformerScreen::new);
  }

  public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(
        AdvancedSolarPanelsMod.MOLECULAR_TRANSFORMER_BE.get(), MolecularTransformerRenderer::new);
  }
}
