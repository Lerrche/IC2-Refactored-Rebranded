package ic2.api.registry;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * Builder for common material item/block forms (for example uranium dust, ingot, plate, pellet,
 * fuel rod, raw ore and storage block). It registers content only; recipes, tags and assets remain
 * ordinary add-on resources so modpacks can freely override them with datapacks/resource packs.
 */
public final class AddonMaterialSet {
  private final String material;
  private final Map<String, DeferredItem<Item>> items;
  private final Map<String, DeferredBlock<Block>> blocks;
  private final Map<String, DeferredItem<BlockItem>> blockItems;

  private AddonMaterialSet(
      String material,
      Map<String, DeferredItem<Item>> items,
      Map<String, DeferredBlock<Block>> blocks,
      Map<String, DeferredItem<BlockItem>> blockItems) {
    this.material = material;
    this.items = Collections.unmodifiableMap(new LinkedHashMap<>(items));
    this.blocks = Collections.unmodifiableMap(new LinkedHashMap<>(blocks));
    this.blockItems = Collections.unmodifiableMap(new LinkedHashMap<>(blockItems));
  }

  public String material() {
    return material;
  }

  public Map<String, DeferredItem<Item>> items() {
    return items;
  }

  public Map<String, DeferredBlock<Block>> blocks() {
    return blocks;
  }

  public Map<String, DeferredItem<BlockItem>> blockItems() {
    return blockItems;
  }

  public DeferredItem<BlockItem> blockItem(String form) {
    return Objects.requireNonNull(blockItems.get(form), "No block item registered: " + form);
  }

  public DeferredItem<Item> item(String form) {
    return Objects.requireNonNull(items.get(form), "No item form registered: " + form);
  }

  public DeferredBlock<Block> block(String form) {
    return Objects.requireNonNull(blocks.get(form), "No block form registered: " + form);
  }

  public static Builder builder(Ic2AddonContent content, String material) {
    return new Builder(content, material);
  }

  public static final class Builder {
    private final Ic2AddonContent content;
    private final String material;
    private final Map<String, DeferredItem<Item>> items = new LinkedHashMap<>();
    private final Map<String, DeferredBlock<Block>> blocks = new LinkedHashMap<>();
    private final Map<String, DeferredItem<BlockItem>> blockItems = new LinkedHashMap<>();

    private Builder(Ic2AddonContent content, String material) {
      this.content = Objects.requireNonNull(content, "content");
      if (material == null || !material.matches("[a-z0-9_./-]+"))
        throw new IllegalArgumentException("invalid material path: " + material);
      this.material = material;
    }

    /** Registers an ordinary item named {@code <material>_<form>}. */
    public Builder itemForm(String form) {
      validateForm(form);
      String id = material + "_" + form;
      if (items.containsKey(form) || blocks.containsKey(form))
        throw new IllegalArgumentException("duplicate material form: " + form);
      items.put(form, content.registerItem(id, () -> new Item(new Item.Properties())));
      return this;
    }

    /** Registers a block and matching block item named {@code <material>_<form>}. */
    public Builder blockForm(String form, BlockBehaviour.Properties properties) {
      validateForm(form);
      String id = material + "_" + form;
      if (items.containsKey(form) || blocks.containsKey(form))
        throw new IllegalArgumentException("duplicate material form: " + form);
      DeferredBlock<Block> block = content.registerSimpleBlock(id, properties);
      blocks.put(form, block);
      blockItems.put(form, content.registerBlockItem(id, block));
      return this;
    }

    public AddonMaterialSet build() {
      if (items.isEmpty() && blocks.isEmpty())
        throw new IllegalStateException("material set has no forms");
      return new AddonMaterialSet(material, items, blocks, blockItems);
    }

    private static void validateForm(String form) {
      if (form == null || !form.matches("[a-z0-9_./-]+"))
        throw new IllegalArgumentException("invalid material form: " + form);
    }
  }
}
