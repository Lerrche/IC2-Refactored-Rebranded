package ic2.gravisuite.items;

import ic2.api.item.AddonElectricTool;
import ic2.api.item.BlockBreakableItem;
import ic2.core.IC2;
import ic2.core.util.KeyboardClient;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.neoforged.neoforge.common.IShearable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Advanced Chainsaw from Gravitation Suite.
 *
 * <p>Supports Normal woodcutting, Shears mode (for leaves/vines/wool/sheep), and Tree-feller mode.
 */
public class AdvancedChainsawItem extends AddonElectricTool implements BlockBreakableItem {
  public static final double MAX_CHARGE = 3_000_000.0;
  public static final double TRANSFER_LIMIT = 5_000.0;
  public static final int TIER = 2;
  public static final double ENERGY_PER_BLOCK = 100.0;

  public enum Mode {
    NORMAL("normal", 30.0F),
    SHEARS("shears", 20.0F),
    TREE_FELLER("tree_feller", 25.0F);

    private final String id;
    private final float speed;

    Mode(String id, float speed) {
      this.id = id;
      this.speed = speed;
    }

    public String getId() {
      return id;
    }

    public float getSpeed() {
      return speed;
    }

    public Mode next() {
      Mode[] values = values();
      return values[(ordinal() + 1) % values.length];
    }
  }

  public AdvancedChainsawItem() {
    super(
        new Item.Properties().stacksTo(1).rarity(Rarity.RARE),
        MAX_CHARGE,
        TRANSFER_LIMIT,
        TIER,
        ENERGY_PER_BLOCK);
  }

  public static Mode getMode(ItemStack stack) {
    CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
    if (custom != null && custom.copyTag().contains("ChainsawMode")) {
      int ordinal = custom.copyTag().getInt("ChainsawMode");
      Mode[] values = Mode.values();
      if (ordinal >= 0 && ordinal < values.length) {
        return values[ordinal];
      }
    }
    return Mode.NORMAL;
  }

  public static void setMode(ItemStack stack, Mode mode) {
    CustomData.update(
        DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt("ChainsawMode", mode.ordinal()));
  }

  public static void cycleMode(Level level, Player player, ItemStack stack) {
    Mode next = getMode(stack).next();
    setMode(stack, next);
    if (!level.isClientSide) {
      Component modeName = Component.translatable("gravisuite.mode." + next.getId());
      player.displayClientMessage(
          Component.translatable("gravisuite.message.mode", modeName), false);
      level.playSound(
          null,
          player.blockPosition(),
          SoundEvents.UI_BUTTON_CLICK.value(),
          SoundSource.PLAYERS,
          0.6F,
          1.2F);
    }
  }

  @Override
  public float getDestroySpeed(ItemStack stack, BlockState state) {
    if (hasEnergy(stack, ENERGY_PER_BLOCK)) {
      Mode mode = getMode(stack);
      if (state.is(BlockTags.MINEABLE_WITH_AXE)
          || (mode == Mode.SHEARS
              && (Util.canShear(state)
                  || state.is(BlockTags.LEAVES)
                  || state.is(BlockTags.WOOL)
                  || state.is(Blocks.COBWEB)))) {
        return mode.getSpeed();
      }
    }
    return 1.0F;
  }

  @Override
  public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
    Mode mode = getMode(stack);
    return hasEnergy(stack, ENERGY_PER_BLOCK)
        && (state.is(BlockTags.MINEABLE_WITH_AXE)
            || (mode == Mode.SHEARS
                && (Util.canShear(state)
                    || state.is(BlockTags.LEAVES)
                    || state.is(BlockTags.WOOL)
                    || state.is(Blocks.COBWEB))));
  }

  @Override
  public boolean mineBlock(
      ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
    if (level.isClientSide) return true;
    useEnergy(stack, ENERGY_PER_BLOCK, entity);

    Mode mode = getMode(stack);
    if (mode == Mode.TREE_FELLER && state.is(BlockTags.LOGS) && entity instanceof Player player) {
      BlockPos current = pos.above();
      int limit = 32;
      while (limit-- > 0
          && level.getBlockState(current).is(BlockTags.LOGS)
          && hasEnergy(stack, ENERGY_PER_BLOCK)) {
        level.destroyBlock(current, true, player);
        useEnergy(stack, ENERGY_PER_BLOCK, player);
        current = current.above();
      }
    }
    return true;
  }

  @Override
  public InteractionResult onBlockStartBreak(
      Player player, Level world, InteractionHand hand, BlockPos pos, Direction direction) {
    BlockState state = world.getBlockState(pos);
    ItemStack stack = player.getMainHandItem();
    Mode mode = getMode(stack);
    if (mode == Mode.SHEARS
        && (Util.canShear(state)
            || state.is(BlockTags.LEAVES)
            || state.is(BlockTags.WOOL)
            || state.is(Blocks.COBWEB)
            || state.is(Blocks.VINE))) {
      if (useEnergy(stack, ENERGY_PER_BLOCK, player)) {
        world.levelEvent(player, 2001, pos, Block.getId(state));
        world.gameEvent(GameEvent.BLOCK_DESTROY, pos, Context.of(player, state));
        StackUtil.dropAsEntity(world, pos, new ItemStack(state.getBlock().asItem()));
        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
        if (!world.isClientSide) {
          world.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 0.8F, 1.0F);
        }
        return InteractionResult.SUCCESS;
      }
    }
    return InteractionResult.PASS;
  }

  @Override
  public @NotNull InteractionResult interactLivingEntity(
      @NotNull ItemStack stack,
      @NotNull Player user,
      @NotNull LivingEntity entity,
      @NotNull InteractionHand hand) {
    Mode mode = getMode(stack);
    if (mode == Mode.SHEARS && entity instanceof IShearable shearable) {
      Level level = entity.level();
      BlockPos pos = entity.blockPosition();
      if (shearable.isShearable(user, stack, level, pos)) {
        if (useEnergy(stack, ENERGY_PER_BLOCK, user)) {
          if (!level.isClientSide) {
            List<ItemStack> drops = shearable.onSheared(user, stack, level, pos);
            for (ItemStack drop : drops) {
              if (!drop.isEmpty()) {
                entity.spawnAtLocation(drop);
              }
            }
          }
          level.playSound(null, pos, SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);
          return InteractionResult.sidedSuccess(level.isClientSide);
        }
      }
    }
    return super.interactLivingEntity(stack, user, entity, hand);
  }

  @Override
  public @NotNull InteractionResult useOn(UseOnContext context) {
    Player player = context.getPlayer();
    if (player != null && IC2.keyboard.isModeSwitchKeyDown(player)) {
      cycleMode(context.getLevel(), player, context.getItemInHand());
      return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }
    return super.useOn(context);
  }

  @Override
  public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
    ItemStack stack = player.getItemInHand(hand);
    if (IC2.keyboard.isModeSwitchKeyDown(player)) {
      cycleMode(level, player, stack);
      return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
    return super.use(level, player, hand);
  }

  @Override
  public void appendHoverText(
      ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
    super.appendHoverText(stack, context, tooltip, flag);
    Mode mode = getMode(stack);
    tooltip.add(
        Component.translatable(
            "gravisuite.message.mode", Component.translatable("gravisuite.mode." + mode.getId())));
    Component modeKey = KeyboardClient.modeSwitchKey.getKey().getDisplayName();
    Component useKey = Minecraft.getInstance().options.keyUse.getKey().getDisplayName();
    tooltip.add(Component.translatable("item.ic2.tooltip.mode.switch", modeKey, useKey));
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
