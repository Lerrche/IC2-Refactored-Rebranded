package ic2.integration.create;

import net.minecraft.core.BlockPos;

/** Bridge interface mixed into Create's KineticNetwork to track external IC2 stress loads. */
public interface IKineticNetworkBridge {
  void ic2$setExternalStress(BlockPos pos, float stressSU);

  float ic2$getCurrentStress();

  float ic2$getCurrentCapacity();
}
