package ic2.integration.farmersdelight;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FarmersDelightCompatTest {

  @Test
  @DisplayName("isCuttingBoard identifies standard and modded cutting board blocks")
  void testIsCuttingBoard() {
    assertTrue(
        FarmersDelightCompat.isCuttingBoard(
            ResourceLocation.fromNamespaceAndPath("farmersdelight", "cutting_board")));
    assertTrue(
        FarmersDelightCompat.isCuttingBoard(
            ResourceLocation.fromNamespaceAndPath("farmersdelight", "oak_cutting_board")));
    assertTrue(
        FarmersDelightCompat.isCuttingBoard(
            ResourceLocation.fromNamespaceAndPath("farmersrespite", "cutting_board")));
    assertTrue(
        FarmersDelightCompat.isCuttingBoard(
            ResourceLocation.fromNamespaceAndPath("some_addon", "stone_cutting_board")));

    assertFalse(
        FarmersDelightCompat.isCuttingBoard(
            ResourceLocation.fromNamespaceAndPath("minecraft", "crafting_table")));
    assertFalse(
        FarmersDelightCompat.isCuttingBoard(
            ResourceLocation.fromNamespaceAndPath("ic2", "nuclear_reactor")));
    assertFalse(FarmersDelightCompat.isCuttingBoard(null));
  }

  @Test
  @DisplayName("Conventional and Farmer's Delight tool tags contain IC2 Cutter and Chainsaw")
  void testToolTagsConfiguration() {
    JsonObject shearsTag = readTag("data/c/tags/item/tools/shears.json");
    assertNotNull(shearsTag);
    JsonArray shearsValues = shearsTag.getAsJsonArray("values");
    assertTrue(containsTagValue(shearsValues, "ic2:cutter"));
    assertTrue(containsTagValue(shearsValues, "ic2:chainsaw"));
    assertTrue(containsTagValue(shearsValues, "gravisuite:advanced_chainsaw"));

    JsonObject knivesTag = readTag("data/c/tags/item/tools/knives.json");
    assertNotNull(knivesTag);
    JsonArray knivesValues = knivesTag.getAsJsonArray("values");
    assertTrue(containsTagValue(knivesValues, "ic2:chainsaw"));
    assertTrue(containsTagValue(knivesValues, "gravisuite:advanced_chainsaw"));

    JsonObject fdKnivesTag = readTag("data/farmersdelight/tags/item/tools/knives.json");
    assertNotNull(fdKnivesTag);
    assertTrue(containsTagValue(fdKnivesTag.getAsJsonArray("values"), "#c:tools/knives"));

    JsonObject fdShearsTag = readTag("data/farmersdelight/tags/item/tools/shears.json");
    assertNotNull(fdShearsTag);
    assertTrue(containsTagValue(fdShearsTag.getAsJsonArray("values"), "#c:tools/shears"));

    JsonObject mcShearsTag = readTag("data/minecraft/tags/item/shears.json");
    assertNotNull(mcShearsTag);
    assertTrue(containsTagValue(mcShearsTag.getAsJsonArray("values"), "ic2:cutter"));
    assertTrue(containsTagValue(mcShearsTag.getAsJsonArray("values"), "ic2:chainsaw"));
  }

  private JsonObject readTag(String path) {
    try (InputStream stream = getClass().getClassLoader().getResourceAsStream(path)) {
      if (stream == null) {
        throw new IllegalArgumentException("Tag file not found on classpath: " + path);
      }
      return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
          .getAsJsonObject();
    } catch (Exception e) {
      throw new RuntimeException("Failed to read tag: " + path, e);
    }
  }

  private boolean containsTagValue(JsonArray values, String expected) {
    for (int i = 0; i < values.size(); i++) {
      var elem = values.get(i);
      if (elem.isJsonPrimitive() && elem.getAsString().equals(expected)) {
        return true;
      }
      if (elem.isJsonObject() && elem.getAsJsonObject().has("id")) {
        if (elem.getAsJsonObject().get("id").getAsString().equals(expected)) {
          return true;
        }
      }
    }
    return false;
  }
}
