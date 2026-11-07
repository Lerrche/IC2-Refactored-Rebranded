package ic2.core.recipe;

import ic2.api.recipe.Recipes;
import ic2.core.ref.Ic2Items;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

public final class OreWasherCompatibility {
  private static final TagKey<Item> ORES = itemTag("c:ores");
  private static final TagKey<Item> RAW_MATERIALS = itemTag("c:raw_materials");

  private OreWasherCompatibility() {}

  public static void addAutomaticRecipes(
      RecipeManager recipeManager, BasicMachineRecipeManager oreWasher) {
    Set<Item> processed = new HashSet<>();

    for (var holder : recipeManager.getAllRecipesFor(RecipeType.SMELTING)) {
      addFromCookingRecipe(holder.value(), oreWasher, processed);
    }
    for (var holder : recipeManager.getAllRecipesFor(RecipeType.BLASTING)) {
      addFromCookingRecipe(holder.value(), oreWasher, processed);
    }

    tryAddFromTag(RAW_MATERIALS, oreWasher, processed, false);
    tryAddFromTag(ORES, oreWasher, processed, true);
  }

  private static void addFromCookingRecipe(
      AbstractCookingRecipe cookingRecipe,
      BasicMachineRecipeManager oreWasher,
      Set<Item> processed) {
    ItemStack result = cookingRecipe.getResultItem((RegistryAccess) null);
    if (result.isEmpty() || cookingRecipe.getIngredients().size() != 1) {
      return;
    }

    Ingredient ingredient = cookingRecipe.getIngredients().getFirst();
    for (ItemStack input : ingredient.getItems()) {
      if (input.isEmpty() || !isOreOrRawMaterial(input)) {
        continue;
      }
      Item item = input.getItem();
      if (processed.contains(item)) {
        continue;
      }
      boolean ore = isOre(input);
      addRecipeForItem(item, oreWasher, processed, ore);
    }
  }

  private static void tryAddFromTag(
      TagKey<Item> tag, BasicMachineRecipeManager oreWasher, Set<Item> processed, boolean isOre) {
    try {
      for (ItemStack stack : Ingredient.of(tag).getItems()) {
        if (stack.isEmpty()) {
          continue;
        }
        Item item = stack.getItem();
        if (processed.contains(item)) {
          continue;
        }
        String path = BuiltInRegistries.ITEM.getKey(item).getPath();
        if (path.endsWith("_block") || path.endsWith("_blocks")) {
          continue;
        }
        addRecipeForItem(item, oreWasher, processed, isOre);
      }
    } catch (Throwable ignored) {
      // Tags may be empty or unresolvable in test harnesses
    }
  }

  private static void addRecipeForItem(
      Item item, BasicMachineRecipeManager oreWasher, Set<Item> processed, boolean isOre) {
    ItemStack sample = new ItemStack(item);
    if (oreWasher.getRecipe(sample) != null) {
      processed.add(item);
      return;
    }

    String material = extractMaterialName(sample);
    if (material == null || material.isBlank()) {
      return;
    }

    List<ItemStack> outputs = getOutputsForMaterial(material, isOre);
    if (outputs == null || outputs.isEmpty()) {
      return;
    }

    CompoundTag meta = new CompoundTag();
    meta.putInt("amount", 1000);

    oreWasher.addRecipe(Recipes.inputFactory.forItem(item), outputs, meta, false);
    processed.add(item);
  }

  private static boolean isOreOrRawMaterial(ItemStack stack) {
    if (stack.is(ORES) || stack.is(RAW_MATERIALS)) {
      return true;
    }
    String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
    return isOrePath(path)
        || path.startsWith("raw_")
        || path.startsWith("crushed_raw_")
        || path.startsWith("crushed_")
        || path.startsWith("raw/");
  }

  private static boolean isOre(ItemStack stack) {
    if (stack.is(ORES)) {
      return true;
    }
    return isOrePath(BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath());
  }

  private static boolean isOrePath(String path) {
    return path.endsWith("_ore") || path.startsWith("deepslate_") && path.endsWith("_ore");
  }

