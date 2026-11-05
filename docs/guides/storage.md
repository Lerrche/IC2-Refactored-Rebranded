# Chapter 4: Energy Storage Blocks (Batteries)

Energy storage blocks (like the BatBox, CESU, MFE, and MFSU) act as large accumulators. They store excess EU from generators and discharge power into downstream machines and cables when needed. They also feature charging and discharging slots for handheld batteries and electric tools.

---

## 1. Creating the Block Entity

Extend `AddonEnergyStorageBlockEntity`:

```java
package com.example.myaddon.block.entity;

import ic2.api.block.AddonEnergyStorageBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DenseBatteryBoxBlockEntity extends AddonEnergyStorageBlockEntity {

    public DenseBatteryBoxBlockEntity(
            BlockEntityType<? extends AddonEnergyStorageBlockEntity> type,
            BlockPos pos,
            BlockState state) {
        super(
            type,
            pos,
            state,
            1_000_000, // Capacity: 1,000,000 EU
            128,       // Output Rate: 128 EU/t max output
            2          // Voltage Tier: 2 (Medium Voltage)
        );
    }
}
```

`AddonEnergyStorageBlockEntity` handles:
- **Top / Input sides**: Accepts incoming EU up to the specified voltage tier.
- **Output side**: Emits packets of EU up to `maxOutput` per tick.
- **Charge & Discharge Slots**: Internal item slots automatically recharge items placed in them or drain energy from batteries into the main storage.
- **Redstone Control**: Responds to redstone signals (emit with signal, emit without signal, do not output).

---

## 2. Registering the Storage Block

Use `CONTENT.registerWrenchableBlock`:

```java
import com.example.myaddon.MyAddon;
import com.example.myaddon.block.entity.DenseBatteryBoxBlockEntity;
import ic2.api.block.AddonWrenchableBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

public class MyContent {

    public static final DeferredBlock<AddonWrenchableBlock> DENSE_BATTERY_BOX =
        MyAddon.CONTENT.registerWrenchableBlock(
            "dense_battery_box",
            BlockBehaviour.Properties.of().strength(3.0F, 6.0F),
            (pos, state) -> new DenseBatteryBoxBlockEntity(DENSE_BATTERY_BOX_BE.get(), pos, state)
        );

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DenseBatteryBoxBlockEntity>>
        DENSE_BATTERY_BOX_BE = MyAddon.CONTENT.registerBlockEntityType(
            "dense_battery_box",
            (pos, state) -> new DenseBatteryBoxBlockEntity(DENSE_BATTERY_BOX_BE.get(), pos, state),
            DENSE_BATTERY_BOX
        );
}
```

---

## 3. Forge Energy (FE) Bridge

To let other mods connect FE cables (from Mekanism, Thermal, Flux Networks, etc.) to your battery block:

```java
private void registerCapabilities(RegisterCapabilitiesEvent event) {
    AddonEnergyCapabilities.registerEnergyStorage(event, MyContent.DENSE_BATTERY_BOX_BE.get());
}
```
Now, other mods can charge your battery with FE or draw FE power from your battery automatically (`1 EU = 2 FE`).
