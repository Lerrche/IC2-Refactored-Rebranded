# Compatibility Notes

## Minecraft and NeoForge Versions

This documentation targets **Minecraft 1.21.1** on **NeoForge**. Always use the NeoForge version specified in the root `gradle.properties` / `build.gradle` of this repository.

## Public API vs Implementation

- **Use `ic2.api.*` wherever possible**: Classes in `ic2.api.*` (`Ic2AddonContent`, `AddonWrenchableBlock`, `AddonMachineBlockEntity`, `AddonGeneratorBlockEntity`, `AddonEnergyStorageBlockEntity`, `AddonElectricTool`, `AddonUpgradeItem`, `AddonRecipeBuilder`, `ElectricItem.manager`) are specifically designed for add-ons and provide stable, clean interfaces.
- **Avoid depending on `ic2.core.*` internals**: Internal classes are subject to refactoring between mod versions.

## Energy Interoperability (EU & FE)

IC2 Rebranded includes a built-in bidirectional Forge Energy (FE) bridge:
- **Conversion Ratio**: $1\text{ EU} = 2\text{ FE}$ ($1\text{ FE} = 0.5\text{ EU}$).
- All `AddonEnergyBlockEntity`, `AddonMachineBlockEntity`, `AddonGeneratorBlockEntity`, and `AddonEnergyStorageBlockEntity` instances support FE automatically when registered with `AddonEnergyCapabilities.registerEnergyStorage(event, blockEntityType.get())`.

## Common Troubleshooting Tips

- **Missing IC2 Classes**: Ensure `implementation "me.halfcooler.ic2r.core:ic2r-neoforge:1.0.3"` is in your add-on's `build.gradle`, and `ic2` is declared as a required dependency in `neoforge.mods.toml`.
- **Block/Item Not Showing Up**: Ensure `CONTENT.register(modEventBus)` is called in your mod constructor, and items are added to your Creative Mode Tab or vanilla tags.
- **Wrench Not Rotating Block**: Ensure you registered your block with `CONTENT.registerWrenchableBlock(...)`.
- **Code Style / Build Failures**: Run `.\gradlew.bat spotlessApply` before running `build`.
