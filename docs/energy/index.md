# Energy API overview

IC2R's native energy system uses **EU**. The inspected add-on API provides base classes for certain energy-storage blocks and generators, while lower-level energy interfaces and prefabs are available in `ic2.api.energy`.

## Which base should I start with?

| Goal | Source type to inspect | Notes |
| --- | --- | --- |
| Battery / energy box | `ic2.api.block.AddonEnergyStorageBlockEntity` | Extends IC2R's `TileEntityElectricBlock`. |
| Generator | `ic2.api.block.AddonGeneratorBlockEntity` | Extends `TileEntityBaseGenerator`; implement `gainFuel()`. |
| Existing energy-net tile patterns | `ic2.api.energy.prefab` and `ic2.api.energy.tile` | Read the interfaces and lifecycle expectations before implementing directly. |
| Chargeable item | `ic2.api.item.AddonElectricItem` | Uses IC2R's electric-item manager through its base class. |

The energy storage and generator helpers extend `ic2.core.*` classes. They are convenient, but they are more coupled to IC2R internals than a stable cross-version abstraction would be. Re-check signatures when updating IC2R versions.

## EU tiers

The helper constructors validate tiers in the range `0` through `3` inclusive. Choose the tier and transfer/output values to match the balance and behavior you want; do not infer that tier alone sets every machine's transfer rate.

## Interoperability

Do not assume that a third-party cable or machine will accept EU directly. Cross-mod energy compatibility depends on explicit adapters/capabilities and conversion rules in the target versions. Test both directions, storage limits, and rounding behavior when implementing an integration.
