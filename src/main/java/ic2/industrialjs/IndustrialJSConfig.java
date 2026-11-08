package ic2.industrialjs;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ic2.core.IC2;
import ic2.core.util.LogCategory;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.neoforged.fml.loading.FMLPaths;

public class IndustrialJSConfig {

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  public static void load() {
    try {
      Path configDir = FMLPaths.CONFIGDIR.get().resolve("industrialjs");
      if (!Files.exists(configDir)) {
        Files.createDirectories(configDir);
      }

      loadMultipliers(configDir.resolve("multipliers.json"));
      loadRecipes(configDir.resolve("recipes.json"));
    } catch (Throwable t) {
      IC2.log.warn(LogCategory.General, "[IndustrialJS] Failed to load config: %s", t.getMessage());
    }
  }

  private static void loadMultipliers(Path path) {
    try {
      if (!Files.exists(path)) {
        JsonObject def = new JsonObject();
        def.addProperty(
            "_comment",
            "IndustrialJS Machine & Balance Multipliers. 1.0 = Default Vanilla IC2 Balance.");
        def.addProperty("generator_output_multiplier", 1.0);
        def.addProperty("solar_day_output_multiplier", 1.0);
        def.addProperty("solar_night_output_multiplier", 1.0);
        def.addProperty("machine_energy_consumption_multiplier", 1.0);
        def.addProperty("machine_speed_multiplier", 1.0);
        def.addProperty("molecular_transformer_cost_multiplier", 1.0);
        def.addProperty("nuclear_reactor_multiplier", 1.0);
        def.addProperty("reactor_explosion_multiplier", 1.0);
        def.addProperty("uu_energy_multiplier", 1.0);
        def.addProperty("matter_amplifier_multiplier", 1.0);
        def.addProperty("teleporter_cost_multiplier", 1.0);
        def.addProperty("teleporter_cross_dimension", true);
        def.addProperty("teleporter_cross_dimension_flat_cost", 1_000_000.0);
        def.addProperty("mining_speed_multiplier", 1.0);
        def.addProperty("armor_absorption_multiplier", 1.0);
        def.addProperty("armor_energy_cost_multiplier", 1.0);
        def.addProperty("nano_saber_active_damage", 20.0);
        def.addProperty("nano_saber_passive_damage", 4.0);
        def.addProperty("nano_saber_energy_per_hit", 400.0);
        def.addProperty("nano_saber_armor_bypass", 0.0);
        def.addProperty("laser_explosive_destruction", true);
        def.addProperty("quantum_void_protection", true);
        def.addProperty("quantum_sprint_multiplier", 1.0);
        def.addProperty("eu_to_fe_ratio", 2.0);
        def.addProperty("lossless_wrench", false);
        def.addProperty("disable_radiation", false);
        def.addProperty("recycler_scrap_chance", 8);

        JsonArray blacklist = new JsonArray();
        blacklist.add("minecraft:glass_bottle");
        def.add("recycler_blacklist", blacklist);

        JsonArray hazmat = new JsonArray();
        hazmat.add("#c:armors/hazmat");
        def.add("hazmat_items", hazmat);

        JsonArray mutes = new JsonArray();
        mutes.add("ic2:forge_hammer");
        mutes.add("#c:tools/wrench");
        def.add("mute_tools", mutes);

        JsonObject customMachines = new JsonObject();
        customMachines.addProperty("macerator", 1.0);
        customMachines.addProperty("compressor", 1.0);
        customMachines.addProperty("extractor", 1.0);
        def.add("machine_multipliers", customMachines);

        JsonObject drillSpeeds = new JsonObject();
        drillSpeeds.addProperty("ic2:iridium_drill", 35.0);
        drillSpeeds.addProperty("ic2:diamond_drill", 16.0);
        def.add("drill_mining_speeds", drillSpeeds);

        JsonObject drillCosts = new JsonObject();
        drillCosts.addProperty("ic2:iridium_drill", 200);
        drillCosts.addProperty("ic2:diamond_drill", 80);
        def.add("drill_energy_costs", drillCosts);

        JsonObject storageCaps = new JsonObject();
        storageCaps.addProperty("batbox", 40000);
        storageCaps.addProperty("cesu", 300000);
        storageCaps.addProperty("mfe", 4000000);
        storageCaps.addProperty("mfsu", 40000000);
        def.add("storage_capacities", storageCaps);

        JsonObject storageOuts = new JsonObject();
        storageOuts.addProperty("batbox", 32);
        storageOuts.addProperty("cesu", 128);
        storageOuts.addProperty("mfe", 512);
        storageOuts.addProperty("mfsu", 2048);
        def.add("storage_outputs", storageOuts);

        try (FileWriter writer = new FileWriter(path.toFile())) {
          GSON.toJson(def, writer);
        }
        return;
      }

      try (FileReader reader = new FileReader(path.toFile())) {
        JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
        if (obj.has("generator_output_multiplier")) {
          IndustrialJS.setGeneratorMultiplier(obj.get("generator_output_multiplier").getAsDouble());
        }
        if (obj.has("solar_day_output_multiplier")) {
          IndustrialJS.setSolarDayMultiplier(obj.get("solar_day_output_multiplier").getAsDouble());
        }
        if (obj.has("solar_night_output_multiplier")) {
          IndustrialJS.setSolarNightMultiplier(
              obj.get("solar_night_output_multiplier").getAsDouble());
        }
        if (obj.has("machine_energy_consumption_multiplier")) {
          IndustrialJS.setMachineConsumptionMultiplier(
              obj.get("machine_energy_consumption_multiplier").getAsDouble());
        }
        if (obj.has("machine_speed_multiplier")) {
          IndustrialJS.setMachineSpeedMultiplier(obj.get("machine_speed_multiplier").getAsDouble());
        }
        if (obj.has("molecular_transformer_cost_multiplier")) {
          IndustrialJS.setMolecularTransformerMultiplier(
              obj.get("molecular_transformer_cost_multiplier").getAsDouble());
        }
        if (obj.has("nuclear_reactor_multiplier")) {
          IndustrialJS.setNuclearReactorMultiplier(
              obj.get("nuclear_reactor_multiplier").getAsDouble());
        }
        if (obj.has("reactor_explosion_multiplier")) {
          IndustrialJS.setReactorExplosionMultiplier(
              obj.get("reactor_explosion_multiplier").getAsDouble());
        }
        if (obj.has("uu_energy_multiplier")) {
          IndustrialJS.setUuEnergyMultiplier(obj.get("uu_energy_multiplier").getAsDouble());
        }
        if (obj.has("matter_amplifier_multiplier")) {
          IndustrialJS.setMatterAmplifierMultiplier(
              obj.get("matter_amplifier_multiplier").getAsDouble());
        }
        if (obj.has("teleporter_cost_multiplier")) {
          IndustrialJS.setTeleporterCostMultiplier(
              obj.get("teleporter_cost_multiplier").getAsDouble());
        }
        if (obj.has("teleporter_cross_dimension")) {
          IndustrialJS.setTeleporterCrossDimension(
              obj.get("teleporter_cross_dimension").getAsBoolean());
        }
        if (obj.has("teleporter_cross_dimension_flat_cost")) {
          IndustrialJS.setTeleporterCrossDimensionFlatCost(
              obj.get("teleporter_cross_dimension_flat_cost").getAsDouble());
        }
        if (obj.has("mining_speed_multiplier")) {
          IndustrialJS.setMiningSpeedMultiplier(obj.get("mining_speed_multiplier").getAsDouble());
        }
        if (obj.has("armor_absorption_multiplier")) {
          IndustrialJS.setArmorAbsorptionMultiplier(
              obj.get("armor_absorption_multiplier").getAsDouble());
        }
        if (obj.has("armor_energy_cost_multiplier")) {
          IndustrialJS.setArmorEnergyCostMultiplier(
              obj.get("armor_energy_cost_multiplier").getAsDouble());
        }
        if (obj.has("nano_saber_active_damage")) {
          IndustrialJS.setNanoSaberActiveDamage(obj.get("nano_saber_active_damage").getAsDouble());
        }
        if (obj.has("nano_saber_passive_damage")) {
          IndustrialJS.setNanoSaberPassiveDamage(
              obj.get("nano_saber_passive_damage").getAsDouble());
        }
        if (obj.has("nano_saber_energy_per_hit")) {
          IndustrialJS.setNanoSaberEnergyPerHit(obj.get("nano_saber_energy_per_hit").getAsDouble());
        }
        if (obj.has("nano_saber_armor_bypass")) {
          IndustrialJS.setNanoSaberArmorBypass(obj.get("nano_saber_armor_bypass").getAsDouble());
        }
        if (obj.has("laser_explosive_destruction")) {
          IndustrialJS.setLaserExplosiveDestruction(
              obj.get("laser_explosive_destruction").getAsBoolean());
        }
        if (obj.has("quantum_void_protection")) {
          IndustrialJS.setQuantumSuitVoidProtection(
              obj.get("quantum_void_protection").getAsBoolean());
        }
        if (obj.has("quantum_sprint_multiplier")) {
          IndustrialJS.setQuantumSprintMultiplier(
              obj.get("quantum_sprint_multiplier").getAsDouble());
        }
        if (obj.has("eu_to_fe_ratio")) {
          IndustrialJS.setEuToFeRatio(obj.get("eu_to_fe_ratio").getAsDouble());
        }
        if (obj.has("lossless_wrench")) {
          IndustrialJS.setLosslessWrench(obj.get("lossless_wrench").getAsBoolean());
        }
        if (obj.has("disable_radiation")) {
          IndustrialJS.setRadiationDisabled(obj.get("disable_radiation").getAsBoolean());
        }
        if (obj.has("recycler_scrap_chance")) {
          IndustrialJS.setRecyclerScrapChance(obj.get("recycler_scrap_chance").getAsInt());
        }
        if (obj.has("recycler_blacklist") && obj.get("recycler_blacklist").isJsonArray()) {
          for (JsonElement e : obj.getAsJsonArray("recycler_blacklist")) {
            IndustrialJS.addRecyclerBlacklist(e.getAsString());
          }
        }
        if (obj.has("hazmat_items") && obj.get("hazmat_items").isJsonArray()) {
          for (JsonElement e : obj.getAsJsonArray("hazmat_items")) {
            IndustrialJS.addHazmatItem(e.getAsString());
          }
        }
        if (obj.has("mute_tools") && obj.get("mute_tools").isJsonArray()) {
          for (JsonElement e : obj.getAsJsonArray("mute_tools")) {
            IndustrialJS.addMuteTool(e.getAsString());
          }
        }
        if (obj.has("machine_multipliers") && obj.get("machine_multipliers").isJsonObject()) {
          JsonObject machines = obj.getAsJsonObject("machine_multipliers");
          for (String key : machines.keySet()) {
            IndustrialJS.setMachineMultiplier(key, machines.get(key).getAsDouble());
          }
        }
        if (obj.has("drill_mining_speeds") && obj.get("drill_mining_speeds").isJsonObject()) {
          JsonObject speeds = obj.getAsJsonObject("drill_mining_speeds");
          for (String key : speeds.keySet()) {
            IndustrialJS.setDrillMiningSpeed(key, speeds.get(key).getAsDouble());
          }
        }
        if (obj.has("drill_energy_costs") && obj.get("drill_energy_costs").isJsonObject()) {
          JsonObject costs = obj.getAsJsonObject("drill_energy_costs");
          for (String key : costs.keySet()) {
            IndustrialJS.setDrillEnergyPerBlock(key, costs.get(key).getAsInt());
          }
        }
        if (obj.has("storage_capacities") && obj.get("storage_capacities").isJsonObject()) {
          JsonObject caps = obj.getAsJsonObject("storage_capacities");
          for (String key : caps.keySet()) {
            IndustrialJS.setStorageCapacity(key, caps.get(key).getAsInt());
          }
        }
        if (obj.has("storage_outputs") && obj.get("storage_outputs").isJsonObject()) {
          JsonObject outs = obj.getAsJsonObject("storage_outputs");
          for (String key : outs.keySet()) {
            IndustrialJS.setStorageOutput(key, outs.get(key).getAsInt());
          }
        }
      }
    } catch (Throwable t) {
      IC2.log.warn(
          LogCategory.General, "[IndustrialJS] Error loading multipliers.json: %s", t.getMessage());
    }
  }

