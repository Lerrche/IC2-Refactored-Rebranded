package ic2.mixin.create;

import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import ic2.integration.create.IKineticBlockEntityBridge;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = KineticBlockEntity.class, remap = false)
public abstract class KineticBlockEntityMixin extends SmartBlockEntity
    implements IKineticBlockEntityBridge {
  @Shadow protected float speed;
  @Shadow public @Nullable BlockPos source;

  @Shadow
  public abstract void setSpeed(float speed);

  @Shadow
  public abstract void setNetwork(@Nullable Long networkIn);

  @Shadow
  public abstract KineticNetwork getOrCreateNetwork();

  @Shadow
  public abstract boolean hasNetwork();

  @Shadow
  public abstract boolean hasSource();

  @Shadow
  public abstract void attachKinetics();

  @Shadow
  public abstract void detachKinetics();

  @Shadow
  public abstract void onSpeedChanged(float previousSpeed);

  @Unique private float ic2$externalSpeed;
  @Unique private float ic2$externalCapacity;

  protected KineticBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state);
  }

  @Inject(method = "getGeneratedSpeed", at = @At("RETURN"), cancellable = true)
  private void ic2$getExternalGeneratedSpeed(CallbackInfoReturnable<Float> cir) {
    if (!((Object) this instanceof GeneratingKineticBlockEntity)
        && cir.getReturnValueF() == 0.0F
        && this.ic2$externalSpeed != 0.0F) {
      cir.setReturnValue(this.ic2$externalSpeed);
    }
  }

  @Inject(method = "calculateAddedStressCapacity", at = @At("RETURN"), cancellable = true)
  private void ic2$getExternalStressCapacity(CallbackInfoReturnable<Float> cir) {
    if (!((Object) this instanceof GeneratingKineticBlockEntity)
        && cir.getReturnValueF() == 0.0F
        && this.ic2$externalCapacity != 0.0F) {
      cir.setReturnValue(this.ic2$externalCapacity);
    }
  }

  @Override
  public float ic2$getExternalSpeed() {
    return this.ic2$externalSpeed;
  }

  @Override
  public void ic2$updateExternalRotation(float newSpeed, float newCapacity) {
    if ((Object) this instanceof GeneratingKineticBlockEntity) {
      return;
    }
    if (this.level == null || this.level.isClientSide) {
      return;
    }

    boolean capacityChanged = Math.abs(this.ic2$externalCapacity - newCapacity) > 0.01F;
    this.ic2$externalSpeed = newSpeed;
    this.ic2$externalCapacity = newCapacity;
    float prevSpeed = this.speed;

    if (prevSpeed != newSpeed) {
      if (newSpeed == 0.0F) {
        if (this.hasSource() && !this.worldPosition.equals(this.source)) {
          if (this.hasNetwork()) {
            this.getOrCreateNetwork().updateCapacityFor((KineticBlockEntity) (Object) this, 0.0F);
          }
          return;
        }
        this.detachKinetics();
        this.setSpeed(0.0F);
        this.source = null;
        this.setNetwork(null);
      } else if (prevSpeed == 0.0F) {
        this.setSpeed(newSpeed);
        this.source = null;
        this.setNetwork(this.worldPosition.asLong());
        this.attachKinetics();
      } else if (this.hasSource()) {
        if (Math.abs(prevSpeed) >= Math.abs(newSpeed)) {
          return;
        }
        this.detachKinetics();
        this.setSpeed(newSpeed);
        this.source = null;
        this.setNetwork(this.worldPosition.asLong());
        this.attachKinetics();
      } else {
        this.detachKinetics();
        this.setSpeed(newSpeed);
        this.attachKinetics();
      }
    }

    if (this.hasNetwork() && newSpeed != 0.0F && (prevSpeed != newSpeed || capacityChanged)) {
      this.getOrCreateNetwork().updateCapacityFor((KineticBlockEntity) (Object) this, newCapacity);
    }

    if (prevSpeed != newSpeed) {
      this.onSpeedChanged(prevSpeed);
      this.sendData();
    }
  }
}
