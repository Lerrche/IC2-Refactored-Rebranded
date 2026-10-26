# Generators

`ic2.api.block.AddonGeneratorBlockEntity` is an abstract base for add-on generators. It extends IC2R's `TileEntityBaseGenerator`, which handles normal generator ticking and existing generator energy/charging behavior.

## Constructor contract

```java
protected AddonGeneratorBlockEntity(
    BlockEntityType<? extends TileEntityBaseGenerator> type,
    BlockPos pos,
    BlockState state,
    double euPerTick,
    int tier,
    int capacityEu)
```

The base validates that `euPerTick` is finite and greater than zero, the tier is between `0` and `3`, and capacity is positive. Your subclass must implement the inherited abstract method `gainFuel()` and any fuel-specific state/logic.

## Fuel logic

The method signature present on the parent class is:

```java
public abstract boolean gainFuel();
```

Return behavior should match the parent generator's expectation: inspect `TileEntityBaseGenerator.updateEntityServer()` in the same source revision before implementing fuel consumption. This documentation intentionally does not invent a fuel-slot layout or fuel API that the base class does not define.

## Block entity registration

Register your concrete generator block entity and block using the normal NeoForge 1.21.1 registration approach for your project. The `AddonGeneratorBlockEntity` base does not register those objects for you.
