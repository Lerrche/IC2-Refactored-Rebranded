package ic2.core.util;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.Recipes;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

public class ConfigUtil {
  public static List<String> asList(String str) {
    str = str.trim();
    return str.isEmpty() ? Collections.emptyList() : Arrays.asList(str.split("\\s*,\\s*"));
  }

  public static List<ItemStack> asStackList(String str) throws ParseException {
    List<String> parts = asList(str);
    List<ItemStack> ret = new ArrayList<>(parts.size());

    for (String part : parts) {
      ItemStack stack = asStack(part);
      if (stack != null && !stack.isEmpty()) {
        ret.add(stack);
      }
    }

    return ret;
  }

  public static List<IRecipeInput> asRecipeInputList(String str) throws ParseException {
    return asRecipeInputList(str, false);
  }

  public static List<IRecipeInput> asRecipeInputList(String str, boolean allowNull)
      throws ParseException {
    List<String> parts = asList(str);
    List<IRecipeInput> ret = new ArrayList<>(parts.size());

    for (String part : parts) {
      part = part.trim();
      if (part.isEmpty()) continue;
      try {
        IRecipeInput input = asRecipeInput(part);
        if (input != null) {
          ret.add(input);
        } else if (!allowNull) {
          ic2.core.IC2.log.warn(
              ic2.core.util.LogCategory.General,
              "Skipping invalid or missing item in config: '%s'",
              part);
        }
      } catch (Exception e) {
        ic2.core.IC2.log.warn(
            ic2.core.util.LogCategory.General,
            "Failed to parse recipe input '%s' from config: %s",
            part,
            e.getMessage());
      }
    }

    return ret;
  }

  private static ItemStack asStack(String str, boolean checkAmount) throws ParseException {
    if (str == null || str.isBlank()) {
      return null;
    }
    String[] parts = str.split("(?=(\\*))");
    String itemName = parts[0].trim();
    if (itemName.isEmpty()) {
      return null;
    }
    Item item = Util.getItem(itemName);
    if (item == null || item == net.minecraft.world.item.Items.AIR) {
      return null;
    }

    ItemStack stack = new ItemStack(item);
    if (stack.isEmpty()) {
      return null;
    }
    int amount = 1;

    for (int i = 1; i < parts.length; i++) {
      String tmp = parts[i];
      if (tmp.startsWith("*")) {
        if (!checkAmount) {
          throw new ParseException("We do not support amount here.", -1);
        }

        amount = Integer.parseInt(tmp.substring(1));
      }
    }

    if (checkAmount) {
      stack = StackUtil.setSize(stack, amount);
    }

    return stack;
  }

  public static ItemStack asStack(String str) throws ParseException {
    return asStack(str, false);
  }

  public static ItemStack asStackWithAmount(String str) throws ParseException {
    return asStack(str, true);
  }

  public static String fromStack(ItemStack stack) {
    return fromStack(stack, false);
  }

  private static String fromStack(ItemStack stack, boolean amount) {
    String ret = Util.getName(stack.getItem()).toString();
    if (amount) {
      ret = ret + "*" + StackUtil.getSize(stack);
    }

    return ret;
  }

  public static String fromStackWithAmount(ItemStack stack) {
    return fromStack(stack, true);
  }

  public static IRecipeInput asRecipeInput(String str) throws ParseException {
    return asRecipeInput(str, false);
  }

  public static IRecipeInput asRecipeInputWithAmount(String str) throws ParseException {
    return asRecipeInput(str, true);
  }

  private static IRecipeInput asRecipeInput(String str, boolean checkAmount) throws ParseException {
    String[] parts = str.split("(?=(\\*))");
    String itemName = parts[0];
    if (!itemName.startsWith("Tag:") && !itemName.startsWith("Fluid:")) {
      ItemStack stack = asStack(str, checkAmount);
      return (stack == null || stack.isEmpty()) ? null : Recipes.inputFactory.forStack(stack);
    }

    Integer amount = null;

    for (int i = 1; i < parts.length; i++) {
      String tmp = parts[i];
      if (tmp.startsWith("*")) {
        if (!checkAmount) {
          throw new ParseException("We do not support amount here.", -1);
        }

        amount = Integer.parseInt(tmp.substring(1));
      }
    }

    if (itemName.startsWith("Tag:")) {
      if (amount == null) {
        amount = 1;
      }

      return Recipes.inputFactory.forTag(itemName.substring("Tag:".length()), amount);
    } else if (itemName.startsWith("Fluid:")) {
      if (amount == null) {
        amount = 1000;
      }
      // TODO
      ResourceLocation id =
          ResourceLocation.withDefaultNamespace(itemName.substring("Fluid:".length()));
      Fluid fluid = Util.getFluid(id);
      return fluid == null ? null : Recipes.inputFactory.forFluidContainer(fluid, amount);
    } else {
      return null;
    }
  }
}
