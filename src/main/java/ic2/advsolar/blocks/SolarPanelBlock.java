package ic2.advsolar.blocks;

import ic2.api.block.AddonEnergyBlock;
import ic2.api.tile.IWrenchAble;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class SolarPanelBlock extends AddonEnergyBlock implements IWrenchAble {
  public SolarPanelBlock(
      BlockBehaviour.Properties properties,
      BiFunction<BlockPos, BlockState, ? extends BlockEntity> factory) {
    super(properties, factory);
  }

  @Override
  protected InteractionResult useWithoutItem(
      BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
    if (!level.isClientSide) {
      BlockEntity be = level.getBlockEntity(pos);
      if (be instanceof MenuProvider menuProvider) {
        player.openMenu(menuProvider, pos);
      }
    }
    return InteractionResult.sidedSuccess(level.isClientSide);
  }

  @Override
  protected ItemInteractionResult useItemOn(
      ItemStack stack,
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hitResult) {
    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
  }

  @Override
  public void onRemove(
      BlockState state, Level level, BlockPos pos, BlockState newState, boolean moved) {
    if (!state.is(newState.getBlock()) && !moved) {
      BlockEntity be = level.getBlockEntity(pos);
      if (be instanceof Container container) {
        Containers.dropContents(level, pos, container);
        container.clearContent();
      }
    }
    super.onRemove(state, level, pos, newState, moved);
  }

  @Override
  public Direction getFacing(Level level, BlockPos pos) {
    return Direction.UP;
  }

  @Override
  public boolean setFacing(Level level, BlockPos pos, Direction newDirection, Player player) {
    return false;
  }

  @Override
  public boolean wrenchCanRemove(Level level, BlockPos pos, Player player) {
    return true;
  }

  @Override
  public List<ItemStack> getWrenchDrops(
      Level level, BlockPos pos, BlockState state, BlockEntity te, Player player, int fortune) {
    List<ItemStack> drops = new ArrayList<>();
    drops.add(new ItemStack(this.asItem()));
    if (te instanceof Container container) {
      for (int i = 0; i < container.getContainerSize(); i++) {
        ItemStack item = container.getItem(i);
        if (!item.isEmpty()) {
          drops.add(item.copy());
          container.setItem(i, ItemStack.EMPTY);
        }
      }
    }
    return drops;
  }
}
