# Content registration

`ic2.api.registry.Ic2AddonContent` is a helper in the inspected source for registering an add-on's blocks and items with NeoForge deferred registers. It keeps content under the add-on's own namespace.

## Register a simple item and block

```java
package example.addon;

import ic2.api.registry.Ic2AddonContent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

@Mod(ExampleIc2Addon.MOD_ID)
public final class ExampleIc2Addon {
    public static final String MOD_ID = "example_ic2_addon";
    public static final Ic2AddonContent CONTENT = Ic2AddonContent.create(MOD_ID);

    public static final DeferredItem<Item> BRONZE_GEAR = CONTENT.registerItem(
        "bronze_gear", () -> new Item(new Item.Properties()));

    public static final DeferredBlock<Block> ALLOY_BLOCK = CONTENT.registerSimpleBlock(
        "alloy_block",
        BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F));

    public static final DeferredItem<BlockItem> ALLOY_BLOCK_ITEM =
        CONTENT.registerBlockItem("alloy_block", ALLOY_BLOCK);

    public ExampleIc2Addon(IEventBus modEventBus) {
        CONTENT.register(modEventBus);
    }
}
```

Register content in the mod constructor before registries fire. Call `CONTENT.register(modEventBus)` once. The helper rejects registration calls made after its `register` method has run.

## Available helpers

- `create(namespace)` — creates a registry helper for the add-on's namespace.
- `registerItem(name, factory)` — registers an item or item subclass.
- `registerElectricItem(name, maxCharge, transferLimit, tier, canProvideEnergy)` — registers an `AddonElectricItem` with default item properties.
- `registerElectricItem(name, properties, maxCharge, transferLimit, tier, canProvideEnergy)` — same with custom item properties.
- `registerBlock(name, factory, properties)` — registers a custom block.
- `registerSimpleBlock(name, properties)` — registers a plain block.
- `registerBlockItem(name, block)` / `registerBlockItem(name, block, properties)` — registers a block item.
- `id(path)` — creates a `ResourceLocation` under the helper's namespace.
- `namespace()` — returns the namespace string.

## Add assets in your namespace

```text
src/main/resources/
  assets/example_ic2_addon/
    lang/en_us.json
    models/item/bronze_gear.json
    textures/item/bronze_gear.png
  data/example_ic2_addon/
    recipe/bronze_gear.json
```

Blocks usually also need blockstates, block models, item models, textures, loot tables, and any recipes or tags they use. These are normal Minecraft resources, not generated automatically by `Ic2AddonContent`.
