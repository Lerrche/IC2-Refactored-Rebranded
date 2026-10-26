package ic2.api.item.armor;

import ic2.api.item.ElectricItem;
import ic2.core.item.armor.jetpack.IJetpack;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Ready-to-register electric chest-slot jetpack. For custom toggles, fuel types, or flight logic,
 * subclass this class and override {@link #isJetpackActive(ItemStack)} or {@link #drainEnergy}.
 */
public class AddonElectricJetpack extends AddonElectricArmor implements IJetpack {
  private final int energyPerTick;
  private final float power;
  private final float dropPercentage;
  private final float hoverMultiplier;
  private final float worldHeightDivisor;

  public AddonElectricJetpack(
      Holder<ArmorMaterial> material,
      Item.Properties properties,
      double maxCharge,
      double transferLimit,
      int tier,
      int energyPerTick,
      float power,
      float dropPercentage,
      float hoverMultiplier,
      float worldHeightDivisor) {
    super(material, EquipmentSlot.CHEST, properties, maxCharge, transferLimit, tier, 0, 0.0, false);
    if (energyPerTick < 0) throw new IllegalArgumentException("energyPerTick cannot be negative");
    if (!Float.isFinite(power) || power < 0.0F)
      throw new IllegalArgumentException("power must be finite and non-negative");
    if (!Float.isFinite(dropPercentage) || dropPercentage < 0.0F || dropPercentage > 1.0F)
      throw new IllegalArgumentException("dropPercentage must be between 0 and 1");
    if (!Float.isFinite(hoverMultiplier) || hoverMultiplier < 0.0F)
      throw new IllegalArgumentException("hoverMultiplier must be finite and non-negative");
    if (!Float.isFinite(worldHeightDivisor) || worldHeightDivisor <= 0.0F)
      throw new IllegalArgumentException("worldHeightDivisor must be positive");
    this.energyPerTick = energyPerTick;
    this.power = power;
    this.dropPercentage = dropPercentage;
    this.hoverMultiplier = hoverMultiplier;
    this.worldHeightDivisor = worldHeightDivisor;
  }

  @Override
  public void drainEnergy(ItemStack stack, int amount) {
    ElectricItem.manager.discharge(
        stack, (double) amount * this.energyPerTick, Integer.MAX_VALUE, true, false, false);
  }

  @Override
  public float getPower(ItemStack stack) {
    return this.power;
  }

  @Override
  public float getDropPercentage(ItemStack stack) {
    return this.dropPercentage;
  }

  @Override
  public double getChargeLevel(ItemStack stack) {
    return ElectricItem.manager.getCharge(stack) / this.getMaxCharge(stack);
  }

  @Override
  public boolean isJetpackActive(ItemStack stack) {
    return ElectricItem.manager.getCharge(stack) >= this.energyPerTick;
  }

  @Override
  public float getHoverMultiplier(ItemStack stack, boolean upwards) {
    return this.hoverMultiplier;
  }

  @Override
  public float getWorldHeightDivisor(ItemStack stack) {
    return this.worldHeightDivisor;
  }
}
