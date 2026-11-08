package ic2.core.recipe.input;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.IRecipeInputFactory;
import ic2.core.util.StackUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;

public class RecipeInputFactory implements IRecipeInputFactory {
  @Override
  public IRecipeInput forItem(ItemLike item) {
    if (item == null || item.asItem() == Items.AIR) {
      return new RecipeInputItemStack(ItemStack.EMPTY);
    }
    return new RecipeInputItemStack(new ItemStack(item));
  }

  @Override
  public IRecipeInput forStack(ItemStack stack) {
    if (stack == null || stack.isEmpty()) {
      return new RecipeInputItemStack(ItemStack.EMPTY);
    }
    return new RecipeInputItemStack(stack);
  }

  @Override
  public IRecipeInput forStack(ItemStack stack, int amount) {
    if (stack == null || stack.isEmpty()) {
      return new RecipeInputItemStack(ItemStack.EMPTY);
    }
    return new RecipeInputItemStack(StackUtil.copyWithSize(stack, Math.max(0, amount)));
  }

  @Override
  public IRecipeInput forTag(String name, int amount) {
    try {
      return this.forIngredient(
          Ingredient.of(TagKey.create(Registries.ITEM, ResourceLocation.parse(name))),
          Math.max(1, amount));
    } catch (Throwable t) {
      return new RecipeInputItemStack(ItemStack.EMPTY);
    }
  }

  @Override
  public IRecipeInput forFluidContainer(Fluid fluid, int amount) {
    return new RecipeInputFluidContainer(fluid, amount);
  }

  @Override
  public Ingredient getIngredient(IRecipeInput input) {
    return input.getIngredient();
  }

  @Override
  public IRecipeInput forIngredient(Ingredient ingredient, int amount) {
    if (ingredient == null || ingredient.isEmpty()) {
      return new RecipeInputItemStack(ItemStack.EMPTY);
    }
    return new RecipeInputIngredient(ingredient, Math.max(1, amount));
  }
}
