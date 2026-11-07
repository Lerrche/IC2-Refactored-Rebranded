package ic2.core.recipe;

import ic2.api.recipe.Recipes;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;

/**
 * Adds universal compressor recipes for 3x3 (9 identical items) crafting recipes, including 9 scrap
 * -> 1 scrap box, 9 nuggets -> 1 ingot, 9 ingots -> 1 block, etc.
 */
public final class CompressorCompatibility {
  private CompressorCompatibility() {}

  public static void addAutomaticRecipes(
      RecipeManager recipeManager, BasicMachineRecipeManager compressor) {
    Set<Item> processedInputs = new HashSet<>();

    for (RecipeHolder<CraftingRecipe> holder :
        recipeManager.getAllRecipesFor(RecipeType.CRAFTING)) {
      CraftingRecipe recipe = holder.value();
      NonNullList<Ingredient> ingredients = recipe.getIngredients();

      if (ingredients.size() != 9) {
        continue;
      }

      if (recipe instanceof ShapedRecipe shaped) {
        if (shaped.getWidth() != 3 || shaped.getHeight() != 3) {
          continue;
        }
      } else if (recipe instanceof AdvRecipe adv) {
        if (adv.inputWidth != 3 || adv.inputHeight != 3) {
          continue;
        }
      }

      boolean anyEmpty = false;
      for (Ingredient ing : ingredients) {
        if (ing.isEmpty()) {
          anyEmpty = true;
          break;
        }
      }
      if (anyEmpty) {
        continue;
      }

      ItemStack[] candidates = ingredients.getFirst().getItems();
      if (candidates.length == 0) {
        continue;
      }

      ItemStack output;
      try {
        output = recipe.getResultItem((RegistryAccess) null);
      } catch (Throwable t) {
        continue;
      }

      if (output == null || output.isEmpty()) {
        continue;
      }

      for (ItemStack candidate : candidates) {
        if (candidate.isEmpty() || candidate.getItem() == Items.AIR) {
          continue;
        }
        Item item = candidate.getItem();
        if (processedInputs.contains(item) || output.getItem() == item) {
          continue;
        }

        ItemStack sample = new ItemStack(item);
        boolean allMatch = true;
        for (int i = 1; i < 9; i++) {
          if (!ingredients.get(i).test(sample)) {
            allMatch = false;
            break;
          }
        }

        if (allMatch) {
          if (compressor.getRecipe(new ItemStack(item, 9)) == null) {
            compressor.addRecipe(
                Recipes.inputFactory.forStack(sample, 9), null, false, output.copy());
            processedInputs.add(item);
          }
        }
      }
    }
  }
}
