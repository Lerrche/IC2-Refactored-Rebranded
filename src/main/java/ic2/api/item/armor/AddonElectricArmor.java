package ic2.api.item.armor;

import ic2.api.item.IAddonArmorEffect;
import ic2.core.item.armor.ItemArmorElectric;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;

/** Configurable electric armor base for IC2R add-ons. */
public class AddonElectricArmor extends ItemArmorElectric implements IAddonArmorEffect {
  private final int energyPerDamage;
  private final double damageAbsorptionRatio;
  private final boolean canProvideEnergy;

  public AddonElectricArmor(
      Holder<ArmorMaterial> material,
      EquipmentSlot slot,
      Item.Properties properties,
      double maxCharge,
      double transferLimit,
      int tier,
      int energyPerDamage,
      double damageAbsorptionRatio,
      boolean canProvideEnergy) {
    super(material, slot, properties, maxCharge, transferLimit, tier);
    if (energyPerDamage < 0)
      throw new IllegalArgumentException("energyPerDamage cannot be negative");
    if (!Double.isFinite(damageAbsorptionRatio)
        || damageAbsorptionRatio < 0.0
        || damageAbsorptionRatio > 1.0) {
      throw new IllegalArgumentException("damageAbsorptionRatio must be between 0 and 1");
    }
    this.energyPerDamage = energyPerDamage;
    this.damageAbsorptionRatio = damageAbsorptionRatio;
    this.canProvideEnergy = canProvideEnergy;
  }

  @Override
  public int getEnergyPerDamage() {
    return this.energyPerDamage;
  }

  @Override
  public double getDamageAbsorptionRatio() {
    return this.damageAbsorptionRatio;
  }

  @Override
  public boolean canProvideEnergy(net.minecraft.world.item.ItemStack stack) {
    return this.canProvideEnergy;
  }
}
