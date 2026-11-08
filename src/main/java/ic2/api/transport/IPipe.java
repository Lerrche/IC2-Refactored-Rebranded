package ic2.api.transport;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Interface implemented by transport pipes.
 */
public interface IPipe {
  BlockEntity getBlockEntity();

  boolean isConnected(Direction side);

  void flipConnection(Direction side);
}
