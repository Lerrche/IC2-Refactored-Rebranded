package ic2.api.block;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.info.ILocatable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

/**
 * Independent, add-on-friendly EU storage base. Unlike IC2R's internal tile entities this class
 * does not require an IC2R block class, container, or private component. Register your own vanilla
 * BlockEntityType and expose {@link #getEnergyStorage(Direction)} through RegisterCapabilitiesEvent
 * when you want NeoForge FE compatibility too.
 */
public abstract class AddonEnergyBlockEntity extends BlockEntity
    implements IEnergySink, IEnergySource, ILocatable, IAddonServerTickable {
  /** Existing IC2R conversion: one EU corresponds to two FE. */
  public static final double FE_PER_EU = 2.0D;

  private final int tier;
  private final double capacity;
  private final double inputLimit;
  private final double outputLimit;
  private double stored;
  private boolean registered;
  private long transferTick = Long.MIN_VALUE;
  private double inputUsedThisTick;
  private double outputUsedThisTick;
  private final IEnergyStorage feStorage =
      new IEnergyStorage() {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
          if (maxReceive <= 0 || !canReceive()) return 0;
          double eu =
              Math.min(
                  maxReceive / FE_PER_EU, Math.min(availableInputThisTick(), capacity - stored));
          int fe = (int) Math.floor(eu * FE_PER_EU + 1.0E-9);
          if (!simulate && fe > 0) {
            stored += fe / FE_PER_EU;
            inputUsedThisTick += fe / FE_PER_EU;
            setChanged();
          }
          return Math.max(0, fe);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
          if (maxExtract <= 0 || !canExtract()) return 0;
          double eu = Math.min(maxExtract / FE_PER_EU, Math.min(availableOutputThisTick(), stored));
          int fe = (int) Math.floor(eu * FE_PER_EU + 1.0E-9);
          if (!simulate && fe > 0) {
            stored -= fe / FE_PER_EU;
            outputUsedThisTick += fe / FE_PER_EU;
            setChanged();
          }
          return Math.max(0, fe);
        }

        @Override
        public int getEnergyStored() {
          return (int) Math.min(Integer.MAX_VALUE, Math.floor(stored * FE_PER_EU));
        }

        @Override
        public int getMaxEnergyStored() {
          return (int) Math.min(Integer.MAX_VALUE, Math.floor(capacity * FE_PER_EU));
        }

        @Override
        public boolean canExtract() {
          return outputLimit > 0;
        }

        @Override
        public boolean canReceive() {
          return inputLimit > 0;
        }
      };

  protected AddonEnergyBlockEntity(
      BlockEntityType<?> type,
      BlockPos pos,
      BlockState state,
      int tier,
      double capacityEu,
      double inputEuPerTick,
      double outputEuPerTick) {
    super(type, pos, state);
    if (tier < 0 || tier > 3) throw new IllegalArgumentException("tier must be between 0 and 3");
    if (!Double.isFinite(capacityEu) || capacityEu <= 0)
      throw new IllegalArgumentException("capacityEu must be finite and positive");
    if (!Double.isFinite(inputEuPerTick) || inputEuPerTick < 0)
      throw new IllegalArgumentException("inputEuPerTick must be finite and non-negative");
    if (!Double.isFinite(outputEuPerTick) || outputEuPerTick < 0)
      throw new IllegalArgumentException("outputEuPerTick must be finite and non-negative");
    this.tier = tier;
    this.capacity = capacityEu;
    this.inputLimit = inputEuPerTick;
    this.outputLimit = outputEuPerTick;
  }

  @Override
  public void onLoad() {
    super.onLoad();
    registerWithEnergyNet();
  }

  @Override
  public void clearRemoved() {
    super.clearRemoved();
    registerWithEnergyNet();
  }

  @Override
  public void setRemoved() {
    unregisterFromEnergyNet();
    super.setRemoved();
  }

  private void registerWithEnergyNet() {
    if (this.level != null
        && !this.level.isClientSide
        && EnergyNet.instance != null
        && !this.registered) {
      EnergyNet.instance.addBlockEntityTile(this);
      this.registered = true;
    }
  }

  private void unregisterFromEnergyNet() {
    if (this.registered && EnergyNet.instance != null) {
      EnergyNet.instance.removeTile(this);
      this.registered = false;
    }
  }

  /** Override for custom server-side behavior; no-op for a plain battery. */
  @Override
  public void serverTick() {}

  @Override
  public Level getWorldObj() {
    return this.level;
  }

  @Override
  public BlockPos getPosition() {
    return this.worldPosition;
  }

  @Override
  public boolean acceptsEnergyFrom(IEnergyEmitter emitter, Direction side) {
    return inputLimit > 0 && stored < capacity;
  }

  @Override
  public boolean emitsEnergyTo(IEnergyAcceptor acceptor, Direction side) {
    return outputLimit > 0 && stored > 0;
  }

  @Override
  public double getDemandedEnergy() {
    return Math.min(availableInputThisTick(), Math.max(0, capacity - stored));
  }

  @Override
  public int getSinkTier() {
    return tier;
  }

  @Override
  public double injectEnergy(Direction from, double amount, double voltage) {
    if (!Double.isFinite(amount) || amount <= 0) return 0;
    if (this.level == null || this.level.isClientSide) return amount;
    double accepted = Math.min(amount, Math.min(availableInputThisTick(), capacity - stored));
    if (accepted > 0) {
      stored += accepted;
      inputUsedThisTick += accepted;
      setChanged();
    }
    return Math.max(0, amount - accepted);
  }

  @Override
  public double getOfferedEnergy() {
    return Math.min(stored, availableOutputThisTick());
  }

  @Override
  public void drawEnergy(double amount) {
    if (amount > 0 && Double.isFinite(amount)) {
      double drawn = Math.min(amount, Math.min(stored, availableOutputThisTick()));
      stored = Math.max(0, stored - drawn);
      outputUsedThisTick += drawn;
      if (drawn > 0) setChanged();
    }
  }

  @Override
  public int getSourceTier() {
    return tier;
  }

  @Override
  protected void loadAdditional(
      CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    double saved = tag.getDouble("addonEu");
    this.stored = Double.isFinite(saved) ? Math.max(0, Math.min(capacity, saved)) : 0;
  }

  @Override
  protected void saveAdditional(
      CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    tag.putDouble("addonEu", this.stored);
  }

  private void beginTransferTick() {
    long now = this.level == null ? 0L : this.level.getGameTime();
    if (now != this.transferTick) {
      this.transferTick = now;
      this.inputUsedThisTick = 0;
      this.outputUsedThisTick = 0;
    }
  }

  private double availableInputThisTick() {
    beginTransferTick();
    return Math.max(0, inputLimit - inputUsedThisTick);
  }

  private double availableOutputThisTick() {
    beginTransferTick();
    return Math.max(0, outputLimit - outputUsedThisTick);
  }

  public final double getStoredEu() {
    return stored;
  }

  public final void setStoredEu(double amount) {
    if (!Double.isFinite(amount)) throw new IllegalArgumentException("amount must be finite");
    double next = Math.max(0, Math.min(capacity, amount));
    if (this.stored != next) {
      this.stored = next;
      setChanged();
    }
  }

  public final double getCapacityEu() {
    return capacity;
  }

  public final double getInputLimitEuPerTick() {
    return inputLimit;
  }

  public final double getOutputLimitEuPerTick() {
    return outputLimit;
  }

  public final int getEnergyTier() {
    return tier;
  }

  public final double addEu(double amount) {
    if (this.level != null && this.level.isClientSide || !Double.isFinite(amount) || amount <= 0)
      return 0;
    double accepted = Math.min(amount, capacity - stored);
    stored += accepted;
    if (accepted > 0) setChanged();
    return accepted;
  }

  public final double removeEu(double amount) {
    if (this.level != null && this.level.isClientSide || !Double.isFinite(amount) || amount <= 0)
      return 0;
    double removed = Math.min(amount, stored);
    stored -= removed;
    if (removed > 0) setChanged();
    return removed;
  }

  public IEnergyStorage getEnergyStorage(@Nullable Direction side) {
    return this.feStorage;
  }
}
