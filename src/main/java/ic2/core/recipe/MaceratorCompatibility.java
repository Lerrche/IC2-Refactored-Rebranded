package ic2.core.recipe;

import ic2.api.recipe.Recipes;
import ic2.core.IC2;
import ic2.core.util.LogCategory;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;

/**
 * Adds universal macerator compatibility for other mods: 1. Safely falls back cooking recipes
 * (smelting/blasting) from ores/raw materials to dusts. 2. Scans other mods' crushing, milling,
 * pulverizing, and grinding recipes (e.g. Mekanism, Create, Thermal, EnderIO) and registers
 * matching recipes in the IC2 Macerator. 3. Scans all custom dusts registered in the game
 * (#c:dusts, *_dust, dust_*) and connects them to their corresponding ores, raw materials, ingots,
 * gems, storage blocks, and known items.
 */
public final class MaceratorCompatibility {
  private static final TagKey<Item> ORES = itemTag("c:ores");
  private static final TagKey<Item> RAW_MATERIALS = itemTag("c:raw_materials");
  private static final TagKey<Item> INGOTS = itemTag("c:ingots");
  private static final TagKey<Item> DUSTS = itemTag("c:dusts");
  private static final TagKey<Item> GEMS = itemTag("c:gems");
  private static final TagKey<Item> STORAGE_BLOCKS = itemTag("c:storage_blocks");

  private MaceratorCompatibility() {}

  public static void addAutomaticRecipes(
      RecipeManager recipeManager, BasicMachineRecipeManager macerator) {
    // 1. Existing cooking recipe fallback (smelting / blasting)
    try {
      for (var holder : recipeManager.getAllRecipesFor(RecipeType.SMELTING)) {
        addFromCookingRecipe(holder.value(), macerator);
      }
      for (var holder : recipeManager.getAllRecipesFor(RecipeType.BLASTING)) {
        addFromCookingRecipe(holder.value(), macerator);
      }
    } catch (Throwable t) {
      IC2.log.warn(
          LogCategory.General,
          "[MaceratorCompatibility] Error processing cooking recipes: %s",
          t.getMessage());
    }

    // 2. Modded crushing / milling / grinding / pulverizing recipes (Mekanism, Create, etc.)
    try {
      addFromExternalCrushingRecipes(recipeManager, macerator);
    } catch (Throwable t) {
      IC2.log.warn(
          LogCategory.General,
          "[MaceratorCompatibility] Error processing external crushing recipes: %s",
          t.getMessage());
    }

    // 3. Scan all custom dusts registered across loaded mods and connect to ores/ingots/gems/blocks
    try {
      addFromModdedDusts(macerator);
    } catch (Throwable t) {
      IC2.log.warn(
          LogCategory.General,
          "[MaceratorCompatibility] Error processing modded dusts: %s",
          t.getMessage());
    }
  }

  // -------------------------------------------------------------------------
  // 1. Cooking Recipe Fallback
  // -------------------------------------------------------------------------

  private static void addFromCookingRecipe(
      AbstractCookingRecipe cookingRecipe, BasicMachineRecipeManager macerator) {
    try {
      ItemStack ingot = cookingRecipe.getResultItem((RegistryAccess) null);
      if (ingot.isEmpty() || !isIngot(ingot) || cookingRecipe.getIngredients().size() != 1) {
        return;
      }

      Item dust = findDustForIngot(ingot);
      if (dust == null || dust == Items.AIR) {
        return;
      }

      Ingredient ingredient = cookingRecipe.getIngredients().getFirst();
      for (ItemStack input : ingredient.getItems()) {
        if (input.isEmpty() || input.getItem() == Items.AIR || !isOreOrRawMaterial(input)) {
          continue;
        }
        if (macerator.getRecipe(input) != null) {
          continue;
        }
        int dustCount = isOre(input) ? 2 : 1;
        macerator.addRecipe(
            Recipes.inputFactory.forItem(input.getItem()),
            null,
            false,
            new ItemStack(dust, dustCount));
      }
    } catch (Throwable ignored) {
    }
  }

  // -------------------------------------------------------------------------
  // 2. External Crushing / Milling / Grinding Recipes
  // -------------------------------------------------------------------------

