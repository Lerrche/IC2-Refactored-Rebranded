# Content registration

`ic2.api.registry.Ic2AddonContent` is a helper around NeoForge deferred block/item registries. It uses the namespace passed to `create(...)` and returns the usual `DeferredBlock` and `DeferredItem` handles.

## Register a few items and blocks

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

@Mod(ExampleAddon.MOD_ID)
public final class ExampleAddon {
    public static final String MOD_ID = "example_ic2_addon";
    public static final Ic2AddonContent CONTENT = Ic2AddonContent.create(MOD_ID);

    public static final DeferredItem<Item> ALLOY_DUST = CONTENT.registerItem(
        "alloy_dust", () -> new Item(new Item.Properties()));

    public static final DeferredBlock<Block> ALLOY_BLOCK = CONTENT.registerSimpleBlock(
        "alloy_block",
        BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3.0F));

    public static final DeferredItem<BlockItem> ALLOY_BLOCK_ITEM =
        CONTENT.registerBlockItem("alloy_block", ALLOY_BLOCK);

    public ExampleAddon(IEventBus modEventBus) {
        CONTENT.register(modEventBus);
    }
}
```

The example shows the helper's supported pattern. Your project still needs the correct imports and NeoForge project setup.

## Methods confirmed in `Ic2AddonContent`

- `create(String namespace)` — creates the helper for your mod ID.
- `registerItem(String name, Supplier<? extends T> factory)` — registers a custom item.
- `registerBlock(String name, Function<BlockBehaviour.Properties, ? extends T> factory, BlockBehaviour.Properties properties)` — registers a block using a factory and properties.
- `registerSimpleBlock(String name, BlockBehaviour.Properties properties)` — registers a plain block.
- `registerBlockItem(String name, Supplier<? extends Block> block)` — registers a block item with default item properties.
- `registerBlockItem(String name, Supplier<? extends Block> block, Item.Properties properties)` — registers a block item with custom properties.
- `registerElectricItem(...)` — registers an `AddonElectricItem` with charge parameters.
- `id(String path)` — creates a `ResourceLocation` in your namespace.
- `namespace()` — returns the namespace.
- `register(IEventBus modEventBus)` — attaches its deferred registers to the mod event bus.

**Register content before calling `CONTENT.register(modEventBus)`.** The helper intentionally rejects additional registration after it has been registered.

## Resources are still your job

For an item named `alloy_dust`, add your own resources, such as:

```text
assets/example_ic2_addon/lang/en_us.json
assets/example_ic2_addon/models/item/alloy_dust.json
assets/example_ic2_addon/textures/item/alloy_dust.png
```

For blocks, you also need blockstates, block models, item models, textures, loot tables, and any recipes or data tags your content uses. Registration does not generate assets automatically.
