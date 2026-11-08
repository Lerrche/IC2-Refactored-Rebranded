package ic2.industrialjs;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.Recipes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

public class ItemParser {

  public static IRecipeInput parseInput(Object obj) {
    if (obj == null) {
      return null;
    }
    if (obj instanceof IRecipeInput ri) {
      return ri;
    }
    if (obj instanceof ItemStack stack) {
      return Recipes.inputFactory.forStack(stack, Math.max(1, stack.getCount()));
    }
    if (obj instanceof ItemLike itemLike) {
      return Recipes.inputFactory.forItem(itemLike);
    }
    if (obj instanceof Ingredient ing) {
      return Recipes.inputFactory.forIngredient(ing, 1);
    }
    if (obj instanceof String str) {
      str = str.trim();
      int count = 1;
      if (str.matches("^\\d+[x*]\\s*.*")) {
        int splitIdx = str.indexOf('x') != -1 ? str.indexOf('x') : str.indexOf('*');
        count = Integer.parseInt(str.substring(0, splitIdx).trim());
        str = str.substring(splitIdx + 1).trim();
      }

      if (str.startsWith("#")) {
        String tagStr = str.substring(1).trim();
        return Recipes.inputFactory.forTag(tagStr, count);
      } else {
        ResourceLocation rl = ResourceLocation.tryParse(str);
        if (rl != null && BuiltInRegistries.ITEM.containsKey(rl)) {
          Item item = BuiltInRegistries.ITEM.get(rl);
          return Recipes.inputFactory.forStack(new ItemStack(item, count), count);
        }
      }
    }
    return null;
  }

  public static ItemStack parseOutput(Object obj) {
    if (obj == null) {
      return ItemStack.EMPTY;
    }
    if (obj instanceof ItemStack stack) {
      return stack.copy();
    }
    if (obj instanceof ItemLike itemLike) {
      return new ItemStack(itemLike);
    }
    if (obj instanceof String str) {
      str = str.trim();
      int count = 1;
      if (str.matches("^\\d+[x*]\\s*.*")) {
        int splitIdx = str.indexOf('x') != -1 ? str.indexOf('x') : str.indexOf('*');
        count = Integer.parseInt(str.substring(0, splitIdx).trim());
        str = str.substring(splitIdx + 1).trim();
      }

      ResourceLocation rl = ResourceLocation.tryParse(str);
      if (rl != null && BuiltInRegistries.ITEM.containsKey(rl)) {
        Item item = BuiltInRegistries.ITEM.get(rl);
        return new ItemStack(item, count);
      }
    }
    return ItemStack.EMPTY;
  }

  public static Collection<ItemStack> parseOutputs(Object obj) {
    if (obj == null) {
      return Collections.emptyList();
    }
    if (obj instanceof Collection<?> col) {
      List<ItemStack> list = new ArrayList<>();
      for (Object elem : col) {
        ItemStack stack = parseOutput(elem);
        if (!stack.isEmpty()) {
          list.add(stack);
        }
      }
      return list;
    }
    if (obj.getClass().isArray()) {
      Object[] arr = (Object[]) obj;
      List<ItemStack> list = new ArrayList<>();
      for (Object elem : arr) {
        ItemStack stack = parseOutput(elem);
        if (!stack.isEmpty()) {
          list.add(stack);
        }
      }
      return list;
    }
    ItemStack single = parseOutput(obj);
    return single.isEmpty() ? Collections.emptyList() : Collections.singletonList(single);
  }

  public static Ingredient parseIngredient(Object obj) {
    if (obj == null) {
      return Ingredient.EMPTY;
    }
    if (obj instanceof Ingredient ing) {
      return ing;
    }
    if (obj instanceof ItemStack stack) {
      return Ingredient.of(stack);
    }
    if (obj instanceof ItemLike itemLike) {
      return Ingredient.of(itemLike);
    }
    if (obj instanceof String str) {
      str = str.trim();
      if (str.matches("^\\d+[x*]\\s*.*")) {
        int splitIdx = str.indexOf('x') != -1 ? str.indexOf('x') : str.indexOf('*');
        str = str.substring(splitIdx + 1).trim();
      }

      if (str.startsWith("#")) {
        ResourceLocation rl = ResourceLocation.tryParse(str.substring(1).trim());
        if (rl != null) {
          TagKey<Item> tagKey = ItemTags.create(rl);
          return Ingredient.of(tagKey);
        }
      } else {
        ResourceLocation rl = ResourceLocation.tryParse(str);
        if (rl != null && BuiltInRegistries.ITEM.containsKey(rl)) {
          return Ingredient.of(BuiltInRegistries.ITEM.get(rl));
        }
      }
    }
    return Ingredient.EMPTY;
  }

  public static boolean matches(ItemStack stack, String pattern) {
    if (stack == null || stack.isEmpty() || pattern == null || pattern.isBlank()) {
      return false;
    }
    pattern = pattern.trim().toLowerCase();
    if (pattern.startsWith("#")) {
      ResourceLocation rl = ResourceLocation.tryParse(pattern.substring(1).trim());
      if (rl != null) {
        TagKey<Item> tag = ItemTags.create(rl);
        return stack.is(tag);
      }
      return false;
    }
    ResourceLocation itemLoc = BuiltInRegistries.ITEM.getKey(stack.getItem());
    return itemLoc != null && itemLoc.toString().equalsIgnoreCase(pattern);
  }
}