  private static void addFromExternalCrushingRecipes(
      RecipeManager recipeManager, BasicMachineRecipeManager macerator) {
    for (RecipeHolder<?> holder : recipeManager.getRecipes()) {
      try {
        Recipe<?> recipe = holder.value();
        ResourceLocation recipeId = holder.id();
        if (recipeId != null && "ic2".equals(recipeId.getNamespace())) {
          continue;
        }

        ResourceLocation typeId = BuiltInRegistries.RECIPE_TYPE.getKey(recipe.getType());
        String typePath = typeId != null ? typeId.getPath().toLowerCase(Locale.ROOT) : "";
        String recipePath = recipeId != null ? recipeId.getPath().toLowerCase(Locale.ROOT) : "";

        boolean isCrushing =
            typePath.contains("crush")
                || typePath.contains("milling")
                || typePath.contains("mill")
                || typePath.contains("pulveriz")
                || typePath.contains("grind")
                || typePath.contains("macerat")
                || typePath.contains("sag_mill")
                || recipePath.contains("crushing/")
                || recipePath.contains("milling/")
                || recipePath.contains("pulverizing/")
                || recipePath.contains("grinding/");

        if (!isCrushing) {
          continue;
        }

        ItemStack result = getSafeResult(recipe);
        if (result.isEmpty() || result.getItem() == Items.AIR) {
          continue;
        }

        List<Ingredient> ingredients = getSafeIngredients(recipe);
        if (ingredients.size() != 1) {
          continue;
        }

        Ingredient ingredient = ingredients.getFirst();
        for (ItemStack input : ingredient.getItems()) {
          if (input.isEmpty() || input.getItem() == Items.AIR || input.is(result.getItem())) {
            continue;
          }
          if (macerator.getRecipe(input) != null) {
            continue;
          }
          macerator.addRecipe(
              Recipes.inputFactory.forItem(input.getItem()), null, false, result.copy());
        }
      } catch (Throwable ignored) {
      }
    }
  }

  private static ItemStack getSafeResult(Recipe<?> recipe) {
    try {
      ItemStack res = recipe.getResultItem((RegistryAccess) null);
      if (res != null && !res.isEmpty()) {
        return res;
      }
    } catch (Throwable ignored) {
    }
    try {
      ItemStack res = recipe.getResultItem(RegistryAccess.EMPTY);
      if (res != null && !res.isEmpty()) {
        return res;
      }
    } catch (Throwable ignored) {
    }
    for (String methodName :
        new String[] {"getResult", "getOutput", "getResultItem", "getRollableResults"}) {
      try {
        Method m = recipe.getClass().getMethod(methodName);
        Object obj = m.invoke(recipe);
        if (obj instanceof ItemStack is && !is.isEmpty()) {
          return is;
        }
        if (obj instanceof List<?> list && !list.isEmpty()) {
          Object first = list.get(0);
          if (first instanceof ItemStack is && !is.isEmpty()) {
            return is;
          }
          try {
            Method getStack = first.getClass().getMethod("getStack");
            Object stackObj = getStack.invoke(first);
            if (stackObj instanceof ItemStack is && !is.isEmpty()) {
              return is;
            }
          } catch (Throwable ignored) {
          }
        }
      } catch (Throwable ignored) {
      }
    }
    return ItemStack.EMPTY;
  }

  private static List<Ingredient> getSafeIngredients(Recipe<?> recipe) {
    try {
      List<Ingredient> ingredients = recipe.getIngredients();
      if (ingredients != null && !ingredients.isEmpty()) {
        return ingredients;
      }
    } catch (Throwable ignored) {
    }
    for (String methodName : new String[] {"getInput", "getIngredient"}) {
      try {
        Method m = recipe.getClass().getMethod(methodName);
        Object obj = m.invoke(recipe);
        if (obj instanceof Ingredient ing && !ing.isEmpty()) {
          return List.of(ing);
        }
        if (obj != null) {
          try {
            Method asIng = obj.getClass().getMethod("asIngredient");
            Object ingObj = asIng.invoke(obj);
            if (ingObj instanceof Ingredient ing && !ing.isEmpty()) {
              return List.of(ing);
            }
          } catch (Throwable ignored) {
          }
        }
      } catch (Throwable ignored) {
      }
    }
    return List.of();
  }

