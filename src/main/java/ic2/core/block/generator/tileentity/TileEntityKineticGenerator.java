package ic2.core.block.generator.tileentity;

import ic2.api.energy.tile.IKineticSource;
import ic2.core.init.IC2Config;
import ic2.core.profile.NotClassic;
import ic2.core.ref.Ic2BlockEntities;
import ic2.integration.create.CreateKineticCompat;
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
  protected void onUnloaded() {
    if (this.createKineticSource != null && ModList.get().isLoaded("create")) {
      CreateKineticCompat.clearConsumerStress(this.worldPosition, this.createKineticSource);
      this.createKineticSource = null;
    }
    super.onUnloaded();
  }

  @Override
  protected void updateEntityServer() {
    super.updateEntityServer();
    if (this.createKineticSource != null && ModList.get().isLoaded("create")) {
      if (this.createKineticSource.isRemoved()) {
        this.createKineticSource = null;
      } else {
        CreateKineticCompat.updateConsumerStress(this.worldPosition, this.createKineticSource);
      }
    }
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
    if (this.createKineticSource != null && ModList.get().isLoaded("create")) {
      CreateKineticCompat.clearConsumerStress(this.worldPosition, this.createKineticSource);
    }
    BlockEntity te = this.level.getBlockEntity(this.worldPosition.relative(this.getFacing()));
    this.createKineticSource = null;
    if (te instanceof IKineticSource kineticSource && !te.isRemoved()) {
      this.source = kineticSource;
    } else {
      this.source = null;
      if (te != null
          && !te.isRemoved()
          && ModList.get().isLoaded("create")
          && CreateKineticCompat.isKineticBlockEntity(te)) {
        this.createKineticSource = te;
        CreateKineticCompat.updateConsumerStress(this.worldPosition, te);
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
    } else if (this.createKineticSource != null && ModList.get().isLoaded("create")) {
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
}
