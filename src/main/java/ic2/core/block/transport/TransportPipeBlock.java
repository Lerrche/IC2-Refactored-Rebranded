package ic2.core.block.transport;

import com.mojang.serialization.MapCodec;
import ic2.api.tile.IWrenchAble;
import ic2.core.item.tool.ItemToolWrench;
import ic2.core.ref.Ic2ItemTags;
import ic2.core.ref.Ic2SoundEvents;
import ic2.core.util.CauldronUtil;
import ic2.core.util.RotationUtil;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TransportPipeBlock extends PipeBlock
    implements EntityBlock, SimpleWaterloggedBlock, IWrenchAble {
  public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
  public static final BooleanProperty UP = BlockStateProperties.UP;
  public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
  public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
  public static final BooleanProperty EAST = BlockStateProperties.EAST;
  public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
  public static final BooleanProperty WEST = BlockStateProperties.WEST;

  private static final MapCodec<TransportPipeBlock> CODEC =
      MapCodec.unit((TransportPipeBlock) null);

  public final PipeType type;
  public final PipeSize size;

  public TransportPipeBlock(PipeType type, PipeSize size, Properties properties) {
    super(size.getApothem(), properties);
    this.type = type;
    this.size = size;

    this.registerDefaultState(
        this.stateDefinition
            .any()
            .setValue(UP, false)
            .setValue(DOWN, false)
            .setValue(NORTH, false)
            .setValue(EAST, false)
            .setValue(SOUTH, false)
            .setValue(WEST, false)
            .setValue(WATERLOGGED, false));
  }

  @Override
  public @NotNull MapCodec<? extends PipeBlock> codec() {
    return CODEC;
  }

  @Override
  protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(UP, DOWN, NORTH, EAST, SOUTH, WEST, WATERLOGGED);
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext context) {
    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();
    FluidState fluidState = level.getFluidState(pos);
    BlockState state = this.defaultBlockState();
    if (fluidState.getType() == Fluids.WATER) {
      state = state.setValue(WATERLOGGED, true);
    }

    for (Direction dir : Direction.values()) {
      BlockPos neighborPos = pos.relative(dir);
      BlockState neighborState = level.getBlockState(neighborPos);
      boolean connected =
          neighborState.getBlock() instanceof TransportPipeBlock
              || CauldronUtil.isCauldron(neighborState)
              || level.getBlockEntity(neighborPos) != null;
      state = state.setValue(PROPERTY_BY_DIRECTION.get(dir), connected);
    }
    return state;
  }

  @Override
  public @NotNull BlockState updateShape(
      @NotNull BlockState state,
      @NotNull Direction direction,
      @NotNull BlockState neighborState,
      @NotNull LevelAccessor world,
      @NotNull BlockPos pos,
      @NotNull BlockPos neighborPos) {
    if (state.getValue(WATERLOGGED)) {
      world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
    }

    BlockEntity be = world.getBlockEntity(pos);
    boolean connected = false;
    if (be instanceof TransportPipeBlockEntity pipe) {
      connected = pipe.isFaceConnected(direction);
    } else {
      connected =
          neighborState.getBlock() instanceof TransportPipeBlock
              || CauldronUtil.isCauldron(neighborState)
              || world.getBlockEntity(neighborPos) != null;
    }
    return state.setValue(PROPERTY_BY_DIRECTION.get(direction), connected);
  }

  @Override
  public @NotNull FluidState getFluidState(BlockState state) {
    return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
  }

  @Override
  protected @NotNull ItemInteractionResult useItemOn(
      ItemStack stack,
      BlockState state,
      Level level,
      BlockPos pos,
      Player player,
      InteractionHand hand,
      BlockHitResult hit) {
    boolean isWrench =
        stack.is(Ic2ItemTags.WRENCHES)
            || stack.getItem() instanceof ItemToolWrench
            || stack.is(ItemTags.create(ResourceLocation.parse("c:tools/wrench")));

    if (isWrench) {
      if (player.isShiftKeyDown()) {
        // Dismantle pipe with wrench
        if (!level.isClientSide) {
          BlockEntity be = level.getBlockEntity(pos);
          if (be instanceof TransportPipeBlockEntity pipe) {
            pipe.dropInventoryAndCovers();
          }
          popResource(level, pos, new ItemStack(this));
          level.removeBlock(pos, false);
          player.playSound(Ic2SoundEvents.ITEM_WRENCH_USE, 1.0F, 1.0F);
        }
        return ItemInteractionResult.SUCCESS;
      } else {
        // Toggle face or remove cover on face
        Direction face = RotationUtil.rotateByRay(hit);
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TransportPipeBlockEntity pipe) {
          if (!level.isClientSide) {
            if (pipe.hasCover(face)) {
              ItemStack removed = pipe.removeCover(face);
              if (!removed.isEmpty()) {
                if (!player.getInventory().add(removed)) {
                  popResource(level, pos, removed);
                }
                player.playSound(Ic2SoundEvents.ITEM_WRENCH_USE, 1.0F, 1.0F);
                return ItemInteractionResult.SUCCESS;
              }
            }
            pipe.flipConnection(face);
            player.playSound(Ic2SoundEvents.ITEM_WRENCH_USE, 1.0F, 1.0F);
          }
          return ItemInteractionResult.SUCCESS;
        }
      }
    }

    if (stack.getItem() instanceof ItemPumpCover) {
      Direction face = RotationUtil.rotateByRay(hit);
      BlockEntity be = level.getBlockEntity(pos);
      if (be instanceof TransportPipeBlockEntity pipe) {
        if (!pipe.hasCover(face)) {
          if (!level.isClientSide) {
            pipe.installCover(face, stack.copyWithCount(1));
            if (!player.getAbilities().instabuild) {
              stack.shrink(1);
            }
            level.playSound(null, pos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.5F, 1.5F);
          }
          return ItemInteractionResult.SUCCESS;
        }
      }
    }

    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
  }

  @Override
  public void onRemove(
      BlockState state,
      @NotNull Level level,
      @NotNull BlockPos pos,
      BlockState newState,
      boolean isMoving) {
    if (!state.is(newState.getBlock())) {
      BlockEntity be = level.getBlockEntity(pos);
      if (be instanceof TransportPipeBlockEntity pipe) {
        pipe.dropInventoryAndCovers();
      }
      super.onRemove(state, level, pos, newState, isMoving);
    }
  }

  // --- IWrenchAble ---
  @Override
  public Direction getFacing(Level level, BlockPos pos) {
    return Direction.NORTH;
  }

  @Override
  public boolean setFacing(Level level, BlockPos pos, Direction newFacing, Player player) {
    BlockEntity be = level.getBlockEntity(pos);
    if (be instanceof TransportPipeBlockEntity pipe) {
      if (player != null && player.isShiftKeyDown()) {
        if (!level.isClientSide) {
          pipe.dropInventoryAndCovers();
          popResource(level, pos, new ItemStack(this));
          level.removeBlock(pos, false);
          player.playSound(Ic2SoundEvents.ITEM_WRENCH_USE, 1.0F, 1.0F);
        }
        return true;
      }

      if (!level.isClientSide) {
        if (pipe.hasCover(newFacing)) {
          ItemStack removed = pipe.removeCover(newFacing);
          if (!removed.isEmpty()) {
            if (player == null || !player.getInventory().add(removed)) {
              popResource(level, pos, removed);
            }
            if (player != null) {
              player.playSound(Ic2SoundEvents.ITEM_WRENCH_USE, 1.0F, 1.0F);
            }
            return true;
          }
        }
        pipe.flipConnection(newFacing);
        if (player != null) {
          player.playSound(Ic2SoundEvents.ITEM_WRENCH_USE, 1.0F, 1.0F);
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean wrenchCanRemove(Level level, BlockPos pos, Player player) {
    return true;
  }

  @Override
  public List<ItemStack> getWrenchDrops(
      Level level,
      BlockPos pos,
      BlockState state,
      BlockEntity blockEntity,
      Player player,
      int fortune) {
    List<ItemStack> drops = new ArrayList<>();
    drops.add(new ItemStack(this));
    if (blockEntity instanceof TransportPipeBlockEntity pipe) {
      for (Direction dir : Direction.values()) {
        ItemStack cover = pipe.getCover(dir);
        if (!cover.isEmpty()) {
          drops.add(cover.copy());
        }
      }
      ItemStack items = pipe.getContents();
      if (!items.isEmpty()) {
        drops.add(items.copy());
      }
    }
    return drops;
  }

  // --- EntityBlock ---
  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return new TransportPipeBlockEntity(this.type, this.size, pos, state);
  }

  @Override
  public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> blockEntityType) {
    return level.isClientSide
        ? null
        : (lvl, p, st, be) -> {
          if (be instanceof TransportPipeBlockEntity pipe) {
            pipe.serverTick(lvl, p, st);
          }
        };
  }
}
