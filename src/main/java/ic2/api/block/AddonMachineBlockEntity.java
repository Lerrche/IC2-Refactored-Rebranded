package ic2.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Independent EU-powered processing machine. A block should tick this entity using {@link
 * #serverTick()}; {@link AddonEnergyBlock} already does so. Add-ons own input/output slots, fluids,
 * recipes, GUI, sounds, upgrades and visual state.
 */
public abstract class AddonMachineBlockEntity extends AddonEnergyBlockEntity
    implements IAddonMachine {
  private int addonProgress;

  protected AddonMachineBlockEntity(
      BlockEntityType<?> type,
      BlockPos pos,
      BlockState state,
      int tier,
      double capacityEu,
      double inputEuPerTick) {
    this(type, pos, state, tier, capacityEu, inputEuPerTick, 0);
  }

  protected AddonMachineBlockEntity(
      BlockEntityType<?> type,
      BlockPos pos,
      BlockState state,
      int tier,
      double capacityEu,
      double inputEuPerTick,
      double outputEuPerTick) {
    super(type, pos, state, tier, capacityEu, inputEuPerTick, outputEuPerTick);
  }

  /** Call from a server-side block entity ticker. */
  @Override
  public final void serverTick() {
    if (this.level == null || this.level.isClientSide) return;
    int duration = Math.max(1, getProcessingTime());
    int cost = Math.max(0, getEnergyPerTick());
    if (!canProcess()) {
      if (this.addonProgress != 0) {
        this.addonProgress = 0;
        setChanged();
      }
      return;
    }
    // Preserve progress during a temporary power shortage instead of resetting the operation.
    if (cost > 0 && getStoredEu() < cost) return;
    if (cost > 0 && removeEu(cost) + 1.0E-9 < cost) return;
    if (++this.addonProgress >= duration) {
      this.addonProgress = 0;
      processOperation();
      setChanged();
    }
  }

  @Override
  protected void loadAdditional(
      CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    this.addonProgress = Math.max(0, tag.getInt("addonProgress"));
  }

  @Override
  protected void saveAdditional(
      CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    tag.putInt("addonProgress", this.addonProgress);
  }

  @Override
  public int getAddonProgress() {
    return this.addonProgress;
  }

  public int getAddonProgressPercent() {
    return Math.min(100, this.addonProgress * 100 / Math.max(1, getProcessingTime()));
  }
}
