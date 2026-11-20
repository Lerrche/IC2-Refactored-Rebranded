package ic2.core.item;

import ic2.core.ref.Ic2Items;
import ic2.core.util.StackUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public class ItemMug extends Item implements ItemLike {
  public ItemMug.MugType mugType;

  public ItemMug(Properties settings, ItemMug.MugType mugType) {
    super(settings);
    this.mugType = mugType;
  }

  public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
    if (!(entityLiving instanceof Player player)) {
      return stack;
    } else {
      ItemMug.MugType type = this.getType(stack);
      if (type != null && type != ItemMug.MugType.empty) {
        if (!worldIn.isClientSide) {
          switch (type) {
            case cold_coffee -> this.applyCoffee(player, 1, 600, false);
            case dark_coffee -> this.applyCoffee(player, 5, 1200, false);
            case coffee -> this.applyCoffee(player, 6, 1200, true);
            case brew -> {
              player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0));
              player.getFoodData().eat(1, 0.2F);
            }
            case youngster -> {
              player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 0));
              player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 1200, 0));
            }
            case beer -> {
              player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 3600, 1));
              player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 3600, 0));
              player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 1));
            }
            case ale -> {
              player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 3600, 1));
              player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 3600, 0));
              player.addEffect(new MobEffectInstance(MobEffects.HEALTH_BOOST, 2400, 0));
            }
            case black_stuff -> {
              player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2400, 1));
              player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 2400, 1));
              player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 600, 0));
              player.addEffect(new MobEffectInstance(MobEffects.POISON, 200, 0));
            }
            case dragon_blood -> {
              player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 4800, 2));
              player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 4800, 1));
              player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 4800, 2));
              player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 4800, 0));
              player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1200, 0));
            }
            case rum -> {
              player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 3600, 1));
              player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 3600, 0));
              player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 3600, 0));
              MobEffectInstance existingConf = player.getEffect(MobEffects.CONFUSION);
              int confDur = existingConf != null ? existingConf.getDuration() + 300 : 200;
              player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, confDur, 0));
            }
            default -> {}
          }
        }

        return new ItemStack(Ic2Items.EMPTY_MUG, 1);
      } else {
        return stack;
      }
    }
  }

  private void applyCoffee(
      Player player, int maxAmplifier, int extraDuration, boolean isRegularCoffee) {
    int highest = 0;
    int x = this.amplifyEffect(player, MobEffects.MOVEMENT_SPEED, maxAmplifier, extraDuration);
    if (x > highest) {
      highest = x;
    }

    x = this.amplifyEffect(player, MobEffects.DIG_SPEED, maxAmplifier, extraDuration);
    if (x > highest) {
      highest = x;
    }

    if (isRegularCoffee) {
      highest -= 2;
    }

    if (highest >= 3) {
      player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, (highest - 2) * 200, 0));
      if (highest >= 4) {
        player.addEffect(new MobEffectInstance(MobEffects.HARM, 1, highest - 3));
      }
    }
  }

  private int amplifyEffect(
      Player player, Holder<MobEffect> potion, int maxAmplifier, int extraDuration) {
    MobEffectInstance eff = player.getEffect(potion);
    if (eff != null) {
      int newAmp = eff.getAmplifier();
      int newDur = eff.getDuration();
      if (newAmp < maxAmplifier) {
        newAmp++;
      }

      newDur += extraDuration;
      assert potion == eff.getEffect();
      player.addEffect(new MobEffectInstance(potion, newDur, newAmp));
      return newAmp;
    } else {
      player.addEffect(new MobEffectInstance(potion, 300, 0));
      return 1;
    }
  }

  public int getUseDuration(ItemStack stack, net.minecraft.world.entity.LivingEntity entity) {
    ItemMug.MugType type = this.getType(stack);
    return type != null && type != ItemMug.MugType.empty ? 32 : 0;
  }

  public UseAnim getUseAnimation(ItemStack stack) {
    ItemMug.MugType type = this.getType(stack);
    return type != null && type != ItemMug.MugType.empty ? UseAnim.DRINK : UseAnim.NONE;
  }

  public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
    ItemMug.MugType type = this.getType(StackUtil.get(player, hand));
    if (type != null && type != ItemMug.MugType.empty) {
      player.startUsingItem(hand);
    }

    return super.use(world, player, hand);
  }

  private ItemMug.MugType getType(ItemStack stack) {
    return ((ItemMug) stack.getItem()).mugType;
  }

  public enum MugType {
    empty,
    cold_coffee,
    dark_coffee,
    coffee,
    brew,
    youngster,
    beer,
    ale,
    black_stuff,
    dragon_blood,
    rum
  }
}
