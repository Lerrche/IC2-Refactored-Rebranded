# Chapter 2: Creating Custom Machines

An electric machine consumes Energy Units (EU) each tick to perform an operation (such as crushing, purifying, compressing, or smelting items).

IC2 Rebranded provides two classes that take care of 95% of machine boilerplate:
1. **`AddonMachineBlockEntity`**: Handles energy storage, power consumption, progress tracking, operation duration, and recipe ticking.
2. **`AddonWrenchableBlock`**: Gives your machine horizontal facing (North, South, East, West) and full IC2 Wrench compatibility (rotating with right-click, dismantling with Shift+Right-Click).

---

## 1. Creating the Block Entity

Extend `AddonMachineBlockEntity`:

```java
package com.example.myaddon.block.entity;

import ic2.api.block.AddonMachineBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MatterInfuserBlockEntity extends AddonMachineBlockEntity {

    public MatterInfuserBlockEntity(
            BlockEntityType<? extends AddonMachineBlockEntity> type,
            BlockPos pos,
            BlockState state) {
        super(
            type,
            pos,
            state,
            1,      // Tier 1 (Low Voltage, accepts up to 32 EU/t)
            10_000, // Energy Buffer: 10,000 EU max storage
            4.0,    // Power Consumption: 4 EU/tick while running
            200     // Operation Length: 200 ticks (10 seconds) per operation
        );
    }

    /**
     * Called every tick on the server while the machine has enough energy.
     * Return true if the machine has valid input items and is ready to work.
     */
    @Override
    public boolean canProcess() {
        ItemStack input = this.inventory.getItem(0);
        return !input.isEmpty() && input.is(Items.RAW_IRON);
    }

    /**
     * Called when the progress bar reaches 100%.
     * Consume your input items and produce the finished output items here.
     */
    @Override
    public void processItem() {
        ItemStack input = this.inventory.getItem(0);
        input.shrink(1);

        ItemStack currentOutput = this.inventory.getItem(1);
        if (currentOutput.isEmpty()) {
            this.inventory.setItem(1, new ItemStack(Items.IRON_INGOT, 2)); // 2x output!
        } else {
            currentOutput.grow(2);
        }
    }
}
```

---

## 2. Registering the Block & Block Entity

In your registration class, register the block using `CONTENT.registerWrenchableBlock` and the block entity using `CONTENT.registerBlockEntityType`:

```java
import com.example.myaddon.MyAddon;
import com.example.myaddon.block.entity.MatterInfuserBlockEntity;
import ic2.api.block.AddonWrenchableBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

public class MyBlocks {

    // 1. Register the block with Wrench rotation and metal sound
    public static final DeferredBlock<AddonWrenchableBlock> MATTER_INFUSER =
        MyAddon.CONTENT.registerWrenchableBlock(
            "matter_infuser",
            BlockBehaviour.Properties.of().strength(3.0F, 6.0F),
            (pos, state) -> new MatterInfuserBlockEntity(MATTER_INFUSER_BE.get(), pos, state)
        );

    // 2. Register the matching Block Entity Type
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MatterInfuserBlockEntity>>
        MATTER_INFUSER_BE = MyAddon.CONTENT.registerBlockEntityType(
            "matter_infuser",
            (pos, state) -> new MatterInfuserBlockEntity(MATTER_INFUSER_BE.get(), pos, state),
            MATTER_INFUSER
        );
}
```

---

## 3. Adding Forge Energy (FE) Compatibility

To allow other mods (like Mekanism, Thermal, or Ender IO) to power your machine with Forge Energy cables or generators, register the capability in your mod constructor:

```java
private void registerCapabilities(RegisterCapabilitiesEvent event) {
    // 1 EU = 2 FE automatic bidirectional bridge
    AddonEnergyCapabilities.registerEnergyStorage(event, MyBlocks.MATTER_INFUSER_BE.get());
}
```

---

## 4. Machine Upgrades Support

`AddonMachineBlockEntity` automatically supports IC2 upgrades! If a player inserts an Overclock Upgrade, Energy Storage Upgrade, or Transformer Upgrade into the machine:
- **Overclock Upgrade**: Multiplies process speed and adjusts energy demand.
- **Energy Storage Upgrade**: Expands the EU buffer.
- **Transformer Upgrade**: Raises the supported input voltage tier so the machine does not explode or reject higher voltages.
