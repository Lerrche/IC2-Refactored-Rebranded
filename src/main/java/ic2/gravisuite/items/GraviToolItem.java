package ic2.gravisuite.items;

import ic2.api.item.AddonElectricTool;
import ic2.api.item.BlockBreakableItem;
import ic2.api.tile.IWrenchAble;
import ic2.core.IC2;
import ic2.core.item.PriorityUsableItem;
import ic2.core.item.tool.ItemToolWrench;
import ic2.core.item.tool.ItemTreetap;
import ic2.core.ref.Ic2Blocks;
import ic2.core.util.KeyboardClient;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

/**
 * GraviTool from Gravitation Suite.
 *
 * <p>A 4-in-1 multi-functional electric tool:
 *
 * <ul>
 *   <li><b>Wrench:</b> Rotates and dismantles IC2 machines losslessly.
 *   <li><b>TreeTap:</b> Extracts sticky resin from rubber trees.
 *   <li><b>Screwdriver:</b> Rotates redstone and directional blocks.
 *   <li><b>Hoe:</b> Tills soil into farmland.
 * </ul>
 */
public class GraviToolItem extends AddonElectricTool
    implements PriorityUsableItem, BlockBreakableItem {
  public static final double MAX_CHARGE = 300_000.0;
  public static final double TRANSFER_LIMIT = 5_000.0;
  public static final int TIER = 2;

  public enum Mode {
    HOE("hoe", 50.0),
    TREETAP("treetap", 50.0),
    WRENCH("wrench", 100.0),
    SCREWDRIVER("screwdriver", 50.0);

    private final String id;
    private final double cost;

    Mode(String id, double cost) {
      this.id = id;
      this.cost = cost;
    }

    public String getId() {
      return id;
    }

    public double getCost() {
      return cost;
    }

    public Mode next() {
      Mode[] values = values();
      return values[(ordinal() + 1) % values.length];
    }
  }

  public GraviToolItem() {
    super(
        new Item.Properties().stacksTo(1).rarity(Rarity.RARE),
        MAX_CHARGE,
        TRANSFER_LIMIT,
        TIER,
        100.0);
  }

  public static Mode getMode(ItemStack stack) {
    CustomData custom = stack.get(DataComponents.CUSTOM_DATA);
    if (custom != null && custom.copyTag().contains("ToolMode")) {
      int ordinal = custom.copyTag().getInt("ToolMode");
      Mode[] values = Mode.values();
      if (ordinal >= 0 && ordinal < values.length) {
        return values[ordinal];
      }
    }
    return Mode.WRENCH;
  }

  public static void setMode(ItemStack stack, Mode mode) {
    CustomData.update(
        DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt("ToolMode", mode.ordinal()));
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
  public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
    if (getMode(stack) == Mode.WRENCH && hasEnergy(stack, Mode.WRENCH.getCost())) {
      return ItemToolWrench.isWrenchTarget(state);
    }
    return super.isCorrectToolForDrops(stack, state);
  }

  @Override
  public float getDestroySpeed(ItemStack stack, BlockState state) {
    if (getMode(stack) == Mode.WRENCH && hasEnergy(stack, Mode.WRENCH.getCost())) {
      if (ItemToolWrench.isWrenchTarget(state)) {
        return ItemToolWrench.WRENCH_DESTROY_SPEED;
      }
    }
    return super.getDestroySpeed(stack, state);
  }

  @Override
  public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
    if (getMode(stack) == Mode.WRENCH) {
      Player player = context.getPlayer();
      if (player == null) return InteractionResult.PASS;
      if (IC2.keyboard.isModeSwitchKeyDown(player)) {
        return InteractionResult.PASS;
      }
      if (hasEnergy(stack, Mode.WRENCH.getCost())) {
        InteractionResult res = ItemToolWrench.trySetFacingFromHit(context, player);
        if (res.consumesAction()) {
          useEnergy(stack, Mode.WRENCH.getCost(), player);
          return res;
        }
      }
    }
    return InteractionResult.PASS;
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();
    Player player = context.getPlayer();
    ItemStack stack = context.getItemInHand();
    Mode mode = getMode(stack);

    if (player != null && IC2.keyboard.isModeSwitchKeyDown(player)) {
      cycleMode(level, player, stack);
      return InteractionResult.sidedSuccess(level.isClientSide);
    }

    if (!hasEnergy(stack, mode.getCost())) {
      if (player != null && !level.isClientSide) {
        player.displayClientMessage(Component.translatable("gravisuite.message.no_energy"), false);
      }
      return InteractionResult.FAIL;
    }

    // 1. Wrench Mode
    if (mode == Mode.WRENCH && player != null) {
      BlockState state = level.getBlockState(pos);
      if (state.getBlock() instanceof IWrenchAble) {
        if (player.isShiftKeyDown()) {
          // Dismantle machine
          if (!level.isClientSide) {
            if (ItemToolWrench.tryRemoveWithWrench(level, player, pos, state)) {
              useEnergy(stack, mode.getCost(), player);
            }
          }
          return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
          // Rotate machine facing
          InteractionResult res = ItemToolWrench.trySetFacingFromHit(context, player);
          if (res.consumesAction()) {
            useEnergy(stack, mode.getCost(), player);
            return InteractionResult.sidedSuccess(level.isClientSide);
          }
        }
      }

      int res = ItemToolWrench.onWrenchUse(player, context, player.isShiftKeyDown());
      if (res >= 0) {
        useEnergy(stack, mode.getCost(), player);
        return InteractionResult.sidedSuccess(level.isClientSide);
      }
    }

    // 2. TreeTap Mode
    else if (mode == Mode.TREETAP) {
      BlockState state = level.getBlockState(pos);
      if (state.getBlock() == Ic2Blocks.RUBBER_LOG) {
        if (ItemTreetap.attemptExtract(
            player, level, pos, context.getClickedFace(), state, null, true)) {
          useEnergy(stack, mode.getCost(), player);
          return InteractionResult.sidedSuccess(level.isClientSide);
        }
      }
    }

    // 3. Screwdriver Mode
    else if (mode == Mode.SCREWDRIVER) {
      BlockState state = level.getBlockState(pos);
      if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
        Direction current = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        Direction next =
            player != null && player.isShiftKeyDown()
                ? current.getCounterClockWise()
                : current.getClockWise();
        BlockState rotated = state.setValue(BlockStateProperties.HORIZONTAL_FACING, next);
        if (!level.isClientSide) {
          level.setBlock(pos, rotated, 11);
          level.playSound(
              null, pos, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 0.7F, 1.5F);
          useEnergy(stack, mode.getCost(), player);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
      } else if (state.hasProperty(BlockStateProperties.FACING)) {
        Direction current = state.getValue(BlockStateProperties.FACING);
        Direction[] dirs = Direction.values();
        Direction next = dirs[(current.ordinal() + 1) % dirs.length];
        BlockState rotated = state.setValue(BlockStateProperties.FACING, next);
        if (!level.isClientSide) {
          level.setBlock(pos, rotated, 11);
          level.playSound(
              null, pos, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 0.7F, 1.5F);
          useEnergy(stack, mode.getCost(), player);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
      } else {
        BlockState rotated = state.rotate(level, pos, Rotation.CLOCKWISE_90);
        if (rotated != state) {
          if (!level.isClientSide) {
            level.setBlock(pos, rotated, 11);
            level.playSound(
                null, pos, SoundEvents.DISPENSER_DISPENSE, SoundSource.BLOCKS, 0.7F, 1.5F);
            useEnergy(stack, mode.getCost(), player);
          }
          return InteractionResult.sidedSuccess(level.isClientSide);
        }
      }
    }

    // 4. Hoe Mode
    else if (mode == Mode.HOE) {
      BlockState state = level.getBlockState(pos);
      if (state.is(Blocks.GRASS_BLOCK)
          || state.is(Blocks.DIRT_PATH)
          || state.is(Blocks.DIRT)
          || state.is(Blocks.ROOTED_DIRT)) {
        if (!level.isClientSide) {
          level.setBlock(pos, Blocks.FARMLAND.defaultBlockState(), 11);
          level.playSound(null, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
          useEnergy(stack, mode.getCost(), player);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
      }
    }

    return super.useOn(context);
  }

  @Override
  public boolean beforeBlockBreak(
      Level world,
      Player player,
      BlockPos pos,
      BlockState state,
      @Nullable BlockEntity blockEntity) {
    if (getMode(player.getMainHandItem()) == Mode.WRENCH
        && state.getBlock() instanceof IWrenchAble) {
      ItemStack stack = player.getMainHandItem();
      if (hasEnergy(stack, Mode.WRENCH.getCost())) {
        if (ItemToolWrench.tryRemoveWithWrench(world, player, pos, state)) {
          useEnergy(stack, Mode.WRENCH.getCost(), player);
        }
      }
      return false;
    }
    return true;
  }

  @Override
  public void afterBlockBreak(
      Level world,
      Player player,
      BlockPos pos,
      BlockState state,
      @Nullable BlockEntity blockEntity) {}

  @Override
  public InteractionResult onBlockStartBreak(
      Player player, Level world, InteractionHand hand, BlockPos pos, Direction direction) {
    return InteractionResult.PASS;
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
}
