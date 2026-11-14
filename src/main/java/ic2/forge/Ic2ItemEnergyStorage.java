package ic2.forge;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.core.energy.EnergyBridgeMath;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * Exposes NeoForge's {@link IEnergyStorage} item capability for IC2 electric items, allowing them
 * to be charged and discharged by external mods such as Mekanism, Thermal, AE2, and Powah.
 */
public class Ic2ItemEnergyStorage implements IEnergyStorage {
  private final ItemStack stack;

  public Ic2ItemEnergyStorage(ItemStack stack) {
    this.stack = stack;
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    if (maxReceive <= 0 || stack.isEmpty()) {
      return 0;
    }
    double euAvailable = EnergyBridgeMath.feToEu(maxReceive);
    if (euAvailable <= 0.0) {
      return 0;
    }
    double euAccepted =
        ElectricItem.manager.charge(stack, euAvailable, Integer.MAX_VALUE, true, simulate);
    if (euAccepted <= 0.0) {
      return 0;
    }
    long feAccepted = EnergyBridgeMath.euToFeCeil(euAccepted);
    return EnergyBridgeMath.clampToIntEnergy(Math.min(maxReceive, feAccepted));
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    if (maxExtract <= 0 || stack.isEmpty() || !canExtract()) {
      return 0;
    }
    double euNeeded = EnergyBridgeMath.feToEu(maxExtract);
    if (euNeeded <= 0.0) {
      return 0;
    }
    double euDrawn =
        ElectricItem.manager.discharge(
            stack, euNeeded, Integer.MAX_VALUE, true, true, simulate);
    if (euDrawn <= 0.0) {
      return 0;
    }
    long feDrawn = EnergyBridgeMath.euToFeFloor(euDrawn);
    return EnergyBridgeMath.clampToIntEnergy(Math.min(maxExtract, feDrawn));
  }

  @Override
  public int getEnergyStored() {
    if (stack.isEmpty()) {
      return 0;
    }
    double charge = ElectricItem.manager.getCharge(stack);
    return EnergyBridgeMath.clampToIntEnergy(EnergyBridgeMath.euToFeFloor(charge));
  }

  @Override
  public int getMaxEnergyStored() {
    if (stack.isEmpty()) {
      return 0;
    }
    double maxCharge = ElectricItem.manager.getMaxCharge(stack);
    return EnergyBridgeMath.clampToIntEnergy(EnergyBridgeMath.euToFeFloor(maxCharge));
  }

  @Override
  public boolean canExtract() {
    if (stack.getItem() instanceof IElectricItem electricItem) {
      return electricItem.canProvideEnergy(stack);
    }
    return false;
  }

  @Override
  public boolean canReceive() {
    if (stack.isEmpty()) {
      return false;
    }
    return ElectricItem.manager.getMaxCharge(stack) > 0.0;
  }
}
