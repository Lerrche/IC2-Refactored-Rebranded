package ic2.api.example;

import ic2.api.block.AddonMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** Example processing machine (e.g. an Alloy Smelter or Super-Compressor) powered by EU. */
public class ExampleMachineBlockEntity extends AddonMachineBlockEntity {
  private boolean hasWorkToPerform = false;

  public ExampleMachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    // Tier 1 (32V), 10,000 EU capacity, accepts up to 32 EU/t
    super(type, pos, state, 1, 10000.0, 32.0);
  }

  @Override
  public int getProcessingTime() {
    return 100; // 5 seconds (100 ticks)
  }

  @Override
  public int getEnergyPerTick() {
    return 4; // 4 EU/t
  }

  @Override
  public boolean canProcess() {
    // Check if input items and output space are available in inventory
    return this.hasWorkToPerform;
  }

  @Override
  public void processOperation() {
    // Consume inputs and produce outputs
    this.hasWorkToPerform = false;
  }

  public void setHasWork(boolean work) {
    this.hasWorkToPerform = work;
  }
}
