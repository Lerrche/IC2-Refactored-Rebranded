package ic2.advsolar.client.jei;

import ic2.advsolar.AdvancedSolarPanelsMod;
import ic2.advsolar.blocks.entity.MolecularTransformerBlockEntity;
import ic2.advsolar.client.gui.MolecularTransformerScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class AdvancedSolarJeiPlugin implements IModPlugin {
  public static final ResourceLocation UID =
      ResourceLocation.fromNamespaceAndPath("advanced_solar_panels", "jei_plugin");

  @Override
  @NotNull public ResourceLocation getPluginUid() {
    return UID;
  }

  @Override
  public void registerCategories(IRecipeCategoryRegistration registration) {
    registration.addRecipeCategories(
        new MolecularTransformerCategory(registration.getJeiHelpers().getGuiHelper()));
  }

  @Override
  public void registerRecipes(@NotNull IRecipeRegistration registration) {
    registration.addRecipes(
        MolecularTransformerCategory.RECIPE_TYPE, MolecularTransformerBlockEntity.getRecipes());
  }

  @Override
  public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
    registration.addRecipeCatalyst(
        new ItemStack(AdvancedSolarPanelsMod.MOLECULAR_TRANSFORMER.get()),
        MolecularTransformerCategory.RECIPE_TYPE);
  }

  @Override
  public void registerGuiHandlers(IGuiHandlerRegistration registration) {
    registration.addRecipeClickArea(
        MolecularTransformerScreen.class, 23, 48, 10, 15, MolecularTransformerCategory.RECIPE_TYPE);
  }
}
