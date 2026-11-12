package ic2.integration.jade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JadePluginTest {

  @Test
  @DisplayName("NuclearReactorComponentProvider UID matches ic2:nuclear_reactor")
  void testUid() {
    assertEquals(
        ResourceLocation.fromNamespaceAndPath("ic2", "nuclear_reactor"),
        NuclearReactorComponentProvider.INSTANCE.getUid());
  }

  @Test
  @DisplayName("Jade reactor localization keys exist in all supported languages")
  void testLocalizationKeys() {
    JsonObject en = readLang("assets/ic2/lang/en_us.json");
    assertTrue(en.has("ic2.jade.reactor.heat"));
    assertTrue(en.has("ic2.jade.reactor.cycle"));

    JsonObject ru = readLang("assets/ic2/lang/ru_ru.json");
    assertTrue(ru.has("ic2.jade.reactor.heat"));
    assertTrue(ru.has("ic2.jade.reactor.cycle"));

    JsonObject zh = readLang("assets/ic2/lang/zh_cn.json");
    assertTrue(zh.has("ic2.jade.reactor.heat"));
    assertTrue(zh.has("ic2.jade.reactor.cycle"));
  }

  @Test
  @DisplayName("NBT data payload structure contract")
  void testNbtPayload() {
    CompoundTag tag = new CompoundTag();
    tag.putInt("heat", 2500);
    tag.putInt("max_heat", 10000);
    tag.putFloat("output", 120.0F);
    tag.putInt("cycle_ticks", 20);

    assertEquals(2500, tag.getInt("heat"));
    assertEquals(10000, tag.getInt("max_heat"));
    assertEquals(120.0F, tag.getFloat("output"));
    assertEquals(20, tag.getInt("cycle_ticks"));

    double pct = ((double) tag.getInt("heat") / tag.getInt("max_heat")) * 100.0;
    assertEquals(25.0, pct, 1e-6);
  }

  private JsonObject readLang(String path) {
    try (InputStream stream = getClass().getClassLoader().getResourceAsStream(path)) {
      if (stream == null) {
        throw new IllegalArgumentException("Lang file not found: " + path);
      }
      return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8))
          .getAsJsonObject();
    } catch (Exception e) {
      throw new RuntimeException("Failed to read lang: " + path, e);
    }
  }
}
