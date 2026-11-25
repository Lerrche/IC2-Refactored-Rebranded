package ic2.integration.jei.recipe.machine;

import ic2.api.recipe.IElectrolyzerRecipeManager;
import ic2.core.ref.Ic2Blocks;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ElectrolyzerCategory implements IRecipeCategory<ElectrolyzerRecipeWrapper> {
  private final RecipeType<ElectrolyzerRecipeWrapper> recipeType;
  private final IDrawable icon;
  private final IDrawable background;
  private final IDrawableStatic slotDrawable;
  private final IDrawableAnimated arrowDrawable;

  public ElectrolyzerCategory(
      RecipeType<ElectrolyzerRecipeWrapper> recipeType, IGuiHelper guiHelper) {
    this.recipeType = recipeType;
    this.icon = guiHelper.createDrawableItemStack(new ItemStack(Ic2Blocks.ELECTROLYZER));
    this.slotDrawable = guiHelper.getSlotDrawable();
    this.arrowDrawable = guiHelper.createAnimatedRecipeArrow(60);
    this.background = guiHelper.createBlankDrawable(160, 76);
  }

  @Override
  public @NotNull RecipeType<ElectrolyzerRecipeWrapper> getRecipeType() {
    return this.recipeType;
  }

  @Override
  public @NotNull Component getTitle() {
    return Component.translatable("block.ic2.electrolyzer");
  }

  @Override
  public int getWidth() {
    return 160;
  }

  @Override
  public int getHeight() {
    return 76;
  }

  @Override
  public IDrawable getIcon() {
    return this.icon;
  }

  @Override
  public void setRecipe(
      @NotNull IRecipeLayoutBuilder builder,
      @NotNull ElectrolyzerRecipeWrapper recipe,
      @NotNull IFocusGroup focuses) {
    // Input Fluid slot at (16, 12), size 16x16 inside 18x18 border at (15, 11)
    int inAmount = recipe.getInputFluid().getAmount();
    builder
        .addSlot(RecipeIngredientRole.INPUT, 16, 12)
        .addFluidStack(recipe.getInputFluid().getFluid(), inAmount)
        .setFluidRenderer(Math.max(inAmount, 1000), false, 16, 16);

    // Output Fluid slots at (80, 12) and (122, 12)
    List<IElectrolyzerRecipeManager.ElectrolyzerOutput> outputs = recipe.getOutputs();
    for (int i = 0; i < outputs.size(); i++) {
      IElectrolyzerRecipeManager.ElectrolyzerOutput out = outputs.get(i);
      int slotX = outputs.size() == 1 ? 101 : (80 + i * 42);
      builder
          .addSlot(RecipeIngredientRole.OUTPUT, slotX, 12)
          .addFluidStack(out.fluid(), out.fluidAmount())
          .setFluidRenderer(Math.max(out.fluidAmount(), 1000), false, 16, 16);
    }
  }

  @Override
  public void draw(
      @NotNull ElectrolyzerRecipeWrapper recipe,
      @NotNull IRecipeSlotsView recipeSlotsView,
      @NotNull GuiGraphics guiGraphics,
      double mouseX,
      double mouseY) {
    // Slot boxes (18x18 with 1px border around 16x16 slots)
    this.slotDrawable.draw(guiGraphics, 15, 11);
    this.arrowDrawable.draw(guiGraphics, 44, 12);

    Font font = Minecraft.getInstance().font;

    // Input labels
    String inHeader = "Input";
    guiGraphics.drawString(font, inHeader, 24 - font.width(inHeader) / 2, 33, 0x666666, false);
    String inAmount = recipe.getInputFluid().getAmount() + " mB";
    guiGraphics.drawString(font, inAmount, 24 - font.width(inAmount) / 2, 44, 0x555555, false);

    // Output slots & labels
    List<IElectrolyzerRecipeManager.ElectrolyzerOutput> outputs = recipe.getOutputs();
    for (int i = 0; i < outputs.size(); i++) {
      IElectrolyzerRecipeManager.ElectrolyzerOutput out = outputs.get(i);
      int slotX = outputs.size() == 1 ? 101 : (80 + i * 42);
      this.slotDrawable.draw(guiGraphics, slotX - 1, 11);

      int centerX = slotX + 8;

      // Direction label (e.g. "↓ Down", "↑ Up")
      String dirText = formatDirection(out.tankDirection());
      guiGraphics.drawString(font, dirText, centerX - font.width(dirText) / 2, 33, 0x1a4687, false);

      // Amount label (e.g. "26 mB")
      String amountText = out.fluidAmount() + " mB";
      guiGraphics.drawString(
          font, amountText, centerX - font.width(amountText) / 2, 44, 0x555555, false);
    }

    // Energy & Time description line centered at x = 80
    String timeStr =
        recipe.getTicks() % 20 == 0
            ? (recipe.getTicks() / 20) + "s"
            : String.format(java.util.Locale.US, "%.1fs", recipe.getTicks() / 20.0);
    String info =
        recipe.getEuPerTick() + " EU/t (" + recipe.getTotalEu() + " EU) \u2022 " + timeStr;
    guiGraphics.drawString(font, info, 80 - font.width(info) / 2, 59, 0x444444, false);
  }

  private static String formatDirection(Direction dir) {
    if (dir == null) return "Tank";
    return switch (dir) {
      case DOWN -> "\u2193 Down";
      case UP -> "\u2191 Up";
      case NORTH -> "North";
      case SOUTH -> "South";
      case WEST -> "West";
      case EAST -> "East";
    };
  }
}
