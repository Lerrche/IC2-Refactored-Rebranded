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
        def.addProperty("solar_helmet_multiplier", 1.0);
        def.addProperty("machine_energy_consumption_multiplier", 1.0);
        def.addProperty("machine_speed_multiplier", 1.0);
        def.addProperty("molecular_transformer_cost_multiplier", 1.0);
        def.addProperty("nuclear_reactor_multiplier", 1.0);
        def.addProperty("reactor_explosion_multiplier", 1.0);
        def.addProperty("reactor_heat_multiplier", 1.0);
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
        def.addProperty("quantum_jump_multiplier", 1.0);
        def.addProperty("eu_to_fe_ratio", 2.0);
        def.addProperty("lossless_wrench", false);
        def.addProperty("disable_radiation", false);
        def.addProperty("recycler_scrap_chance", 8);
        def.addProperty("machine_volume_multiplier", 1.0);
        def.addProperty("audio_disabled", false);
        def.addProperty("batch_crafter_speed_multiplier", 1.0);
        def.addProperty("batch_crafter_energy_multiplier", 1.0);
        def.addProperty("batch_crafter_capacity", 20000);
        def.addProperty("jetpack_thrust_multiplier", 1.0);
        def.addProperty("jetpack_height_multiplier", 1.0);
        def.addProperty("jetpack_energy_cost_multiplier", 1.0);
        def.addProperty("jetpack_boost_multiplier", 1.0);
        def.addProperty("rotor_efficiency_multiplier", 1.0);
        def.addProperty("overclocker_speed_multiplier", 1.0);
        def.addProperty("overclocker_energy_multiplier", 1.0);
        def.addProperty("energy_storage_upgrade_bonus", 10000);
        def.addProperty("transformer_upgrade_tier_bonus", 1);
        def.addProperty("terraformer_energy_multiplier", 1.0);
        def.addProperty("terraformer_range_multiplier", 1.0);
        def.addProperty("crop_growth_multiplier", 1.0);
        def.addProperty("crop_stat_gain_multiplier", 1.0);
        def.addProperty("crop_crossing_chance", 1.0);

        JsonArray blacklist = new JsonArray();
        blacklist.add("minecraft:glass_bottle");
        def.add("recycler_blacklist", blacklist);

        JsonArray hazmat = new JsonArray();
        hazmat.add("#c:armors/hazmat");
        def.add("hazmat_items", hazmat);

        JsonArray hazmatSuits = new JsonArray();
        def.add("hazmat_suits", hazmatSuits);

        JsonArray radioactive = new JsonArray();
        JsonObject radEx = new JsonObject();
        radEx.addProperty("item", "ic2:uranium_ore");
        radEx.addProperty("duration", 10);
        radEx.addProperty("amplifier", 1);
        radioactive.add(radEx);
        def.add("radioactive_items", radioactive);

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

        JsonObject fuelRods = new JsonObject();
        JsonObject uRod = new JsonObject();
        uRod.addProperty("power_multiplier", 1.0);
        uRod.addProperty("heat_multiplier", 1.0);
        uRod.addProperty("lifespan", 20000);
        fuelRods.add("ic2:fuel_rod_uranium", uRod);
        def.add("fuel_rods", fuelRods);

        JsonObject solarHelmets = new JsonObject();
        solarHelmets.addProperty("ic2:solar_helmet", 1.0);
        def.add("solar_helmets", solarHelmets);

        JsonObject rotors = new JsonObject();
        JsonObject woodRotor = new JsonObject();
        woodRotor.addProperty("efficiency", 1.0);
        woodRotor.addProperty("min_wind", 10);
        woodRotor.addProperty("max_wind", 60);
        rotors.add("ic2:rotor_wood", woodRotor);
        def.add("rotors", rotors);

        JsonObject armorCaps = new JsonObject();
        armorCaps.addProperty("ic2:quantum_chestplate", 10000000.0);
        def.add("electric_armor_capacities", armorCaps);

        JsonObject armorTransfers = new JsonObject();
        armorTransfers.addProperty("ic2:quantum_chestplate", 10000.0);
        def.add("electric_armor_transfer_limits", armorTransfers);

        JsonArray structLoot = new JsonArray();
        JsonObject l1 = new JsonObject();
        l1.addProperty("table", "abandoned_mineshaft");
        l1.addProperty("item", "ic2:iridium_shard");
        l1.addProperty("chance", 0.4);
        l1.addProperty("min_count", 1);
        l1.addProperty("max_count", 2);
        structLoot.add(l1);
        def.add("structure_loot", structLoot);

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
        if (obj.has("solar_helmet_multiplier")) {
          IndustrialJS.setSolarHelmetMultiplier(obj.get("solar_helmet_multiplier").getAsDouble());
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
        if (obj.has("reactor_heat_multiplier")) {
          IndustrialJS.setReactorHeatMultiplier(obj.get("reactor_heat_multiplier").getAsDouble());
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
        if (obj.has("quantum_jump_multiplier")) {
          IndustrialJS.setQuantumJumpMultiplier(obj.get("quantum_jump_multiplier").getAsDouble());
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
        if (obj.has("machine_volume_multiplier")) {
          IndustrialJS.setMachineVolumeMultiplier(
              obj.get("machine_volume_multiplier").getAsDouble());
        }
        if (obj.has("audio_disabled")) {
          IndustrialJS.setAudioDisabled(obj.get("audio_disabled").getAsBoolean());
        }
        if (obj.has("batch_crafter_speed_multiplier")) {
          IndustrialJS.setBatchCrafterSpeedMultiplier(
              obj.get("batch_crafter_speed_multiplier").getAsDouble());
        }
        if (obj.has("batch_crafter_energy_multiplier")) {
          IndustrialJS.setBatchCrafterEnergyMultiplier(
              obj.get("batch_crafter_energy_multiplier").getAsDouble());
        }
        if (obj.has("batch_crafter_capacity")) {
          IndustrialJS.setBatchCrafterCapacity(obj.get("batch_crafter_capacity").getAsInt());
        }
        if (obj.has("jetpack_thrust_multiplier")) {
          IndustrialJS.setJetpackThrustMultiplier(
              obj.get("jetpack_thrust_multiplier").getAsDouble());
        }
        if (obj.has("jetpack_height_multiplier")) {
          IndustrialJS.setJetpackHeightMultiplier(
              obj.get("jetpack_height_multiplier").getAsDouble());
        }
        if (obj.has("jetpack_energy_cost_multiplier")) {
          IndustrialJS.setJetpackEnergyCostMultiplier(
              obj.get("jetpack_energy_cost_multiplier").getAsDouble());
        }
        if (obj.has("jetpack_boost_multiplier")) {
          IndustrialJS.setJetpackBoostMultiplier(obj.get("jetpack_boost_multiplier").getAsDouble());
        }
        if (obj.has("rotor_efficiency_multiplier")) {
          IndustrialJS.setRotorEfficiencyMultiplier(
              obj.get("rotor_efficiency_multiplier").getAsDouble());
        }
        if (obj.has("overclocker_speed_multiplier")) {
          IndustrialJS.setOverclockerSpeedMultiplier(
              obj.get("overclocker_speed_multiplier").getAsDouble());
        }
        if (obj.has("overclocker_energy_multiplier")) {
          IndustrialJS.setOverclockerEnergyMultiplier(
              obj.get("overclocker_energy_multiplier").getAsDouble());
        }
        if (obj.has("energy_storage_upgrade_bonus")) {
          IndustrialJS.setEnergyStorageUpgradeBonus(
              obj.get("energy_storage_upgrade_bonus").getAsInt());
        }
        if (obj.has("transformer_upgrade_tier_bonus")) {
          IndustrialJS.setTransformerUpgradeTier(
              obj.get("transformer_upgrade_tier_bonus").getAsInt());
        }
        if (obj.has("terraformer_energy_multiplier")) {
          IndustrialJS.setTerraformerEnergyMultiplier(
              obj.get("terraformer_energy_multiplier").getAsDouble());
        }
        if (obj.has("terraformer_range_multiplier")) {
          IndustrialJS.setTerraformerRangeMultiplier(
              obj.get("terraformer_range_multiplier").getAsDouble());
        }
        if (obj.has("crop_growth_multiplier")) {
          IndustrialJS.setCropGrowthMultiplier(obj.get("crop_growth_multiplier").getAsDouble());
        }
        if (obj.has("crop_stat_gain_multiplier")) {
          IndustrialJS.setCropStatGainMultiplier(
              obj.get("crop_stat_gain_multiplier").getAsDouble());
        }
        if (obj.has("crop_crossing_chance")) {
          IndustrialJS.setCropCrossingChance(obj.get("crop_crossing_chance").getAsDouble());
        }

        if (obj.has("recycler_blacklist")) {
          for (JsonElement e : obj.getAsJsonArray("recycler_blacklist")) {
            IndustrialJS.addRecyclerBlacklist(e.getAsString());
          }
        }
        if (obj.has("hazmat_items")) {
          for (JsonElement e : obj.getAsJsonArray("hazmat_items")) {
            IndustrialJS.addHazmatItem(e.getAsString());
          }
        }
        if (obj.has("hazmat_suits")) {
          for (JsonElement e : obj.getAsJsonArray("hazmat_suits")) {
            IndustrialJS.addHazmatSuit(e.getAsString());
          }
        }
        if (obj.has("radioactive_items")) {
          for (JsonElement e : obj.getAsJsonArray("radioactive_items")) {
            if (e.isJsonObject()) {
              JsonObject rObj = e.getAsJsonObject();
              String item = rObj.get("item").getAsString();
              int dur = rObj.has("duration") ? rObj.get("duration").getAsInt() : 10;
              int amp = rObj.has("amplifier") ? rObj.get("amplifier").getAsInt() : 1;
              IndustrialJS.addRadioactiveItem(item, dur, amp);
            } else if (e.isJsonPrimitive()) {
              IndustrialJS.addRadioactiveItem(e.getAsString());
            }
          }
        }
        if (obj.has("mute_tools")) {
          for (JsonElement e : obj.getAsJsonArray("mute_tools")) {
            IndustrialJS.addMuteTool(e.getAsString());
          }
        }
        if (obj.has("machine_multipliers")) {
          JsonObject machines = obj.getAsJsonObject("machine_multipliers");
          for (String key : machines.keySet()) {
            IndustrialJS.setMachineMultiplier(key, machines.get(key).getAsDouble());
          }
        }
        if (obj.has("drill_mining_speeds")) {
          JsonObject drillSpeeds = obj.getAsJsonObject("drill_mining_speeds");
          for (String key : drillSpeeds.keySet()) {
            IndustrialJS.setDrillMiningSpeed(key, drillSpeeds.get(key).getAsDouble());
          }
        }
        if (obj.has("drill_energy_costs")) {
          JsonObject drillCosts = obj.getAsJsonObject("drill_energy_costs");
          for (String key : drillCosts.keySet()) {
            IndustrialJS.setDrillEnergyPerBlock(key, drillCosts.get(key).getAsInt());
          }
        }
        if (obj.has("storage_capacities")) {
          JsonObject storageCaps = obj.getAsJsonObject("storage_capacities");
          for (String key : storageCaps.keySet()) {
            IndustrialJS.setStorageCapacity(key, storageCaps.get(key).getAsInt());
          }
        }
        if (obj.has("storage_outputs")) {
          JsonObject storageOuts = obj.getAsJsonObject("storage_outputs");
          for (String key : storageOuts.keySet()) {
            IndustrialJS.setStorageOutput(key, storageOuts.get(key).getAsInt());
          }
        }
        if (obj.has("fuel_rods")) {
          JsonObject rods = obj.getAsJsonObject("fuel_rods");
          for (String key : rods.keySet()) {
            JsonObject r = rods.getAsJsonObject(key);
            double pow = r.has("power_multiplier") ? r.get("power_multiplier").getAsDouble() : 1.0;
            double heat = r.has("heat_multiplier") ? r.get("heat_multiplier").getAsDouble() : 1.0;
            int lifespan = r.has("lifespan") ? r.get("lifespan").getAsInt() : 20000;
            IndustrialJS.setFuelRodStats(key, pow, heat, lifespan);
          }
        }
        if (obj.has("solar_helmets")) {
          JsonObject helmets = obj.getAsJsonObject("solar_helmets");
          for (String key : helmets.keySet()) {
            IndustrialJS.setSolarHelmetMultiplier(key, helmets.get(key).getAsDouble());
          }
        }
        if (obj.has("rotors")) {
          JsonObject rotors = obj.getAsJsonObject("rotors");
          for (String key : rotors.keySet()) {
            JsonObject r = rotors.getAsJsonObject(key);
            double eff = r.has("efficiency") ? r.get("efficiency").getAsDouble() : 1.0;
            int minW = r.has("min_wind") ? r.get("min_wind").getAsInt() : 10;
            int maxW = r.has("max_wind") ? r.get("max_wind").getAsInt() : 60;
            IndustrialJS.setRotorStats(key, eff, minW, maxW);
          }
        }
        if (obj.has("electric_armor_capacities")) {
          JsonObject caps = obj.getAsJsonObject("electric_armor_capacities");
          for (String key : caps.keySet()) {
            IndustrialJS.setArmorCapacity(key, caps.get(key).getAsDouble());
          }
        }
        if (obj.has("electric_armor_transfer_limits")) {
          JsonObject limits = obj.getAsJsonObject("electric_armor_transfer_limits");
          for (String key : limits.keySet()) {
            IndustrialJS.setArmorTransferLimit(key, limits.get(key).getAsDouble());
          }
        }
        if (obj.has("structure_loot")) {
          for (JsonElement e : obj.getAsJsonArray("structure_loot")) {
            if (e.isJsonObject()) {
              JsonObject l = e.getAsJsonObject();
              String table = l.get("table").getAsString();
              String item = l.get("item").getAsString();
              float chance = l.has("chance") ? l.get("chance").getAsFloat() : 1.0f;
              int minC = l.has("min_count") ? l.get("min_count").getAsInt() : 1;
              int maxC = l.has("max_count") ? l.get("max_count").getAsInt() : 1;
              IndustrialJS.addStructureLoot(table, item, chance, minC, maxC);
            }
          }
        }

        IC2.log.info(LogCategory.General, "[IndustrialJS] Loaded custom balance multipliers");
      }
    } catch (Throwable t) {
      IC2.log.warn(
          LogCategory.General,
          "[IndustrialJS] Could not load multipliers.json: %s",
          t.getMessage());
    }
  }

  private static void loadRecipes(Path path) {
    try {
      if (!Files.exists(path)) {
        JsonArray def = new JsonArray();

        JsonObject ex1 = new JsonObject();
        ex1.addProperty("_comment", "Example Macerator Recipe: 1 Cobblestone -> 1 Sand");
        ex1.addProperty("type", "macerator");
        ex1.addProperty("input", "minecraft:cobblestone");
        ex1.addProperty("output", "minecraft:sand");
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

        JsonObject ex7 = new JsonObject();
        ex7.addProperty("_comment", "Example Scrap Box Surprise Drop");
        ex7.addProperty("type", "scrapbox");
        ex7.addProperty("item", "minecraft:emerald");
        ex7.addProperty("weight", 0.05);
        def.add(ex7);

        JsonObject ex8 = new JsonObject();
        ex8.addProperty(
            "_comment", "Example Canner Enrich: 1000mB Water + Bio Chaff -> 1000mB Biomass");
        ex8.addProperty("type", "canner_enrich");
        ex8.addProperty("input_fluid", "1000mB minecraft:water");
        ex8.addProperty("additive", "ic2:bio_chaff");
        ex8.addProperty("output_fluid", "1000mB ic2:biomass");
        def.add(ex8);

        JsonObject ex9 = new JsonObject();
        ex9.addProperty(
            "_comment", "Example Canner Bottle: Empty Cell + Fuel Rod -> Uranium Fuel Rod");
        ex9.addProperty("type", "canner_bottle");
        ex9.addProperty("container", "ic2:empty_cell");
        ex9.addProperty("fill", "ic2:fuel_rod");
        ex9.addProperty("output", "ic2:fuel_rod_uranium");
        def.add(ex9);

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
          if (!r.has("type")) continue;

          String type = r.get("type").getAsString().toLowerCase().trim();

          if (type.equals("amplifier") || type.equals("matter_amplifier")) {
            if (r.has("input")) {
              String input = r.get("input").getAsString().trim();
              int amplification = r.has("amplification") ? r.get("amplification").getAsInt() : 1000;
              IndustrialJS.amplifier(input, amplification);
            }
            continue;
          }

          if (type.equals("laser_smelting") || type.equals("laser_smelt")) {
            if (r.has("input") && r.has("output")) {
              IndustrialJS.laserSmelting(
                  r.get("input").getAsString().trim(), r.get("output").getAsString().trim());
            }
            continue;
          }

          if (type.equals("scrapbox") || type.equals("scrap_box")) {
            if (r.has("item")) {
              float weight = r.has("weight") ? r.get("weight").getAsFloat() : 1.0f;
              IndustrialJS.scrapbox(r.get("item").getAsString().trim(), weight);
            }
            continue;
          }

          if (type.equals("canner_enrich") || type.equals("fluid_enrich")) {
            if (r.has("output_fluid") && r.has("input_fluid") && r.has("additive")) {
              IndustrialJS.cannerEnrich(
                  r.get("output_fluid").getAsString().trim(),
                  r.get("input_fluid").getAsString().trim(),
                  r.get("additive").getAsString().trim());
            }
            continue;
          }

          if (type.equals("canner_bottle") || type.equals("canner_fill")) {
            if (r.has("output") && r.has("container") && r.has("fill")) {
              IndustrialJS.cannerBottle(
                  r.get("output").getAsString().trim(),
                  r.get("container").getAsString().trim(),
                  r.get("fill").getAsString().trim());
            }
            continue;
          }

          if (!r.has("input") || !r.has("output")) continue;
          String input = r.get("input").getAsString().trim();

          Object outputObj;
          if (r.get("output").isJsonArray()) {
            List<String> outList = new ArrayList<>();
            for (JsonElement o : r.get("output").getAsJsonArray()) {
              outList.add(o.getAsString().trim());
            }
            outputObj = outList;
          } else {
            outputObj = r.get("output").getAsString().trim();
          }

          switch (type) {
            case "macerator" -> IndustrialJS.macerator(outputObj, input);
            case "compressor" -> IndustrialJS.compressor(outputObj, input);
            case "extractor" -> IndustrialJS.extractor(outputObj, input);
            case "metal_former_extruding", "extruding" ->
                IndustrialJS.metalFormerExtruding(outputObj, input);
            case "metal_former_cutting", "cutting" ->
                IndustrialJS.metalFormerCutting(outputObj, input);
            case "metal_former_rolling", "rolling" ->
                IndustrialJS.metalFormerRolling(outputObj, input);
            case "molecular_transformer", "molecular" -> {
              double euCost = r.has("eu_cost") ? r.get("eu_cost").getAsDouble() : 1_000_000.0;
              IndustrialJS.molecularTransformer(outputObj, input, euCost);
            }
            case "ore_washer", "ore_washing" -> {
              int water = r.has("water") ? r.get("water").getAsInt() : 1000;
              IndustrialJS.oreWasher(outputObj, input, water);
            }
            case "thermal_centrifuge", "centrifuge" -> {
              int heat = r.has("heat") ? r.get("heat").getAsInt() : 500;
              IndustrialJS.thermalCentrifuge(outputObj, input, heat);
            }
            case "blast_furnace" -> {
              int duration = r.has("duration") ? r.get("duration").getAsInt() : 6000;
              int heat = r.has("heat") ? r.get("heat").getAsInt() : 1000;
              IndustrialJS.blastFurnace(outputObj, input, duration, heat);
            }
            case "block_cutter" -> {
              int hardness = r.has("hardness") ? r.get("hardness").getAsInt() : 1;
              IndustrialJS.blockCutter(outputObj, input, hardness);
            }
            default ->
                IC2.log.warn(
                    LogCategory.General,
                    "[IndustrialJS] Unknown recipe type '%s' in recipes.json",
                    type);
          }
        }

        IC2.log.info(LogCategory.General, "[IndustrialJS] Loaded custom recipes from recipes.json");
      }
    } catch (Throwable t) {
      IC2.log.warn(
          LogCategory.General, "[IndustrialJS] Could not load recipes.json: %s", t.getMessage());
    }
  }
}
