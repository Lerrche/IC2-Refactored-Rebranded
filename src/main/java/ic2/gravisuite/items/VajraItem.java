package ic2.gravisuite.items;

import ic2.api.item.AddonElectricTool;
import ic2.api.item.BlockBreakableItem;
import ic2.core.IC2;
import ic2.core.util.KeyboardClient;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The legendary Vajra from Gravitation Suite.
 *
 * <p>Instantly obliterates blocks when charged and features a toggleable Silk Touch / Accurate
 * mode.
 */
public class VajraItem extends AddonElectricTool implements BlockBreakableItem {
  public static final double MAX_CHARGE = 3_000_000.0;
  public static final double TRANSFER_LIMIT = 30_000.0;
  public static final int TIER = 3;
  public static final double ENERGY_PER_BLOCK = 3_333.0;
  public static final double ENERGY_PER_HIT = 5_000.0;
  public static final float INSTANT_MINE_SPEED = 60.0F;

  public VajraItem() {
    super(
        new Item.Properties().stacksTo(1).rarity(Rarity.EPIC),
        MAX_CHARGE,
        TRANSFER_LIMIT,
        TIER,
        ENERGY_PER_BLOCK);
  }

  public static boolean isSilkTouch(ItemStack stack) {
    CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
    return custom != null && custom.copyTag().getBoolean("SilkTouch");
  }

  public static void setSilkTouch(ItemStack stack, Level level, boolean silk) {
    CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean("SilkTouch", silk));
    if (!level.isClientSide) {
      HolderLookup.RegistryLookup<Enchantment> lookup =
          level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
      Holder<Enchantment> silkTouch = lookup.getOrThrow(Enchantments.SILK_TOUCH);
      EnchantmentHelper.updateEnchantments(
          stack,
          mutable -> {
            if (silk) {
              mutable.set(silkTouch, 1);
            } else {
              mutable.set(silkTouch, 0);
            }
          });
    }
  }

  public static boolean toggleSilkTouch(Level level, Player player, ItemStack stack) {
    boolean next = !isSilkTouch(stack);
    setSilkTouch(stack, level, next);
    if (!level.isClientSide) {
      Component status =
          next
              ? Component.translatable("gravisuite.message.enabled")
              : Component.translatable("gravisuite.message.disabled");
      player.displayClientMessage(
          Component.translatable("gravisuite.message.silk_touch", status), false);
      level.playSound(
          null,
          player.blockPosition(),
          SoundEvents.UI_BUTTON_CLICK.value(),
          SoundSource.PLAYERS,
          0.6F,
          next ? 1.4F : 0.9F);
    }
    return next;
  }

  @Override
  public void inventoryTick(
      ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
    super.inventoryTick(stack, level, entity, slotId, isSelected);
    if (!level.isClientSide && entity instanceof Player) {
      boolean silk = isSilkTouch(stack);
      HolderLookup.RegistryLookup<Enchantment> lookup =
          level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
      Holder<Enchantment> silkTouch = lookup.getOrThrow(Enchantments.SILK_TOUCH);
      int currentLevel = EnchantmentHelper.getItemEnchantmentLevel(silkTouch, stack);
      if (silk && currentLevel == 0) {
        EnchantmentHelper.updateEnchantments(stack, m -> m.set(silkTouch, 1));
      } else if (!silk && currentLevel > 0) {
        EnchantmentHelper.updateEnchantments(stack, m -> m.set(silkTouch, 0));
      }
    }
  }

  @Override
  public float getDestroySpeed(ItemStack stack, BlockState state) {
    if (hasEnergy(stack, ENERGY_PER_BLOCK)) {
      return INSTANT_MINE_SPEED;
    }
    return 1.0F;
  }

  @Override
  public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
    return hasEnergy(stack, ENERGY_PER_BLOCK) && state.getDestroySpeed(null, null) >= 0.0F;
  }

  @Override
  public boolean mineBlock(
      ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
    if (!level.isClientSide && state.getDestroySpeed(level, pos) != 0.0F) {
      useEnergy(stack, ENERGY_PER_BLOCK, entity);
    }
    return true;
  }

  @Override
  public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    if (useEnergy(stack, ENERGY_PER_HIT, attacker)) {
      if (attacker instanceof Player player) {
        target.hurt(attacker.damageSources().playerAttack(player), 25.0F);
      }
    }
    return true;
  }

  @Override
  public @NotNull InteractionResult useOn(UseOnContext context) {
    Player player = context.getPlayer();
    if (player != null && IC2.keyboard.isModeSwitchKeyDown(player)) {
      toggleSilkTouch(context.getLevel(), player, context.getItemInHand());
      return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }
    return super.useOn(context);
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);
    if (IC2.keyboard.isModeSwitchKeyDown(player)) {
      toggleSilkTouch(level, player, stack);
      return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    return super.use(level, player, hand);
  }

  @Override
  public void appendHoverText(
      ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    super.appendHoverText(stack, context, tooltip, flag);
    boolean silk = isSilkTouch(stack);
    tooltip.add(
        Component.translatable(
            "gravisuite.message.silk_touch",
            silk
                ? Component.translatable("gravisuite.message.enabled")
                : Component.translatable("gravisuite.message.disabled")));
    Component modeKey = KeyboardClient.modeSwitchKey.getKey().getDisplayName();
    Component useKey = Minecraft.getInstance().options.keyUse.getKey().getDisplayName();
    tooltip.add(Component.translatable("item.ic2.tooltip.mode.switch", modeKey, useKey));
  }

  @Override
  public InteractionResult onBlockStartBreak(
      Player player, Level world, InteractionHand hand, BlockPos pos, Direction direction) {
    return InteractionResult.PASS;
  }

  @Override
  public boolean beforeBlockBreak(
      Level world,
      Player player,
      BlockPos pos,
      BlockState state,
      @Nullable BlockEntity blockEntity) {
    return true;
  }

  @Override
  public void afterBlockBreak(
      Level world,
      Player player,
      BlockPos pos,
      BlockState state,
      @Nullable BlockEntity blockEntity) {}
}
