package ic2.integration.jei.recipe.machine;

import ic2.core.ref.Ic2Blocks;
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
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

public class BoozeBarrelCategory implements IRecipeCategory<BoozeBarrelRecipeWrapper> {
  private final RecipeType<BoozeBarrelRecipeWrapper> recipeType;
  private final IDrawable icon;
  private final IDrawableStatic slotDrawable;
  private final IDrawableAnimated arrowDrawable;

  public BoozeBarrelCategory(
      RecipeType<BoozeBarrelRecipeWrapper> recipeType, IGuiHelper guiHelper) {
    this.recipeType = recipeType;
    this.icon = guiHelper.createDrawableItemStack(new ItemStack(Ic2Blocks.BARREL));
    this.slotDrawable = guiHelper.getSlotDrawable();
    this.arrowDrawable = guiHelper.createAnimatedRecipeArrow(60);
  }

  @Override
  public @NotNull RecipeType<BoozeBarrelRecipeWrapper> getRecipeType() {
    return this.recipeType;
  }

  @Override
  public @NotNull Component getTitle() {
    return Component.translatable("ic2.barrel.gui.brewing");
  }

  @Override
  public int getWidth() {
    return 160;
  }

  @Override
  public int getHeight() {
    return 68;
  }

  @Override
  public IDrawable getIcon() {
    return this.icon;
  }

  @Override
  public void setRecipe(
      @NotNull IRecipeLayoutBuilder builder,
      @NotNull BoozeBarrelRecipeWrapper recipe,
      @NotNull IFocusGroup focuses) {
    // Slot 0: Water fluid (1000 mB)
    builder
        .addSlot(RecipeIngredientRole.INPUT, 5, 5)
        .addFluidStack(Fluids.WATER, 1000)
        .setFluidRenderer(1000, false, 16, 16);

    // Slot 1: Water Container (Buckets / Cells)
    if (!recipe.getWaterContainers().isEmpty()) {
      builder
          .addSlot(RecipeIngredientRole.INPUT, 5, 25)
          .addItemStacks(recipe.getWaterContainers());
    }

    // Slot 2: Ingredient 1 (Wheat / Sugar Cane)
    if (!recipe.getIngredient1().isEmpty()) {
      builder
          .addSlot(RecipeIngredientRole.INPUT, 25, 5)
          .addItemStacks(recipe.getIngredient1());
    }

    // Slot 3: Ingredient 2 (Hops / Sugar)
    if (!recipe.getIngredient2().isEmpty()) {
      builder
          .addSlot(RecipeIngredientRole.INPUT, 25, 25)
          .addItemStacks(recipe.getIngredient2());
    }

    // Slot 4: Treetap (Catalyst/Tool)
    if (!recipe.getTaps().isEmpty()) {
      builder
          .addSlot(RecipeIngredientRole.INPUT, 45, 5)
          .addItemStacks(recipe.getTaps());
    }

    // Slot 5: Empty Stone Mug
    builder
        .addSlot(RecipeIngredientRole.INPUT, 45, 25)
        .addItemStack(recipe.getEmptyMug());

    // Slot 6: Output Mug
    builder
        .addSlot(RecipeIngredientRole.OUTPUT, 119, 15)
        .addItemStack(recipe.getOutput());
  }

  @Override
  public void draw(
      @NotNull BoozeBarrelRecipeWrapper recipe,
      @NotNull IRecipeSlotsView recipeSlotsView,
      @NotNull GuiGraphics guiGraphics,
      double mouseX,
      double mouseY) {
    this.slotDrawable.draw(guiGraphics, 4, 4);
    this.slotDrawable.draw(guiGraphics, 4, 24);
    this.slotDrawable.draw(guiGraphics, 24, 4);
    this.slotDrawable.draw(guiGraphics, 24, 24);
    this.slotDrawable.draw(guiGraphics, 44, 4);
    this.slotDrawable.draw(guiGraphics, 44, 24);

    this.arrowDrawable.draw(guiGraphics, 76, 14);
    this.slotDrawable.draw(guiGraphics, 118, 14);

    Font font = Minecraft.getInstance().font;
    String dur = recipe.getDuration().getString();
    if (font.width(dur) > 152) {
      dur = font.plainSubstrByWidth(dur, 146) + "...";
    }
    String ratio = recipe.getRatioDesc().getString();
    if (font.width(ratio) > 152) {
      ratio = font.plainSubstrByWidth(ratio, 146) + "...";
    }
    guiGraphics.drawString(font, dur, 4, 46, 0x303030, false);
    guiGraphics.drawString(font, ratio, 4, 56, 0x606060, false);
  }
}
