# Compatibility and troubleshooting

## Keep your namespace separate

Register add-on content under your own mod ID. Keep assets and data under `assets/<your_modid>/` and `data/<your_modid>/`. Do not replace `ic2:*` resources from an add-on.

## Depend on the correct IC2R version

The project source snapshot's Gradle properties are for Minecraft 1.21.1 and NeoForge 21.1.234. Other builds may use different NeoForge versions or APIs. Compile and test against the exact IC2R artifact or source revision you intend to support.

## Energy interop is not automatic

Having EU storage does not automatically make a block compatible with FE or another mod's energy API. Check whether a capability or explicit adapter exists in the target source. Test import/export direction, side configuration, buffer capacity, transfer limits, and rounding at conversion boundaries.

## Common problems

- **Registry object is missing:** ensure `Ic2AddonContent.register(modEventBus)` is called once in the mod constructor.
- **Content has the wrong ID:** verify the namespace passed to `Ic2AddonContent.create(...)` matches your mod ID.
- **Missing texture or model:** check resource paths and JSON IDs; registration helpers do not create assets.
- **Generator never consumes fuel:** inspect the parent `TileEntityBaseGenerator.updateEntityServer()` flow and implement `gainFuel()` according to its contract.
- **Electric item does not mine blocks:** `AddonElectricItem` provides charge handling, not drill behavior. Inspect `IMiningDrill` and existing drill implementations.
- **Compiles but fails in-game:** test block-entity registration, metadata dependency constraints, assets, and server-side behavior in a development client.

## Reporting an issue

Include the Minecraft version, NeoForge version, IC2R revision, add-on revision, relevant log or crash report, and exact reproduction steps. Avoid posting only a screenshot when a log is available.
