# Energy API overview

IC2R uses EU for its native energy system. The add-on helpers documented here provide reusable starting points, not automatic content registration.

## Available bases in the source snapshot

- `ic2.api.block.AddonEnergyStorageBlockEntity` — energy storage block entity base.
- `ic2.api.block.AddonGeneratorBlockEntity` — generator base that requires a `gainFuel()` implementation.
- `ic2.api.item.AddonElectricItem` — electric item base with charge/tier configuration.
- `ic2.api.energy.prefab.BasicEnergyTile` — existing energy tile prefab; consult its source for the exact methods required by your implementation.

Start with [storage block entities](storage.md) or [generators](generators.md). For registration helper details, see [Content registration](../registration.md).

## Important

Do not assume EU values and Forge Energy (FE) values are interchangeable. If your add-on bridges another energy API, use an explicit conversion policy and test transfer limits, rounding, and buffering. Compatibility behavior depends on the relevant adapter implementation.
