package ic2.api.registry;

import ic2.api.block.AddonWrenchableBlock;
import ic2.api.item.AddonElectricTool;
import ic2.api.upgrade.AddonUpgradeItem;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Small registration helper for NeoForge 1.21.1 IC2R add-ons.
 *
 * <p>Each add-on owns its namespace and its assets. Register this object from the add-on's mod
 * constructor before registries fire, then use the returned deferred holders from other classes.
 * Textures, models, blockstates, translations, loot tables and recipes belong in the add-on's
 * normal {@code src/main/resources} tree under the same namespace.
 */
public final class Ic2AddonContent {
  private final String namespace;
  private final DeferredRegister.Blocks blocks;
  private final DeferredRegister.Items items;
  private final DeferredRegister<BlockEntityType<?>> blockEntities;
  private final DeferredRegister<RecipeType<?>> recipeTypes;
  private final DeferredRegister<RecipeSerializer<?>> recipeSerializers;
  private final DeferredRegister<MenuType<?>> menuTypes;
  private final DeferredRegister<CreativeModeTab> creativeTabs;
  private boolean registered;

  private Ic2AddonContent(String namespace) {
    ResourceLocation.fromNamespaceAndPath(namespace, "validation");
    this.namespace = namespace;
    this.blocks = DeferredRegister.createBlocks(namespace);
    this.items = DeferredRegister.createItems(namespace);
    this.blockEntities = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, namespace);
    this.recipeTypes = DeferredRegister.create(Registries.RECIPE_TYPE, namespace);
    this.recipeSerializers = DeferredRegister.create(Registries.RECIPE_SERIALIZER, namespace);
    this.menuTypes = DeferredRegister.create(Registries.MENU, namespace);
    this.creativeTabs = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, namespace);
  }

  /** Creates a content registry for an add-on's mod id (for example {@code my_ic2_addon}). */
  public static Ic2AddonContent create(String namespace) {
    return new Ic2AddonContent(Objects.requireNonNull(namespace, "namespace"));
  }

  /** Registers a block using the add-on namespace. */
  public <T extends Block> DeferredBlock<T> registerBlock(
      String name,
      Function<BlockBehaviour.Properties, ? extends T> factory,
      BlockBehaviour.Properties properties) {
    ensureNotRegistered();
    return this.blocks.registerBlock(name, factory, properties);
  }

  /** Registers a custom block entity type in the add-on namespace. */
  @SafeVarargs
  public final <T extends BlockEntity>
      DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> registerBlockEntityType(
          String name,
          BlockEntityType.BlockEntitySupplier<T> factory,
          Supplier<? extends Block>... validBlocks) {
    ensureNotRegistered();
    if (validBlocks == null || validBlocks.length == 0)
      throw new IllegalArgumentException("at least one valid block is required");
    Supplier<? extends Block>[] blocks = validBlocks.clone();
    return this.blockEntities.register(
        name,
        () -> {
          Block[] resolved = new Block[blocks.length];
          for (int i = 0; i < blocks.length; i++)
            resolved[i] = Objects.requireNonNull(blocks[i].get(), "valid block");
          return BlockEntityType.Builder.of(factory, resolved).build(null);
        });
  }

  /** Registers a basic menu type for a custom machine/container. */
  public <T extends AbstractContainerMenu>
      DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(
          String name, MenuType.MenuSupplier<T> factory) {
    ensureNotRegistered();
    return this.menuTypes.register(name, () -> new MenuType<>(factory, FeatureFlags.DEFAULT_FLAGS));
  }

  /** Registers a custom recipe type in this add-on's namespace. */
  public <T extends Recipe<?>> DeferredHolder<RecipeType<?>, RecipeType<T>> registerRecipeType(
      String name, Supplier<RecipeType<T>> factory) {
    ensureNotRegistered();
    return this.recipeTypes.register(name, factory);
  }

  /** Registers a custom recipe serializer in this add-on's namespace. */
  public <T extends Recipe<?>>
      DeferredHolder<RecipeSerializer<?>, RecipeSerializer<T>> registerRecipeSerializer(
          String name, Supplier<RecipeSerializer<T>> factory) {
    ensureNotRegistered();
    return this.recipeSerializers.register(name, factory);
  }

  /** Registers a configurable electric melee weapon. */
  public DeferredItem<ic2.api.item.AddonElectricWeapon> registerElectricWeapon(
      String name,
      Item.Properties properties,
      double capacity,
      double transferLimit,
      int tier,
      double energyPerHit) {
    return registerItem(
        name,
        () ->
            new ic2.api.item.AddonElectricWeapon(
                properties, capacity, transferLimit, tier, energyPerHit));
  }

  /** Registers a configurable electric mining tool. */
  public DeferredItem<ic2.api.item.AddonElectricMiningTool> registerElectricMiningTool(
      String name,
      Item.Properties properties,
      double capacity,
      double transferLimit,
      int tier,
      boolean canProvideEnergy,
      int energyPerBlock,
      int breakTime,
      float miningSpeed) {
    return registerItem(
        name,
        () ->
            new ic2.api.item.AddonElectricMiningTool(
                properties,
                capacity,
                transferLimit,
                tier,
                canProvideEnergy,
                energyPerBlock,
                breakTime,
                miningSpeed));
  }

  /** Registers configurable electric armor for a selected vanilla armor slot. */
  public DeferredItem<ic2.api.item.armor.AddonElectricArmor> registerElectricArmor(
      String name,
      Holder<ArmorMaterial> material,
      EquipmentSlot slot,
      Item.Properties properties,
      double capacity,
      double transferLimit,
      int tier,
      int energyPerDamage,
      double damageAbsorptionRatio,
      boolean canProvideEnergy) {
    return registerItem(
        name,
        () ->
            new ic2.api.item.armor.AddonElectricArmor(
                material,
                slot,
                properties,
                capacity,
                transferLimit,
                tier,
                energyPerDamage,
                damageAbsorptionRatio,
                canProvideEnergy));
  }

  /** Registers a configurable chest-slot energy pack that can discharge into electric gear. */
  public DeferredItem<ic2.api.item.armor.AddonEnergyPack> registerEnergyPack(
      String name,
      Holder<ArmorMaterial> material,
      Item.Properties properties,
      double capacity,
      double transferLimit,
      int tier) {
    return registerItem(
        name,
        () ->
            new ic2.api.item.armor.AddonEnergyPack(
                material, properties, capacity, transferLimit, tier));
  }

  /** Registers a configurable chest-slot electric jetpack. */
  public DeferredItem<ic2.api.item.armor.AddonElectricJetpack> registerJetpack(
      String name,
      Holder<ArmorMaterial> material,
      Item.Properties properties,
      double capacity,
      double transferLimit,
      int tier,
      int energyPerTick,
      float power,
      float dropPercentage,
      float hoverMultiplier,
      float worldHeightDivisor) {
    return registerItem(
        name,
        () ->
            new ic2.api.item.armor.AddonElectricJetpack(
                material,
                properties,
                capacity,
                transferLimit,
                tier,
                energyPerTick,
                power,
                dropPercentage,
                hoverMultiplier,
                worldHeightDivisor));
  }

  /** Registers a plain block using its supplied properties. */
  public DeferredBlock<Block> registerSimpleBlock(
      String name, BlockBehaviour.Properties properties) {
    ensureNotRegistered();
    return this.blocks.registerSimpleBlock(name, properties);
  }

  /** Registers an item using a supplier, allowing custom Item subclasses. */
  public <T extends Item> DeferredItem<T> registerItem(String name, Supplier<? extends T> factory) {
    ensureNotRegistered();
    return this.items.register(name, factory);
  }

  /** Registers a plain IC2R-compatible electric item with charge/tier metadata. */
  public DeferredItem<ic2.api.item.AddonElectricItem> registerElectricItem(
      String name, double maxCharge, double transferLimit, int tier, boolean canProvideEnergy) {
    return registerElectricItem(
        name, new Item.Properties(), maxCharge, transferLimit, tier, canProvideEnergy);
  }

  /** Registers a configurable IC2R-compatible electric item with custom item properties. */
  public DeferredItem<ic2.api.item.AddonElectricItem> registerElectricItem(
      String name,
      Item.Properties properties,
      double maxCharge,
      double transferLimit,
      int tier,
      boolean canProvideEnergy) {
    return registerItem(
        name,
        () ->
            new ic2.api.item.AddonElectricItem(
                properties, maxCharge, transferLimit, tier, canProvideEnergy));
  }

  /** Registers a simple block item using the block's registry path as its item path. */
  public DeferredItem<BlockItem> registerBlockItem(
      String name, Supplier<? extends Block> block, Item.Properties properties) {
    ensureNotRegistered();
    return this.items.registerSimpleBlockItem(name, block, properties);
  }

  /** Registers a simple block item using default item properties. */
  public DeferredItem<BlockItem> registerBlockItem(String name, Supplier<? extends Block> block) {
    return registerBlockItem(name, block, new Item.Properties());
  }

  /** Registers a custom Creative Mode tab in this add-on's namespace. */
  public DeferredHolder<CreativeModeTab, CreativeModeTab> registerCreativeModeTab(
      String name,
      Supplier<ItemStack> iconSupplier,
      CreativeModeTab.DisplayItemsGenerator displayItemsGenerator) {
    ensureNotRegistered();
    return this.creativeTabs.register(
        name,
        () ->
            CreativeModeTab.builder()
                .title(Component.translatable("itemGroup." + this.namespace + "." + name))
                .icon(iconSupplier)
                .displayItems(displayItemsGenerator)
                .build());
  }

  /**
   * Registers a wrenchable machine or energy block with horizontal facing and safe wrench
   * dismantling drops.
   */
  public DeferredBlock<AddonWrenchableBlock> registerWrenchableBlock(
      String name,
      BlockBehaviour.Properties properties,
      BiFunction<BlockPos, BlockState, ? extends BlockEntity> factory) {
    ensureNotRegistered();
    return this.blocks.registerBlock(
        name, props -> new AddonWrenchableBlock(props, factory), properties);
  }

  /** Registers a custom configurable machine upgrade item using a configuration builder. */
  public DeferredItem<AddonUpgradeItem> registerUpgradeItem(
      String name, Item.Properties properties, Consumer<AddonUpgradeItem.Builder> configurator) {
    ensureNotRegistered();
    AddonUpgradeItem.Builder builder = AddonUpgradeItem.builder(properties);
    configurator.accept(builder);
    return this.items.register(name, builder::build);
  }

  /**
   * Registers a configurable electric tool (e.g. electric wrench, tree tap, shears, or multipurpose
   * tool).
   */
  public DeferredItem<AddonElectricTool> registerElectricTool(
      String name,
      Item.Properties properties,
      double capacity,
      double transferLimit,
      int tier,
      double energyPerOperation) {
    ensureNotRegistered();
    return this.items.register(
        name,
        () -> new AddonElectricTool(properties, capacity, transferLimit, tier, energyPerOperation));
  }

  /**
   * Adds this add-on's blocks and items to its mod event bus. Call once from the mod constructor.
   */
  public void register(IEventBus modEventBus) {
    Objects.requireNonNull(modEventBus, "modEventBus");
    ensureNotRegistered();
    this.registered = true;
    this.blocks.register(modEventBus);
    this.items.register(modEventBus);
    this.blockEntities.register(modEventBus);
    this.recipeTypes.register(modEventBus);
    this.recipeSerializers.register(modEventBus);
    this.menuTypes.register(modEventBus);
    this.creativeTabs.register(modEventBus);
  }

  /** Builds a resource id inside this add-on's namespace. */
  public ResourceLocation id(String path) {
    return ResourceLocation.fromNamespaceAndPath(this.namespace, path);
  }

  public String namespace() {
    return this.namespace;
  }

  private void ensureNotRegistered() {
    if (this.registered) {
      throw new IllegalStateException(
          "IC2R add-on content for namespace '" + this.namespace + "' has already been registered");
    }
  }
}