  private static void loadRecipes(Path path) {
    try {
      if (!Files.exists(path)) {
        JsonArray def = new JsonArray();

        JsonObject ex1 = new JsonObject();
        ex1.addProperty("_comment", "Example Macerator Recipe: 1 Granite -> 2 Sand");
        ex1.addProperty("type", "macerator");
        ex1.addProperty("input", "minecraft:granite");
        ex1.addProperty("output", "2x minecraft:sand");
        def.add(ex1);

        JsonObject ex2 = new JsonObject();
        ex2.addProperty("_comment", "Example Compressor Recipe: 9 Dirt -> 1 Clay");
        ex2.addProperty("type", "compressor");
        ex2.addProperty("input", "9x minecraft:dirt");
        ex2.addProperty("output", "minecraft:clay");
        def.add(ex2);

        JsonObject ex3 = new JsonObject();
        ex3.addProperty("_comment", "Example Extractor Recipe: Dandelion -> 2 Rubber");
        ex3.addProperty("type", "extractor");
        ex3.addProperty("input", "minecraft:dandelion");
        ex3.addProperty("output", "2x ic2:rubber");
        def.add(ex3);

        JsonObject ex4 = new JsonObject();
        ex4.addProperty(
            "_comment", "Example Molecular Transformer Recipe: Iron Block -> Gold Block (5M EU)");
        ex4.addProperty("type", "molecular_transformer");
        ex4.addProperty("input", "minecraft:iron_block");
        ex4.addProperty("output", "minecraft:gold_block");
        ex4.addProperty("eu_cost", 5_000_000.0);
        def.add(ex4);

        JsonObject ex5 = new JsonObject();
        ex5.addProperty(
            "_comment", "Example Matter Fabricator Scrap Amplifier: Cobblestone -> 100 amplifier");
        ex5.addProperty("type", "amplifier");
        ex5.addProperty("input", "minecraft:cobblestone");
        ex5.addProperty("amplification", 100);
        def.add(ex5);

        JsonObject ex6 = new JsonObject();
        ex6.addProperty("_comment", "Example Mining Laser Super-Heat Smelting: Sand -> Glass");
        ex6.addProperty("type", "laser_smelting");
        ex6.addProperty("input", "minecraft:sand");
        ex6.addProperty("output", "minecraft:glass");
        def.add(ex6);

        try (FileWriter writer = new FileWriter(path.toFile())) {
          GSON.toJson(def, writer);
        }
        return;
      }

      try (FileReader reader = new FileReader(path.toFile())) {
        JsonElement elem = JsonParser.parseReader(reader);
        if (!elem.isJsonArray()) {
          return;
        }

        JsonArray arr = elem.getAsJsonArray();
        for (JsonElement item : arr) {
          if (!item.isJsonObject()) continue;
          JsonObject r = item.getAsJsonObject();
          if (!r.has("type") || !r.has("input")) continue;

          String type = r.get("type").getAsString().toLowerCase().trim();
          String input = r.get("input").getAsString().trim();

          if (type.equals("amplifier") || type.equals("matter_amplifier")) {
            int amplification = r.has("amplification") ? r.get("amplification").getAsInt() : 1000;
            IndustrialJS.amplifier(input, amplification);
            continue;
          }

          if (type.equals("laser_smelting") || type.equals("laser_smelt")) {
            if (r.has("output")) {
              IndustrialJS.laserSmelting(input, r.get("output").getAsString().trim());
            }
            continue;
          }

          if (!r.has("output")) continue;

          Object outputObj;
          if (r.get("output").isJsonArray()) {
            List<String> outList = new ArrayList<>();
            for (JsonElement o : r.get("output").getAsJsonArray()) {
              outList.add(o.getAsString());
            }
            outputObj = outList;
          } else {
            outputObj = r.get("output").getAsString().trim();
          }

          switch (type) {
            case "macerator" -> IndustrialJS.macerator(outputObj, input);
            case "compressor" -> IndustrialJS.compressor(outputObj, input);
            case "extractor" -> IndustrialJS.extractor(outputObj, input);
            case "ore_washer", "ore_washing" -> {
              int water = r.has("water") ? r.get("water").getAsInt() : 1000;
              IndustrialJS.oreWasher(outputObj, input, water);
            }
            case "centrifuge", "thermal_centrifuge" -> {
              int heat = r.has("min_heat") ? r.get("min_heat").getAsInt() : 500;
              IndustrialJS.thermalCentrifuge(outputObj, input, heat);
            }
            case "blast_furnace" -> {
              int duration = r.has("duration") ? r.get("duration").getAsInt() : 6000;
              int heat = r.has("min_heat") ? r.get("min_heat").getAsInt() : 1000;
              IndustrialJS.blastFurnace(outputObj, input, duration, heat);
            }
            case "block_cutter" -> {
              int hardness = r.has("hardness") ? r.get("hardness").getAsInt() : 1;
              IndustrialJS.blockCutter(outputObj, input, hardness);
            }
            case "metal_former_extruding", "extruding" ->
                IndustrialJS.metalFormerExtruding(outputObj, input);
            case "metal_former_cutting", "cutting" ->
                IndustrialJS.metalFormerCutting(outputObj, input);
            case "metal_former_rolling", "rolling" ->
                IndustrialJS.metalFormerRolling(outputObj, input);
            case "molecular_transformer" -> {
              double euCost = r.has("eu_cost") ? r.get("eu_cost").getAsDouble() : 1_000_000.0;
              IndustrialJS.molecularTransformer(outputObj, input, euCost);
            }
            case "scrapbox" -> {
              float weight = r.has("weight") ? r.get("weight").getAsFloat() : 1.0F;
              IndustrialJS.scrapbox(outputObj, weight);
            }
          }
        }
      }
    } catch (Throwable t) {
      IC2.log.warn(
          LogCategory.General, "[IndustrialJS] Error loading recipes.json: %s", t.getMessage());
    }
  }
}
