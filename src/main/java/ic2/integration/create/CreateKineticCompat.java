package ic2.integration.create;

import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Optional Create 6.x kinetic bridge. Only loaded when the {@code create} mod is present.
 *
 * <p>Provides bidirectional conversion between IC2 Kinetic Units (KU) and Create Stress Units (SU)
 * / RPM:
 *
 * <ul>
 *   <li>Create -> KU -> EU via {@code TileEntityKineticGenerator} (consumes SU from the network).
 *   <li>EU -> KU -> Create via {@code TileEntityElectricKineticGenerator} (drives a connected
 *       Create shaft/cogwheel with RPM and SU capacity).
 * </ul>
 */
public final class CreateKineticCompat {
  public static final float SU_PER_KU = 4.0F;
  public static final float MAX_CREATE_RPM = 256.0F;
  // 14 SU/RPM * 256 RPM = 3584 SU (~896 KU/t at 4 SU/KU), preventing any EU->KU->SU->EU loop.
  public static final float ELECTRIC_CAPACITY_PER_RPM = 14.0F;

  private CreateKineticCompat() {}

  public static boolean isKineticBlockEntity(BlockEntity blockEntity) {
    return blockEntity instanceof KineticBlockEntity;
  }

  public static int getKineticUnits(BlockEntity source) {
    if (!(source instanceof KineticBlockEntity kbe) || kbe.isRemoved()) {
      return 0;
    }
    // KineticBlockEntity#getSpeed() returns 0 when overstressed or tick-frozen.
    return Math.min(1000, Math.round(Math.abs(kbe.getSpeed())));
  }

  public static void updateConsumerStress(BlockPos generatorPos, BlockEntity source) {
    if (!(source instanceof KineticBlockEntity kbe) || kbe.isRemoved() || !kbe.hasNetwork()) {
      return;
    }
    KineticNetwork net = kbe.getOrCreateNetwork();
    if (net instanceof IKineticNetworkBridge bridge) {
      int kuLoad = Math.min(1000, Math.round(Math.abs(kbe.getTheoreticalSpeed())));
      bridge.ic2$setExternalStress(generatorPos, kuLoad * SU_PER_KU);
    }
  }

  public static void clearConsumerStress(BlockPos generatorPos, BlockEntity previousSource) {
    if (!(previousSource instanceof KineticBlockEntity kbe)
        || kbe.isRemoved()
        || !kbe.hasNetwork()) {
      return;
    }
    KineticNetwork net = kbe.getOrCreateNetwork();
    if (net instanceof IKineticNetworkBridge bridge) {
      bridge.ic2$setExternalStress(generatorPos, 0.0F);
    }
  }

  public static boolean isShaftTarget(BlockEntity target, Direction sideTowardsGenerator) {
    if (!(target instanceof KineticBlockEntity kbe)
        || kbe.isRemoved()
        || kbe instanceof GeneratingKineticBlockEntity) {
      return false;
    }
    if (kbe.getLevel() == null) {
      return false;
    }
    if (kbe.getBlockState().getBlock() instanceof IRotate rotate) {
      return rotate.hasShaftTowards(
          kbe.getLevel(), kbe.getBlockPos(), kbe.getBlockState(), sideTowardsGenerator);
    }
    return false;
  }

  public static int updateDrivenRotation(
      BlockEntity target, Direction facingFromGenerator, int maxKU, double availableKU) {
    if (!(target instanceof KineticBlockEntity kbe)
        || kbe.isRemoved()
        || !(kbe instanceof IKineticBlockEntityBridge bridge)) {
      return 0;
    }

    if (maxKU <= 0 || availableKU < 1.0) {
      bridge.ic2$updateExternalRotation(0.0F, 0.0F);
      return 0;
    }

    float rpm = Math.max(1.0F, Math.round((maxKU / 1000.0F) * MAX_CREATE_RPM));
    float signedSpeed =
        facingFromGenerator.getAxisDirection() == Direction.AxisDirection.POSITIVE ? rpm : -rpm;
    bridge.ic2$updateExternalRotation(signedSpeed, ELECTRIC_CAPACITY_PER_RPM);

    if (!kbe.hasNetwork() || kbe.isOverStressed()) {
      return 0;
    }

    KineticNetwork net = kbe.getOrCreateNetwork();
    float currentStress =
        (net instanceof IKineticNetworkBridge netBridge) ? netBridge.ic2$getCurrentStress() : 0.0F;
    int kuCost = Math.min(maxKU, Math.max(1, Math.round(currentStress / SU_PER_KU)));
    return (int) Math.min(kuCost, Math.floor(availableKU));
  }

  public static void stopDrivenRotation(BlockEntity target) {
    if (target instanceof IKineticBlockEntityBridge bridge) {
      bridge.ic2$updateExternalRotation(0.0F, 0.0F);
    }
  }
}
