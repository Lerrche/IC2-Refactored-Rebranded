package ic2.integration.jei.recipe.machine;

import ic2.core.ref.Ic2Blocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MatterFabricatorCategory implements IRecipeCategory<MatterFabricatorRecipeWrapper> {
  private static final ResourceLocation GUI_TEXTURE =
      ResourceLocation.fromNamespaceAndPath("ic2", "textures/gui/guimatter.png");

  private final RecipeType<MatterFabricatorRecipeWrapper> recipeType;
  private final IDrawable icon;
  private final IDrawable background;
  private final String progressLabel;
  private final String amplifierLabel;

  public MatterFabricatorCategory(
      RecipeType<MatterFabricatorRecipeWrapper> recipeType, IGuiHelper guiHelper) {
    this.recipeType = recipeType;
    this.icon = guiHelper.createDrawableItemStack(new ItemStack(Ic2Blocks.MATTER_GENERATOR));
    this.background = guiHelper.createDrawable(GUI_TEXTURE, 4, 16, 146, 66);
    this.progressLabel = Component.translatable("ic2.Matter.gui.info.progress").getString();
    this.amplifierLabel = Component.translatable("ic2.Matter.gui.info.amplifier").getString();
  }

  @Override
  public @NotNull RecipeType<MatterFabricatorRecipeWrapper> getRecipeType() {
    return this.recipeType;
  }

  @Override
  public @NotNull Component getTitle() {
    return Component.translatable("block.ic2.matter_generator");
  }

  @Override
  public int getWidth() {
    return 146;
  }

  @Override
  public int getHeight() {
    return 66;
  }

  @Override
  public IDrawable getIcon() {
    return this.icon;
  }

  @Override
  public void setRecipe(
      @NotNull IRecipeLayoutBuilder builder,
      @NotNull MatterFabricatorRecipeWrapper recipe,
      @NotNull IFocusGroup focuses) {
    // Input Slot (Amplifier / Scrap)
    if (!recipe.getInputs().isEmpty()) {
      builder.addSlot(RecipeIngredientRole.INPUT, 68, 24).addItemStacks(recipe.getInputs());
    }

    // Output Fluid Tank (Liquid UU-Matter)
    if (!recipe.getOutputFluid().isEmpty()) {
      builder
          .addSlot(RecipeIngredientRole.OUTPUT, 96, 10)
          .addFluidStack(recipe.getOutputFluid().getFluid(), recipe.getOutputFluid().getAmount())
          .setFluidRenderer(1000, false, 12, 47);
    }

    // Input Slot (Empty Cell / Container)
    if (!recipe.getContainerInput().isEmpty()) {
      builder.addSlot(RecipeIngredientRole.INPUT, 121, 7).addItemStack(recipe.getContainerInput());
    }

    // Output Slot (UU-Matter item & UU-Matter Cell)
    if (!recipe.getOutputs().isEmpty()) {
      builder.addSlot(RecipeIngredientRole.OUTPUT, 121, 43).addItemStacks(recipe.getOutputs());
    }
  }

  @Override
  public void draw(
      @NotNull MatterFabricatorRecipeWrapper recipe,
      @NotNull IRecipeSlotsView recipeSlotsView,
      @NotNull GuiGraphics guiGraphics,
      double mouseX,
      double mouseY) {
    // Draw background texture directly from guimatter.png
    this.background.draw(guiGraphics, 0, 0);

    Font font = Minecraft.getInstance().font;
    guiGraphics.drawString(font, this.progressLabel, 4, 6, 0x404040, false);
    guiGraphics.drawString(font, "100%", 14, 15, 0x404040, false);
    guiGraphics.drawString(font, this.amplifierLabel, 4, 30, 0x404040, false);
    guiGraphics.drawString(font, recipe.getAmplifierText(), 4, 42, 0x404040, false);
    if (recipe.getEnergyText() != null && !recipe.getEnergyText().isEmpty()) {
      guiGraphics.drawString(font, recipe.getEnergyText(), 4, 54, 0x555555, false);
    }
  }
}
