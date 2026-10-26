# Electric items and weapons

## Chargeable items

`ic2.api.item.AddonElectricItem` extends IC2R's `BaseElectricItem`. It delegates charge handling to IC2R's `ElectricItem.manager` and adds a configurable `canProvideEnergy` value.

Constructor:

```java
public AddonElectricItem(
    Item.Properties properties,
    double maxCharge,
    double transferLimit,
    int tier,
    boolean canProvideEnergy)
```

The class requires finite, positive values for `maxCharge` and `transferLimit`, and a tier from `0` to `3`. Subclass it when you need custom interaction behavior, or register it directly through `Ic2AddonContent.registerElectricItem(...)`.

Example registration:

```java
public static final DeferredItem<AddonElectricItem> BATTERY_TOOL = CONTENT.registerElectricItem(
    "battery_tool", 20_000.0, 256.0, 1, false);
```

The example registers an item with charge metadata; it does not add mining or combat behavior by itself.

## Electric melee weapons

`ic2.api.item.AddonElectricWeapon` extends `AddonElectricItem` and takes `Item.Properties`, maximum charge, transfer limit, tier, and `energyPerHit`. The inspected implementation calls `ElectricItem.manager.use(...)` on the logical server when a successful melee hit is processed. It leaves normal melee damage to the Minecraft item properties and the normal combat flow.

```java
public AddonElectricWeapon(
    Item.Properties properties,
    double maxCharge,
    double transferLimit,
    int tier,
    double energyPerHit)
```

`energyPerHit` must be finite and non-negative. The base class does not configure attack damage or attack speed; set those using the item properties supported by your target Minecraft/NeoForge version.

## Drill-specific behavior

The source includes `ic2.api.item.IMiningDrill`, which is an existing IC2 API contract. `AddonElectricItem` alone is not a drill implementation. Before writing a custom drill, inspect `IMiningDrill` and the current IC2R drill implementations to match the required method signatures and mining behavior. This page intentionally does not invent a universal `AddonElectricMiningTool` class.
