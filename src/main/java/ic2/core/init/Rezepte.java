package ic2.core.init;

import ic2.api.recipe.IBasicMachineRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.Recipes;
import ic2.core.block.machine.CannerBottleRecipeManager;
import ic2.core.block.machine.CannerEnrichRecipeManager;
import ic2.core.block.machine.EmptyFluidContainerRecipeManager;
import ic2.core.block.machine.FillFluidContainerRecipeManager;
import ic2.core.recipe.BasicMachineRecipeManager;
import ic2.core.recipe.CompressorCompatibility;
import ic2.core.recipe.MaceratorCompatibility;
import ic2.core.recipe.MatterAmplifierRecipeManager;
import ic2.core.recipe.OreWasherCompatibility;
import ic2.core.recipe.ScrapboxRecipeManager;
import ic2.core.recipe.SmeltingRecipeManager;
import ic2.core.recipe.v2.RecipeHolder;
import ic2.core.recipe.v2.RecipeManagerGetter;
import ic2.core.ref.Ic2RecipeTypes;
import ic2.industrialjs.IndustrialJSConfig;
import ic2.industrialjs.IndustrialJSRecipeRegistry;
import java.util.Collection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

public class Rezepte {
  static void loadRecipes() {
    Recipes.furnace = new SmeltingRecipeManager();
    ScrapboxRecipeManager.setup();
    ScrapboxRecipeManager.load();
    IndustrialJSRecipeRegistry.applyMolecularTransformer();
    IndustrialJSRecipeRegistry.applyScrapbox();
    Recipes.emptyFluidContainer = new EmptyFluidContainerRecipeManager();
    Recipes.fillFluidContainer = new FillFluidContainerRecipeManager();
    Recipes.macerator = basicRecipe(Ic2RecipeTypes.MACERATOR);
    Recipes.extractor = basicRecipe(Ic2RecipeTypes.EXTRACTOR);
    Recipes.compressor = basicRecipe(Ic2RecipeTypes.COMPRESSOR);
    Recipes.centrifuge = basicRecipe(Ic2RecipeTypes.CENTRIFUGE);
    Recipes.block_cutter = basicRecipe(Ic2RecipeTypes.BLOCK_CUTTER);
    Recipes.blast_furnace = basicRecipe(Ic2RecipeTypes.BLAST_FURNACE);
    Recipes.metalformerExtruding = basicRecipe(Ic2RecipeTypes.METAL_FORMER_EXTRUDING);
    Recipes.metalformerCutting = basicRecipe(Ic2RecipeTypes.METAL_FORMER_CUTTING);
    Recipes.metalformerRolling = basicRecipe(Ic2RecipeTypes.METAL_FORMER_ROLLING);
    Recipes.oreWashing = basicRecipe(Ic2RecipeTypes.ORE_WASHER);
    Recipes.matterFabricator =
        new RecipeManagerGetter<>(
            recipeManager -> {
              MatterAmplifierRecipeManager manager = new MatterAmplifierRecipeManager();

              for (var holder : recipeManager.getAllRecipesFor(Ic2RecipeTypes.MATTER_FABRICATOR)) {
                manager.addRecipe(
                    holder.value().recipe().getInput(),
                    holder.value().recipe().getOutput(),
                    null,
                    false);
              }
              ic2.industrialjs.IndustrialJSRecipeRegistry.applyAmplifiers(manager);

              return manager;
            });
    Recipes.cannerBottle =
        new RecipeManagerGetter<>(
            recipeManager -> {
              CannerBottleRecipeManager manager = new CannerBottleRecipeManager();

              for (var holder : recipeManager.getAllRecipesFor(Ic2RecipeTypes.CANNER_BOTTLE)) {
                manager.addRecipe(
                    holder.value().recipe().getInput(),
                    holder.value().recipe().getOutput(),
                    null,
                    false);
              }
              ic2.industrialjs.IndustrialJSRecipeRegistry.applyCannerBottle(manager);

              return manager;
            });
    Recipes.cannerEnrich =
        new RecipeManagerGetter<>(
            recipeManager -> {
              CannerEnrichRecipeManager manager = new CannerEnrichRecipeManager();

              for (var holder : recipeManager.getAllRecipesFor(Ic2RecipeTypes.CANNER_ENRICH)) {
                manager.addRecipe(
                    holder.value().recipe().getInput(),
                    holder.value().recipe().getOutput(),
                    null,
                    false);
              }
              ic2.industrialjs.IndustrialJSRecipeRegistry.applyCannerEnrich(manager);

              return manager;
            });
  }

  public static void registerRecipes() {
    IndustrialJSConfig.load();
    loadRecipes();
  }

  private static RecipeManagerGetter<IBasicMachineRecipeManager> basicRecipe(
      RecipeType<RecipeHolder<IRecipeInput, Collection<ItemStack>>> recipeType) {
    return new RecipeManagerGetter<>(
        recipeManager -> {
          BasicMachineRecipeManager manager = new BasicMachineRecipeManager();

          for (var holder : recipeManager.getAllRecipesFor(recipeType)) {
            manager.addRecipe(holder.value().recipe(), false);
          }

          if (recipeType == Ic2RecipeTypes.MACERATOR) {
            MaceratorCompatibility.addAutomaticRecipes(recipeManager, manager);
          }

          if (recipeType == Ic2RecipeTypes.COMPRESSOR) {
            CompressorCompatibility.addAutomaticRecipes(recipeManager, manager);
          }

          if (recipeType == Ic2RecipeTypes.ORE_WASHER) {
            OreWasherCompatibility.addAutomaticRecipes(recipeManager, manager);
          }

          IndustrialJSRecipeRegistry.applyTo(recipeType, manager);

          return manager;
        });
  }
}
