package ic2.api.energy.prefab;

import ic2.api.block.AddonEnergyBlockEntity;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyConductor;
import ic2.api.energy.tile.IEnergyEmitter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * Hybrid IC2/FE cable block entity base. It joins the IC2 EU network as a conductor and exposes an
 * FE storage bridge. Call {@link #serverTick()} from the owning block's server ticker to move FE to
 * and from adjacent FE-compatible blocks. Override removal callbacks to update blockstate/drops.
 */
public abstract class AddonEnergyCableBlockEntity extends AddonEnergyBlockEntity
    implements IEnergyConductor {
  private final double conductionLoss;
  private final double insulationAbsorption;
  private final double insulationBreakdown;
  private final double conductorBreakdown;
  private boolean insulated = true;
  private boolean conductorIntact = true;

  protected AddonEnergyCableBlockEntity(
      BlockEntityType<?> type,
      BlockPos pos,
      BlockState state,
      double capacityEu,
      double transferEuPerTick,
      int tier,
      double conductionLoss,
      double insulationAbsorption,
      double insulationBreakdown,
      double conductorBreakdown) {
    super(type, pos, state, tier, capacityEu, transferEuPerTick, transferEuPerTick);
    this.conductionLoss = nonNegative(conductionLoss, "conductionLoss");
    this.insulationAbsorption = nonNegative(insulationAbsorption, "insulationAbsorption");
    this.insulationBreakdown = nonNegative(insulationBreakdown, "insulationBreakdown");
    this.conductorBreakdown = nonNegative(conductorBreakdown, "conductorBreakdown");
  }

  @Override
  public boolean acceptsEnergyFrom(IEnergyEmitter emitter, Direction side) {
    return this.conductorIntact;
  }

  @Override
  public boolean emitsEnergyTo(IEnergyAcceptor acceptor, Direction side) {
    return this.conductorIntact;
  }

  @Override
  public double getConductionLoss() {
    return this.conductionLoss;
  }

  @Override
  public double getInsulationEnergyAbsorption() {
    return this.insulated ? this.insulationAbsorption : 0.0;
  }

  @Override
  public double getInsulationBreakdownEnergy() {
    return this.insulated ? this.insulationBreakdown : 0.0;
  }

  @Override
  public double getConductorBreakdownEnergy() {
    return this.conductorBreakdown;
  }

  @Override
  public void removeInsulation() {
    this.insulated = false;
    this.onInsulationRemoved();
    setChanged();
  }

  @Override
  public void removeConductor() {
    this.conductorIntact = false;
    this.onConductorRemoved();
    setChanged();
  }

  public boolean hasInsulation() {
    return this.insulated;
  }

  public boolean isConductorIntact() {
    return this.conductorIntact;
  }

  protected void onInsulationRemoved() {}

  protected void onConductorRemoved() {}

  /** Transfers energy with adjacent FE blocks; call once per server tick. */
  @Override
  public void serverTick() {
    Level level = this.level;
    if (level == null || level.isClientSide || !this.conductorIntact) return;
    for (Direction direction : Direction.values()) {
      var adjacentPos = this.worldPosition.relative(direction);
      if (!level.hasChunkAt(adjacentPos)) continue;
      IEnergyStorage adjacent =
          level.getCapability(
              Capabilities.EnergyStorage.BLOCK, adjacentPos, direction.getOpposite());
      if (adjacent == null) continue;
      IEnergyStorage own = getEnergyStorage(direction);
      int available = own.extractEnergy(Integer.MAX_VALUE, true);
      if (available > 0 && adjacent.canReceive()) {
        int accepted = adjacent.receiveEnergy(available, true);
        if (accepted > 0) {
          int extracted = own.extractEnergy(accepted, false);
          int actuallyAccepted = adjacent.receiveEnergy(extracted, false);
          if (actuallyAccepted < extracted) addEu((extracted - actuallyAccepted) / FE_PER_EU);
        }
      }
      if (adjacent.canExtract()) {
        int room = own.receiveEnergy(Integer.MAX_VALUE, true);
        int offered = adjacent.extractEnergy(room, true);
        if (offered > 0) {
          int extracted = adjacent.extractEnergy(offered, false);
          own.receiveEnergy(extracted, false);
        }
      }
    }
  }

  @Override
  protected void loadAdditional(
      CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    this.insulated = !tag.contains("addonCableInsulated") || tag.getBoolean("addonCableInsulated");
    this.conductorIntact = !tag.contains("addonCableIntact") || tag.getBoolean("addonCableIntact");
  }

  @Override
  protected void saveAdditional(
      CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    tag.putBoolean("addonCableInsulated", this.insulated);
    tag.putBoolean("addonCableIntact", this.conductorIntact);
  }

  private static double nonNegative(double value, String name) {
    if (!Double.isFinite(value) || value < 0)
      throw new IllegalArgumentException(name + " must be finite and non-negative");
    return value;
  }
}
