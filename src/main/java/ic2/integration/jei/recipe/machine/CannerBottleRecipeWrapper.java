package ic2.integration.jei.recipe.machine;

import ic2.api.recipe.ICannerBottleRecipeManager;
import ic2.api.recipe.MachineRecipe;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public class CannerBottleRecipeWrapper implements IJeiRecipeWrapper {
  private final ICannerBottleRecipeManager.Input input;
  private final ItemStack output;

  public CannerBottleRecipeWrapper(
      MachineRecipe<ICannerBottleRecipeManager.Input, ItemStack> recipe) {
    this.input = recipe.getInput();
    this.output = recipe.getOutput();
  }

  public List<ItemStack> getContainers() {
    return this.input.container().getInputs();
  }

  public List<ItemStack> getFills() {
    return this.input.fill().getInputs();
  }

  public ItemStack getOutput() {
    return this.output;
  }

  @Override
  public List<List<ItemStack>> getInputs() {
    return List.of(this.getContainers(), this.getFills());
  }

  @Override
  public List<ItemStack> getOutputs() {
    return List.of(this.output);
  }
}
