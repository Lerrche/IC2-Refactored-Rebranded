# Generators

`ic2.api.block.AddonGeneratorBlockEntity` extends IC2R's generator base. It provides EU storage, output handling, the charging slot, and the normal generator tick loop.

The subclass must implement:

```java
@Override
public boolean gainFuel() {
    // Check your inventory and fuel state.
    // Consume a fuel item and set your fuel duration/state when valid.
    // Return true if inventory/state changed, otherwise false.
}
```

The base tick calls `gainFuel()` when `needsFuel()` is true, then calls `gainEnergy()`. The base generator's fuel counter is an integer field and the base class saves it, but you must define how your fuel items and additional state work.

Constructor parameters are `BlockEntityType`, `BlockPos`, `BlockState`, `euPerTick`, `tier`, and `capacityEu`. In the current source, `euPerTick` must be finite and positive, tier must be 0–3, and capacity must be positive.

See [the generator example](../examples/generator.md) for the subclass skeleton and its limitations. A usable generator also needs its own block, block-entity registration, visuals, and usually a menu/screen or inventory interaction.
