package ic2.core.block.generator.tileentity;

import ic2.api.energy.tile.IKineticSource;
import ic2.core.init.IC2Config;
import ic2.core.profile.NotClassic;
import ic2.core.ref.Ic2BlockEntities;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;

@NotClassic
public class TileEntityKineticGenerator extends TileEntityConversionGenerator {
  private final double euPerKu =
      0.25 * (float) IC2Config.balance.energy.generator.kinetic.get().floatValue();
  protected IKineticSource source;
  private BlockEntity createKineticSource;

  public TileEntityKineticGenerator(BlockPos pos, BlockState state) {
    super(Ic2BlockEntities.KINETIC_GENERATOR, pos, state);
  }

  @Override
  protected void onLoaded() {
    super.onLoaded();
    this.updateSource();
  }

  @Override
  protected void setFacing(Level world, Direction facing) {
    super.setFacing(world, facing);
    this.updateSource();
  }

  @Override
  protected void onNeighborChange(Block neighbor, BlockPos neighborPos) {
    super.onNeighborChange(neighbor, neighborPos);
    if (this.getBlockPos().relative(this.getFacing()).equals(neighborPos)) {
      this.updateSource();
    }
  }

  protected void updateSource() {
    BlockEntity te = this.level.getBlockEntity(this.worldPosition.relative(this.getFacing()));
    this.createKineticSource = null;
    if (te instanceof IKineticSource kineticSource && !te.isRemoved()) {
      this.source = kineticSource;
    } else {
      this.source = null;
      if (te != null && !te.isRemoved() && CreateKineticCompat.isKineticBlockEntity(te)) {
        this.createKineticSource = te;
      }
    }
  }

  @Override
  protected int getEnergyAvailable() {
    if (this.source != null) {
      assert !((BlockEntity) this.source).isRemoved();
      return this.source.drawKineticEnergy(
          this.getFacing().getOpposite(),
          this.source.getConnectionBandwidth(this.getFacing().getOpposite()),
          true);
    } else if (this.createKineticSource != null) {
      return CreateKineticCompat.getKineticUnits(this.createKineticSource);
    } else {
      return 0;
    }
  }

  @Override
  protected void drawEnergyAvailable(int amount) {
    if (this.source != null) {
      assert !((BlockEntity) this.source).isRemoved();
      this.source.drawKineticEnergy(this.getFacing().getOpposite(), amount, false);
    } else if (this.createKineticSource == null) {
      assert false;
    }
  }

  @Override
  protected double getMultiplier() {
    return this.euPerKu;
  }

  /** Optional Create 6.x bridge. Reflection keeps Create genuinely optional at runtime. */
  private static final class CreateKineticCompat {
    private static final String KINETIC_BLOCK_ENTITY =
        "com.simibubi.create.content.kinetics.base.KineticBlockEntity";

    private static boolean isKineticBlockEntity(BlockEntity blockEntity) {
      if (!ModList.get().isLoaded("create")) {
        return false;
      }
      for (Class<?> type = blockEntity.getClass(); type != null; type = type.getSuperclass()) {
        if (KINETIC_BLOCK_ENTITY.equals(type.getName())) {
          return true;
        }
      }
      return false;
    }

    /**
     * Create exposes rotational speed rather than IC2's buffered KU API. Treat one RPM as one KU
     * per tick, bounded by the existing kinetic-generator input limit. The Create source is
     * continuous, so unlike an IC2 IKineticSource it has no finite KU buffer to debit here.
     */
    private static int getKineticUnits(BlockEntity source) {
      if (source == null || source.isRemoved()) {
        return 0;
      }
      try {
        Method getSpeed = source.getClass().getMethod("getSpeed");
        Object value = getSpeed.invoke(source);
        if (value instanceof Number number) {
          return Math.min(1000, Math.round(Math.abs(number.floatValue())));
        }
      } catch (ReflectiveOperationException ignored) {
        // Fall back to the speed field used by Create's KineticBlockEntity in supported releases.
      }
      for (Class<?> type = source.getClass(); type != null; type = type.getSuperclass()) {
        try {
          Field speed = type.getDeclaredField("speed");
          speed.setAccessible(true);
          return Math.min(1000, Math.round(Math.abs(speed.getFloat(source))));
        } catch (ReflectiveOperationException ignored) {
          // Continue through the class hierarchy.
        }
      }
      return 0;
    }
  }
}
