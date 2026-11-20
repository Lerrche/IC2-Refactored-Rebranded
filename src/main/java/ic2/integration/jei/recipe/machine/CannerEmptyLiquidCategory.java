package ic2.integration.jei.recipe.machine;

import ic2.core.ref.Ic2Blocks;
import java.util.List;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
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

public class CannerEmptyLiquidCategory implements IRecipeCategory<CannerEmptyLiquidRecipeWrapper> {
  private final RecipeType<CannerEmptyLiquidRecipeWrapper> recipeType;
  private final IDrawable icon;
  private final IDrawable background;
  private final IDrawableStatic slotDrawable;
  private final IDrawableAnimated arrowDrawable;

  public CannerEmptyLiquidCategory(
      RecipeType<CannerEmptyLiquidRecipeWrapper> recipeType, IGuiHelper guiHelper) {
    this.recipeType = recipeType;
    this.icon = guiHelper.createDrawableItemStack(new ItemStack(Ic2Blocks.CANNER));
    this.slotDrawable = guiHelper.getSlotDrawable();
    this.arrowDrawable = guiHelper.createAnimatedRecipeArrow(60);
    this.background = guiHelper.createBlankDrawable(160, 54);
  }

  @Override
  public @NotNull RecipeType<CannerEmptyLiquidRecipeWrapper> getRecipeType() {
    return this.recipeType;
  }

  @Override
  public @NotNull Component getTitle() {
    return Component.translatable("ic2.Canner.gui.switch.EmptyLiquid");
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
      @NotNull CannerEmptyLiquidRecipeWrapper recipe,
      @NotNull IFocusGroup focuses) {
    // Input: Filled Container (16x16 inside 18x18 slot at (17, 11))
    builder.addSlot(RecipeIngredientRole.INPUT, 18, 12).addItemStack(recipe.getFilledContainer());

    // Output: Drained / Empty Container (16x16 inside 18x18 slot at (81, 11))
    IRecipeSlotBuilder drainedSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, 82, 12);
    List<ItemStack> drainedContainers = recipe.getDrainedContainers();
    if (!drainedContainers.isEmpty()) {
      drainedSlot.addItemStacks(drainedContainers);
    }

    // Output: Drained Fluid into Tank (16x16 inside 18x18 slot at (117, 11))
    int fluidAmount = recipe.getFluidOutput().getAmount();
    builder
        .addSlot(RecipeIngredientRole.OUTPUT, 118, 12)
        .addFluidStack(recipe.getFluidOutput().getFluid(), fluidAmount)
        .setFluidRenderer(Math.max(fluidAmount, 1000), false, 16, 16);
  }

  @Override
  public void draw(
      @NotNull CannerEmptyLiquidRecipeWrapper recipe,
      @NotNull IRecipeSlotsView recipeSlotsView,
      @NotNull GuiGraphics guiGraphics,
      double mouseX,
      double mouseY) {
    this.slotDrawable.draw(guiGraphics, 17, 11);
    this.arrowDrawable.draw(guiGraphics, 48, 12);
    this.slotDrawable.draw(guiGraphics, 81, 11);
    this.slotDrawable.draw(guiGraphics, 117, 11);

    Font font = Minecraft.getInstance().font;
    guiGraphics.drawString(font, "+", 104, 16, 0x555555, false);

    String fluidDesc =
        recipe.getFluidOutput().getAmount()
            + " mB "
            + recipe.getFluidOutput().getHoverName().getString();
    if (font.width(fluidDesc) > 150) {
      fluidDesc = font.plainSubstrByWidth(fluidDesc, 144) + "...";
    }
    guiGraphics.drawString(font, fluidDesc, 6, 36, 0x555555, false);
  }
}
