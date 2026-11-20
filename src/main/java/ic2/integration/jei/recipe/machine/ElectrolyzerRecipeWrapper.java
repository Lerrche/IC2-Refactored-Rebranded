package ic2.integration.jei.recipe.machine;

import ic2.api.recipe.IElectrolyzerRecipeManager;
import java.util.List;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class ElectrolyzerRecipeWrapper {
  private final FluidStack inputFluid;
  private final int euPerTick;
  private final int ticks;
  private final List<IElectrolyzerRecipeManager.ElectrolyzerOutput> outputs;

  public ElectrolyzerRecipeWrapper(
      Fluid inputFluid,
      int inputAmount,
      int euPerTick,
      int ticks,
      IElectrolyzerRecipeManager.ElectrolyzerOutput... outputs) {
    this.inputFluid = new FluidStack(inputFluid, inputAmount);
    this.euPerTick = euPerTick;
    this.ticks = ticks;
    this.outputs = List.of(outputs);
  }

  public FluidStack getInputFluid() {
    return this.inputFluid;
  }

  public int getEuPerTick() {
    return this.euPerTick;
  }

  public int getTicks() {
    return this.ticks;
  }

  public int getTotalEu() {
    return this.euPerTick * this.ticks;
  }

  public List<IElectrolyzerRecipeManager.ElectrolyzerOutput> getOutputs() {
    return this.outputs;
  }
}
