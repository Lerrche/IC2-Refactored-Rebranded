# API Reference

This is an index of core entry points for IC2 Rebranded add-on developers.

| Class | Package | Purpose |
|---|---|---|
| `Ic2AddonContent` | `ic2.api.registry` | Central deferred registration helper for add-on blocks, items, block entities, creative tabs, recipes, and menus. |
| `AddonWrenchableBlock` | `ic2.api.block` | Vanilla block base combining energy ticking, horizontal facing state, and full `IWrenchAble` rotation/dismantle drops. |
| `AddonEnergyBlock` | `ic2.api.block` | Base block for add-on energy tiles with automated server ticking. |
| `AddonMachineBlockEntity` | `ic2.api.block` | Standard EU-powered processing machine base with progress, operation duration, and power consumption. |
| `AddonGeneratorBlockEntity` | `ic2.api.block` | Base for add-on generators; requires implementing `gainFuel()`. |
| `AddonEnergyStorageBlockEntity` | `ic2.api.block` | Base for add-on energy storage blocks (batteries/accumulators). |
| `AddonEnergyCableBlockEntity` | `ic2.api.energy.prefab` | Conductor cable base with conduction loss and insulation breakdown. |
| `AddonElectricTool` | `ic2.api.item` | Extensible electric tool base (wrenches, treetaps, shears, active tools). |
| `AddonElectricItem` | `ic2.api.item` | Configurable electric item base. |
| `AddonElectricWeapon` | `ic2.api.item` | Charge-powered melee weapon base. |
| `AddonElectricMiningTool` | `ic2.api.item` | Configurable electric mining tool base (drills/chainsaws). |
| `AddonElectricArmor` | `ic2.api.item.armor` | Configurable electric armor base. |
| `AddonElectricJetpack` | `ic2.api.item.armor` | Electric jetpack base implementing `IJetpack`. |
| `AddonUpgradeItem` | `ic2.api.upgrade` | Configurable machine upgrade base with fluent builder (speed, storage, tier boost). |
| `UpgradeDefinition` | `ic2.api.upgrade` | Pure data and logic modifier definition for machine upgrades. |
| `AddonRecipeBuilder` | `ic2.api.recipe` | Fluent recipe builder generating JSON definitions for all IC2 machine types. |
| `AddonMaterialSet` | `ic2.api.registry` | Builder for registering related item and block forms (ingot, dust, plate, block). |
| `AddonEnergyCapabilities` | `ic2.api.registry` | Helpers for exposing Forge Energy (FE) capabilities on EU block entities. |

## `Ic2AddonContent` Methods

- **Creation & Mod Lifecycle**: `create(namespace)`, `register(modEventBus)`, `id(path)`, `namespace()`
- **Creative Mode**: `registerCreativeModeTab(name, iconSupplier, displayItemsGenerator)`
- **Blocks & Block Entities**: `registerBlock(name, factory, props)`, `registerSimpleBlock(name, props)`, `registerWrenchableBlock(name, props, factory)`, `registerBlockEntityType(name, factory, blocks...)`
- **Items & Tools**: `registerItem(name, factory)`, `registerBlockItem(name, block)`, `registerElectricItem(...)`, `registerElectricTool(...)`, `registerElectricMiningTool(...)`, `registerElectricWeapon(...)`
- **Armor & Jetpacks**: `registerElectricArmor(...)`, `registerEnergyPack(...)`, `registerJetpack(...)`
- **Upgrades**: `registerUpgradeItem(name, props, configurator)`
- **Recipes & Containers**: `registerRecipeType(...)`, `registerRecipeSerializer(...)`, `registerMenuType(...)`

For a full step-by-step walkthrough, see the [Add-on Creation Guide](addon-development-guide.md).