  // -------------------------------------------------------------------------
  // 3. Modded Dust Registration & Cross-Mod Mapping
  // -------------------------------------------------------------------------

  private static void addFromModdedDusts(BasicMachineRecipeManager macerator) {
    Set<Item> registeredDusts = new HashSet<>();

    // Collect all dust items in the game
    for (Item item : BuiltInRegistries.ITEM) {
      if (item == null || item == Items.AIR) {
        continue;
      }
      ItemStack stack = new ItemStack(item);
      if (isDust(stack)) {
        registeredDusts.add(item);
      }
    }

    for (Item dustItem : registeredDusts) {
      String material = extractMaterialFromDust(dustItem);
      if (material == null || material.isBlank()) {
        continue;
      }

      // 1. Tag based mappings
      // c:ores/<material> -> 2x dust
      addFromTag(itemTag("c:ores/" + material), macerator, dustItem, 2);

      // c:raw_materials/<material> -> 2x dust
      addFromTag(itemTag("c:raw_materials/" + material), macerator, dustItem, 2);

      // c:ingots/<material> -> 1x dust
      addFromTag(itemTag("c:ingots/" + material), macerator, dustItem, 1);

      // c:gems/<material> -> 1x dust
      addFromTag(itemTag("c:gems/" + material), macerator, dustItem, 1);

      // c:storage_blocks/<material> -> 9x dust
      addFromTag(itemTag("c:storage_blocks/" + material), macerator, dustItem, 9);

      // 2. Direct name matching across all loaded items (covers mod items without proper tags)
      addFromItemNames(material, dustItem, macerator);

      // 3. Specific known mappings for materials that are common vanilla/mod blocks or items
      addKnownDirectInputs(material, dustItem, macerator);
    }
  }

  private static void addFromTag(
      TagKey<Item> tag, BasicMachineRecipeManager macerator, Item outputDust, int outputCount) {
    try {
      if (outputDust == null || outputDust == Items.AIR || outputCount <= 0) {
        return;
      }
      ItemStack[] items = Ingredient.of(tag).getItems();
      for (ItemStack stack : items) {
        if (stack.isEmpty() || stack.getItem() == Items.AIR || stack.is(outputDust)) {
          continue;
        }
        if (macerator.getRecipe(stack) != null) {
          continue;
        }
        macerator.addRecipe(
            Recipes.inputFactory.forItem(stack.getItem()),
            null,
            false,
            new ItemStack(outputDust, outputCount));
      }
    } catch (Throwable ignored) {
    }
  }

  private static void addFromItemNames(
      String material, Item dustItem, BasicMachineRecipeManager macerator) {
    String lowerMat = material.toLowerCase(Locale.ROOT);
    for (Item item : BuiltInRegistries.ITEM) {
      if (item == null || item == Items.AIR || item == dustItem) {
        continue;
      }
      ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
      if (id == null) {
        continue;
      }
      String path = id.getPath().toLowerCase(Locale.ROOT);

      // Match raw ore: raw_<material>
      if (path.equals("raw_" + lowerMat) || path.equals(lowerMat + "_raw")) {
        tryAddDirectItem(id, macerator, dustItem, 2);
      }
      // Match ore: <material>_ore, deepslate_<material>_ore, etc.
      else if (path.equals(lowerMat + "_ore") || path.endsWith("_" + lowerMat + "_ore")) {
        tryAddDirectItem(id, macerator, dustItem, 2);
      }
      // Match ingot: <material>_ingot, ingot_<material>
      else if (path.equals(lowerMat + "_ingot") || path.equals("ingot_" + lowerMat)) {
        tryAddDirectItem(id, macerator, dustItem, 1);
      }
      // Match gem: <material>_gem, gem_<material>
      else if (path.equals(lowerMat + "_gem") || path.equals("gem_" + lowerMat)) {
        tryAddDirectItem(id, macerator, dustItem, 1);
      }
      // Match storage block: <material>_block, block_<material>
      else if (path.equals(lowerMat + "_block") || path.equals("block_" + lowerMat)) {
        tryAddDirectItem(id, macerator, dustItem, 9);
      }
    }
  }

