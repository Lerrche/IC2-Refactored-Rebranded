package ic2.api.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Extensible electric tool base class for IC2R add-ons.
 *
 * <p>Provides standard energy consumption and verification helpers for custom active tools such as
 * electric wrenches, treetaps, shears, or multipurpose tools.
 */
public class AddonElectricTool extends AddonElectricItem {
  private final double energyPerOperation;

  public AddonElectricTool(
      Item.Properties properties,
      double maxCharge,
      double transferLimit,
      int tier,
      double energyPerOperation) {
    super(properties, maxCharge, transferLimit, tier, false);
    if (!Double.isFinite(energyPerOperation) || energyPerOperation < 0.0) {
      throw new IllegalArgumentException("energyPerOperation must be finite and non-negative");
    }
    this.energyPerOperation = energyPerOperation;
  }

  public double getEnergyPerOperation() {
    return this.energyPerOperation;
  }

  /** Checks if the item stack has at least the default operation energy. */
  public boolean hasEnergy(ItemStack stack) {
    return hasEnergy(stack, this.energyPerOperation);
  }

  /** Checks if the item stack has at least the specified amount of EU. */
  public boolean hasEnergy(ItemStack stack, double amount) {
    if (amount <= 0.0) return true;
    return ElectricItem.manager != null && ElectricItem.manager.canUse(stack, amount);
  }

  /** Consumes the default operation energy. Returns true if successful. */
  public boolean useEnergy(ItemStack stack, LivingEntity entity) {
    return useEnergy(stack, this.energyPerOperation, entity);
  }

  /** Consumes a specific amount of energy from the tool stack. Returns true if successful. */
  public boolean useEnergy(ItemStack stack, double amount, LivingEntity entity) {
    if (amount <= 0.0) return true;
    return ElectricItem.manager != null && ElectricItem.manager.use(stack, amount, entity);
  }

  /** Returns current charge of the tool. */
  public double getCharge(ItemStack stack) {
    return ElectricItem.manager != null ? ElectricItem.manager.getCharge(stack) : 0.0;
  }
}
