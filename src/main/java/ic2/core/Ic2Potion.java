package ic2.core;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class Ic2Potion extends MobEffect {
  public static Ic2Potion radiation;

  public Ic2Potion(MobEffectCategory type, int liquidColor) {
    super(type, liquidColor);
  }

  /**
   * Returns the registry Holder for the radiation effect. In 1.21 the mob-effect system is
   * Holder-based, so effect look-ups/additions require the Holder rather than the raw {@link
   * MobEffect} instance.
   */
  public static Holder<MobEffect> radiationHolder() {
    return BuiltInRegistries.MOB_EFFECT.wrapAsHolder(radiation);
  }

  public boolean applyEffectTick(LivingEntity entity, int amplifier) {
    if (this != radiation || entity.level().isClientSide || !entity.isAlive()) {
      return true;
    }

    // Resolve the damage type from the current level instead of retaining a static DamageSource.
    // A cached holder can outlive the registry access that created it, and damage effects are
    // evaluated during entity ticks where that can turn into a server-side exception.
    int safeAmplifier = Math.max(0, Math.min(amplifier, 100));
    float damage = safeAmplifier / 100.0F + 0.5F;
    if (Float.isFinite(damage) && damage > 0.0F) {
      entity.hurt(Ic2DamageSource.radiation(entity.level()), damage);
    }

    return true;
  }

  public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
    if (this != radiation) {
      return false;
    }

    // Shift counts >= 5 produce zero; higher IC2 radiation amplifiers intentionally tick every
    // tick. Clamp the shift input so malformed values cannot change that cadence unexpectedly.
    int rate = 25 >> Math.min(Math.max(amplifier, 0), 5);
    return rate > 0 ? duration % rate == 0 : true;
  }

  public void applyTo(LivingEntity entity, int duration, int amplifier) {
    if (entity == null || entity.level().isClientSide || duration <= 0) {
      return;
    }

    // IC2's uranium fuel items intentionally use amplifier 100. Keep it representable in the
    // entity-effect packet and avoid corrupted stacks/effects supplying an overflowing amplifier.
    int safeDuration = Math.min(duration, 20 * 60 * 60 * 24);
    int safeAmplifier = Math.max(0, Math.min(amplifier, 100));
    Holder<MobEffect> effectHolder = radiationHolder();
    MobEffectInstance current = entity.getEffect(effectHolder);

    // A malformed or legacy effect may already have an amplifier outside the safe byte-sized
    // range. Replace it before it can be sent to the player or their trackers again.
    if (current != null && current.getAmplifier() > 100) {
      entity.removeEffect(effectHolder);
      current = null;
    }

    // Nuclear resources call this from inventoryTick, so it may run every game tick. Re-adding
    // an effect with a slightly longer duration every tick repeatedly sends effect packets to the
    // entity and its trackers. Refresh only when the effect is nearly half expired, or upgrade it
    // immediately when a stronger source is encountered.
    if (current != null
        && current.getAmplifier() >= safeAmplifier
        && current.getDuration() > safeDuration / 2) {
      return;
    }

    int appliedAmplifier =
        current == null ? safeAmplifier : Math.max(safeAmplifier, current.getAmplifier());
    entity.addEffect(new MobEffectInstance(effectHolder, safeDuration, appliedAmplifier));
  }
}
