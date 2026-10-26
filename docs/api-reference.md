# API reference

This is a concise index of entry points confirmed in the source snapshot used to prepare these docs. It is not a generated Javadoc dump; consult source for complete method documentation and visibility.

| Class | Package | Purpose |
|---|---|---|
| `Ic2AddonContent` | `ic2.api.registry` | Deferred registration helper for add-on blocks and items. |
| `AddonElectricItem` | `ic2.api.item` | Configurable electric item base. |
| `AddonElectricWeapon` | `ic2.api.item` | Charge-powered melee weapon base. |
| `AddonElectricArmor` | `ic2.api.item.armor` | Configurable electric armor base. |
| `AddonElectricJetpack` | `ic2.api.item.armor` | Electric jetpack base implementing `IJetpack`. |
| `AddonEnergyStorageBlockEntity` | `ic2.api.block` | Base for add-on energy storage blocks. |
| `AddonGeneratorBlockEntity` | `ic2.api.block` | Base for add-on generators; requires `gainFuel()`. |
| `BasicEnergyTile` | `ic2.api.energy.prefab` | Existing energy-tile prefab; consult source for required implementation details. |

## `Ic2AddonContent`

Confirmed methods include `create`, `registerBlock`, `registerSimpleBlock`, `registerItem`, `registerElectricItem`, `registerBlockItem`, `register`, `id`, and `namespace`.

## Version compatibility

The base classes under `ic2.api.*` are intended as add-on integration points, but signatures can change between project versions. Some of the block-entity bases extend IC2R implementation classes. Treat those inheritance relationships as more version-sensitive than ordinary Minecraft item registration.