  private static String extractMaterialName(ItemStack stack) {
    String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
    if (path.contains("/")) {
      path = path.substring(path.lastIndexOf('/') + 1);
    }
    if (path.endsWith("_block") || path.endsWith("_blocks")) {
      return null;
    }
    if (path.startsWith("crushed_raw_")) {
      return path.substring("crushed_raw_".length());
    }
    if (path.startsWith("raw_")) {
      return path.substring("raw_".length());
    }
    if (path.endsWith("_raw")) {
      return path.substring(0, path.length() - "_raw".length());
    }
    if (path.startsWith("deepslate_")) {
      path = path.substring("deepslate_".length());
    }
    if (path.startsWith("nether_")) {
      path = path.substring("nether_".length());
    }
    if (path.startsWith("end_")) {
      path = path.substring("end_".length());
    }
    if (path.endsWith("_ore")) {
      return path.substring(0, path.length() - "_ore".length());
    }
    if (path.startsWith("ore_")) {
      return path.substring("ore_".length());
    }
    if (path.endsWith("_ores")) {
      return path.substring(0, path.length() - "_ores".length());
    }
    if (path.startsWith("crushed_")) {
      return path.substring("crushed_".length());
    }
    return null;
  }

  private static List<ItemStack> getOutputsForMaterial(String material, boolean isOre) {
    int stoneCount = isOre ? 2 : 1;
    ItemStack stoneDust = new ItemStack(Ic2Items.STONE_DUST, stoneCount);

    switch (material.toLowerCase(Locale.ROOT)) {
      case "iron":
        return List.of(
            new ItemStack(Ic2Items.PURIFIED_IRON),
            new ItemStack(Ic2Items.SMALL_IRON_DUST, 2),
            stoneDust);
      case "copper":
        return List.of(
            new ItemStack(Ic2Items.PURIFIED_COPPER),
            new ItemStack(Ic2Items.SMALL_COPPER_DUST, 2),
            stoneDust);
      case "gold":
        return List.of(
            new ItemStack(Ic2Items.PURIFIED_GOLD),
            new ItemStack(Ic2Items.SMALL_GOLD_DUST, 2),
            stoneDust);
      case "tin":
        return List.of(
            new ItemStack(Ic2Items.PURIFIED_TIN),
            new ItemStack(Ic2Items.SMALL_TIN_DUST, 2),
            stoneDust);
      case "lead":
        return List.of(
            new ItemStack(Ic2Items.PURIFIED_LEAD),
            new ItemStack(Ic2Items.SMALL_LEAD_DUST, 2),
            stoneDust);
      case "silver":
        return List.of(
            new ItemStack(Ic2Items.PURIFIED_SILVER),
            new ItemStack(Ic2Items.SMALL_SILVER_DUST, 2),
            stoneDust);
      case "uranium":
        return List.of(
            new ItemStack(Ic2Items.PURIFIED_URANIUM),
            new ItemStack(Ic2Items.SMALL_LEAD_DUST, 2),
            stoneDust);
      default:
        Item dust = findDustForMaterial(material);
        if (dust != null) {
          int dustCount = isOre ? 2 : 1;
          return List.of(new ItemStack(dust, dustCount), stoneDust);
        }
        return null;
    }
  }

  private static Item findDustForMaterial(String material) {
    if (material == null || material.isBlank()) {
      return null;
    }

    try {
      TagKey<Item> dustTag = itemTag("c:dusts/" + material);
      ItemStack[] taggedDusts = Ingredient.of(dustTag).getItems();
      if (taggedDusts.length > 0) {
        return java.util.Arrays.stream(taggedDusts)
            .map(ItemStack::getItem)
            .distinct()
            .sorted(Comparator.comparing(item -> BuiltInRegistries.ITEM.getKey(item).toString()))
            .findFirst()
            .orElse(null);
      }
    } catch (Throwable ignored) {
    }

    ResourceLocation ic2DustId = ResourceLocation.fromNamespaceAndPath("ic2", material + "_dust");
    return BuiltInRegistries.ITEM.getOptional(ic2DustId).orElse(null);
  }

  private static TagKey<Item> itemTag(String id) {
    return TagKey.create(Registries.ITEM, ResourceLocation.parse(id));
  }
}