  private static void addKnownDirectInputs(
      String material, Item dustItem, BasicMachineRecipeManager macerator) {
    switch (material) {
      case "obsidian" -> {
        tryAddDirectItem("minecraft:obsidian", macerator, dustItem, 4);
        tryAddDirectItem("minecraft:crying_obsidian", macerator, dustItem, 4);
      }
      case "refined_obsidian" -> {
        tryAddDirectItem("mekanism:ingot_refined_obsidian", macerator, dustItem, 1);
      }
      case "coal" -> {
        tryAddDirectItem("minecraft:coal", macerator, dustItem, 1);
        tryAddDirectItem("minecraft:coal_ore", macerator, dustItem, 2);
        tryAddDirectItem("minecraft:deepslate_coal_ore", macerator, dustItem, 2);
        tryAddDirectItem("minecraft:coal_block", macerator, dustItem, 9);
      }
      case "charcoal" -> {
        tryAddDirectItem("minecraft:charcoal", macerator, dustItem, 1);
      }
      case "diamond" -> {
        tryAddDirectItem("minecraft:diamond", macerator, dustItem, 1);
        tryAddDirectItem("minecraft:diamond_ore", macerator, dustItem, 2);
        tryAddDirectItem("minecraft:deepslate_diamond_ore", macerator, dustItem, 2);
        tryAddDirectItem("minecraft:diamond_block", macerator, dustItem, 9);
      }
      case "emerald" -> {
        tryAddDirectItem("minecraft:emerald", macerator, dustItem, 1);
        tryAddDirectItem("minecraft:emerald_ore", macerator, dustItem, 2);
        tryAddDirectItem("minecraft:deepslate_emerald_ore", macerator, dustItem, 2);
        tryAddDirectItem("minecraft:emerald_block", macerator, dustItem, 9);
      }
      case "lapis", "lapis_lazuli" -> {
        tryAddDirectItem("minecraft:lapis_lazuli", macerator, dustItem, 1);
        tryAddDirectItem("minecraft:lapis_ore", macerator, dustItem, 9);
        tryAddDirectItem("minecraft:deepslate_lapis_ore", macerator, dustItem, 9);
        tryAddDirectItem("minecraft:lapis_block", macerator, dustItem, 9);
      }
      case "quartz", "nether_quartz" -> {
        tryAddDirectItem("minecraft:quartz", macerator, dustItem, 1);
        tryAddDirectItem("minecraft:nether_quartz_ore", macerator, dustItem, 2);
        tryAddDirectItem("minecraft:quartz_block", macerator, dustItem, 4);
      }
      case "amethyst", "amethyst_shard" -> {
        tryAddDirectItem("minecraft:amethyst_shard", macerator, dustItem, 1);
        tryAddDirectItem("minecraft:amethyst_block", macerator, dustItem, 4);
      }
      case "ender", "ender_pearl" -> {
        tryAddDirectItem("minecraft:ender_pearl", macerator, dustItem, 1);
      }
      case "fluorite" -> {
        tryAddDirectItem("mekanism:fluorite_gem", macerator, dustItem, 1);
        tryAddDirectItem("mekanism:ore_fluorite", macerator, dustItem, 2);
        tryAddDirectItem("mekanism:ore_deepslate_fluorite", macerator, dustItem, 2);
        tryAddDirectItem("mekanism:block_fluorite", macerator, dustItem, 9);
      }
      case "bone" -> {
        tryAddDirectItem("minecraft:bone", macerator, dustItem, 3);
        tryAddDirectItem("minecraft:bone_block", macerator, dustItem, 9);
      }
      case "blaze", "blaze_rod" -> {
        tryAddDirectItem("minecraft:blaze_rod", macerator, dustItem, 2);
      }
      case "clay" -> {
        tryAddDirectItem("minecraft:clay_ball", macerator, dustItem, 1);
        tryAddDirectItem("minecraft:clay", macerator, dustItem, 4);
      }
      case "glowstone" -> {
        tryAddDirectItem("minecraft:glowstone", macerator, dustItem, 4);
      }
      case "iron" -> {
        tryAddDirectItem("minecraft:iron_ingot", macerator, dustItem, 1);
        tryAddDirectItem("minecraft:raw_iron", macerator, dustItem, 2);
        tryAddDirectItem("minecraft:iron_ore", macerator, dustItem, 2);
        tryAddDirectItem("minecraft:deepslate_iron_ore", macerator, dustItem, 2);
        tryAddDirectItem("minecraft:iron_block", macerator, dustItem, 9);
        tryAddDirectItem("minecraft:raw_iron_block", macerator, dustItem, 18);
      }
      case "gold" -> {
        tryAddDirectItem("minecraft:gold_ingot", macerator, dustItem, 1);
        tryAddDirectItem("minecraft:raw_gold", macerator, dustItem, 2);
        tryAddDirectItem("minecraft:gold_ore", macerator, dustItem, 2);
        tryAddDirectItem("minecraft:deepslate_gold_ore", macerator, dustItem, 2);
        tryAddDirectItem("minecraft:nether_gold_ore", macerator, dustItem, 2);
        tryAddDirectItem("minecraft:gold_block", macerator, dustItem, 9);
        tryAddDirectItem("minecraft:raw_gold_block", macerator, dustItem, 18);
      }
      case "copper" -> {
        tryAddDirectItem("minecraft:copper_ingot", macerator, dustItem, 1);
        tryAddDirectItem("minecraft:raw_copper", macerator, dustItem, 2);
        tryAddDirectItem("minecraft:copper_ore", macerator, dustItem, 2);
        tryAddDirectItem("minecraft:deepslate_copper_ore", macerator, dustItem, 2);
        tryAddDirectItem("minecraft:copper_block", macerator, dustItem, 9);
        tryAddDirectItem("minecraft:raw_copper_block", macerator, dustItem, 18);
      }
      case "netherite" -> {
        tryAddDirectItem("minecraft:netherite_ingot", macerator, dustItem, 1);
        tryAddDirectItem("minecraft:netherite_block", macerator, dustItem, 9);
        tryAddDirectItem("minecraft:ancient_debris", macerator, dustItem, 2);
      }
      case "redstone" -> {
        tryAddDirectItem("minecraft:redstone_ore", macerator, dustItem, 9);
        tryAddDirectItem("minecraft:deepslate_redstone_ore", macerator, dustItem, 9);
        tryAddDirectItem("minecraft:redstone_block", macerator, dustItem, 9);
      }
      default -> {}
    }
  }

