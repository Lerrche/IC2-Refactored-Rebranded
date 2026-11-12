package ic2.api.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for blocks that want to be treated as EU‑connectable by IC2 cable rendering logic.
 * Implement this on your block class (or provide a separate handler) and register it via {@link
 * CableConnectableRegistry}.
 */
public interface ICableConnectable {
  /**
   * Determines whether the block at {@code pos} should visually connect to an IC2 cable.
   *
   * @param world the level instance
   * @param pos the block position being queried
   * @param side the side from which the neighbouring cable is looking, may be null
   * @return true if a side‑arm should be rendered, false otherwise
   */
  boolean isCableConnectable(Level world, BlockPos pos, @Nullable Direction side);
}
