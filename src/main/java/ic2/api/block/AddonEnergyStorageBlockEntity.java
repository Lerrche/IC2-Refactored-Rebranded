package ic2.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/** Independent add-on battery/storage block entity with IC2 EU-network and FE bridge support. */
public abstract class AddonEnergyStorageBlockEntity extends AddonEnergyBlockEntity {
  protected AddonEnergyStorageBlockEntity(
      BlockEntityType<?> type,
      BlockPos pos,
      BlockState state,
      int tier,
      double outputEuPerTick,
      double capacityEu) {
    this(type, pos, state, tier, capacityEu, outputEuPerTick, outputEuPerTick);
  }

  protected AddonEnergyStorageBlockEntity(
      BlockEntityType<?> type,
      BlockPos pos,
      BlockState state,
      int tier,
      double capacityEu,
      double inputEuPerTick,
      double outputEuPerTick) {
    super(type, pos, state, tier, capacityEu, inputEuPerTick, outputEuPerTick);
  }
}
