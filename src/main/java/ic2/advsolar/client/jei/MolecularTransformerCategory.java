package ic2.advsolar.client.jei;

import ic2.advsolar.AdvancedSolarPanelsMod;
import ic2.advsolar.blocks.entity.MolecularTransformerBlockEntity;
import java.util.Locale;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MolecularTransformerCategory
    implements IRecipeCategory<MolecularTransformerBlockEntity.TransmutationRecipe> {
  public static final RecipeType<MolecularTransformerBlockEntity.TransmutationRecipe> RECIPE_TYPE =
      RecipeType.create(
          "advanced_solar_panels",
          "molecular_transformer",
          MolecularTransformerBlockEntity.TransmutationRecipe.class);

  public static final ResourceLocation TEXTURE =
      ResourceLocation.fromNamespaceAndPath(
          "advanced_solar_panels", "textures/gui/moleculartransformer.png");

  private final IDrawable background;
  private final IDrawable icon;
  private final IDrawableAnimated arrow;
  private final Component title;

  public MolecularTransformerCategory(IGuiHelper guiHelper) {
    this.background = guiHelper.createDrawable(TEXTURE, 0, 0, 220, 92);
    this.icon =
        guiHelper.createDrawableItemStack(
            new ItemStack(AdvancedSolarPanelsMod.MOLECULAR_TRANSFORMER.get()));
    this.title = Component.translatable("advanced_solar_panels.machines.molecular_transformer");

    IDrawableStatic arrowStatic = guiHelper.createDrawable(TEXTURE, 221, 7, 10, 15);
    this.arrow =
        guiHelper.createAnimatedDrawable(
            arrowStatic, 50, IDrawableAnimated.StartDirection.TOP, false);
  }

  @Override
  @NotNull public RecipeType<MolecularTransformerBlockEntity.TransmutationRecipe> getRecipeType() {
    return RECIPE_TYPE;
  }

  @Override
  @NotNull public Component getTitle() {
    return this.title;
  }

  @Override
  @NotNull public IDrawable getBackground() {
    return this.background;
  }

  @Override
  @Nullable public IDrawable getIcon() {
    return this.icon;
  }

  @Override
  public void setRecipe(
      IRecipeLayoutBuilder builder,
      MolecularTransformerBlockEntity.TransmutationRecipe recipe,
      IFocusGroup focuses) {
    builder.addSlot(RecipeIngredientRole.INPUT, 20, 27).addIngredients(recipe.input());
    builder.addSlot(RecipeIngredientRole.OUTPUT, 20, 68).addItemStack(recipe.output());
  }

  @Override
  public void draw(
      MolecularTransformerBlockEntity.TransmutationRecipe recipe,
      IRecipeSlotsView recipeSlotsView,
      GuiGraphics guiGraphics,
      double mouseX,
      double mouseY) {
    this.arrow.draw(guiGraphics, 23, 48);

    Minecraft mc = Minecraft.getInstance();
    Font font = mc.font;

    // Title centered
    guiGraphics.drawCenteredString(font, this.title, 110, 9, 0xFFFFFF);

    // Labels right-aligned to 107
    drawRightAligned(guiGraphics, font, "Input:", 107, 26, 0xFFFFFF);
    drawRightAligned(guiGraphics, font, "Output:", 107, 40, 0xFFFFFF);
    drawRightAligned(guiGraphics, font, "Energy:", 107, 54, 0xFFFFFF);
    drawRightAligned(guiGraphics, font, "Tier:", 107, 68, 0xFFFFFF);

    // Values at 112
    ItemStack[] matching = recipe.input().getItems();
    String inName = matching.length > 0 ? matching[0].getHoverName().getString() : "-";
    if (matching.length > 1) {
      inName += " (" + matching.length + ")";
    }
    String outName =
        recipe.output().getCount() > 1
            ? recipe.output().getCount() + "x " + recipe.output().getHoverName().getString()
            : recipe.output().getHoverName().getString();
    String formattedEu =
        String.format(Locale.ROOT, "%,d", (long) recipe.euCost()).replace(',', ' ') + " EU";

    guiGraphics.drawString(font, trim(font, inName, 95), 112, 26, 0xFFFFFF, false);
    guiGraphics.drawString(font, trim(font, outName, 95), 112, 40, 0xFFFFFF, false);
    guiGraphics.drawString(font, formattedEu, 112, 54, 0xFFFF55, false);
    guiGraphics.drawString(font, "5 (Extreme / HV)", 112, 68, 0x55FF55, false);
  }

  private void drawRightAligned(
      GuiGraphics graphics, Font font, String text, int rightX, int y, int color) {
    int x = rightX - font.width(text);
    graphics.drawString(font, text, x, y, color, false);
  }

  private String trim(Font font, String text, int maxWidth) {
    if (font.width(text) <= maxWidth) {
      return text;
    }
    String s = text;
    while (s.length() > 3 && font.width(s + "...") > maxWidth) {
      s = s.substring(0, s.length() - 1);
    }
    return s + "...";
  }
}
