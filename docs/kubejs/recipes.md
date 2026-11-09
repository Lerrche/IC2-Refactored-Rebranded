# Recipe Creation API

## 3. Recipe Creation API (All Machines & Overloads)

### Macerator
Grinds items and ores into crushed/powdered forms.
- **Signature**: `IndustrialJS.macerator(output, input)` -> `IndustrialJS`
```javascript
IndustrialJS.macerator('2x ic2:crushed_iron', 'minecraft:raw_iron');
IndustrialJS.macerator('4x minecraft:bone_meal', 'minecraft:bone');
```

### Compressor
Compresses materials under extreme hydraulic pressure.
- **Signature**: `IndustrialJS.compressor(output, input)` -> `IndustrialJS`
```javascript
IndustrialJS.compressor('minecraft:diamond', '64x minecraft:coal');
IndustrialJS.compressor('ic2:dense_copper_plate', '9x ic2:copper_plate');
```

### Extractor
Extracts rubber, dyes, and biochemical fluids from raw organic materials.
- **Signature**: `IndustrialJS.extractor(output, input)` -> `IndustrialJS`
```javascript
IndustrialJS.extractor('3x ic2:rubber', 'ic2:resin');
IndustrialJS.extractor('ic2:rubber', 'ic2:rubber_wood');
```

### Ore Washing Plant
Cleans crushed ores using water to separate stone dust and valuable sub-products.
- **Overload 1**: `IndustrialJS.oreWasher(outputs, input, waterAmount)` -> `IndustrialJS`
- **Overload 2**: `IndustrialJS.oreWasher(outputs, input)` -> `IndustrialJS` *(defaults to 1000 mB water)*
```javascript
// Outputs array: [Primary, Secondary, Tertiary]
IndustrialJS.oreWasher(
  ['ic2:purified_iron', '2x ic2:small_iron_dust', 'ic2:stone_dust'],
  'ic2:crushed_iron',
  1000 // 1000 mB water
);
```

### Thermal Centrifuge
Spins purified ores under high rotational speed and temperature to isolate refined metals and rare dusts.
- **Overload 1**: `IndustrialJS.thermalCentrifuge(outputs, input, minHeat)` -> `IndustrialJS`
- **Overload 2**: `IndustrialJS.thermalCentrifuge(outputs, input)` -> `IndustrialJS` *(defaults to 500 minHeat)*
```javascript
// Requires 1500 degrees minimum temperature:
IndustrialJS.thermalCentrifuge(
  ['ic2:refined_iron', 'ic2:small_gold_dust'],
  'ic2:purified_iron',
  1500
);
```

### Blast Furnace
Refines iron into refined iron and slag using heat and compressed air.
- **Overload 1**: `IndustrialJS.blastFurnace(outputs, input, duration, minHeat)` -> `IndustrialJS`
- **Overload 2**: `IndustrialJS.blastFurnace(outputs, input)` -> `IndustrialJS` *(defaults to 6000 ticks, 1000 minHeat)*
```javascript
IndustrialJS.blastFurnace(
  ['ic2:refined_iron_ingot', 'ic2:slag'],
  'minecraft:iron_ingot',
  6000, // Duration in ticks
  1000  // Minimum operational heat
);
```

### Block Cutter
Slices dense blocks and ingots into precision plates with minimal waste.
- **Overload 1**: `IndustrialJS.blockCutter(output, input, hardness)` -> `IndustrialJS`
- **Overload 2**: `IndustrialJS.blockCutter(output, input)` -> `IndustrialJS` *(defaults to hardness 1)*
```javascript
// Standard plate cutting (hardness 1):
IndustrialJS.blockCutter('9x ic2:tin_plate', 'ic2:tin_block');

// Custom required hardness (3 for tough obsidian/steel blocks):
IndustrialJS.blockCutter('9x ic2:steel_plate', 'ic2:steel_block', 3);
```

