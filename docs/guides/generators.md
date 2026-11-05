# Chapter 3: Creating Custom Generators

Generators produce Energy Units (EU) and emit that power into adjacent IC2 cables, machines, or energy storage blocks.

IC2 Rebranded supports two generator models:
1. **Fuel-Burning Generators** (using `AddonGeneratorBlockEntity`): Consumes fuel items, lava, or fluids.
2. **Passive / Solar Generators** (using `AddonEnergyBlockEntity`): Generates EU from the sun, heat differentials, or environment.

---

## 1. Fuel-Burning Generator (Solid or Fluid)

Extend `AddonGeneratorBlockEntity` and implement `gainFuel()`:

```java
package com.example.myaddon.block.entity;

import ic2.api.block.AddonGeneratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BioGeneratorBlockEntity extends AddonGeneratorBlockEntity {

    public BioGeneratorBlockEntity(
            BlockEntityType<? extends AddonGeneratorBlockEntity> type,
            BlockPos pos,
            BlockState state) {
        super(
            type,
            pos,
            state,
            16.0,  // Production: 16 EU per tick while burning fuel
            1,     // Voltage Tier: 1 (Low Voltage, 32 EU/t packets)
            20_000 // Internal Buffer: 20,000 EU max storage
        );
    }

    /**
     * Called whenever the generator has run out of fuel and needs more.
     * Return true if fuel was successfully consumed; false otherwise.
     */
    @Override
    public boolean gainFuel() {
        ItemStack fuelItem = this.chargeSlot.getContainer().getItem(0);

        // Burn biomass, saplings, or blaze rods
        if (!fuelItem.isEmpty() && fuelItem.is(Items.BLAZE_ROD)) {
            fuelItem.shrink(1);
            this.fuel += 1200; // Runs for 1200 ticks (60 seconds)
            return true;
        }

        return false;
    }
}
```

---

## 2. Passive / Environmental Generator (e.g. Solar)

For solar panels or passive generators that generate EU continuously based on world conditions, extend `AddonEnergyBlockEntity`:

```java
package com.example.myaddon.block.entity;

import ic2.api.block.AddonEnergyBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class DarkSolarPanelBlockEntity extends AddonEnergyBlockEntity {

    public DarkSolarPanelBlockEntity(
            BlockEntityType<?> type,
            BlockPos pos,
            BlockState state) {
        super(
            type,
            pos,
            state,
            32_000, // Max Storage: 32,000 EU
            1,      // Tier: 1 (Low Voltage)
            true,   // isSource: emits power to neighbors
            false   // isSink: does not consume external power
        );
    }

    @Override
    public void serverTick() {
        super.serverTick();

        // Check if skylight reaches the panel and it is daytime
        if (this.level != null && !this.level.isClientSide) {
            boolean canSeeSky = this.level.canSeeSky(this.worldPosition.above());
            boolean isDay = this.level.isDay();

            if (canSeeSky && isDay) {
                // Generate 20 EU per tick
                this.energy.addEnergy(20.0);
            }
        }
    }
}
```

---

## 3. Dynamic Block Lighting When Active

In IC2 Rebranded, active generators and furnaces can emit block light when operating.

You can enable dynamic lighting using the `AddonEnergyBlock.litWhenActive` helper:

```java
import ic2.api.block.AddonEnergyBlock;
import ic2.core.block.tileentity.Ic2TileEntityBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public static final DeferredBlock<AddonWrenchableBlock> BIO_GENERATOR =
    MyAddon.CONTENT.registerWrenchableBlock(
        "bio_generator",
        BlockBehaviour.Properties.of()
            .strength(3.0F, 6.0F)
            // Emits light level 13 when ACTIVE is true, 0 when idle
            .lightLevel(AddonEnergyBlock.litWhenActive(Ic2TileEntityBlock.ACTIVE, 13)),
        (pos, state) -> new BioGeneratorBlockEntity(BIO_GENERATOR_BE.get(), pos, state)
    );
```

Whenever the generator is burning fuel (`getActive() == true`), the block state's `ACTIVE` property is set, lighting up the area like a vanilla furnace.
