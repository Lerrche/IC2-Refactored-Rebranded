# Chapter 5: Electric Tools & Weapons

IC2 tools run on stored electrical energy instead of standard vanilla durability. When an electric tool is discharged, it does not break; instead, it loses its speed or ability to operate until recharged.

---

## 1. Portable Batteries (`AddonElectricItem`)

To create a rechargeable battery item that can charge tools in your inventory or power armor:

```java
import ic2.api.item.AddonElectricItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;

// 100,000 EU capacity, transfers at 128 EU/t, Tier 1, can supply power to other items
public static final DeferredItem<AddonElectricItem> QUANTUM_BATTERY =
    MyAddon.CONTENT.registerElectricItem(
        "quantum_battery",
        new Item.Properties().rarity(Rarity.UNCOMMON),
        100_000, // Max Charge
        128,     // Transfer Limit
        1,       // Voltage Tier (1 = Low Voltage)
        true     // Can discharge into other items
    );
```

---

## 2. Electric Drills & Chainsaws (`AddonElectricMiningTool`)

Electric mining tools mine blocks quickly while powered and consume a set amount of EU per broken block:

```java
import ic2.api.item.AddonElectricMiningTool;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tiers;
import net.neoforged.neoforge.registries.DeferredItem;

public static final DeferredItem<AddonElectricMiningTool> ADVANCED_DRILL =
    MyAddon.CONTENT.registerElectricMiningTool(
        "advanced_drill",
        new Item.Properties(),
        Tiers.DIAMOND,
        BlockTags.MINEABLE_WITH_PICKAXE,
        50_000,  // Max Charge (50k EU)
        250,     // Transfer Rate (250 EU/t)
        1,       // Tier 1
        16.0F,   // Mining Speed (very fast!)
        100.0    // Energy Cost per block broken (100 EU)
    );
```

---

## 3. Utility Tools: Wrenches, Shears, Treetaps (`AddonElectricTool`)

`AddonElectricTool` is the base for specialized electric tools. It handles energy consumption on right-click or use:

```java
package com.example.myaddon.item;

import ic2.api.item.AddonElectricTool;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class OmniWrenchItem extends AddonElectricTool {

    public OmniWrenchItem() {
        super(
            new Item.Properties().stacksTo(1),
            20_000, // Max Charge
            100,    // Transfer Rate
            1,      // Tier 1
            50.0    // Cost: 50 EU per use
        );
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // Only perform action if we have enough energy
        if (canOperate(context.getItemInHand())) {
            // Your custom wrenching or block turning logic here...

            // Drain energy upon successful operation
            consumeOperationEnergy(context.getItemInHand(), context.getPlayer());
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }
}
```

---

## 4. Electric Weapons (`AddonElectricWeapon`)

Like the iconic NanoSaber, `AddonElectricWeapon` deals devastating damage when charged:

```java
import ic2.api.item.AddonElectricWeapon;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;

public static final DeferredItem<AddonElectricWeapon> PLASMA_BLADE =
    MyAddon.CONTENT.registerElectricWeapon(
        "plasma_blade",
        new Item.Properties().rarity(Rarity.RARE),
        100_000, // Max Charge (100k EU)
        256,     // Transfer Rate
        2,       // Tier 2
        18.0F,   // Active Damage: 18 Attack Damage (9 hearts!)
        150.0    // Energy Cost per hit (150 EU)
    );
```

---

## 5. Working with Electric Items via `ElectricItem.manager`

Never modify NBT tags directly for energy. Instead, use IC2's thread-safe `ElectricItem.manager`:

```java
import ic2.api.item.ElectricItem;
import net.minecraft.world.item.ItemStack;

// 1. Get current charge
double currentEu = ElectricItem.manager.getCharge(stack);

// 2. Charge an item (e.g. 500 EU)
double accepted = ElectricItem.manager.charge(stack, 500.0, 1, false, false);

// 3. Discharge / consume energy (e.g. 100 EU)
double drained = ElectricItem.manager.discharge(stack, 100.0, 1, false, false, false);

// 4. Use charge for an operation (returns true if enough power was available and consumed)
boolean success = ElectricItem.manager.use(stack, 50.0, player);
```
