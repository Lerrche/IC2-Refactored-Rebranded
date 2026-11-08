package ic2.core.util;

import ic2.core.fluid.Ic2FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

public final class CauldronUtil {
  private CauldronUtil() {}

  public static boolean isCauldron(BlockState state) {
    return state != null && state.getBlock() instanceof AbstractCauldronBlock;
  }

  public static boolean hasDrainableFluid(BlockState state) {
    if (state == null) {
      return false;
    }
    Block block = state.getBlock();
    if (block == Blocks.LAVA_CAULDRON) {
      return true;
    }
    if (block == Blocks.WATER_CAULDRON) {
      return state.hasProperty(LayeredCauldronBlock.LEVEL)
          && state.getValue(LayeredCauldronBlock.LEVEL) > 0;
    }
    return false;
  }

  public static Fluid getFluidType(BlockState state) {
    if (state == null) {
      return Fluids.EMPTY;
    }
    Block block = state.getBlock();
    if (block == Blocks.LAVA_CAULDRON) {
      return Fluids.LAVA;
    }
    if (block == Blocks.WATER_CAULDRON) {
      return Fluids.WATER;
    }
    return Fluids.EMPTY;
  }

  public static Ic2FluidStack drainCauldronIc2(
      Level world, BlockPos pos, BlockState state, int maxMb, boolean simulate) {
    FluidStack neoStack = drainCauldronNeo(world, pos, state, maxMb, simulate);
    if (neoStack.isEmpty()) {
      return null;
    }
    return Ic2FluidStack.create(neoStack.getFluid(), neoStack.getAmount());
  }

  public static FluidStack drainCauldronNeo(
      Level world, BlockPos pos, BlockState state, int maxMb, boolean simulate) {
    if (world == null || pos == null || state == null || maxMb <= 0) {
      return FluidStack.EMPTY;
    }

    Block block = state.getBlock();

    // Lava cauldron contains exactly 1 bucket (1000 mB)
    if (block == Blocks.LAVA_CAULDRON) {
      if (maxMb >= 1000) {
        if (!simulate) {
          world.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
          world.playSound(null, pos, SoundEvents.BUCKET_FILL_LAVA, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return new FluidStack(Fluids.LAVA, 1000);
      }
      return FluidStack.EMPTY;
    }

    // Water cauldron has levels 1 to 3
    if (block == Blocks.WATER_CAULDRON) {
      if (!state.hasProperty(LayeredCauldronBlock.LEVEL)) {
        return FluidStack.EMPTY;
      }
      int level = state.getValue(LayeredCauldronBlock.LEVEL);
      if (level <= 0) {
        return FluidStack.EMPTY;
      }

      // If full level 3 and can take 1000 mB, drain all 1000 mB at once
      if (level == 3 && maxMb >= 1000) {
        if (!simulate) {
          world.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
          world.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return new FluidStack(Fluids.WATER, 1000);
      }

      // Otherwise drain 1 level (approx 333 mB)
      if (maxMb >= 333) {
        if (!simulate) {
          if (level <= 1) {
            world.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
          } else {
            world.setBlock(pos, state.setValue(LayeredCauldronBlock.LEVEL, level - 1), 3);
          }
          world.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
        return new FluidStack(Fluids.WATER, 333);
      }
    }

    return FluidStack.EMPTY;
  }
}
