# Example: subclass the generator base

`ic2.api.block.AddonGeneratorBlockEntity` provides IC2R's generator storage, output, charge slot, and generator ticking. Your subclass must implement `gainFuel()` and its own fuel-specific behavior.

The constructor signature in the current source is:

```java
AddonGeneratorBlockEntity(
    BlockEntityType<? extends TileEntityBaseGenerator> type,
    BlockPos pos,
    BlockState state,
    double euPerTick,
    int tier,
    int capacityEu)
```

It is `protected`, so call it from your subclass constructor. A minimal subclass shape is:

```java
public final class ExampleGeneratorBlockEntity extends AddonGeneratorBlockEntity {
    public ExampleGeneratorBlockEntity(
            BlockEntityType<? extends TileEntityBaseGenerator> type,
            BlockPos pos,
            BlockState state) {
        super(type, pos, state, 10.0, 1, 10_000);
    }

    @Override
    public boolean gainFuel() {
        // TODO: check and consume your actual fuel item/state here.
        // Return true if inventory/state changed; false otherwise.
        return false;
    }
}
```

This is a subclass skeleton, not a complete working generator. `gainFuel()` must be implemented for your fuel system, and you still need to register the block, its `BlockEntityType`, and all assets. The exact `BlockEntityType` registration pattern belongs to NeoForge 1.21.1 and your add-on's registry setup; don't register your block entity in IC2R's private registry.

The constructor requires a finite positive `euPerTick`, tier 0–3, and positive capacity. Confirm the block entity generic types against your current NeoForge and IC2R checkout.
