package ic2.api.energy;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Global registry for {@link ICableConnectable} handlers.
 *
 * <p>Mods can register a handler for a specific block class. When IC2 checks {@link
 * ic2.core.block.wiring.AbstractCableBlock#isEuConnectable(Level, BlockPos, Direction)} it will
 * query this registry, allowing the block to be treated as visually connectable without needing a
 * mixin.
 */
public final class CableConnectableRegistry {
  private static final Map<Class<?>, ICableConnectable> REGISTRY = new IdentityHashMap<>();

  private CableConnectableRegistry() {}

  /** Register a handler for the given block class. */
  public static void register(Class<?> blockClass, ICableConnectable handler) {
    if (blockClass == null || handler == null) {
      throw new IllegalArgumentException("blockClass and handler must not be null");
    }
    REGISTRY.put(blockClass, handler);
  }

  /** Convenience method for blocks that implement {@link ICableConnectable} themselves. */
  public static void registerSelf(Class<?> blockClass) {
    register(
        blockClass,
        (world, pos, side) -> {
          // Cast is safe because the block implements the interface.
          return ((ICableConnectable) world.getBlockState(pos).getBlock())
              .isCableConnectable(world, pos, side);
        });
  }

  /** Retrieve the handler for a block class, or {@code null} if none is registered. */
  @Nullable public static ICableConnectable get(Class<?> blockClass) {
    return REGISTRY.get(blockClass);
  }
}
