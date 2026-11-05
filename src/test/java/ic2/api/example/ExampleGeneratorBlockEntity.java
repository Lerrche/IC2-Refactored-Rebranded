package ic2.api.example;

import ic2.api.block.AddonGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** Example add-on generator that produces 32 EU/t while fueled. */
public class ExampleGeneratorBlockEntity extends AddonGeneratorBlockEntity {
  private int fuelTicksRemaining = 0;

  public ExampleGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    // 32 EU/t, Tier 1 (Low Voltage: 32V), 40,000 EU storage buffer, 32 EU/t output
    super(type, pos, state, 32.0, 1, 40000, 32.0);
  }

  @Override
  protected boolean gainFuel() {
    if (this.fuelTicksRemaining > 0) {
      this.fuelTicksRemaining--;
      return true;
    }
    // In a real mod, inspect inventory slot for coal / lava / custom fuel and burn it here
    return false;
  }

  public void addFuel(int ticks) {
    this.fuelTicksRemaining += ticks;
  }
}
