package ic2.api.block;

import com.mojang.serialization.MapCodec;
import ic2.api.tile.IWrenchAble;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;

/**
 * Add-on friendly block base combining {@link AddonEnergyBlock} with full {@link IWrenchAble}
 * support and horizontal facing state.
 *
 * <p>Machine, generator, and battery add-on blocks extending this class automatically support:
 *
 * <ul>
 *   <li>Horizontal placement facing (faces player when placed).
 *   <li>Rotation using IC2 and electric wrenches via {@link #setFacing}.
 *   <li>Safe dismantling via {@link #getWrenchDrops}.
 *   <li>Standard server tick dispatch for {@link IAddonServerTickable}.
 * </ul>
 */
public class AddonWrenchableBlock extends AddonEnergyBlock implements IWrenchAble {
  public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
  private final MapCodec<AddonWrenchableBlock> codec;

  public AddonWrenchableBlock(
      BlockBehaviour.Properties properties,
      BiFunction<BlockPos, BlockState, ? extends BlockEntity> factory) {
    super(properties, factory);
    this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    this.codec =
        simpleCodec(blockProperties -> new AddonWrenchableBlock(blockProperties, this.factory));
  }

  @Override
  protected MapCodec<? extends AddonEnergyBlock> codec() {
    return this.codec;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(FACING);
  }

  @Nullable @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    Direction horizontal = context.getHorizontalDirection().getOpposite();
    return this.defaultBlockState().setValue(FACING, horizontal);
  }

  @Override
  public Direction getFacing(Level world, BlockPos pos) {
    BlockState state = world.getBlockState(pos);
    return state.hasProperty(FACING) ? state.getValue(FACING) : Direction.NORTH;
  }

  @Override
  public boolean setFacing(Level world, BlockPos pos, Direction newFacing, Player player) {
    if (newFacing.getAxis().isVertical()) {
      return false;
    }
    BlockState current = world.getBlockState(pos);
    if (!current.hasProperty(FACING) || current.getValue(FACING) == newFacing) {
      return false;
    }
    world.setBlockAndUpdate(pos, current.setValue(FACING, newFacing));
    return true;
  }

  @Override
  public boolean wrenchCanRemove(Level world, BlockPos pos, Player player) {
    return true;
  }

  @Override
  public List<ItemStack> getWrenchDrops(
      Level world, BlockPos pos, BlockState state, BlockEntity te, Player player, int fortune) {
    return Collections.singletonList(new ItemStack(this.asItem()));
  }
}
