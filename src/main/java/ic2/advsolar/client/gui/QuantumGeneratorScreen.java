package ic2.advsolar.client.gui;

import ic2.advsolar.gui.QuantumGeneratorMenu;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class QuantumGeneratorScreen extends AbstractContainerScreen<QuantumGeneratorMenu> {
  private static final ResourceLocation TEXTURE =
      ResourceLocation.fromNamespaceAndPath(
          "advanced_solar_panels", "textures/gui/quantumgenerator.png");

  private final List<Button> prodButtons = new ArrayList<>();

  public QuantumGeneratorScreen(
      QuantumGeneratorMenu menu, Inventory playerInventory, Component title) {
    super(menu, playerInventory, title);
    this.imageWidth = 176;
    this.imageHeight = 193;
    this.inventoryLabelY = 1000; // Hide default label
  }

  @Override
  protected void init() {
    super.init();
    this.prodButtons.clear();
    int relX = (this.width - this.imageWidth) / 2;
    int relY = (this.height - this.imageHeight) / 2;

    // Row 1: Production buttons (ids 0..5 normal, 10..15 shift)
    int[] normalValues = {-100, -10, -1, 1, 10, 100};
    int[] shiftValues = {-500, -50, -5, 5, 50, 500};
    int[] widths = {32, 26, 20, 20, 26, 32};
    int[] xOffsets = {6, 39, 66, 89, 110, 137};

    for (int i = 0; i < 6; i++) {
      final int buttonIdx = i;
      Button btn =
          Button.builder(
                  Component.literal(formatNumber(normalValues[i])),
                  b -> {
                    int id = Screen.hasShiftDown() ? (10 + buttonIdx) : buttonIdx;
                    if (this.minecraft != null && this.minecraft.gameMode != null) {
                      this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
                    }
                  })
              .bounds(relX + xOffsets[i], relY + 40, widths[i], 20)
              .build();
      this.addRenderableWidget(btn);
      this.prodButtons.add(btn);
    }

    // Row 2: Tier buttons (20..25)
    String[] tierLabels = {"1", "2", "3", "4", "5", "Max"};
    int[] tierWidths = {24, 24, 24, 24, 24, 32};
    int[] tierX = {6, 32, 58, 84, 110, 138};

    for (int i = 0; i < 6; i++) {
      final int id = 20 + i;
      this.addRenderableWidget(
          Button.builder(
                  Component.literal(tierLabels[i]),
                  b -> {
                    if (this.minecraft != null && this.minecraft.gameMode != null) {
                      this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
                    }
                  })
              .bounds(relX + tierX[i], relY + 84, tierWidths[i], 20)
              .build());
    }
  }

  private String formatNumber(int val) {
    return val > 0 ? ("+" + val) : Integer.toString(val);
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
    int relX = (this.width - this.imageWidth) / 2;
    int relY = (this.height - this.imageHeight) / 2;

    // Background texture
    graphics.blit(TEXTURE, relX, relY, 0, 0, this.imageWidth, this.imageHeight);

    // Active lightning icon at x=145, y=21
    if (this.menu.isActive()) {
      graphics.blit(TEXTURE, relX + 145, relY + 21, 176, 3, 14, 14);
    }

    // Update button text if Shift is pressed
    int[] normalValues = {-100, -10, -1, 1, 10, 100};
    int[] shiftValues = {-500, -50, -5, 5, 50, 500};
    boolean shift = Screen.hasShiftDown();
    for (int i = 0; i < this.prodButtons.size(); i++) {
      int val = shift ? shiftValues[i] : normalValues[i];
      this.prodButtons.get(i).setMessage(Component.literal(formatNumber(val)));
    }
  }

  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    // Title centered at y=7
    graphics.drawCenteredString(this.font, this.title, this.imageWidth / 2, 7, 0xFFFFFF);

    // Output label right-aligned to x=88, value inside black box at x=95
    String outLabel = "Output:";
    int outLabelX = 88 - this.font.width(outLabel);
    graphics.drawString(this.font, outLabel, outLabelX, 25, 0xFFFFFF, false);
    graphics.drawString(
        this.font, String.format("%,d", this.menu.getProduction()), 95, 25, 0x55FF55, false);

    // Tier label right-aligned to x=88, value inside black box at x=95
    String tierLabel = "Tier:";
    int tierLabelX = 88 - this.font.width(tierLabel);
    graphics.drawString(this.font, tierLabel, tierLabelX, 69, 0xFFFFFF, false);
    graphics.drawString(this.font, String.valueOf(this.menu.getTier()), 95, 69, 0xFFFF55, false);
  }

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    super.render(graphics, mouseX, mouseY, partialTick);
    this.renderTooltip(graphics, mouseX, mouseY);
  }
}
