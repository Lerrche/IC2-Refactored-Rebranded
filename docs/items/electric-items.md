# Electric items and batteries

## `AddonElectricItem`

`ic2.api.item.AddonElectricItem` extends IC2R's `BaseElectricItem`. Its constructor takes:

```java
AddonElectricItem(
    Item.Properties properties,
    double maxCharge,
    double transferLimit,
    int tier,
    boolean canProvideEnergy)
```

The current implementation validates that `maxCharge` and `transferLimit` are finite positive values and that `tier` is between 0 and 3. `canProvideEnergy` controls whether the item can discharge energy to consumers.

You can register a standard electric item directly with `Ic2AddonContent.registerElectricItem(...)`, or subclass `AddonElectricItem` for custom behavior. See the [electric item example](../examples/electric-item.md).

Use `ic2.api.item.ElectricItem.manager` for charging, discharging, reading charge, and consuming EU. Avoid manually editing internal data components/NBT.

## Other item bases

- `AddonElectricWeapon` — charge-powered melee weapon base; see [weapon example](../examples/electric-weapon.md).
- `AddonElectricArmor` — electric armor with energy-per-damage and absorption-ratio settings.
- `AddonElectricJetpack` — chest-slot electric jetpack implementing IC2R's `IJetpack` interface.

These bases do not generate their own models, textures, language entries, recipes, or creative-tab entries.
