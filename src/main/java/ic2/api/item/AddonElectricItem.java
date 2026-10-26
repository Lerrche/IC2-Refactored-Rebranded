package ic2.api.item;

import ic2.core.item.BaseElectricItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Configurable electric item base for IC2R add-ons. Charge handling is provided by IC2R's {@link
 * ElectricItem#manager}; subclasses only need to add their own interaction behavior.
 */
public class AddonElectricItem extends BaseElectricItem {
  private final boolean canProvideEnergy;

  public AddonElectricItem(
      Item.Properties properties,
      double maxCharge,
      double transferLimit,
      int tier,
      boolean canProvideEnergy) {
    super(
        properties,
        requirePositive(maxCharge, "maxCharge"),
        requirePositive(transferLimit, "transferLimit"),
        requireTier(tier));
    this.canProvideEnergy = canProvideEnergy;
  }

  @Override
  public boolean canProvideEnergy(ItemStack stack) {
    return this.canProvideEnergy;
  }

  private static double requirePositive(double value, String name) {
    if (!Double.isFinite(value) || value <= 0.0) {
      throw new IllegalArgumentException(name + " must be a finite positive number");
    }
    return value;
  }

  private static int requireTier(int tier) {
    if (tier < 0 || tier > 3) {
      throw new IllegalArgumentException("tier must be between 0 and 3");
    }
    return tier;
  }
}
