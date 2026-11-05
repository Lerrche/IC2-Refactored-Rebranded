# Chapter 7: Custom Machine Upgrades

Machine upgrades can be inserted into the 4 upgrade slots on any IC2 machine to boost its speed, increase its energy buffer, reduce heat in reactors, or upgrade its voltage tier.

IC2 Rebranded provides a fluent builder to create custom upgrades in just a few lines.

---

## 1. Creating Upgrades via `CONTENT.registerUpgradeItem`

Use `CONTENT.registerUpgradeItem` and configure your upgrade with the fluent builder:

```java
import ic2.api.upgrade.AddonUpgradeItem;
import ic2.api.upgrade.UpgradableProperty;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

// 1. Super Overclock Upgrade: 40% faster (0.6x time), 50% higher power demand
public static final DeferredItem<AddonUpgradeItem> HYPER_OVERCLOCKER =
    MyAddon.CONTENT.registerUpgradeItem(
        "hyper_overclocker",
        new Item.Properties().stacksTo(16),
        builder -> builder
            .suitableFor(UpgradableProperty.Processing)
            .processTimeMultiplier(0.6)
            .energyDemandMultiplier(1.5)
    );

// 2. High-Capacity Energy Buffer Upgrade: Adds +50,000 EU storage per module
public static final DeferredItem<AddonUpgradeItem> DENSE_ENERGY_STORAGE_UPGRADE =
    MyAddon.CONTENT.registerUpgradeItem(
        "dense_energy_storage_upgrade",
        new Item.Properties().stacksTo(16),
        builder -> builder
            .suitableFor(UpgradableProperty.EnergyStorage)
            .extraEnergyStorage(50_000)
    );

// 3. Super Transformer Upgrade: Raises supported input tier by +2
public static final DeferredItem<AddonUpgradeItem> ADVANCED_TRANSFORMER_UPGRADE =
    MyAddon.CONTENT.registerUpgradeItem(
        "advanced_transformer_upgrade",
        new Item.Properties().stacksTo(4),
        builder -> builder
            .suitableFor(UpgradableProperty.Transformer)
            .tierBoost(2)
    );
```

---

## 2. Using Standalone `UpgradeDefinition`

If you already have your own custom item class and simply want to attach IC2 upgrade behavior to it, create an `UpgradeDefinition`:

```java
import ic2.api.upgrade.UpgradeDefinition;

// Creates a definition that makes machines 25% faster
UpgradeDefinition speedDef = UpgradeDefinition.speed(0.75, 1.3);

// Creates a definition that adds 10,000 EU storage
UpgradeDefinition energyDef = UpgradeDefinition.energyStorage(10_000);

// Creates a transformer definition (+1 tier)
UpgradeDefinition transformerDef = UpgradeDefinition.transformer(1);
```

You can then implement `IUpgradeItem` on your item and delegate directly to your definition:

```java
import ic2.api.upgrade.IUpgradeItem;
import ic2.api.upgrade.UpgradeDefinition;
import net.minecraft.world.item.Item;

public class MyCustomUpgradeItem extends Item implements IUpgradeItem {
    private final UpgradeDefinition definition = UpgradeDefinition.speed(0.7, 1.4);

    public MyCustomUpgradeItem(Properties props) {
        super(props);
    }

    @Override
    public boolean isSuitableFor(ItemStack stack, Set<UpgradableProperty> types) {
        return this.definition.isSuitableFor(types);
    }

    @Override
    public void onTick(ItemStack stack, IUpgradableBlock block) {
        this.definition.onTick(block);
    }

    @Override
    public double getProcessTimeMultiplier(ItemStack stack, IUpgradableBlock block) {
        return this.definition.getProcessTimeMultiplier();
    }

    @Override
    public double getEnergyDemandMultiplier(ItemStack stack, IUpgradableBlock block) {
        return this.definition.getEnergyDemandMultiplier();
    }

    @Override
    public int getExtraEnergyStorage(ItemStack stack, IUpgradableBlock block) {
        return this.definition.getExtraEnergyStorage();
    }

    @Override
    public int getExtraTier(ItemStack stack, IUpgradableBlock block) {
        return this.definition.getTierBoost();
    }
}
```
