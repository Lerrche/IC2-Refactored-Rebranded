package ic2.forge;

import ic2.api.item.ElectricItem;
import ic2.api.item.IBackupElectricItemManager;
import ic2.api.item.IElectricItem;
import ic2.api.item.ISpecialElectricItem;
import ic2.core.energy.EnergyBridgeMath;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

/**
 * Backup electric item manager allowing IC2 machines (BatBox, CESU, MFE, MFSU, Chargepad, etc.) to
 * charge and discharge external Forge Energy items (such as Mekanism Energy Tablets, tools, etc.).
 */
public final class ForgeEnergyItemManager implements IBackupElectricItemManager {
  private static final AtomicBoolean REGISTERED = new AtomicBoolean(false);

  public static void init() {
    if (REGISTERED.compareAndSet(false, true)) {
      ElectricItem.registerBackupManager(new ForgeEnergyItemManager());
    }
  }

  @Override
  public boolean handles(ItemStack stack) {
    if (stack == null || stack.isEmpty()) {
      return false;
    }
    // Never hijack native IC2 electric items.
    if (stack.getItem() instanceof IElectricItem
        || stack.getItem() instanceof ISpecialElectricItem) {
      return false;
    }
    IEnergyStorage storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
    return storage != null && (storage.canReceive() || storage.canExtract());
  }

  @Override
  public double charge(
      ItemStack stack, double amount, int tier, boolean ignoreTransferLimit, boolean simulate) {
    if (amount <= 0.0 || stack == null || stack.isEmpty()) {
      return 0.0;
    }
    IEnergyStorage storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
    if (storage == null || !storage.canReceive()) {
      return 0.0;
    }
    long feAvailable = EnergyBridgeMath.euToFeFloor(amount);
    int toSend = EnergyBridgeMath.clampToIntEnergy(feAvailable);
    if (toSend <= 0) {
      return 0.0;
    }
    int acceptedFe = storage.receiveEnergy(toSend, simulate);
    return EnergyBridgeMath.feToEu(acceptedFe);
  }

  @Override
  public double discharge(
      ItemStack stack,
      double amount,
      int tier,
      boolean ignoreTransferLimit,
      boolean externally,
      boolean simulate) {
    if (amount <= 0.0 || stack == null || stack.isEmpty()) {
      return 0.0;
    }
    IEnergyStorage storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
    if (storage == null || !storage.canExtract()) {
      return 0.0;
    }
    long feNeeded = EnergyBridgeMath.euToFeCeil(amount);
    int toExtract = EnergyBridgeMath.clampToIntEnergy(feNeeded);
    if (toExtract <= 0) {
      return 0.0;
    }
    int extractedFe = storage.extractEnergy(toExtract, simulate);
    return EnergyBridgeMath.feToEu(extractedFe);
  }

  @Override
  public double getCharge(ItemStack stack) {
    if (stack == null || stack.isEmpty()) {
      return 0.0;
    }
    IEnergyStorage storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
    return storage == null ? 0.0 : EnergyBridgeMath.feToEu(storage.getEnergyStored());
  }

  @Override
  public double getStackCharge(ItemStack stack) {
    return getCharge(stack);
  }

  @Override
  public double getMaxCharge(ItemStack stack) {
    if (stack == null || stack.isEmpty()) {
      return 0.0;
    }
    IEnergyStorage storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
    return storage == null ? 0.0 : EnergyBridgeMath.feToEu(storage.getMaxEnergyStored());
  }

  @Override
  public boolean canUse(ItemStack stack, double amount) {
    return getCharge(stack) >= amount;
  }

  @Override
  public boolean use(ItemStack stack, double amount, LivingEntity entity) {
    if (canUse(stack, amount)) {
      discharge(stack, amount, Integer.MAX_VALUE, true, false, false);
      return true;
    }
    return false;
  }

  @Override
  public void chargeFromArmor(ItemStack stack, LivingEntity entity) {}

  @Override
  public String getToolTip(ItemStack stack) {
    return null;
  }

  @Override
  public int getTier(ItemStack stack) {
    return 1;
  }
}
