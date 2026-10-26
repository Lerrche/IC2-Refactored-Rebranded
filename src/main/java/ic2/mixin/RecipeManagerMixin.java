package ic2.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ic2.core.IC2;
import ic2.core.util.LogCategory;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Applies cross-mod recipe ingredient compatibility before recipe serializers decode their JSON.
 * The common c:rubber tag contains both IC2 rubber and vanilla dried kelp, so replacing a direct
 * dried-kelp ingredient with that tag preserves dried kelp as a valid ingredient while adding IC2
 * rubber as an alternative.
 */
@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin {
  private static final String DRIED_KELP = "minecraft:dried_kelp";
  private static final String RUBBER_TAG = "c:rubber";

  // These fields describe outputs/metadata rather than inputs and must not be rewritten.
  private static final Set<String> NON_INGREDIENT_FIELDS =
      Set.of(
          "result",
          "results",
          "output",
          "outputs",
          "result_item",
          "result_items",
          "result_item_stack",
          "output_item",
          "output_items",
          "item_output",
          "item_outputs",
          "main_output",
          "main_result",
          "secondary_output",
          "secondary_outputs",
          "secondary_result",
          "secondary_results",
          "fluid_output",
          "fluid_outputs",
          "chemical_output",
          "chemical_outputs",
          "byproduct",
          "byproducts",
          "byproduct_item",
          "byproduct_items",
          "product",
          "products",
          "remainder",
          "remainders",
          "returned_item",
          "returned_items",
          "container_item",
          "container_items",
          "tag",
          "id",
          "neoforge:conditions",
          "conditions",
          "advancement",
          "show_notification",
          "category",
          "group",
          "type",
          "accept_mirrored",
          "acceptmirrored");

  private static final Set<String> INGREDIENT_FIELDS =
      Set.of(
          "ingredient",
          "ingredients",
          "input",
          "inputs",
          "item",
          "input_item",
          "input_items",
          "item_input",
          "item_inputs",
          "input_ingredient",
          "input_ingredients",
          "item_ingredient",
          "item_ingredients",
          "primary_ingredient",
          "secondary_ingredient",
          "main_ingredient",
          "key",
          "pattern",
          "base",
          "addition",
          "template",
          "catalyst",
          "fuel",
          "repair",
          "repair_ingredient",
          "ingredient_a",
          "ingredient_b",
          "ingredient1",
          "ingredient2",
          "top",
          "bottom",
          "left",
          "right",
          "middle",
          "items");

  @Inject(method = "apply", at = @At("HEAD"))
  private void ic2$applyRecipeCompatibility(
      Map<ResourceLocation, JsonElement> recipes,
      ResourceManager resourceManager,
      ProfilerFiller profiler,
      CallbackInfo ci) {
    if (recipes == null || recipes.isEmpty()) {
      return;
    }

    int changedRecipes = 0;
    int changedIngredients = 0;

    for (Map.Entry<ResourceLocation, JsonElement> entry : recipes.entrySet()) {
      JsonElement value = entry.getValue();
      if (value == null || !value.isJsonObject()) {
        continue;
      }

      JsonObject recipe = value.getAsJsonObject();
      normalizeCreateMechanicalCraftingSchema(recipe);

      int[] replacementCount = {0};
      rewriteIngredients(recipe, false, replacementCount);
      if (replacementCount[0] > 0) {
        changedRecipes++;
        changedIngredients += replacementCount[0];
      }
    }

    if (changedIngredients > 0) {
      IC2.log.info(
          LogCategory.Recipe,
          "[IC2 Recipe Compatibility] Added IC2 rubber as a dried-kelp alternative in %d recipe(s) (%d ingredient(s)).",
          changedRecipes,
          changedIngredients);
    }
  }

  /** Create 6.x uses snake_case. The older IC2R files used camelCase, causing decode failures. */
  private static void normalizeCreateMechanicalCraftingSchema(JsonObject recipe) {
    if (!recipe.has("type")
        || !recipe.get("type").isJsonPrimitive()
        || !"create:mechanical_crafting".equals(recipe.get("type").getAsString())) {
      return;
    }

    if (recipe.has("acceptMirrored") && !recipe.has("accept_mirrored")) {
      recipe.add("accept_mirrored", recipe.get("acceptMirrored").deepCopy());
      recipe.remove("acceptMirrored");
    }
  }

  private static void rewriteIngredients(
      JsonElement element, boolean ingredientContext, int[] replacementCount) {
    if (element == null || element.isJsonNull()) {
      return;
    }

    if (element.isJsonObject()) {
      JsonObject object = element.getAsJsonObject();

      // Most recipe ingredient codecs use {"item":"namespace:item"}. Convert a direct kelp
      // ingredient to the common tag, preserving any additional component constraints if present.
      JsonElement item = object.get("item");
      if (isDriedKelp(item)) {
        object.remove("item");
        object.addProperty("tag", RUBBER_TAG);
        replacementCount[0]++;
        return;
      }

      for (String key : new ArrayList<>(object.keySet())) {
        String normalizedKey = normalizeFieldName(key);
        if (NON_INGREDIENT_FIELDS.contains(normalizedKey)) {
          continue;
        }

        boolean childIngredientContext = ingredientContext || isIngredientField(normalizedKey);
        JsonElement child = object.get(key);
        if (childIngredientContext && isDriedKelp(child)) {
          JsonObject tagIngredient = new JsonObject();
          tagIngredient.addProperty("tag", RUBBER_TAG);
          object.add(key, tagIngredient);
          replacementCount[0]++;
        } else {
          rewriteIngredients(child, childIngredientContext, replacementCount);
        }
      }
      return;
    }

    if (element.isJsonArray()) {
      JsonArray array = element.getAsJsonArray();
      for (int index = 0; index < array.size(); index++) {
        JsonElement entry = array.get(index);
        if (ingredientContext && isDriedKelp(entry)) {
          JsonObject tagIngredient = new JsonObject();
          tagIngredient.addProperty("tag", RUBBER_TAG);
          array.set(index, tagIngredient);
          replacementCount[0]++;
        } else {
          rewriteIngredients(entry, ingredientContext, replacementCount);
        }
      }
      return;
    }
  }

  private static String normalizeFieldName(String key) {
    StringBuilder normalized = new StringBuilder(key.length() + 4);
    for (int index = 0; index < key.length(); index++) {
      char character = key.charAt(index);
      if (Character.isUpperCase(character) && index > 0) {
        normalized.append('_');
      }
      normalized.append(Character.toLowerCase(character));
    }
    return normalized.toString();
  }

  private static boolean isIngredientField(String normalizedKey) {
    return INGREDIENT_FIELDS.contains(normalizedKey)
        || normalizedKey.contains("ingredient")
        || normalizedKey.contains("input")
        || normalizedKey.contains("catalyst")
        || normalizedKey.contains("material");
  }

  private static boolean isDriedKelp(JsonElement element) {
    return element != null
        && element.isJsonPrimitive()
        && element.getAsJsonPrimitive().isString()
        && DRIED_KELP.equals(element.getAsString());
  }
}
