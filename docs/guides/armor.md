# Chapter 6: Electric Armor & Jetpacks

IC2 electric armors absorb incoming damage by discharging energy instead of taking durability loss. When fully charged, high-tier armor suits (like NanoSuit and QuantumSuit) make the player nearly invulnerable.

---

## 1. Electric Armor Suits (`AddonElectricArmor`)

To create an electric armor piece (helmets, chestplates, leggings, boots):

```java
import ic2.api.item.armor.AddonElectricArmor;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public static final DeferredItem<AddonElectricArmor> REINFORCED_CHESTPLATE =
    MyAddon.CONTENT.registerElectricArmor(
        "reinforced_chestplate",
        ArmorMaterials.DIAMOND,
        ArmorItem.Type.CHESTPLATE,
        new Item.Properties(),
        1_000_000, // 1,000,000 EU max capacity
        1000,      // Transfer Rate
        2,         // Tier 2 (MV)
        1000.0,    // 1000 EU consumed per damage point absorbed
        0.9        // Absorbs 90% of incoming damage!
    );
```

---

## 2. Wearable Battery Backpacks (Lappacks)

Lappacks are chest-slot batteries that automatically recharge electric tools and handheld items in your main hand and offhand while you work:

```java
import ic2.api.item.armor.AddonElectricArmor;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public static final DeferredItem<AddonElectricArmor> DENSE_LAPPACK =
    MyAddon.CONTENT.registerEnergyPack(
        "dense_lappack",
        ArmorMaterials.IRON,
        new Item.Properties(),
        2_000_000, // 2,000,000 EU capacity
        2000,      // Transfer Rate
        2          // Tier 2
    );
```

---

## 3. Electric Jetpacks (`AddonElectricJetpack`)

Electric jetpacks allow players to fly through the air, hover smoothly in place, and activate speed boosts using the IC2 keybindings:

```java
import ic2.api.item.armor.AddonElectricJetpack;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;

public static final DeferredItem<AddonElectricJetpack> ADVANCED_JETPACK =
    MyAddon.CONTENT.registerJetpack(
        "advanced_jetpack",
        ArmorMaterials.IRON,
        new Item.Properties().rarity(Rarity.RARE),
        100_000, // 100k EU capacity
        250,     // Transfer Rate
        1,       // Tier 1
        10.0,    // 10 EU drained per tick while thrusting upwards
        0.3F     // Thrust power (vertical acceleration speed)
    );
```

### Controls:
- **Jump Key (Space)**: Upward flight thrust.
- **IC2 Mode Key (M) + Jump**: Toggle **Hover Mode** (prevents falling and keeps you floating gently).
- **IC2 Boost Key (Left Ctrl)**: Fast horizontal flight propulsion.
