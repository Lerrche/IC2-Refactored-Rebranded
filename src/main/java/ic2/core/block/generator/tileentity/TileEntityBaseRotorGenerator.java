package ic2.core.block.generator.tileentity;

import ic2.api.tile.IRotorProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class TileEntityBaseRotorGenerator extends TileEntityBaseGenerator
    implements IRotorProvider {
  private static final float rotationSpeed = 0.4F;
  private static final ResourceLocation rotorTexture =
      ResourceLocation.fromNamespaceAndPath("ic2", "textures/item/rotor/iron_rotor_model.png");
  private final int rotorDiameter;
  private float angle = 0.0F;
  // A rotor block entity is recreated when its chunk is loaded. Never let the first render
  // integrate from timestamp zero: converting the current epoch milliseconds to float loses
  // enough precision that subsequent small angle increments can stop changing the value.
  private long lastcheck = System.currentTimeMillis();

  public TileEntityBaseRotorGenerator(
      BlockEntityType<? extends TileEntityBaseRotorGenerator> type,
      BlockPos pos,
      BlockState state,
      double production,
      int tier,
      int maxStorage,
      int rotorDiameter) {
    super(type, pos, state, production, tier, maxStorage);
    this.rotorDiameter = rotorDiameter;
  }

  @Override
  public int getRotorDiameter() {
    return this.rotorDiameter;
  }

  protected abstract boolean shouldRotorRotate();

  protected float rotorSpeedFactor() {
    return 1.0F;
  }

  @Override
  public float getAngle() {
    long now = System.currentTimeMillis();
    // Rendering may be skipped while the player is far away or the game is paused. Clamp the
    // catch-up interval so re-entering render distance cannot cause a large angle jump.
    long elapsedMillis = Math.max(0L, Math.min(now - this.lastcheck, 1000L));
    if (this.shouldRotorRotate() && elapsedMillis > 0L) {
      this.angle =
          (this.angle + (float) elapsedMillis * rotationSpeed * this.rotorSpeedFactor()) % 360.0F;
    }

    this.lastcheck = now;
    return this.angle;
  }

  @Override
  public ResourceLocation getRotorRenderTexture() {
    return rotorTexture;
  }
}