### Metal Former
The Metal Former operates in three distinct modes: Rolling, Cutting, and Extruding.
- **Rolling**: `IndustrialJS.metalFormerRolling(output, input)` -> `IndustrialJS` (Rolls ingots into dense plates)
- **Cutting**: `IndustrialJS.metalFormerCutting(output, input)` -> `IndustrialJS` (Cuts plates into insulated or uninsulated cables)
- **Extruding**: `IndustrialJS.metalFormerExtruding(output, input)` -> `IndustrialJS` (Extrudes ingots/plates into casings, rods, and tubes)
```javascript
IndustrialJS.metalFormerRolling('ic2:bronze_plate', 'ic2:bronze_ingot');
IndustrialJS.metalFormerCutting('3x ic2:copper_cable', 'ic2:copper_plate');
IndustrialJS.metalFormerExtruding('ic2:casing_bronze', 'ic2:bronze_ingot');
```

### Molecular Transformer
Matter transmutation device that consumes immense amounts of EU energy to transform elements.
- **Signature**: `IndustrialJS.molecularTransformer(output, input, euCost)` -> `IndustrialJS`
```javascript
// Transmute Wither Skeleton Skull into a Nether Star for 250,000,000 EU:
IndustrialJS.molecularTransformer('minecraft:nether_star', 'minecraft:wither_skeleton_skull', 250000000.0);

// Transmute Iron Block into Gold Block for 5,000,000 EU:
IndustrialJS.molecularTransformer('minecraft:gold_block', 'minecraft:iron_block', 5000000.0);
```

### Canning Machine: Fluid Enrichment
Enriches or chemically alters a base fluid by mixing it with an item/additive in the Canning Machine.
- **Signature**: `IndustrialJS.cannerEnrich(outputFluid, inputFluid, additive)` -> `IndustrialJS`
```javascript
// Enrich 1000 mB water with coolant dust into 1000 mB coolant fluid:
IndustrialJS.cannerEnrich('1000mB ic2:coolant', '1000mB minecraft:water', 'ic2:coolant_drop');
```

### Canning Machine: Fluid Bottling / Filling
Bottles liquids from the Canning Machine's internal tank into containers (bottles, tin cans, fuel cans).
- **Signature**: `IndustrialJS.cannerBottle(output, container, fillFluid)` -> `IndustrialJS`
```javascript
// Fill glass bottle with 250 mB biomass to make a potion:
IndustrialJS.cannerBottle('minecraft:potion', 'minecraft:glass_bottle', '250mB ic2:biomass');
```

### Scrapbox Drops
Registers new surprise items that can randomly drop when opening an IC2 Scrap Box.
- **Signature**: `IndustrialJS.scrapbox(item, weight)` -> `IndustrialJS`
```javascript
IndustrialJS.scrapbox('minecraft:diamond', 0.05);   // 5% relative weight
IndustrialJS.scrapbox('minecraft:emerald', 0.10);   // 10% relative weight
IndustrialJS.scrapbox('ic2:iridium_ore', 0.01);     // 1% rare drop
```

### Matter Fabricator Amplifiers
Registers custom items or blocks as scrap amplifiers in the Matter Fabricator, accelerating UU-Matter production and drastically reducing EU energy demands.
- **Signature**: `IndustrialJS.amplifier(item, amplification)` -> `IndustrialJS`
```javascript
IndustrialJS.amplifier('minecraft:cobblestone', 100);
IndustrialJS.amplifier('minecraft:netherrack', 250);
IndustrialJS.amplifier('ic2:scrap', 5000);
IndustrialJS.amplifier('ic2:scrap_box', 45000);
```

### Mining Laser Super-Heat Smelting
Registers custom in-world block transformations when struck by the Mining Laser in **Super-Heat** beam mode.
- **Signature**: `IndustrialJS.laserSmelting(input, output)` -> `void`
- **Query**: `IndustrialJS.getLaserSmelting(inputStack)` -> `ItemStack`
```javascript
IndustrialJS.laserSmelting('minecraft:sand', 'minecraft:glass');
IndustrialJS.laserSmelting('minecraft:cobblestone', 'minecraft:stone');
IndustrialJS.laserSmelting('minecraft:raw_iron_block', 'minecraft:iron_block');
```

---
