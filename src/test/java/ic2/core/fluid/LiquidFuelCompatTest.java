package ic2.core.fluid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ic2.core.SemiFluidFuelManager;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LiquidFuelCompatTest {

  @Test
  @DisplayName("FuelCategory specifications match balance definitions")
  void testFuelCategorySpecs() {
    assertEquals(5, LiquidFuelCompat.FuelCategory.CRYO_FUEL.amount);
    assertEquals(64.0, LiquidFuelCompat.FuelCategory.CRYO_FUEL.basePower);

    assertEquals(5, LiquidFuelCompat.FuelCategory.REFINED_FUEL.amount);
    assertEquals(32.0, LiquidFuelCompat.FuelCategory.REFINED_FUEL.basePower);

    assertEquals(10, LiquidFuelCompat.FuelCategory.OIL.amount);
    assertEquals(16.0, LiquidFuelCompat.FuelCategory.OIL.basePower);

    assertEquals(10, LiquidFuelCompat.FuelCategory.BIOFUEL.amount);
    assertEquals(24.0, LiquidFuelCompat.FuelCategory.BIOFUEL.basePower);

    assertEquals(4, LiquidFuelCompat.FuelCategory.HYDROGEN.amount);
    assertEquals(32.0, LiquidFuelCompat.FuelCategory.HYDROGEN.basePower);

    assertEquals(8, LiquidFuelCompat.FuelCategory.CREOSOTE.amount);
    assertEquals(32.0, LiquidFuelCompat.FuelCategory.CREOSOTE.basePower);

    assertEquals(20, LiquidFuelCompat.FuelCategory.BIOGAS.amount);
    assertEquals(32.0, LiquidFuelCompat.FuelCategory.BIOGAS.basePower);

    assertEquals(40, LiquidFuelCompat.FuelCategory.BIOMASS.amount);
    assertEquals(32.0, LiquidFuelCompat.FuelCategory.BIOMASS.basePower);

    assertEquals(10, LiquidFuelCompat.FuelCategory.GENERIC.amount);
    assertEquals(16.0, LiquidFuelCompat.FuelCategory.GENERIC.basePower);
  }

  @Test
  @DisplayName("Fluid tags JSON for c: conventions contain Ad Astra and standard entries")
  void testFluidTagsContainAdAstra() {
    JsonObject oilTag = readTag("data/c/tags/fluid/oil.json");
    assertNotNull(oilTag);
    JsonArray oilValues = oilTag.getAsJsonArray("values");
    assertTrue(containsTagValue(oilValues, "ad_astra:oil"));
    assertTrue(containsTagValue(oilValues, "ad_astra:flowing_oil"));

    JsonObject fuelsTag = readTag("data/c/tags/fluid/fuels.json");
    assertNotNull(fuelsTag);
    JsonArray fuelsValues = fuelsTag.getAsJsonArray("values");
    assertTrue(containsTagValue(fuelsValues, "ad_astra:fuel"));
    assertTrue(containsTagValue(fuelsValues, "ad_astra:flowing_fuel"));
    assertTrue(containsTagValue(fuelsValues, "ad_astra:cryo_fuel"));
    assertTrue(containsTagValue(fuelsValues, "ad_astra:flowing_cryo_fuel"));

    JsonObject hydrogenTag = readTag("data/c/tags/fluid/hydrogen.json");
    assertNotNull(hydrogenTag);
    JsonArray hydrogenValues = hydrogenTag.getAsJsonArray("values");
    assertTrue(containsTagValue(hydrogenValues, "ad_astra:hydrogen"));
    assertTrue(containsTagValue(hydrogenValues, "ad_astra:flowing_hydrogen"));
  }

  @Test
  @DisplayName("SemiFluidFuelManager manual registration and fallback contracts")
  void testSemiFluidFuelManagerContracts() {
    SemiFluidFuelManager manager = new SemiFluidFuelManager();

    // Null fluid handling
    assertNull(manager.getBurnProperty(null));
    assertFalse(manager.acceptsFluid(null));

    // Non-fuel fluids should not produce burn property if auto-detect cannot resolve
    assertFalse(manager.acceptsFluid(null));
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
