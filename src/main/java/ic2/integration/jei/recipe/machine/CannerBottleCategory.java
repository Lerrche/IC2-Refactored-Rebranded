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
import org.jetbrains.annotations.NotNull;

public class CannerBottleCategory implements IRecipeCategory<CannerBottleRecipeWrapper> {
  private final RecipeType<CannerBottleRecipeWrapper> recipeType;
  private final IDrawable icon;
  private final IDrawable background;
  private final IDrawableStatic slotDrawable;
  private final IDrawableAnimated arrowDrawable;

  public CannerBottleCategory(
      RecipeType<CannerBottleRecipeWrapper> recipeType, IGuiHelper guiHelper) {
    this.recipeType = recipeType;
    this.icon = guiHelper.createDrawableItemStack(new ItemStack(Ic2Blocks.SOLID_CANNER));
    this.slotDrawable = guiHelper.getSlotDrawable();
    this.arrowDrawable = guiHelper.createAnimatedRecipeArrow(60);
    this.background = guiHelper.createBlankDrawable(160, 54);
  }

  @Override
  public @NotNull RecipeType<CannerBottleRecipeWrapper> getRecipeType() {
    return this.recipeType;
  }

  @Override
  public @NotNull Component getTitle() {
    return Component.translatable("ic2.Canner.gui.switch.BottleSolid");
  }

  @Override
  public int getWidth() {
    return 160;
  }

  @Override
  public int getHeight() {
    return 54;
  }

  @Override
  public IDrawable getIcon() {
    return this.icon;
  }

  @Override
  public void setRecipe(
      @NotNull IRecipeLayoutBuilder builder,
      @NotNull CannerBottleRecipeWrapper recipe,
      @NotNull IFocusGroup focuses) {
    // Input Container: 16x16 inside 18x18 slot at (17, 11)
    if (!recipe.getContainers().isEmpty()) {
      builder.addSlot(RecipeIngredientRole.INPUT, 18, 12).addItemStacks(recipe.getContainers());
    }
    // Input Fill item: 16x16 inside 18x18 slot at (53, 11)
    if (!recipe.getFills().isEmpty()) {
      builder.addSlot(RecipeIngredientRole.INPUT, 54, 12).addItemStacks(recipe.getFills());
    }
    // Output Canned item: 16x16 inside 18x18 slot at (117, 11)
    if (!recipe.getOutput().isEmpty()) {
      builder.addSlot(RecipeIngredientRole.OUTPUT, 118, 12).addItemStack(recipe.getOutput());
    }
  }

  @Override
  public void draw(
      @NotNull CannerBottleRecipeWrapper recipe,
      @NotNull IRecipeSlotsView recipeSlotsView,
      @NotNull GuiGraphics guiGraphics,
      double mouseX,
      double mouseY) {
    this.slotDrawable.draw(guiGraphics, 17, 11);
    this.slotDrawable.draw(guiGraphics, 53, 11);
    this.arrowDrawable.draw(guiGraphics, 80, 12);
    this.slotDrawable.draw(guiGraphics, 117, 11);

    Font font = Minecraft.getInstance().font;
    guiGraphics.drawString(font, "+", 41, 16, 0x555555, false);

    if (!recipe.getOutput().isEmpty()) {
      String outDesc = recipe.getOutput().getHoverName().getString();
      if (font.width(outDesc) > 150) {
        outDesc = font.plainSubstrByWidth(outDesc, 144) + "...";
      }
      guiGraphics.drawString(font, outDesc, 6, 36, 0x555555, false);
    }
  }
}
