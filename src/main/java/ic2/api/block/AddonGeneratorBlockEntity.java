package ic2.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Add-on generator base independent of IC2R's internal block classes. Implement {@link #gainFuel()}
 * to consume fuel and return true while generating; the base stores and outputs EU.
 */
public abstract class AddonGeneratorBlockEntity extends AddonEnergyStorageBlockEntity {
  private final double euPerTick;
  private boolean generating;

  protected AddonGeneratorBlockEntity(
      BlockEntityType<?> type,
      BlockPos pos,
      BlockState state,
      double euPerTick,
      int tier,
      int capacityEu) {
    this(type, pos, state, euPerTick, tier, capacityEu, euPerTick);
  }

  protected AddonGeneratorBlockEntity(
      BlockEntityType<?> type,
      BlockPos pos,
      BlockState state,
      double euPerTick,
      int tier,
      int capacityEu,
      double outputEuPerTick) {
    super(type, pos, state, tier, capacityEu, 0, outputEuPerTick);
    if (!Double.isFinite(euPerTick) || euPerTick <= 0)
      throw new IllegalArgumentException("euPerTick must be finite and positive");
    this.euPerTick = euPerTick;
  }

  /** Consume fuel/update fuel state; return true if this generator should produce this tick. */
  protected abstract boolean gainFuel();

  /** Called by an AddonEnergyBlock's server ticker. */
  @Override
  public final void serverTick() {
    if (this.level == null || this.level.isClientSide) return;
    boolean wasGenerating = this.generating;
    this.generating = this.getCapacityEu() > this.getStoredEu() && gainFuel();
    if (this.generating) addEu(Math.min(this.euPerTick, this.getCapacityEu() - this.getStoredEu()));
    if (wasGenerating != this.generating || this.generating) setChanged();
  }

  public final boolean isGenerating() {
    return this.generating;
  }

  public final double getProductionEuPerTick() {
    return this.euPerTick;
  }

  @Override
  protected void loadAdditional(
      CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    this.generating = tag.getBoolean("addonGenerating");
  }

  @Override
  protected void saveAdditional(
      CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    tag.putBoolean("addonGenerating", this.generating);
  }
}