  private static void tryAddDirectItem(
      String itemId, BasicMachineRecipeManager macerator, Item outputDust, int count) {
    try {
      ResourceLocation loc = ResourceLocation.parse(itemId);
      tryAddDirectItem(loc, macerator, outputDust, count);
    } catch (Throwable ignored) {
    }
  }

  private static void tryAddDirectItem(
      ResourceLocation loc, BasicMachineRecipeManager macerator, Item outputDust, int count) {
    try {
      if (outputDust == null || outputDust == Items.AIR || count <= 0) {
        return;
      }
      Item inputItem = BuiltInRegistries.ITEM.getOptional(loc).orElse(null);
      if (inputItem != null && inputItem != Items.AIR && inputItem != outputDust) {
        ItemStack testStack = new ItemStack(inputItem);
        if (!testStack.isEmpty() && macerator.getRecipe(testStack) == null) {
          macerator.addRecipe(
              Recipes.inputFactory.forItem(inputItem),
              null,
              false,
              new ItemStack(outputDust, count));
        }
      }
    } catch (Throwable ignored) {
    }
  }

  private static String extractMaterialFromDust(Item dustItem) {
    // 1. Check if the item belongs to a c:dusts/<material> tag
    try {
      for (TagKey<Item> tag : dustItem.builtInRegistryHolder().tags().toList()) {
        ResourceLocation loc = tag.location();
        if ("c".equals(loc.getNamespace()) && loc.getPath().startsWith("dusts/")) {
          String mat = loc.getPath().substring("dusts/".length());
          if (!mat.isBlank()) {
            return mat;
          }
        }
      }
    } catch (Throwable ignored) {
    }

    // 2. Extract from item registry path
    ResourceLocation id = BuiltInRegistries.ITEM.getKey(dustItem);
    if (id == null) {
      return null;
    }
    String path = id.getPath();
    if (path.contains("/")) {
      path = path.substring(path.lastIndexOf('/') + 1);
    }
    if (path.startsWith("dust_")) {
      return path.substring("dust_".length());
    }
    if (path.endsWith("_dust")) {
      return path.substring(0, path.length() - "_dust".length());
    }
    if (path.startsWith("crushed_")) {
      return path.substring("crushed_".length());
    }
    return null;
  }

