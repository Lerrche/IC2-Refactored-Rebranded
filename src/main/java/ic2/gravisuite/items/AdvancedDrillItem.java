package ic2.gravisuite.items;

import ic2.api.item.AddonElectricTool;
import ic2.api.item.BlockBreakableItem;
import ic2.core.IC2;
import ic2.core.util.KeyboardClient;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Advanced Diamond Drill from Gravitation Suite.
 *
 * <p>Supports Normal, Low Power, Fine, and Big Holes (3x3 area) mining modes.
 */
public class AdvancedDrillItem extends AddonElectricTool implements BlockBreakableItem {
  public static final double MAX_CHARGE = 3_000_000.0;
  public static final double TRANSFER_LIMIT = 5_000.0;
  public static final int TIER = 2;

  public enum Mode {
    NORMAL("normal", 35.0F, 160.0),
    LOW_POWER("low_power", 16.0F, 80.0),
    FINE("fine", 10.0F, 50.0),
    BIG_HOLES("big_holes", 25.0F, 300.0);

    private final String id;
    private final float speed;
    private final double energyCost;

    Mode(String id, float speed, double energyCost) {
      this.id = id;
      this.speed = speed;
      this.energyCost = energyCost;
    }

    public String getId() {
      return id;
    }

    public float getSpeed() {
      return speed;
    }

    public double getEnergyCost() {
      return energyCost;
    }

    public Mode next() {
      Mode[] values = values();
      return values[(ordinal() + 1) % values.length];
    }
  }

  public AdvancedDrillItem() {
    super(
        new Item.Properties().stacksTo(1).rarity(Rarity.RARE),
        MAX_CHARGE,
        TRANSFER_LIMIT,
        TIER,
        160.0);
  }

  public static Mode getMode(ItemStack stack) {
    CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
    if (custom != null && custom.copyTag().contains("DrillMode")) {
      int ordinal = custom.copyTag().getInt("DrillMode");
      Mode[] values = Mode.values();
      if (ordinal >= 0 && ordinal < values.length) {
        return values[ordinal];
      }
    }
    return Mode.NORMAL;
  }

  public static void setMode(ItemStack stack, Mode mode) {
    CustomData.update(
        DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt("DrillMode", mode.ordinal()));
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
    Mode mode = getMode(stack);
    if (hasEnergy(stack, mode.getEnergyCost())) {
      if (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
        return mode.getSpeed();
      }
      return 1.0F;
    }
    return 1.0F;
  }

  @Override
  public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
    Mode mode = getMode(stack);
    return hasEnergy(stack, mode.getEnergyCost())
        && (state.is(BlockTags.MINEABLE_WITH_PICKAXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL));
  }

  @Override
  public boolean mineBlock(
      ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity entity) {
    if (level.isClientSide) return true;
    Mode mode = getMode(stack);
    useEnergy(stack, mode.getEnergyCost(), entity);

    // Big Holes 3x3 area mining
    if (mode == Mode.BIG_HOLES && entity instanceof Player player) {
      Direction facing = Direction.orderedByNearest(player)[0].getOpposite();
      for (int x = -1; x <= 1; x++) {
        for (int y = -1; y <= 1; y++) {
          if (x == 0 && y == 0) continue;
          BlockPos neighborPos =
              facing.getAxis() == Direction.Axis.Y
                  ? pos.offset(x, 0, y)
                  : (facing.getAxis() == Direction.Axis.Z
                      ? pos.offset(x, y, 0)
                      : pos.offset(0, y, x));

          BlockState neighborState = level.getBlockState(neighborPos);
          if ((neighborState.is(BlockTags.MINEABLE_WITH_PICKAXE)
                  || neighborState.is(BlockTags.MINEABLE_WITH_SHOVEL))
              && hasEnergy(stack, mode.getEnergyCost())) {
            level.destroyBlock(neighborPos, true, player);
            useEnergy(stack, mode.getEnergyCost(), player);
          }
        }
      }
    }
    return true;
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
