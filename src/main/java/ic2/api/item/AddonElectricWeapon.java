package ic2.api.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Base for charge-powered melee weapons. Configure vanilla attack damage/speed through the supplied
 * {@link Item.Properties}; player attacks pay EU before damage is applied.
 */
public class AddonElectricWeapon extends AddonElectricItem {
  private final double energyPerHit;

  public AddonElectricWeapon(
      Item.Properties properties,
      double maxCharge,
      double transferLimit,
      int tier,
      double energyPerHit) {
    super(properties, maxCharge, transferLimit, tier, false);
    if (!Double.isFinite(energyPerHit) || energyPerHit < 0.0) {
      throw new IllegalArgumentException("energyPerHit must be finite and non-negative");
    }
    this.energyPerHit = energyPerHit;
  }

  /** Consumes the configured energy before a player attack; IC2R's attack event invokes this. */
  public boolean tryConsumeAttackEnergy(ItemStack stack, LivingEntity attacker) {
    return this.energyPerHit <= 0.0 || ElectricItem.manager.use(stack, this.energyPerHit, attacker);
  }

  @Override
  public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    // Player attacks are charged before the hit by EventHandlerForge. Non-player users still pay
    // here, although their vanilla hit cannot be canceled by Item#hurtEnemy after damage is
    // applied.
    if (!(attacker instanceof net.minecraft.world.entity.player.Player)
        && this.energyPerHit > 0.0
        && !target.level().isClientSide) {
      ElectricItem.manager.use(stack, this.energyPerHit, attacker);
    }
    return true;
  }

  public double getEnergyPerHit() {
    return this.energyPerHit;
  }
}
