package ic2.api.item.armor;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Configurable chest-slot electric energy pack that can provide stored EU to compatible consumers.
 */
public class AddonEnergyPack extends AddonElectricArmor {
  public AddonEnergyPack(
      Holder<ArmorMaterial> material,
      Item.Properties properties,
      double capacity,
      double transferLimit,
      int tier) {
    super(material, EquipmentSlot.CHEST, properties, capacity, transferLimit, tier, 0, 0.0, true);
  }

  @Override
  public boolean canProvideEnergy(ItemStack stack) {
    return true;
  }

  @Override
  public int getEnergyPerDamage() {
    return 0;
  }

  @Override
  public double getDamageAbsorptionRatio() {
    return 0.0;
  }
}
