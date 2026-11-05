package ic2.advsolar.client.gui;

import ic2.advsolar.gui.SolarPanelMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class SolarPanelScreen extends AbstractContainerScreen<SolarPanelMenu> {
  private static final ResourceLocation TEXTURE =
      ResourceLocation.fromNamespaceAndPath(
          "advanced_solar_panels", "textures/gui/advancedsolarpanel.png");

  public SolarPanelScreen(SolarPanelMenu menu, Inventory playerInventory, Component title) {
    super(menu, playerInventory, title);
    this.imageWidth = 194;
    this.imageHeight = 168;
    this.titleLabelX = (this.imageWidth - 100) / 2;
    this.titleLabelY = 7;
    this.inventoryLabelY = 1000; // Hide default "Inventory" label since texture has no room
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
    int relX = (this.width - this.imageWidth) / 2;
    int relY = (this.height - this.imageHeight) / 2;

    // Background texture (194 x 168)
    graphics.blit(TEXTURE, relX, relY, 0, 0, this.imageWidth, this.imageHeight);

    // Energy gauge (24 x 14 at x=19, y=24)
    int maxStored = this.menu.getMaxStorageEu();
    int currentStored = this.menu.getStoredEu();
    if (maxStored > 0 && currentStored > 0) {
      int gaugeWidth = (int) Math.min(24, Math.round(24.0 * currentStored / maxStored));
      graphics.blit(TEXTURE, relX + 19, relY + 24, 195, 0, gaugeWidth, 14);
    }

    // Sun / Moon indicator (14 x 14 at x=24, y=41)
    if (this.menu.isSunVisible()) {
      graphics.blit(TEXTURE, relX + 24, relY + 41, 195, 15, 14, 14);
    } else {
      graphics.blit(TEXTURE, relX + 24, relY + 41, 210, 15, 14, 14);
    }
  }

  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    // Title
    graphics.drawCenteredString(this.font, this.title, this.imageWidth / 2, 7, 0xCDCDCD);

    // Energy stats
    int stored = this.menu.getStoredEu();
    int maxStored = this.menu.getMaxStorageEu();
    String fullStorageText = String.format("Storage: %,d / %,d EU", stored, maxStored);
    String displayedStorageText = fullStorageText;

    // If full text overflows available width (> 138 px from x=50 to x=188), format compactly
    if (this.font.width(displayedStorageText) > 138) {
      displayedStorageText =
          String.format("Storage: %s / %s EU", formatCompact(stored), formatCompact(maxStored));
    }

    String maxOutText = String.format("Max Output: %d EU/t", this.menu.getOutputEuPerTick());
    int generating =
        this.menu.isSunVisible() ? this.menu.getDayProduction() : this.menu.getNightProduction();
    String generatingText = String.format("Generating: %d EU/t", generating);

    graphics.drawString(this.font, displayedStorageText, 50, 22, 0xCDCDCD, false);
    graphics.drawString(this.font, maxOutText, 50, 33, 0xCDCDCD, false);
    graphics.drawString(this.font, generatingText, 50, 44, 0xCDCDCD, false);
  }

  private String formatCompact(int eu) {
    if (eu >= 1_000_000_000) {
      return String.format("%.2fB", eu / 1_000_000_000.0);
    } else if (eu >= 1_000_000) {
      return String.format("%.2fM", eu / 1_000_000.0);
    } else if (eu >= 10_000) {
      return String.format("%.1fk", eu / 1000.0);
    }
    return String.format("%,d", eu);
  }

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    super.render(graphics, mouseX, mouseY, partialTick);
    this.renderTooltip(graphics, mouseX, mouseY);

    int relX = (this.width - this.imageWidth) / 2;
    int relY = (this.height - this.imageHeight) / 2;

    // Hover tooltip for exact storage numbers if hovering over gauge or storage text
    boolean hoverGauge =
        mouseX >= relX + 19 && mouseX <= relX + 43 && mouseY >= relY + 24 && mouseY <= relY + 38;
    boolean hoverStorageText =
        mouseX >= relX + 50
            && mouseX <= relX + this.imageWidth - 6
            && mouseY >= relY + 20
            && mouseY <= relY + 32;

    if (hoverGauge || hoverStorageText) {
      String fullStorage =
          String.format(
              "Storage: %,d / %,d EU", this.menu.getStoredEu(), this.menu.getMaxStorageEu());
      graphics.renderTooltip(this.font, Component.literal(fullStorage), mouseX, mouseY);
    }
  }
}
