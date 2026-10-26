package ic2.api.item;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Optional callbacks for add-on armor. Incoming-damage callbacks are called server-side before the
 * damage is applied. Return the remaining damage (0 cancels it); never return a negative value.
 */
public interface IAddonArmorEffect {
  default void onArmorTick(LivingEntity wearer, ItemStack stack) {}

  default float onIncomingDamage(
      LivingEntity wearer, ItemStack stack, DamageSource source, float amount) {
    return amount;
  }
}
