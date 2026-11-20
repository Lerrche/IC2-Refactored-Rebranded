package ic2.integration.jei.recipe.machine;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class BoozeBarrelRecipeWrapper {
  private final List<ItemStack> waterContainers;
  private final List<ItemStack> ingredient1;
  private final List<ItemStack> ingredient2;
  private final List<ItemStack> taps;
  private final ItemStack emptyMug;
  private final ItemStack output;
  private final Component duration;
  private final Component ratioDesc;

  public BoozeBarrelRecipeWrapper(
      List<ItemStack> waterContainers,
      List<ItemStack> ingredient1,
      List<ItemStack> ingredient2,
      List<ItemStack> taps,
      ItemStack emptyMug,
      ItemStack output,
      Component duration,
      Component ratioDesc) {
    this.waterContainers = waterContainers;
    this.ingredient1 = ingredient1;
    this.ingredient2 = ingredient2;
    this.taps = taps;
    this.emptyMug = emptyMug;
    this.output = output;
    this.duration = duration;
    this.ratioDesc = ratioDesc;
  }

  public List<ItemStack> getWaterContainers() {
    return this.waterContainers;
  }

  public List<ItemStack> getIngredient1() {
    return this.ingredient1;
  }

  public List<ItemStack> getIngredient2() {
    return this.ingredient2;
  }

  public List<ItemStack> getTaps() {
    return this.taps;
  }

  public ItemStack getEmptyMug() {
    return this.emptyMug;
  }

  public ItemStack getOutput() {
    return this.output;
  }

  public Component getDuration() {
    return this.duration;
  }

  public Component getRatioDesc() {
    return this.ratioDesc;
  }
}
