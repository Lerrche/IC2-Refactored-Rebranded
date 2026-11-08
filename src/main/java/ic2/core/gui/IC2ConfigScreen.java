package ic2.core.gui;

import net.minecraft.client.gui.screens.Screen;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;

public final class IC2ConfigScreen {
  private IC2ConfigScreen() {}

  public static Screen create(ModContainer mod, Screen parent) {
    return new ConfigurationScreen(
        mod,
        parent,
        (configScreen, type, modConfig, title) ->
            new IC2ConfigSectionScreen(configScreen, type, modConfig, title));
  }
}
