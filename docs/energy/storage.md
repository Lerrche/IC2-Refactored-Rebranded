# Energy storage block entities

`ic2.api.block.AddonEnergyStorageBlockEntity` is a base class for add-on energy boxes or batteries. Its protected constructor accepts:

- Your registered `BlockEntityType` (the current signature is bounded by `TileEntityElectricBlock`).
- Block position and block state.
- Tier (0–3).
- Output in EU per tick (non-negative).
- Capacity in EU (positive).

Example subclass shape:

```java
public final class AlloyBatteryBlockEntity extends AddonEnergyStorageBlockEntity {
    public AlloyBatteryBlockEntity(
            BlockEntityType<? extends TileEntityElectricBlock> type,
            BlockPos pos,
            BlockState state) {
        super(type, pos, state, 2, 512, 100_000);
    }
}
```

Imports are omitted here for readability. Match the generic type used by the exact source you are compiling against. Because the constructor is protected, subclass it rather than trying to instantiate the abstract base directly.

The base inherits IC2R storage, energy component, charging/discharging slots, output behavior, and related machine behavior. It does **not** register your block, block entity type, model, texture, menu, loot table, or recipes for you.

For a full integration, register the subclass's `BlockEntityType` using NeoForge's normal deferred-register pattern and make sure the corresponding block returns/uses that type. The exact registration boilerplate is omitted here rather than guessing at your add-on's registry setup.
