package ic2.core.util;

import ic2.core.fluid.FluidHandler;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class PumpUtil {
  private static final int MAX_TRACE_STEPS = 64;
  private static final int MAX_AIR_BRIDGE = 64;
  private static final int LOCAL_AIR_BRIDGE = 8;
  private static final int FALLBACK_RADIUS = 2;

  private static int moveUp(Level world, MutableBlockPos pos) {
    pos.set(pos.getX(), pos.getY() + 1, pos.getZ());
    int newDecay = getFlowDecay(world, pos);
    if (newDecay >= 0) {
      return newDecay;
    }

    pos.set(pos.getX() + 1, pos.getY(), pos.getZ());
    newDecay = getFlowDecay(world, pos);
    if (newDecay >= 0) {
      return newDecay;
    }

    pos.set(pos.getX() - 2, pos.getY(), pos.getZ());
    newDecay = getFlowDecay(world, pos);
    if (newDecay >= 0) {
      return newDecay;
    }

    pos.set(pos.getX() + 1, pos.getY(), pos.getZ() + 1);
    newDecay = getFlowDecay(world, pos);
    if (newDecay >= 0) {
      return newDecay;
    }

    pos.set(pos.getX(), pos.getY(), pos.getZ() - 2);
    newDecay = getFlowDecay(world, pos);
    if (newDecay >= 0) {
      return newDecay;
    }

    pos.set(pos.getX(), pos.getY() - 1, pos.getZ() + 1);
    return -1;
  }

  private static int moveSideways(Level world, MutableBlockPos pos, int decay) {
    pos.set(pos.getX() - 1, pos.getY(), pos.getZ());
    int newDecay = getFlowDecay(world, pos);
    if (newDecay >= 0 && newDecay < decay) {
      return newDecay;
    }

    pos.set(pos.getX() + 1, pos.getY(), pos.getZ() + 1);
    newDecay = getFlowDecay(world, pos);
    if (newDecay >= 0 && newDecay < decay) {
      return newDecay;
    }

    pos.set(pos.getX(), pos.getY(), pos.getZ() - 2);
    newDecay = getFlowDecay(world, pos);
    if (newDecay >= 0 && newDecay < decay) {
      return newDecay;
    }

    pos.set(pos.getX() + 1, pos.getY(), pos.getZ() + 1);
    newDecay = getFlowDecay(world, pos);
    if (newDecay >= 0 && newDecay < decay) {
      return newDecay;
    }

    pos.set(pos.getX() - 1, pos.getY(), pos.getZ());
    return -1;
  }

  public static BlockPos searchFluidSource(Level world, BlockPos startPos) {
    return searchFluidSource(world, startPos, false);
  }

  public static BlockPos searchFluidSource(Level world, BlockPos startPos, boolean simulate) {
    // Check the immediate work area for renewable sources before tracing any flowing liquid.
    // This gives infinite pools priority even when a stream is closer to the pump intake.
    BlockPos infiniteSource = findInfiniteSourceNear(world, startPos, FALLBACK_RADIUS);
    if (infiniteSource != null) {
      return infiniteSource;
    }

    MutableBlockPos pos = new MutableBlockPos();
    pos.set(startPos.getX(), startPos.getY(), startPos.getZ());

    if (getFlowDecay(world, pos) < 0) {
      BlockPos fluidEntry = findNearestHorizontalFluid(world, startPos, MAX_AIR_BRIDGE);
      if (fluidEntry == null) {
        return scanAreaForSource(world, startPos, simulate);
      }

      pos.set(fluidEntry.getX(), fluidEntry.getY(), fluidEntry.getZ());
    }

    int decay = getFlowDecay(world, pos);

    for (int i = 0; i < MAX_TRACE_STEPS; i++) {
      int newDecay = moveUp(world, pos);
      if (newDecay < 0) {
        newDecay = moveSideways(world, pos, decay);
        if (newDecay < 0) {
          BlockPos bridge = findNearestHorizontalFluid(world, pos, LOCAL_AIR_BRIDGE);
          if (bridge != null) {
            pos.set(bridge.getX(), bridge.getY(), bridge.getZ());
            decay = getFlowDecay(world, pos);
            continue;
          }

          break;
        }
      }

      decay = newDecay;
    }

    Set<BlockPos> visited = new HashSet<>(64);
    int i = 0;

    while (i < MAX_TRACE_STEPS) {
      label85:
      {
        label103:
        {
          visited.add(new BlockPos(pos));
          pos.set(pos.getX() - 1, pos.getY(), pos.getZ());
          if (!visited.contains(pos)) {
            int newDecay = getFlowDecay(world, pos);
            if (newDecay >= 0) {
              if (newDecay == 0 && isInfiniteWaterSource(world, pos)) {
                return pos.immutable();
              }
              break label103;
            }
          }

          pos.set(pos.getX() + 1, pos.getY(), pos.getZ() + 1);
          if (!visited.contains(pos)) {
            int newDecay = getFlowDecay(world, pos);
            if (newDecay >= 0) {
              if (newDecay == 0 && isInfiniteWaterSource(world, pos)) {
                return pos.immutable();
              }
              break label103;
            }
          }

          pos.set(pos.getX(), pos.getY(), pos.getZ() - 2);
          if (!visited.contains(pos)) {
            int newDecay = getFlowDecay(world, pos);
            if (newDecay >= 0) {
              if (newDecay == 0 && isInfiniteWaterSource(world, pos)) {
                return pos.immutable();
              }
              break label103;
            }
          }

          pos.set(pos.getX() + 1, pos.getY(), pos.getZ() + 1);
          if (visited.contains(pos)) {
            break label85;
          }

          int newDecay = getFlowDecay(world, pos);
          if (newDecay < 0) {
            break label85;
          }

          if (newDecay == 0 && isInfiniteWaterSource(world, pos)) {
            return pos.immutable();
          }
        }

        i++;
        continue;
      }

      BlockPos bridge = findNearestHorizontalFluid(world, pos, LOCAL_AIR_BRIDGE);
      if (bridge != null) {
        pos.set(bridge.getX(), bridge.getY(), bridge.getZ());
        i++;
        continue;
      }

      pos.set(pos.getX() - 1, pos.getY(), pos.getZ());
      break;
    }

    BlockPos result = scanAreaForSource(world, pos, simulate);
    if (result != null) {
      return result;
    }

    if (!pos.equals(startPos)) {
      return scanAreaForSource(world, startPos, simulate);
    }

    return null;
  }

  private static BlockPos findNearestHorizontalFluid(Level world, BlockPos origin, int maxDist) {
    if (getFlowDecay(world, origin) >= 0) {
      return origin;
    }

    Queue<BlockPos> queue = new ArrayDeque<>();
    Set<BlockPos> visited = new HashSet<>();
    queue.add(origin);
    visited.add(origin);
    int y = origin.getY();

    while (!queue.isEmpty()) {
      BlockPos current = queue.poll();
      int dx = Math.abs(current.getX() - origin.getX());
      int dz = Math.abs(current.getZ() - origin.getZ());
      if (dx + dz > maxDist) {
        continue;
      }

      for (Direction dir : Util.HORIZONTAL_DIRS) {
        BlockPos next = current.relative(dir);
        if (next.getY() != y || !visited.add(next)) {
          continue;
        }

        if (getFlowDecay(world, next) >= 0) {
          return next;
        }

        if (world.getBlockState(next).isAir()) {
          queue.add(next);
        }
      }
    }

    return null;
  }

  private static BlockPos scanAreaForSource(Level world, BlockPos center, boolean simulate) {
    MutableBlockPos cPos = new MutableBlockPos();

    // Renewable sources always win over flowing/finite water. This prevents the pump from
    // consuming nearby waterlogged decoration when a proper infinite source is available.
    for (int ix = -FALLBACK_RADIUS; ix <= FALLBACK_RADIUS; ix++) {
      for (int iz = -FALLBACK_RADIUS; iz <= FALLBACK_RADIUS; iz++) {
        cPos.set(center.getX() + ix, center.getY(), center.getZ() + iz);
        if (isInfiniteWaterSource(world, cPos)) {
          return cPos.immutable();
        }
      }
    }

    // Preserve the old finite-fluid fallback for ordinary flowing liquid blocks only.
    for (int ix = -FALLBACK_RADIUS; ix <= FALLBACK_RADIUS; ix++) {
      for (int iz = -FALLBACK_RADIUS; iz <= FALLBACK_RADIUS; iz++) {
        cPos.set(center.getX() + ix, center.getY(), center.getZ() + iz);
        BlockState state = world.getBlockState(cPos);
        int decay = getFlowDecay(state, world, cPos);
        if (decay < 0 || isWaterlogged(state)) {
          continue;
        }
        if (decay == 0) {
          return cPos.immutable();
        }
        if (!simulate) {
          applyFallbackFluidStep(world, cPos, state);
        }
      }
    }
    return null;
  }

  private static BlockPos findInfiniteSourceNear(Level world, BlockPos center, int radius) {
    MutableBlockPos candidate = new MutableBlockPos();
    for (int y = -1; y <= 1; y++) {
      for (int x = -radius; x <= radius; x++) {
        for (int z = -radius; z <= radius; z++) {
          candidate.set(center.getX() + x, center.getY() + y, center.getZ() + z);
          if (isInfiniteWaterSource(world, candidate)) {
            return candidate.immutable();
          }
        }
      }
    }
    return null;
  }

  /**
   * A renewable source is a source-fluid block with at least two horizontally adjacent source
   * blocks and a solid floor. Waterlogged blocks participate in the same test. A lone waterlogged
   * slab/stair is deliberately not considered an infinite source.
   */
  public static boolean isInfiniteWaterSource(Level world, BlockPos pos) {
    BlockState state = world.getBlockState(pos);
    if (!isWaterSource(state, world, pos)) {
      return false;
    }
    if (!world.getBlockState(pos.below()).isFaceSturdy(world, pos.below(), Direction.UP)) {
      return false;
    }

    int adjacentSources = 0;
    for (Direction direction : Util.HORIZONTAL_DIRS) {
      BlockPos neighbor = pos.relative(direction);
      if (isWaterSource(world.getBlockState(neighbor), world, neighbor)) {
        adjacentSources++;
        if (adjacentSources >= 2) {
          return true;
        }
      }
    }
    return false;
  }

  private static boolean isWaterSource(BlockState state, Level world, BlockPos pos) {
    if (isWaterlogged(state)) {
      return true;
    }
    if (state.getBlock() instanceof LiquidBlock
        && state.getFluidState().isSource()
        && state.getFluidState().is(net.minecraft.world.level.material.Fluids.WATER)) {
      return true;
    }
    return FluidHandler.getWorldFluidLevel(state, world, pos) == 0
        && state.getFluidState().is(net.minecraft.world.level.material.Fluids.WATER);
  }

  private static boolean isWaterlogged(BlockState state) {
    return state.hasProperty(BlockStateProperties.WATERLOGGED)
        && state.getValue(BlockStateProperties.WATERLOGGED);
  }

  private static void applyFallbackFluidStep(Level world, BlockPos pos, BlockState state) {
    Block block = state.getBlock();
    if (block instanceof LiquidBlock) {
      int level = state.getValue(LiquidBlock.LEVEL);
      if (level == 0) {
        return;
      }

      if (level < 15) {
        world.setBlock(pos, state.setValue(LiquidBlock.LEVEL, level + 1), 3);
      } else {
        world.removeBlock(pos, false);
      }
    } else if (!isWaterlogged(state)) {
      world.removeBlock(pos, false);
    }
  }

  protected static int getFlowDecay(Level world, BlockPos pos) {
    BlockState state = world.getBlockState(pos);
    return getFlowDecay(state, world, pos);
  }

  protected static int getFlowDecay(BlockState state, Level world, BlockPos pos) {
    Block block = state.getBlock();

    // Waterlogged slabs, stairs, fences, and other SimpleWaterloggedBlock implementations hold
    // a full bucket's worth of water even though they are not LiquidBlock instances. Treat them
    // as a source so the pump's normal source search can discover them.
    if (block instanceof SimpleWaterloggedBlock
        && state.hasProperty(BlockStateProperties.WATERLOGGED)
        && state.getValue(BlockStateProperties.WATERLOGGED)) {
      return 0;
    }

    int level = FluidHandler.getWorldFluidLevel(state, world, pos);
    if (level >= 0) {
      return level;
    } else {
      return block instanceof LiquidBlock ? (Integer) state.getValue(LiquidBlock.LEVEL) : -1;
    }
  }

  protected static boolean isExistInArray(int x, int y, int z, int[][] xyz, int end_i) {
    for (int i = 0; i <= end_i; i++) {
      if (xyz[i][0] == x && xyz[i][1] == y && xyz[i][2] == z) {
        return true;
      }
    }

    return false;
  }
}
