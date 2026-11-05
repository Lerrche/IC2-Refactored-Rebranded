package ic2.api.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

class AddonRecipeBuilderTest {

  @Test
  void testMaceratorRecipe() {
    JsonObject json =
        AddonRecipeBuilder.macerator()
            .inputItem("minecraft:iron_ingot")
            .output("ic2:iron_dust")
            .build();

    assertEquals("ic2:macerator", json.get("type").getAsString());
    assertEquals(
        "minecraft:iron_ingot", json.getAsJsonObject("ingredient").get("item").getAsString());
    assertEquals("ic2:iron_dust", json.getAsJsonObject("result").get("item").getAsString());
  }

  @Test
  void testCompressorRecipeWithCounts() {
    JsonObject json =
        AddonRecipeBuilder.compressor()
            .inputItem("minecraft:coal", 8)
            .output("ic2:industrial_diamond")
            .build();

    assertEquals("ic2:compressor", json.get("type").getAsString());
    assertEquals(8, json.getAsJsonObject("ingredient").get("count").getAsInt());
    assertEquals(
        "ic2:industrial_diamond", json.getAsJsonObject("result").get("item").getAsString());
  }

  @Test
  void testCentrifugeRecipeWithMinHeat() {
    JsonObject json =
        AddonRecipeBuilder.centrifuge()
            .inputItem("ic2:clay_dust", 4)
            .output("ic2:silicon_dioxide_dust")
            .minHeat(250)
            .build();

    assertEquals("ic2:centrifuge", json.get("type").getAsString());
    assertEquals(250, json.get("minHeat").getAsInt());
  }

  @Test
  void testBlastFurnaceRecipeMultipleOutputs() {
    JsonObject json =
        AddonRecipeBuilder.blastFurnace()
            .inputItem("ic2:crushed_iron")
            .output("ic2:steel_ingot")
            .output("ic2:slag")
            .fluid(1)
            .duration(6000)
            .build();

    assertEquals("ic2:blast_furnace", json.get("type").getAsString());
    assertEquals(1, json.get("fluid").getAsInt());
    assertEquals(6000, json.get("duration").getAsInt());
    assertTrue(json.get("result").isJsonArray());

    JsonArray results = json.getAsJsonArray("result");
    assertEquals(2, results.size());
    assertEquals("ic2:steel_ingot", results.get(0).getAsJsonObject().get("item").getAsString());
    assertEquals("ic2:slag", results.get(1).getAsJsonObject().get("item").getAsString());
  }

  @Test
  void testBlockCutterHardness() {
    JsonObject json =
        AddonRecipeBuilder.blockCutter()
            .inputItem("minecraft:oak_log")
            .output("minecraft:oak_planks", 6)
            .hardness(3)
            .build();

    assertEquals("ic2:block_cutter", json.get("type").getAsString());
    assertEquals(3, json.get("hardness").getAsInt());
    assertEquals(6, json.getAsJsonObject("result").get("count").getAsInt());
  }

  @Test
  void testTagInput() {
    JsonObject json =
        AddonRecipeBuilder.extractor().inputTag("c:rubber_wood").output("ic2:rubber", 3).build();

    assertEquals("ic2:extractor", json.get("type").getAsString());
    assertEquals("c:rubber_wood", json.getAsJsonObject("ingredient").get("tag").getAsString());
    assertEquals(3, json.getAsJsonObject("result").get("count").getAsInt());
  }

  @Test
  void testValidationErrors() {
    assertThrows(
        IllegalStateException.class,
        () -> AddonRecipeBuilder.macerator().output("minecraft:dirt").build());

    assertThrows(
        IllegalStateException.class,
        () -> AddonRecipeBuilder.macerator().inputItem("minecraft:dirt").build());
  }
}
