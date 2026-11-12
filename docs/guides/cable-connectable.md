# Cable‑Connectable API

## Overview
IC2‑Rebrand now provides a public hook that allows any block to be treated as an EU‑connectable cable for visual rendering.

## Interface
```java
package ic2.api.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Interface for blocks that want to be treated as EU‑connectable by IC2 cable rendering logic.
 */
public interface ICableConnectable {
    /**
     * Determines whether the block at {@code pos} should visually connect to an IC2 cable.
     *
     * @param world the level instance
     * @param pos   the block position being queried
     * @param side  the side from which the neighbouring cable is looking, may be null
     * @return true if a side‑arm should be rendered, false otherwise
     */
    boolean isCableConnectable(Level world, BlockPos pos, @Nullable Direction side);
}
```

## Registry
```java
package ic2.api.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Global registry for {@link ICableConnectable} handlers.
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

    /** Convenience for blocks that implement {@link ICableConnectable} themselves. */
    public static void registerSelf(Class<?> blockClass) {
        register(blockClass, (world, pos, side) -> ((ICableConnectable) world.getBlockState(pos).getBlock())
                .isCableConnectable(world, pos, side));
    }

    /** Retrieve the handler for a block class, or {@code null} if none is registered. */
    @Nullable
    public static ICableConnectable get(Class<?> blockClass) {
        return REGISTRY.get(blockClass);
    }
}
```

## Registration example (in your add‑on)
```java
import ic2.api.energy.CableConnectableRegistry;

public class MyAddon {
    public static void init() {
        CableConnectableRegistry.register(MyCableBlock.class,
            (world, pos, side) -> true // always render a side‑arm
        );
    }
}
```

Or, if `MyCableBlock` implements `ICableConnectable` directly:
```java
CableConnectableRegistry.registerSelf(MyCableBlock.class);
```

## Compatibility notes
* The hook is consulted **after** all built‑in IC2 checks (EnergyNet, tile entities, Forge‑Energy capability). 
* Returning `true` only influences client‑side rendering; energy transfer logic remains unchanged. 
* The `side` argument may be `null` when the check is performed without a specific neighbour direction.