  private static boolean isDust(ItemStack stack) {
    if (stack.isEmpty() || stack.getItem() == Items.AIR) {
      return false;
    }
    if (stack.is(DUSTS)) {
      return true;
    }
    ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
    if (id == null) {
      return false;
    }
    String path = id.getPath();
    return path.endsWith("_dust") || path.startsWith("dust_");
  }

  // -------------------------------------------------------------------------
  // Helpers
  // -------------------------------------------------------------------------

  private static boolean isIngot(ItemStack stack) {
    if (stack.is(INGOTS)) {
      return true;
    }
    String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
    return path.endsWith("_ingot") || path.startsWith("ingot_") || path.startsWith("ingots/");
  }

  private static boolean isOreOrRawMaterial(ItemStack stack) {
    if (stack.is(ORES) || stack.is(RAW_MATERIALS)) {
      return true;
    }
    String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
    return isOrePath(path)
        || path.startsWith("raw_")
        || path.startsWith("crushed_raw_")
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

  private static Item findDustForIngot(ItemStack ingot) {
    String material = materialName(ingot);
    if (material == null || material.isBlank()) {
      return null;
    }

    try {
      TagKey<Item> dustTag = itemTag("c:dusts/" + material);
      ItemStack[] taggedDusts = Ingredient.of(dustTag).getItems();
      if (taggedDusts.length > 0) {
        return java.util.Arrays.stream(taggedDusts)
            .map(ItemStack::getItem)
            .filter(item -> item != null && item != Items.AIR)
            .distinct()
            .sorted(Comparator.comparing(item -> BuiltInRegistries.ITEM.getKey(item).toString()))
            .findFirst()
            .orElse(null);
      }
    } catch (Throwable ignored) {
    }

    ResourceLocation ingotId = BuiltInRegistries.ITEM.getKey(ingot.getItem());
    if (ingotId != null) {
      ResourceLocation localDustId =
          ResourceLocation.fromNamespaceAndPath(ingotId.getNamespace(), material + "_dust");
      Item localDust = BuiltInRegistries.ITEM.getOptional(localDustId).orElse(null);
      if (localDust != null && localDust != Items.AIR) {
        return localDust;
      }

      ResourceLocation prefixDustId =
          ResourceLocation.fromNamespaceAndPath(ingotId.getNamespace(), "dust_" + material);
      Item prefixDust = BuiltInRegistries.ITEM.getOptional(prefixDustId).orElse(null);
      if (prefixDust != null && prefixDust != Items.AIR) {
        return prefixDust;
      }
    }

    ResourceLocation ic2DustId = ResourceLocation.fromNamespaceAndPath("ic2", material + "_dust");
    Item ic2Dust = BuiltInRegistries.ITEM.getOptional(ic2DustId).orElse(null);
    return (ic2Dust != null && ic2Dust != Items.AIR) ? ic2Dust : null;
  }

  private static String materialName(ItemStack ingot) {
    String path = BuiltInRegistries.ITEM.getKey(ingot.getItem()).getPath();
    if (path.contains("/")) {
      path = path.substring(path.lastIndexOf('/') + 1);
    }
    if (path.startsWith("ingot_")) {
      return path.substring("ingot_".length());
    }
    if (path.endsWith("_ingot")) {
      return path.substring(0, path.length() - "_ingot".length());
    }
    if (path.startsWith("raw_")) {
      return path.substring("raw_".length());
    }
    return null;
  }

  private static TagKey<Item> itemTag(String id) {
    return TagKey.create(Registries.ITEM, ResourceLocation.parse(id));
  }
}
