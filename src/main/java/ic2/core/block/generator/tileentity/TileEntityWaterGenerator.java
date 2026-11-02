package ic2.core.block.generator.tileentity;

import ic2.core.IC2;
import ic2.core.block.comp.Fluids;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.fluid.Ic2FluidStack;
import ic2.core.gui.dynamic.IGuiValueProvider;
import ic2.core.init.IC2Config;
import ic2.core.network.GuiSynced;
import ic2.core.ref.Ic2BlockEntities;
import ic2.core.ref.Ic2SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntityWaterGenerator extends TileEntityBaseRotorGenerator
    implements IGuiValueProvider {
  private static final int WATER_PER_FUEL_CYCLE_MB = 1000;
  private static final int FUEL_PER_WATER_CYCLE = 500;
  private static final int WATER_TANK_CAPACITY_MB = 4000;

  public final InvSlotConsumableLiquid fuelSlot;
  public final InvSlotOutput outputSlot;
  protected final Fluids fluids = this.addComponent(new Fluids(this));
  public final Fluids.InternalFluidTank waterTank;

  @GuiSynced public int water = 0;
  public int microStorage = 0;
  public int maxWater = 2000;
  private int waterRefreshTicker = IC2.random.nextInt(20);

  public TileEntityWaterGenerator(BlockPos pos, BlockState state) {
    super(Ic2BlockEntities.WATER_GENERATOR, pos, state, 2.0, 1, 4, 2);
    this.production = 2.0;
    this.waterTank =
        this.fluids.addTankInsert(
            "water",
            WATER_TANK_CAPACITY_MB,
            Fluids.fluidPredicate(net.minecraft.world.level.material.Fluids.WATER));
    this.fuelSlot =
        new InvSlotConsumableLiquidByTank(
            this,
            "fuel",
            IC2Config.balance.watermillAutomation.get() ? InvSlot.Access.IO : InvSlot.Access.NONE,
            1,
            InvSlot.InvSide.TOP,
            InvSlotConsumableLiquid.OpType.Drain,
            this.waterTank);
    this.outputSlot = new InvSlotOutput(this, "output", 1);
  }

  @Override
  protected void onLoaded() {
    super.onLoaded();
    if (this.getLevel() != null && !this.getLevel().isClientSide) {
      this.updateWaterCount();
    }
  }

  @Override
  protected void updateEntityServer() {
    // Process item containers before the generator's fuel step so newly inserted water can be
    // converted into fuel on this tick instead of waiting for the next one.
    if (this.processWaterContainer()) {
      this.setChanged();
    }

    // Refresh independently of fuel conversion. Previously this was only called from flowPower(),
    // which meant the ambient-water state could remain stale while the generator was idle.
    if (++this.waterRefreshTicker >= 20) {
      this.waterRefreshTicker = 0;
      this.updateWaterCount();
    }

    super.updateEntityServer();
  }

  private boolean processWaterContainer() {
    if (this.fuelSlot.isEmpty()) {
      return false;
    }

    int space = this.waterTank.getCapacity() - this.waterTank.getFluidAmount();
    if (space <= 0) {
      return false;
    }

    MutableObject<ItemStack> simulatedOutput = new MutableObject<>();
    Ic2FluidStack simulatedDrain =
        this.fuelSlot.drain(
            net.minecraft.world.level.material.Fluids.WATER, space, simulatedOutput, true);
    if (simulatedDrain == null || simulatedDrain.isEmpty()) {
      return false;
    }

    ItemStack output = simulatedOutput.getValue();
    if (output != null && !output.isEmpty() && !this.outputSlot.canAdd(output)) {
      return false;
    }

    MutableObject<ItemStack> actualOutput = new MutableObject<>();
    Ic2FluidStack actualDrain =
        this.fuelSlot.drain(
            net.minecraft.world.level.material.Fluids.WATER, space, actualOutput, false);
    if (actualDrain == null || actualDrain.isEmpty()) {
      return false;
    }

    int filled = this.waterTank.fillMb(actualDrain, false);
    if (filled <= 0) {
      return false;
    }

    ItemStack remainder = actualOutput.getValue();
    if (remainder != null && !remainder.isEmpty()) {
      this.outputSlot.add(remainder);
    }
    return true;
  }

  @Override
  public boolean gainFuel() {
    if (this.fuel + FUEL_PER_WATER_CYCLE > this.maxWater) {
      return false;
    }

    Ic2FluidStack availableWater = this.waterTank.drainMbUnchecked(WATER_PER_FUEL_CYCLE_MB, true);
    if (availableWater != null && availableWater.getAmountMb() >= WATER_PER_FUEL_CYCLE_MB) {
      this.waterTank.drainMbUnchecked(WATER_PER_FUEL_CYCLE_MB, false);
      this.fuel += FUEL_PER_WATER_CYCLE;
      // Keep the Watermill's established bucket balance: one bucket provides 500 ticks at 1 EU/t.
      this.production = 1.0;
      return true;
    } else if (this.fuel <= 0) {
      this.flowPower();
      this.production = this.microStorage / 100;
      this.microStorage = (int) (this.microStorage - this.production * 100.0);
      if (this.production > 0.0) {
        this.fuel++;
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  @Override
  public boolean gainEnergy() {
    // The inherited method transfers EU when fuel is available. Returning true while water is
    // present also keeps the Water Mill's active state in sync with its available input, rather
    // than waiting for the base generator's delayed activity meter.
    boolean generated = super.gainEnergy();
    return generated || this.water > 0 || this.waterTank.getFluidAmount() > 0;
  }

  @Override
  public boolean isConverting() {
    return this.fuel > 0;
  }

  @Override
  public boolean needsFuel() {
    return this.fuel <= this.maxWater;
  }

  public void flowPower() {
    // Keep water as the real nearby-water count; the configured multiplier affects generated
    // power, not the sampled world state or the client-side rotor animation.
    int powerFromWater =
        (int) Math.round(this.water * IC2Config.balance.energy.generator.water.get());
    if (powerFromWater > 0) {
      this.microStorage += powerFromWater;
    }
  }

  public void updateWaterCount() {
    Level world = this.getLevel();
    if (world == null || world.isClientSide) {
      return;
    }

    int count = 0;
    for (int x = -1; x < 2; x++) {
      for (int y = -1; y < 2; y++) {
        for (int z = -1; z < 2; z++) {
          if (world
              .getBlockState(this.worldPosition.offset(x, y, z))
              .getFluidState()
              .is(net.minecraft.world.level.material.Fluids.WATER)) {
            count++;
          }
        }
      }
    }

    if (this.water != count) {
      this.water = count;
      // This field also drives the rotor renderer. GuiSynced alone only keeps it fresh while a
      // player has the machine GUI open, so publish changes to tracking clients explicitly.
      IC2.network.get(true).updateTileEntityField(this, "water");
    }
  }

  @Override
  public SoundEvent getLoopingSoundEvent() {
    return Ic2SoundEvents.GENERATOR_WATER_LOOP;
  }

  @Override
  protected boolean delayActiveUpdate() {
    // The mill must react as soon as a bucket or a pipe inserts water.
    return false;
  }

  @Override
  protected boolean shouldRotorRotate() {
    // Active is synchronized globally; fuel itself is only GUI-synchronized.
    return this.water > 0 || this.getActive();
  }

  @Override
  protected float rotorSpeedFactor() {
    return this.water > 0 ? this.water / 25.0F : 1.0F;
  }

  @Override
  public double getGuiValue(String name) {
    if ("water".equals(name)) {
      assert this.maxWater > 0;
      return (double) this.fuel / this.maxWater;
    } else {
      throw new IllegalArgumentException("Unexpected value requested: " + name);
    }
  }
}
