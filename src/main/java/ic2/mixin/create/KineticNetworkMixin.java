package ic2.mixin.create;

import com.simibubi.create.content.kinetics.KineticNetwork;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import ic2.core.block.generator.tileentity.TileEntityKineticGenerator;
import ic2.integration.create.IKineticNetworkBridge;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = KineticNetwork.class, remap = false)
public abstract class KineticNetworkMixin implements IKineticNetworkBridge {
  @Shadow private float currentStress;
  @Shadow private float currentCapacity;
  @Shadow public Map<KineticBlockEntity, Float> members;

  @Shadow
  public abstract void updateStress();

  @Unique private final Map<BlockPos, Float> ic2$externalStress = new HashMap<>();

  @Override
  public void ic2$setExternalStress(BlockPos pos, float stressSU) {
    if (pos == null) {
      return;
    }
    if (stressSU <= 0.0F) {
      if (this.ic2$externalStress.remove(pos) != null) {
        this.updateStress();
      }
      return;
    }
    Float prev = this.ic2$externalStress.put(pos, stressSU);
    if (prev == null || Math.abs(prev - stressSU) > 0.01F) {
      this.updateStress();
    }
  }

  @Override
  public float ic2$getCurrentStress() {
    return this.currentStress;
  }

  @Override
  public float ic2$getCurrentCapacity() {
    return this.currentCapacity;
  }

  @Inject(method = "calculateStress", at = @At("RETURN"), cancellable = true)
  private void ic2$addExternalStress(CallbackInfoReturnable<Float> cir) {
    if (this.ic2$externalStress.isEmpty()) {
      return;
    }

    Level level = null;
    for (KineticBlockEntity member : this.members.keySet()) {
      if (member != null && member.getLevel() != null) {
        level = member.getLevel();
        break;
      }
    }

    float extra = 0.0F;
    Iterator<Map.Entry<BlockPos, Float>> it = this.ic2$externalStress.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<BlockPos, Float> entry = it.next();
      BlockPos pos = entry.getKey();
      float val = entry.getValue();
      if (val <= 0.0F) {
        it.remove();
        continue;
      }
      if (level != null
          && level.isLoaded(pos)
          && !(level.getBlockEntity(pos) instanceof TileEntityKineticGenerator)) {
        it.remove();
        continue;
      }
      extra += val;
    }

    if (extra > 0.0F) {
      cir.setReturnValue(cir.getReturnValueF() + extra);
    }
  }
}
