# Chapter 8: Machine Recipes & Data Generation

IC2 Rebranded uses standard data-driven JSON recipes. You can add recipes for IC2 machines (Macerator, Compressor, Extractor, Centrifuge, Blast Furnace, etc.) using:
1. Standard JSON recipe files in `resources/data/<your_mod_id>/recipe/`.
2. Programmatic code generation using `AddonRecipeBuilder`.

---

## 1. Using `AddonRecipeBuilder` in Data Generators

In your NeoForge data generator provider, `AddonRecipeBuilder` gives you a type-safe fluent API:

```java
import ic2.api.recipe.AddonRecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public void buildRecipes(RecipeOutput output) {

    // 1. Macerator Recipe: 1 Granite -> 2 Sand
    AddonRecipeBuilder.macerator()
        .input(Ingredient.of(Items.GRANITE))
        .output(new ItemStack(Items.SAND, 2))
        .save(output, "myaddon:macerator/granite_to_sand");

    // 2. Compressor Recipe: 9 Copper Ingots -> 1 Copper Block
    AddonRecipeBuilder.compressor()
        .input(Ingredient.of(Items.COPPER_INGOT), 9)
        .output(new ItemStack(Items.COPPER_BLOCK))
        .save(output, "myaddon:compressor/copper_ingot_to_block");

    // 3. Extractor Recipe: 1 Slime Block -> 9 Slime Balls
    AddonRecipeBuilder.extractor()
        .input(Ingredient.of(Items.SLIME_BLOCK))
        .output(new ItemStack(Items.SLIME_BALL, 9))
        .save(output, "myaddon:extractor/slime_block_to_balls");

    // 4. Centrifuge Recipe: 1 Magma Cream -> 1 Slimeball + 1 Blaze Powder
    AddonRecipeBuilder.centrifuge()
        .input(Ingredient.of(Items.MAGMA_CREAM))
        .outputs(new ItemStack(Items.SLIME_BALL), new ItemStack(Items.BLAZE_POWDER))
        .save(output, "myaddon:centrifuge/magma_cream");
}
```

---

## 2. Supported IC2 Machine Types in `AddonRecipeBuilder`

| Method | Target Machine | Inputs / Outputs |
|---|---|---|
| `AddonRecipeBuilder.macerator()` | Macerator (Grinder) | 1 Input $\rightarrow$ 1 Output |
| `AddonRecipeBuilder.compressor()` | Compressor | 1 Input $\rightarrow$ 1 Output |
| `AddonRecipeBuilder.extractor()` | Extractor | 1 Input $\rightarrow$ 1 Output |
| `AddonRecipeBuilder.electricFurnace()` | Electric Furnace | 1 Input $\rightarrow$ 1 Output |
| `AddonRecipeBuilder.centrifuge()` | Thermal Centrifuge | 1 Input $\rightarrow$ Up to 3 Outputs |
| `AddonRecipeBuilder.blastFurnace()` | Blast Furnace | 1 Input $\rightarrow$ 1 Output + Slag |
| `AddonRecipeBuilder.blockCutter()` | Block Cutter | 1 Input $\rightarrow$ Sliced items |
| `AddonRecipeBuilder.metalFormer()` | Metal Former | Rolling, Extruding, Cutting |

---

## 3. Writing Plain JSON Recipe Files

If you prefer writing recipes directly as JSON files, place them in `src/main/resources/data/<your_mod_id>/recipe/<name>.json`:

```json
{
  "type": "ic2:macerator",
  "input": {
    "item": "minecraft:granite"
  },
  "output": {
    "id": "minecraft:sand",
    "count": 2
  }
}
```

### Tag-Based Crafting Recipes (Circuit Best Practices)
When creating crafting recipes that require circuits, always use specific tags rather than broad ones:
- **For Advanced Circuit**: Use `#c:circuits/advanced`, `#c:circuit/advanced`, or `"ic2:advanced_circuit"`.
- **For Basic Circuit**: Use `#c:circuits/basic`, `#c:circuit/basic`, or `"ic2:circuit"`.
- *Avoid `#c:circuits` on recipes that require an advanced circuit*, as other mods (like Mekanism) place basic circuits into that category.
