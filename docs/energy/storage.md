# Energy storage blocks

`ic2.api.block.AddonEnergyStorageBlockEntity` is an abstract base for add-on energy boxes or batteries. In the inspected source it extends `TileEntityElectricBlock`, so it inherits IC2R's existing storage, output, inventory/charging-slot, and related behavior from that base.

## Constructor contract

```java
protected AddonEnergyStorageBlockEntity(
    BlockEntityType<? extends TileEntityElectricBlock> type,
    BlockPos pos,
    BlockState state,
    int tier,
    int outputEuPerTick,
    int capacityEu)
```

The constructor validates:

- `tier` must be between `0` and `3` inclusive.
- `outputEuPerTick` must be non-negative.
- `capacityEu` must be at least `1` EU.

Your concrete block entity still needs to be registered with a `BlockEntityType` in your add-on, and its block must use that type. Registration boilerplate depends on your NeoForge setup; this base class does not register a block or block entity automatically.

## Implementation shape

```java
public final class DenseBatteryBlockEntity extends AddonEnergyStorageBlockEntity {
    public DenseBatteryBlockEntity(
            BlockEntityType<? extends TileEntityElectricBlock> type,
            BlockPos pos,
            BlockState state) {
        super(type, pos, state, 2, 128, 100_000);
    }
}
```

This shows the constructor call only. You must use a block-entity type and block registration that match the concrete class, and verify the generic type accepted by the target IC2R revision. The numbers are example balance choices, not recommended defaults.
