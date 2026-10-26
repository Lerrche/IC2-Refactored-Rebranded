# API reference

This is a source-oriented index of the types confirmed in the inspected project snapshot. It is not generated Javadoc; signatures and visibility can change between revisions.

## Registration

### `ic2.api.registry.Ic2AddonContent`

- `create(String namespace)`
- `registerItem(String name, Supplier<? extends T> factory)`
- `registerBlock(String name, Function<BlockBehaviour.Properties, ? extends T> factory, BlockBehaviour.Properties properties)`
- `registerSimpleBlock(String name, BlockBehaviour.Properties properties)`
- `registerBlockItem(String name, Supplier<? extends Block> block, Item.Properties properties)`
- `registerBlockItem(String name, Supplier<? extends Block> block)`
- `registerElectricItem(String name, double maxCharge, double transferLimit, int tier, boolean canProvideEnergy)`
- `registerElectricItem(String name, Item.Properties properties, double maxCharge, double transferLimit, int tier, boolean canProvideEnergy)`
- `register(IEventBus modEventBus)`
- `id(String path)` and `namespace()`

See [Content registration](registration.md) for an example.

## Energy blocks

### `ic2.api.block.AddonEnergyStorageBlockEntity`

Abstract base for energy boxes/batteries. Extends `TileEntityElectricBlock`. Constructor parameters: block entity type, position, state, tier, output EU per tick, and capacity in EU.

### `ic2.api.block.AddonGeneratorBlockEntity`

Abstract base for generators. Extends `TileEntityBaseGenerator`. Constructor parameters: block entity type, position, state, EU per tick, tier, and capacity in EU. A concrete subclass must implement inherited `gainFuel()`.

## Items

### `ic2.api.item.AddonElectricItem`

Configurable chargeable item base. Constructor parameters: item properties, maximum charge, transfer limit, tier, and whether it can provide energy.

### `ic2.api.item.AddonElectricWeapon`

Charge-powered melee weapon base. Constructor parameters: item properties, maximum charge, transfer limit, tier, and energy consumed per hit.

### `ic2.api.item.armor.AddonElectricArmor`

Electric armor base built on IC2R's `ItemArmorElectric`.

### `ic2.api.item.armor.AddonElectricJetpack`

Ready-to-register electric chest-slot jetpack implementation of the IC2R `IJetpack` contract; see its source constructor for required configuration values.

## Existing IC2 API packages

The source also includes established API packages such as `ic2.api.energy.tile`, `ic2.api.energy.prefab`, `ic2.api.item`, `ic2.api.upgrade`, and `ic2.api.recipe`. Read their interfaces and implementations before implementing them; not every interface has a convenient add-on base class.
