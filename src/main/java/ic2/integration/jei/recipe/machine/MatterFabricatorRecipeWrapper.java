package ic2.integration.jei.recipe.machine;

import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

public class MatterFabricatorRecipeWrapper {
  private final List<ItemStack> inputs;
  private final ItemStack containerInput;
  private final List<ItemStack> outputs;
  private final FluidStack outputFluid;
  private final String amplifierText;
  private final String energyText;

  public MatterFabricatorRecipeWrapper(
      List<ItemStack> inputs,
      ItemStack containerInput,
      List<ItemStack> outputs,
      FluidStack outputFluid,
      String amplifierText,
      String energyText) {
    this.inputs = inputs;
    this.containerInput = containerInput;
    this.outputs = outputs;
    this.outputFluid = outputFluid;
    this.amplifierText = amplifierText;
    this.energyText = energyText;
  }

  public List<ItemStack> getInputs() {
    return this.inputs;
  }

  public ItemStack getContainerInput() {
    return this.containerInput;
  }

  public List<ItemStack> getOutputs() {
    return this.outputs;
  }

  public FluidStack getOutputFluid() {
    return this.outputFluid;
  }

  public String getAmplifierText() {
    return this.amplifierText;
  }

  public String getEnergyText() {
    return this.energyText;
  }
}
