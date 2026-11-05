package ic2.api.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Fluent builder for generating IC2 Rebranded machine recipe JSON definitions.
 *
 * <p>Useful for data generators, test fixtures, or dynamic recipe generation in add-ons.
 */
public final class AddonRecipeBuilder {
  private final String type;
  private String ingredientItem;
  private String ingredientTag;
  private int ingredientCount = 1;
  private final List<OutputEntry> results = new ArrayList<>();
  private Integer minHeat;
  private Integer duration;
  private Integer fluid;
  private Integer hardness;

  private record OutputEntry(String item, int count) {}

  private AddonRecipeBuilder(String type) {
    this.type = Objects.requireNonNull(type, "type");
  }

  public static AddonRecipeBuilder macerator() {
    return new AddonRecipeBuilder("ic2:macerator");
  }

  public static AddonRecipeBuilder compressor() {
    return new AddonRecipeBuilder("ic2:compressor");
  }

  public static AddonRecipeBuilder extractor() {
    return new AddonRecipeBuilder("ic2:extractor");
  }

  public static AddonRecipeBuilder centrifuge() {
    return new AddonRecipeBuilder("ic2:centrifuge");
  }

  public static AddonRecipeBuilder blastFurnace() {
    return new AddonRecipeBuilder("ic2:blast_furnace");
  }

  public static AddonRecipeBuilder blockCutter() {
    return new AddonRecipeBuilder("ic2:block_cutter");
  }

  public static AddonRecipeBuilder oreWasher() {
    return new AddonRecipeBuilder("ic2:ore_washer");
  }

  public static AddonRecipeBuilder metalFormerCutting() {
    return new AddonRecipeBuilder("ic2:metal_former_cutting");
  }

  public static AddonRecipeBuilder metalFormerExtruding() {
    return new AddonRecipeBuilder("ic2:metal_former_extruding");
  }

  public static AddonRecipeBuilder metalFormerRolling() {
    return new AddonRecipeBuilder("ic2:metal_former_rolling");
  }

  public AddonRecipeBuilder inputItem(String itemId) {
    return inputItem(itemId, 1);
  }

  public AddonRecipeBuilder inputItem(String itemId, int count) {
    this.ingredientItem = Objects.requireNonNull(itemId, "itemId");
    this.ingredientTag = null;
    this.ingredientCount = Math.max(1, count);
    return this;
  }

  public AddonRecipeBuilder inputTag(String tagId) {
    return inputTag(tagId, 1);
  }

  public AddonRecipeBuilder inputTag(String tagId, int count) {
    this.ingredientTag = Objects.requireNonNull(tagId, "tagId");
    this.ingredientItem = null;
    this.ingredientCount = Math.max(1, count);
    return this;
  }

  public AddonRecipeBuilder output(String itemId) {
    return output(itemId, 1);
  }

  public AddonRecipeBuilder output(String itemId, int count) {
    this.results.add(new OutputEntry(Objects.requireNonNull(itemId, "itemId"), Math.max(1, count)));
    return this;
  }

  public AddonRecipeBuilder minHeat(int heat) {
    this.minHeat = heat;
    return this;
  }

  public AddonRecipeBuilder duration(int durationTicks) {
    this.duration = durationTicks;
    return this;
  }

  public AddonRecipeBuilder fluid(int fluidAmount) {
    this.fluid = fluidAmount;
    return this;
  }

  public AddonRecipeBuilder hardness(int hardness) {
    this.hardness = hardness;
    return this;
  }

  public JsonObject build() {
    JsonObject root = new JsonObject();
    root.addProperty("type", this.type);

    JsonObject ingredient = new JsonObject();
    if (this.ingredientItem != null) {
      ingredient.addProperty("item", this.ingredientItem);
    } else if (this.ingredientTag != null) {
      ingredient.addProperty("tag", this.ingredientTag);
    } else {
      throw new IllegalStateException("Recipe must declare either an input item or input tag");
    }
    if (this.ingredientCount > 1) {
      ingredient.addProperty("count", this.ingredientCount);
    }
    root.add("ingredient", ingredient);

    if (this.results.isEmpty()) {
      throw new IllegalStateException("Recipe must declare at least one output result");
    }

    if (this.results.size() == 1) {
      JsonObject res = new JsonObject();
      res.addProperty("item", this.results.get(0).item());
      if (this.results.get(0).count() > 1) {
        res.addProperty("count", this.results.get(0).count());
      }
      root.add("result", res);
    } else {
      JsonArray array = new JsonArray();
      for (OutputEntry entry : this.results) {
        JsonObject res = new JsonObject();
        res.addProperty("item", entry.item());
        if (entry.count() > 1) {
          res.addProperty("count", entry.count());
        }
        array.add(res);
      }
      root.add("result", array);
    }

    if (this.minHeat != null) {
      root.addProperty("minHeat", this.minHeat);
    }
    if (this.duration != null) {
      root.addProperty("duration", this.duration);
    }
    if (this.fluid != null) {
      root.addProperty("fluid", this.fluid);
    }
    if (this.hardness != null) {
      root.addProperty("hardness", this.hardness);
    }

    return root;
  }
}
