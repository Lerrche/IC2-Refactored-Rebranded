# Example: register items and blocks

This is a focused version of the [registration guide](../registration.md). Use `Ic2AddonContent` for ordinary items and blocks that belong to your add-on's namespace.

```java
public static final String MOD_ID = "example_ic2_addon";
public static final Ic2AddonContent CONTENT = Ic2AddonContent.create(MOD_ID);

public static final DeferredItem<Item> COPPER_COIL = CONTENT.registerItem(
    "copper_coil", () -> new Item(new Item.Properties()));

public static final DeferredBlock<Block> MACHINE_CASING = CONTENT.registerSimpleBlock(
    "machine_casing",
    BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.5F));

public static final DeferredItem<BlockItem> MACHINE_CASING_ITEM =
    CONTENT.registerBlockItem("machine_casing", MACHINE_CASING);
```

Call `CONTENT.register(modEventBus)` once from the mod constructor after the static entries have been declared. The class must import the Minecraft and NeoForge types used above.

For a block subclass, use `CONTENT.registerBlock("machine", YourBlock::new, properties)`.
