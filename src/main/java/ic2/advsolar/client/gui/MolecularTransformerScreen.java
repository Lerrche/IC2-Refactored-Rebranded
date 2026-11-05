package ic2.advsolar.client.gui;

import ic2.advsolar.gui.MolecularTransformerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class MolecularTransformerScreen extends AbstractContainerScreen<MolecularTransformerMenu> {
  private static final ResourceLocation TEXTURE =
      ResourceLocation.fromNamespaceAndPath(
          "advanced_solar_panels", "textures/gui/moleculartransformer.png");

  public MolecularTransformerScreen(
      MolecularTransformerMenu menu, Inventory playerInventory, Component title) {
    super(menu, playerInventory, title);
    this.imageWidth = 220;
    this.imageHeight = 193;
    this.inventoryLabelY = 1000; // Hide default label
  }

  @Override
  protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
    int relX = (this.width - this.imageWidth) / 2;
    int relY = (this.height - this.imageHeight) / 2;

    // Background (220 x 193)
    graphics.blit(TEXTURE, relX, relY, 0, 0, this.imageWidth, this.imageHeight);

    // Progress arrow downwards at x=23, y=48 (10 x 15, u=221, v=7)
    long required = this.menu.getRequiredEu();
    long current = this.menu.getProgressEu();
    if (required > 0 && current > 0) {
      int progressH = (int) Math.min(15, Math.round(15.0 * current / required));
      graphics.blit(TEXTURE, relX + 23, relY + 48, 221, 7, 10, progressH);
    }
  }

  @Override
  protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    // Title centered at y=9
    graphics.drawCenteredString(this.font, this.title, this.imageWidth / 2, 9, 0xFFFFFF);

    ItemStack inputStack = this.menu.getInputItem();
    ItemStack outputStack = this.menu.getOutputItem();
    long current = this.menu.getProgressEu();
    long required = this.menu.getRequiredEu();

    String inputName = inputStack.isEmpty() ? "-" : inputStack.getHoverName().getString();
    String outputName = outputStack.isEmpty() ? "-" : outputStack.getHoverName().getString();

    // In original 1.12 guidef XML:
    // Labels are right-aligned to x=107:
    // y=26: Input:
    // y=38: Output:
    // y=50: Energy per op:
    // y=62: Energy:
    // y=74: Progress:
    // Values start at x=112:
    drawRightAligned(graphics, "Input:", 107, 26, 0xFFFFFF);
    drawRightAligned(graphics, "Output:", 107, 38, 0xFFFFFF);
    drawRightAligned(graphics, "Cost:", 107, 50, 0xFFFFFF);
    drawRightAligned(graphics, "Energy:", 107, 62, 0xFFFFFF);
    drawRightAligned(graphics, "Progress:", 107, 74, 0xFFFFFF);

    graphics.drawString(this.font, trimName(inputName, 95), 112, 26, 0xFFFFFF, false);
    graphics.drawString(this.font, trimName(outputName, 95), 112, 38, 0xFFFFFF, false);

    if (required > 0) {
      graphics.drawString(this.font, formatCompact(required) + " EU", 112, 50, 0xFFFF55, false);
      graphics.drawString(this.font, formatCompact(current) + " EU", 112, 62, 0x55FF55, false);
      double pct = Math.min(100.0, (100.0 * current) / required);
      graphics.drawString(this.font, String.format("%.1f%%", pct), 112, 74, 0x55FF55, false);
    } else {
      graphics.drawString(this.font, "-", 112, 50, 0xAAAAAA, false);
      graphics.drawString(this.font, "-", 112, 62, 0xAAAAAA, false);
      graphics.drawString(this.font, "Idle", 112, 74, 0xAAAAAA, false);
    }
  }

  private void drawRightAligned(GuiGraphics graphics, String text, int rightX, int y, int color) {
    int x = rightX - this.font.width(text);
    graphics.drawString(this.font, text, x, y, color, false);
  }

  private String trimName(String name, int maxWidth) {
    if (this.font.width(name) <= maxWidth) {
      return name;
    }
    String trimmed = name;
    while (trimmed.length() > 3 && this.font.width(trimmed + "...") > maxWidth) {
      trimmed = trimmed.substring(0, trimmed.length() - 1);
    }
    return trimmed + "...";
  }

  private String formatCompact(long eu) {
    if (eu >= 1_000_000_000L) {
      return String.format("%.2fB", eu / 1_000_000_000.0);
    } else if (eu >= 1_000_000L) {
      return String.format("%.2fM", eu / 1_000_000.0);
    } else if (eu >= 10_000L) {
      return String.format("%.1fk", eu / 1000.0);
    }
    return String.format("%,d", eu);
  }

  @Override
  public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    super.render(graphics, mouseX, mouseY, partialTick);
    this.renderTooltip(graphics, mouseX, mouseY);
  }
}
