package ic2.industrialjs;

import ic2.core.Ic2Potion;
import ic2.core.item.armor.ItemArmorHazmat;
import ic2.core.ref.Ic2RecipeTypes;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;

/**
 * IndustrialJS - KubeJS and Modpack Scripting Integration for IndustrialCraft 2.
 *
 * <p>Allows modpack creators to customize machinery energy output, consumption, speed, wrench
 * lossless behavior, recycler chances, EU/FE ratios, reactor stats, cross-dimensional teleporters,
 * mining drills, nano sabers, quantum armor, energy storage blocks, kinetic rotors, upgrades, crops
 * breeding, audio mute tools, structure loot, canner recipes, and full custom recipes.
 */
public class IndustrialJS {

  private static volatile double generatorMultiplier = 1.0;
  private static volatile double solarDayMultiplier = 1.0;
  private static volatile double solarNightMultiplier = 1.0;
  private static volatile double machineConsumptionMultiplier = 1.0;
  private static volatile double machineSpeedMultiplier = 1.0;
  private static volatile double molecularTransformerMultiplier = 1.0;
  private static volatile double euToFeRatio = 2.0;
  private static volatile boolean losslessWrench = false;
  private static volatile int recyclerScrapChance = 8;
  private static volatile double teleporterCostMultiplier = 1.0;
  private static volatile boolean teleporterCrossDimension = true;
  private static volatile double teleporterCrossDimensionFlatCost = 1_000_000.0;
  private static volatile double nuclearReactorMultiplier = 1.0;
  private static volatile double reactorExplosionMultiplier = 1.0;
  private static volatile double reactorHeatMultiplier = 1.0;
  private static volatile double uuEnergyMultiplier = 1.0;
  private static volatile double matterAmplifierMultiplier = 1.0;
  private static volatile boolean radiationDisabled = false;
  private static volatile double miningSpeedMultiplier = 1.0;
  private static volatile double armorAbsorptionMultiplier = 1.0;
  private static volatile double armorEnergyCostMultiplier = 1.0;

  // Tools & Combat
  private static volatile double nanoSaberActiveDamage = 20.0;
  private static volatile double nanoSaberPassiveDamage = 4.0;
  private static volatile double nanoSaberEnergyPerHit = 400.0;
  private static volatile double nanoSaberArmorBypass = 0.0;
  private static volatile boolean laserExplosiveDestruction = true;
  private static volatile boolean quantumSuitVoidProtection = true;
  private static volatile double quantumSprintMultiplier = 1.0;
  private static volatile double quantumJumpMultiplier = 1.0;

  // Audio Control
  private static volatile double machineVolumeMultiplier = 1.0;
  private static volatile boolean audioDisabled = false;

  // Batch Crafter
  private static volatile double batchCrafterSpeedMultiplier = 1.0;
  private static volatile double batchCrafterEnergyMultiplier = 1.0;
  private static volatile int batchCrafterCapacity = 20000;

  // Solar Helmet
  private static volatile double solarHelmetMultiplier = 1.0;

  // Jetpack Stats
  private static volatile double jetpackThrustMultiplier = 1.0;
  private static volatile double jetpackHeightMultiplier = 1.0;
  private static volatile double jetpackEnergyCostMultiplier = 1.0;
  private static volatile double jetpackBoostMultiplier = 1.0;

  // Kinetic Rotors
  private static volatile double rotorEfficiencyMultiplier = 1.0;

  // Upgrades
  private static volatile double overclockerSpeedMultiplier = 1.0;
  private static volatile double overclockerEnergyMultiplier = 1.0;
  private static volatile int energyStorageUpgradeBonus = 10000;
  private static volatile int transformerUpgradeTierBonus = 1;

  // Terraformer
  private static volatile double terraformerEnergyMultiplier = 1.0;
  private static volatile double terraformerRangeMultiplier = 1.0;

  // Crops
  private static volatile double cropGrowthMultiplier = 1.0;
  private static volatile double cropStatGainMultiplier = 1.0;
  private static volatile double cropCrossingChance = 1.0;

  // Data structures for fine-grained per-item tuning
  public record FuelRodStats(double powerMultiplier, double heatMultiplier, int lifespan) {}

  public record RadioactiveItem(int durationSeconds, int amplifier) {}

  public record RotorStats(double efficiencyMultiplier, int minWind, int maxWind) {}

  public record StructureLootEntry(Object item, float chance, int minCount, int maxCount) {}

  private static final Map<String, Double> machineMultipliers = new ConcurrentHashMap<>();
  private static final Set<String> recyclerBlacklist = ConcurrentHashMap.newKeySet();
  private static final Set<String> hazmatItems = ConcurrentHashMap.newKeySet();
  private static final Set<String> hazmatSuits = ConcurrentHashMap.newKeySet();
  private static final Set<String> muteTools = ConcurrentHashMap.newKeySet();
  private static final Map<String, Double> drillMiningSpeeds = new ConcurrentHashMap<>();
  private static final Map<String, Integer> drillEnergyCosts = new ConcurrentHashMap<>();
  private static final Map<String, ItemStack> laserSmeltRecipes = new ConcurrentHashMap<>();
  private static final Map<String, Integer> storageCapacities = new ConcurrentHashMap<>();
  private static final Map<String, Integer> storageOutputs = new ConcurrentHashMap<>();
  private static final Map<String, FuelRodStats> fuelRodStats = new ConcurrentHashMap<>();
  private static final Map<String, RadioactiveItem> radioactiveItems = new ConcurrentHashMap<>();
  private static final Map<String, Double> solarHelmetMultipliers = new ConcurrentHashMap<>();
  private static final Map<String, RotorStats> rotorStats = new ConcurrentHashMap<>();
  private static final Map<String, Double> armorCapacities = new ConcurrentHashMap<>();
  private static final Map<String, Double> armorTransferLimits = new ConcurrentHashMap<>();
  private static final Map<String, List<StructureLootEntry>> structureLoot =
      new ConcurrentHashMap<>();

  public static void reset() {
    generatorMultiplier = 1.0;
    solarDayMultiplier = 1.0;
    solarNightMultiplier = 1.0;
    machineConsumptionMultiplier = 1.0;
    machineSpeedMultiplier = 1.0;
    molecularTransformerMultiplier = 1.0;
    euToFeRatio = 2.0;
    losslessWrench = false;
    recyclerScrapChance = 8;
    teleporterCostMultiplier = 1.0;
    teleporterCrossDimension = true;
    teleporterCrossDimensionFlatCost = 1_000_000.0;
    nuclearReactorMultiplier = 1.0;
    reactorExplosionMultiplier = 1.0;
    reactorHeatMultiplier = 1.0;
    uuEnergyMultiplier = 1.0;
    matterAmplifierMultiplier = 1.0;
    radiationDisabled = false;
    miningSpeedMultiplier = 1.0;
    armorAbsorptionMultiplier = 1.0;
    armorEnergyCostMultiplier = 1.0;
    nanoSaberActiveDamage = 20.0;
    nanoSaberPassiveDamage = 4.0;
    nanoSaberEnergyPerHit = 400.0;
    nanoSaberArmorBypass = 0.0;
    laserExplosiveDestruction = true;
    quantumSuitVoidProtection = true;
    quantumSprintMultiplier = 1.0;
    quantumJumpMultiplier = 1.0;
    machineVolumeMultiplier = 1.0;
    audioDisabled = false;
    batchCrafterSpeedMultiplier = 1.0;
    batchCrafterEnergyMultiplier = 1.0;
    batchCrafterCapacity = 20000;
    solarHelmetMultiplier = 1.0;
    jetpackThrustMultiplier = 1.0;
    jetpackHeightMultiplier = 1.0;
    jetpackEnergyCostMultiplier = 1.0;
    jetpackBoostMultiplier = 1.0;
    rotorEfficiencyMultiplier = 1.0;
    overclockerSpeedMultiplier = 1.0;
    overclockerEnergyMultiplier = 1.0;
    energyStorageUpgradeBonus = 10000;
    transformerUpgradeTierBonus = 1;
    terraformerEnergyMultiplier = 1.0;
    terraformerRangeMultiplier = 1.0;
    cropGrowthMultiplier = 1.0;
    cropStatGainMultiplier = 1.0;
    cropCrossingChance = 1.0;

    machineMultipliers.clear();
    recyclerBlacklist.clear();
    hazmatItems.clear();
    hazmatSuits.clear();
    muteTools.clear();
    drillMiningSpeeds.clear();
    drillEnergyCosts.clear();
    laserSmeltRecipes.clear();
    storageCapacities.clear();
    storageOutputs.clear();
    fuelRodStats.clear();
    radioactiveItems.clear();
    solarHelmetMultipliers.clear();
    rotorStats.clear();
    armorCapacities.clear();
    armorTransferLimits.clear();
    structureLoot.clear();
  }

  // --- Multipliers ---

  public static double getGeneratorMultiplier() {
    return generatorMultiplier;
  }

  public static void setGeneratorMultiplier(double mult) {
    generatorMultiplier = Math.max(0.0, mult);
  }

  public static double getSolarDayMultiplier() {
    return solarDayMultiplier;
  }

  public static void setSolarDayMultiplier(double mult) {
    solarDayMultiplier = Math.max(0.0, mult);
  }

  public static double getSolarNightMultiplier() {
    return solarNightMultiplier;
  }

  public static void setSolarNightMultiplier(double mult) {
    solarNightMultiplier = Math.max(0.0, mult);
  }

  public static void setSolarMultiplier(double mult) {
    setSolarDayMultiplier(mult);
    setSolarNightMultiplier(mult);
  }

  public static double getMachineConsumptionMultiplier() {
    return machineConsumptionMultiplier;
  }

  public static void setMachineConsumptionMultiplier(double mult) {
    machineConsumptionMultiplier = Math.max(0.0, mult);
  }

  public static double getMachineSpeedMultiplier() {
    return machineSpeedMultiplier;
  }

  public static void setMachineSpeedMultiplier(double mult) {
    machineSpeedMultiplier = Math.max(0.01, mult);
  }

  public static double getMolecularTransformerMultiplier() {
    return molecularTransformerMultiplier;
  }

  public static void setMolecularTransformerMultiplier(double mult) {
    molecularTransformerMultiplier = Math.max(0.0, mult);
  }

  public static double getEuToFeRatio() {
    return euToFeRatio;
  }

  public static void setEuToFeRatio(double ratio) {
    if (Double.isFinite(ratio) && ratio > 0.0) {
      euToFeRatio = ratio;
    }
  }

  public static boolean isLosslessWrench() {
    return losslessWrench;
  }

  public static void setLosslessWrench(boolean lossless) {
    losslessWrench = lossless;
  }

  public static int getRecyclerScrapChance() {
    return recyclerScrapChance;
  }

  public static void setRecyclerScrapChance(int chance) {
    recyclerScrapChance = Math.max(1, chance);
  }

  public static double getTeleporterCostMultiplier() {
    return teleporterCostMultiplier;
  }

  public static void setTeleporterCostMultiplier(double mult) {
    teleporterCostMultiplier = Math.max(0.0, mult);
  }

  public static void setTeleporterDistanceCostMultiplier(double mult) {
    setTeleporterCostMultiplier(mult);
  }

  public static boolean isTeleporterCrossDimension() {
    return teleporterCrossDimension;
  }

  public static void setTeleporterCrossDimension(boolean enable) {
    teleporterCrossDimension = enable;
  }

  public static double getTeleporterCrossDimensionFlatCost() {
    return teleporterCrossDimensionFlatCost;
  }

  public static void setTeleporterCrossDimensionFlatCost(double cost) {
    teleporterCrossDimensionFlatCost = Math.max(0.0, cost);
  }

  public static double getNuclearReactorMultiplier() {
    return nuclearReactorMultiplier;
  }

  public static void setNuclearReactorMultiplier(double mult) {
    nuclearReactorMultiplier = Math.max(0.0, mult);
  }

  public static double getReactorExplosionMultiplier() {
    return reactorExplosionMultiplier;
  }

  public static void setReactorExplosionMultiplier(double mult) {
    reactorExplosionMultiplier = Math.max(0.0, mult);
  }

  public static double getReactorHeatMultiplier() {
    return reactorHeatMultiplier;
  }

  public static void setReactorHeatMultiplier(double mult) {
    reactorHeatMultiplier = Math.max(0.0, mult);
  }

  public static double getUuEnergyMultiplier() {
    return uuEnergyMultiplier;
  }

  public static void setUuEnergyMultiplier(double mult) {
    uuEnergyMultiplier = Math.max(0.001, mult);
  }

  public static double getMatterAmplifierMultiplier() {
    return matterAmplifierMultiplier;
  }

  public static void setMatterAmplifierMultiplier(double mult) {
    matterAmplifierMultiplier = Math.max(0.0, mult);
  }

  public static boolean isRadiationDisabled() {
    return radiationDisabled;
  }

  public static void setRadiationDisabled(boolean disabled) {
    radiationDisabled = disabled;
  }

  public static double getMiningSpeedMultiplier() {
    return miningSpeedMultiplier;
  }

  public static void setMiningSpeedMultiplier(double mult) {
    miningSpeedMultiplier = Math.max(0.01, mult);
  }

  public static double getArmorAbsorptionMultiplier() {
    return armorAbsorptionMultiplier;
  }

  public static void setArmorAbsorptionMultiplier(double mult) {
    armorAbsorptionMultiplier = Math.max(0.0, mult);
  }

  public static double getArmorEnergyCostMultiplier() {
    return armorEnergyCostMultiplier;
  }

  public static void setArmorEnergyCostMultiplier(double mult) {
    armorEnergyCostMultiplier = Math.max(0.0, mult);
  }

  public static double getMachineMultiplier(String machineId) {
    if (machineId == null) return 1.0;
    return machineMultipliers.getOrDefault(machineId.toLowerCase().trim(), 1.0);
  }

  public static void setMachineMultiplier(String machineId, double mult) {
    if (machineId != null) {
      machineMultipliers.put(machineId.toLowerCase().trim(), Math.max(0.0, mult));
    }
  }

  // --- Recycler Blacklist ---

  public static void addRecyclerBlacklist(String itemOrTag) {
    if (itemOrTag != null && !itemOrTag.isBlank()) {
      recyclerBlacklist.add(itemOrTag.trim().toLowerCase());
    }
  }

  public static boolean isRecyclerBlacklisted(ItemStack stack) {
    if (stack == null || stack.isEmpty()) return false;
    for (String pattern : recyclerBlacklist) {
      if (ItemParser.matches(stack, pattern)) {
        return true;
      }
    }
    return false;
  }

  // --- Hazmat / Radiation Protection ---

  public static void addHazmatItem(String itemOrTag) {
    if (itemOrTag != null && !itemOrTag.isBlank()) {
      hazmatItems.add(itemOrTag.trim().toLowerCase());
    }
  }

  public static void addHazmatSuit(String itemOrTag) {
    if (itemOrTag != null && !itemOrTag.isBlank()) {
      hazmatSuits.add(itemOrTag.trim().toLowerCase());
    }
  }

  public static boolean isHazmatPiece(ItemStack stack, EquipmentSlot slot) {
    if (stack == null || stack.isEmpty()) return false;
    for (String pattern : hazmatItems) {
      if (ItemParser.matches(stack, pattern)) {
        return true;
      }
    }
    return false;
  }

  public static boolean isFullHazmatSuit(ItemStack stack) {
    if (stack == null || stack.isEmpty()) return false;
    for (String pattern : hazmatSuits) {
      if (ItemParser.matches(stack, pattern)) {
        return true;
      }
    }
    return false;
  }

  // --- Radioactive Items ---

  public static void addRadioactiveItem(String itemOrTag, int durationSeconds, int amplifier) {
    if (itemOrTag != null && !itemOrTag.isBlank()) {
      radioactiveItems.put(
          itemOrTag.trim().toLowerCase(),
          new RadioactiveItem(Math.max(1, durationSeconds), Math.max(0, amplifier)));
    }
  }

  public static void addRadioactiveItem(String itemOrTag) {
    addRadioactiveItem(itemOrTag, 10, 1);
  }

  public static boolean isRadioactive(ItemStack stack) {
    if (stack == null || stack.isEmpty()) return false;
    for (String pattern : radioactiveItems.keySet()) {
      if (ItemParser.matches(stack, pattern)) {
        return true;
      }
    }
    return false;
  }

  public static int getRadiationDuration(ItemStack stack) {
    if (stack == null || stack.isEmpty()) return 10;
    for (Map.Entry<String, RadioactiveItem> entry : radioactiveItems.entrySet()) {
      if (ItemParser.matches(stack, entry.getKey())) {
        return entry.getValue().durationSeconds();
      }
    }
    return 10;
  }

  public static int getRadiationAmplifier(ItemStack stack) {
    if (stack == null || stack.isEmpty()) return 1;
    for (Map.Entry<String, RadioactiveItem> entry : radioactiveItems.entrySet()) {
      if (ItemParser.matches(stack, entry.getKey())) {
        return entry.getValue().amplifier();
      }
    }
    return 1;
  }

  public static void checkRadioactiveInventory(Player player) {
    if (player == null || player.level().isClientSide || radiationDisabled) return;
    if (radioactiveItems.isEmpty()) return;
    if (player.tickCount % 20 != 0) return;
    if (ItemArmorHazmat.hasCompleteHazmat(player)) return;

    for (ItemStack stack : player.getInventory().items) {
      if (!stack.isEmpty() && isRadioactive(stack)) {
        int duration = getRadiationDuration(stack);
        int amp = getRadiationAmplifier(stack);
        Ic2Potion.radiation.applyTo(player, duration * 20, amp);
        return;
      }
    }
    for (ItemStack stack : player.getInventory().offhand) {
      if (!stack.isEmpty() && isRadioactive(stack)) {
        int duration = getRadiationDuration(stack);
        int amp = getRadiationAmplifier(stack);
        Ic2Potion.radiation.applyTo(player, duration * 20, amp);
        return;
      }
    }
  }

  // --- Fuel Rods ---

  public static IndustrialJS fuelRod(
      String itemOrId, double powerMultiplier, double heatMultiplier, int lifespan) {
    setFuelRodStats(itemOrId, powerMultiplier, heatMultiplier, lifespan);
    return instance;
  }

  public static void setFuelRodStats(
      String itemOrId, double powerMultiplier, double heatMultiplier, int lifespan) {
    if (itemOrId != null) {
      fuelRodStats.put(
          itemOrId.trim().toLowerCase(),
          new FuelRodStats(
              Math.max(0.0, powerMultiplier),
              Math.max(0.0, heatMultiplier),
              Math.max(1, lifespan)));
    }
  }

  public static void setFuelRodPowerMultiplier(String itemOrId, double mult) {
    if (itemOrId != null) {
      FuelRodStats cur =
          fuelRodStats.getOrDefault(
              itemOrId.trim().toLowerCase(), new FuelRodStats(1.0, 1.0, 20000));
      fuelRodStats.put(
          itemOrId.trim().toLowerCase(),
          new FuelRodStats(Math.max(0.0, mult), cur.heatMultiplier(), cur.lifespan()));
    }
  }

  public static void setFuelRodHeatMultiplier(String itemOrId, double mult) {
    if (itemOrId != null) {
      FuelRodStats cur =
          fuelRodStats.getOrDefault(
              itemOrId.trim().toLowerCase(), new FuelRodStats(1.0, 1.0, 20000));
      fuelRodStats.put(
          itemOrId.trim().toLowerCase(),
          new FuelRodStats(cur.powerMultiplier(), Math.max(0.0, mult), cur.lifespan()));
    }
  }

  public static void setFuelRodLifespan(String itemOrId, int lifespan) {
    if (itemOrId != null) {
      FuelRodStats cur =
          fuelRodStats.getOrDefault(
              itemOrId.trim().toLowerCase(), new FuelRodStats(1.0, 1.0, 20000));
      fuelRodStats.put(
          itemOrId.trim().toLowerCase(),
          new FuelRodStats(cur.powerMultiplier(), cur.heatMultiplier(), Math.max(1, lifespan)));
    }
  }

  public static double getFuelRodPowerMultiplier(ItemStack stack) {
    if (stack == null || stack.isEmpty()) return 1.0;
    for (Map.Entry<String, FuelRodStats> entry : fuelRodStats.entrySet()) {
      if (ItemParser.matches(stack, entry.getKey())) {
        return entry.getValue().powerMultiplier();
      }
    }
    return 1.0;
  }

  public static double getFuelRodHeatMultiplier(ItemStack stack) {
    if (stack == null || stack.isEmpty()) return 1.0;
    for (Map.Entry<String, FuelRodStats> entry : fuelRodStats.entrySet()) {
      if (ItemParser.matches(stack, entry.getKey())) {
        return entry.getValue().heatMultiplier();
      }
    }
    return 1.0;
  }

  public static int getFuelRodLifespan(ItemStack stack, int def) {
    if (stack == null || stack.isEmpty()) return def;
    for (Map.Entry<String, FuelRodStats> entry : fuelRodStats.entrySet()) {
      if (ItemParser.matches(stack, entry.getKey())) {
        return entry.getValue().lifespan();
      }
    }
    return def;
  }

  // --- Audio Control & Machine Mute Tools ---

  public static double getMachineVolumeMultiplier() {
    return machineVolumeMultiplier;
  }

  public static void setMachineVolumeMultiplier(double mult) {
    machineVolumeMultiplier = Math.max(0.0, mult);
  }

  public static boolean isAudioDisabled() {
    return audioDisabled;
  }

  public static void setAudioDisabled(boolean disabled) {
    audioDisabled = disabled;
  }

  public static void addMuteTool(String itemOrTag) {
    if (itemOrTag != null && !itemOrTag.isBlank()) {
      muteTools.add(itemOrTag.trim().toLowerCase());
    }
  }

  public static boolean isMuteTool(ItemStack stack) {
    if (stack == null || stack.isEmpty()) return false;
    for (String pattern : muteTools) {
      if (ItemParser.matches(stack, pattern)) {
        return true;
      }
    }
    return false;
  }

  // --- Batch Crafter Customizability ---

  public static double getBatchCrafterSpeedMultiplier() {
    return batchCrafterSpeedMultiplier;
  }

  public static void setBatchCrafterSpeedMultiplier(double mult) {
    batchCrafterSpeedMultiplier = Math.max(0.01, mult);
  }

  public static double getBatchCrafterEnergyMultiplier() {
    return batchCrafterEnergyMultiplier;
  }

  public static void setBatchCrafterEnergyMultiplier(double mult) {
    batchCrafterEnergyMultiplier = Math.max(0.0, mult);
  }

  public static int getBatchCrafterCapacity(int def) {
    return batchCrafterCapacity > 0 ? batchCrafterCapacity : def;
  }

  public static void setBatchCrafterCapacity(int capacity) {
    batchCrafterCapacity = Math.max(1, capacity);
  }

  // --- Solar Helmet Control ---

  public static double getSolarHelmetMultiplier() {
    return solarHelmetMultiplier;
  }

  public static void setSolarHelmetMultiplier(double mult) {
    solarHelmetMultiplier = Math.max(0.0, mult);
  }

  public static void setSolarHelmetMultiplier(String helmetId, double mult) {
    if (helmetId != null) {
      solarHelmetMultipliers.put(helmetId.trim().toLowerCase(), Math.max(0.0, mult));
    }
  }

  public static double getSolarHelmetMultiplier(ItemStack stack) {
    if (stack != null && !stack.isEmpty()) {
      for (Map.Entry<String, Double> entry : solarHelmetMultipliers.entrySet()) {
        if (ItemParser.matches(stack, entry.getKey())) {
          return entry.getValue();
        }
      }
    }
    return solarHelmetMultiplier;
  }

  // --- Jetpack Stats ---

  public static double getJetpackThrustMultiplier() {
    return jetpackThrustMultiplier;
  }

  public static void setJetpackThrustMultiplier(double mult) {
    jetpackThrustMultiplier = Math.max(0.0, mult);
  }

  public static double getJetpackHeightMultiplier() {
    return jetpackHeightMultiplier;
  }

  public static void setJetpackHeightMultiplier(double mult) {
    jetpackHeightMultiplier = Math.max(0.1, mult);
  }

  public static double getJetpackEnergyCostMultiplier() {
    return jetpackEnergyCostMultiplier;
  }

  public static void setJetpackEnergyCostMultiplier(double mult) {
    jetpackEnergyCostMultiplier = Math.max(0.0, mult);
  }

  public static double getJetpackBoostMultiplier() {
    return jetpackBoostMultiplier;
  }

  public static void setJetpackBoostMultiplier(double mult) {
    jetpackBoostMultiplier = Math.max(0.0, mult);
  }

  // --- Kinetic Rotors ---

  public static double getRotorEfficiencyMultiplier() {
    return rotorEfficiencyMultiplier;
  }

  public static void setRotorEfficiencyMultiplier(double mult) {
    rotorEfficiencyMultiplier = Math.max(0.0, mult);
  }

  public static void setRotorStats(
      String rotorId, double efficiencyMultiplier, int minWind, int maxWind) {
    if (rotorId != null) {
      rotorStats.put(
          rotorId.trim().toLowerCase(),
          new RotorStats(
              Math.max(0.0, efficiencyMultiplier),
              Math.max(0, minWind),
              Math.max(minWind, maxWind)));
    }
  }

  public static double getRotorEfficiencyMultiplier(ItemStack stack) {
    if (stack != null && !stack.isEmpty()) {
      for (Map.Entry<String, RotorStats> entry : rotorStats.entrySet()) {
        if (ItemParser.matches(stack, entry.getKey())) {
          return entry.getValue().efficiencyMultiplier() * rotorEfficiencyMultiplier;
        }
      }
    }
    return rotorEfficiencyMultiplier;
  }

  public static int getRotorMinWind(ItemStack stack, int def) {
    if (stack != null && !stack.isEmpty()) {
      for (Map.Entry<String, RotorStats> entry : rotorStats.entrySet()) {
        if (ItemParser.matches(stack, entry.getKey())) {
          return entry.getValue().minWind();
        }
      }
    }
    return def;
  }

  public static int getRotorMaxWind(ItemStack stack, int def) {
    if (stack != null && !stack.isEmpty()) {
      for (Map.Entry<String, RotorStats> entry : rotorStats.entrySet()) {
        if (ItemParser.matches(stack, entry.getKey())) {
          return entry.getValue().maxWind();
        }
      }
    }
    return def;
  }

  // --- Upgrades ---

  public static double getOverclockerSpeedMultiplier() {
    return overclockerSpeedMultiplier;
  }

  public static void setOverclockerSpeedMultiplier(double mult) {
    overclockerSpeedMultiplier = Math.max(0.01, mult);
  }

  public static double getOverclockerEnergyMultiplier() {
    return overclockerEnergyMultiplier;
  }

  public static void setOverclockerEnergyMultiplier(double mult) {
    overclockerEnergyMultiplier = Math.max(0.0, mult);
  }

  public static int getEnergyStorageUpgradeBonus() {
    return energyStorageUpgradeBonus;
  }

  public static void setEnergyStorageUpgradeBonus(int bonus) {
    energyStorageUpgradeBonus = Math.max(1, bonus);
  }

  public static int getTransformerUpgradeTier() {
    return transformerUpgradeTierBonus;
  }

  public static void setTransformerUpgradeTier(int tierBonus) {
    transformerUpgradeTierBonus = Math.max(1, tierBonus);
  }

  public static double getOverclockerSpeedMultiplier(ItemStack stack, double base) {
    return base / Math.max(0.01, overclockerSpeedMultiplier);
  }

  public static double getOverclockerEnergyMultiplier(ItemStack stack, double base) {
    return base * overclockerEnergyMultiplier;
  }

  public static int getEnergyStorageUpgradeBonus(ItemStack stack, int base) {
    return energyStorageUpgradeBonus > 0
        ? (int) Math.round(base * ((double) energyStorageUpgradeBonus / 10000.0))
        : base;
  }

  public static int getTransformerUpgradeTier(ItemStack stack, int base) {
    return Math.max(1, base * transformerUpgradeTierBonus);
  }

  // --- Terraformer ---

  public static double getTerraformerEnergyMultiplier() {
    return terraformerEnergyMultiplier;
  }

  public static void setTerraformerEnergyMultiplier(double mult) {
    terraformerEnergyMultiplier = Math.max(0.0, mult);
  }

  public static double getTerraformerRangeMultiplier() {
    return terraformerRangeMultiplier;
  }

  public static void setTerraformerRangeMultiplier(double mult) {
    terraformerRangeMultiplier = Math.max(0.1, mult);
  }

  // --- Crops Breeding ---

  public static double getCropGrowthMultiplier() {
    return cropGrowthMultiplier;
  }

  public static void setCropGrowthMultiplier(double mult) {
    cropGrowthMultiplier = Math.max(0.0, mult);
  }

  public static double getCropStatGainMultiplier() {
    return cropStatGainMultiplier;
  }

  public static void setCropStatGainMultiplier(double mult) {
    cropStatGainMultiplier = Math.max(0.0, mult);
  }

  public static double getCropCrossingChance() {
    return cropCrossingChance;
  }

  public static void setCropCrossingChance(double mult) {
    cropCrossingChance = Math.max(0.0, mult);
  }

  // --- Electric Armor Tuning ---

  public static void setArmorCapacity(String itemOrId, double capacity) {
    if (itemOrId != null) {
      armorCapacities.put(itemOrId.trim().toLowerCase(), Math.max(1.0, capacity));
    }
  }

  public static double getArmorCapacity(ItemStack stack, double def) {
    if (stack == null || stack.isEmpty()) return def;
    for (Map.Entry<String, Double> entry : armorCapacities.entrySet()) {
      if (ItemParser.matches(stack, entry.getKey())) {
        return entry.getValue();
      }
    }
    return def;
  }

  public static void setArmorTransferLimit(String itemOrId, double transferLimit) {
    if (itemOrId != null) {
      armorTransferLimits.put(itemOrId.trim().toLowerCase(), Math.max(1.0, transferLimit));
    }
  }

  public static double getArmorTransferLimit(ItemStack stack, double def) {
    if (stack == null || stack.isEmpty()) return def;
    for (Map.Entry<String, Double> entry : armorTransferLimits.entrySet()) {
      if (ItemParser.matches(stack, entry.getKey())) {
        return entry.getValue();
      }
    }
    return def;
  }

  // --- Drill Customization ---

  public static void setDrillMiningSpeed(String drillId, double speed) {
    if (drillId != null) {
      drillMiningSpeeds.put(drillId.trim().toLowerCase(), Math.max(0.1, speed));
    }
  }

  public static boolean hasDrillMiningSpeed(String drillId) {
    return drillId != null && drillMiningSpeeds.containsKey(drillId.trim().toLowerCase());
  }

  public static double getDrillMiningSpeed(String drillId, double def) {
    if (drillId == null) return def;
    return drillMiningSpeeds.getOrDefault(drillId.trim().toLowerCase(), def);
  }

  public static void setDrillEnergyPerBlock(String drillId, int energy) {
    if (drillId != null) {
      drillEnergyCosts.put(drillId.trim().toLowerCase(), Math.max(1, energy));
    }
  }

  public static boolean hasDrillEnergyPerBlock(String drillId) {
    return drillId != null && drillEnergyCosts.containsKey(drillId.trim().toLowerCase());
  }

  public static int getDrillEnergyPerBlock(String drillId, int def) {
    if (drillId == null) return def;
    return drillEnergyCosts.getOrDefault(drillId.trim().toLowerCase(), def);
  }

  // --- Nano Saber Stats ---

  public static double getNanoSaberActiveDamage() {
    return nanoSaberActiveDamage;
  }

  public static void setNanoSaberActiveDamage(double damage) {
    nanoSaberActiveDamage = Math.max(1.0, damage);
  }

  public static double getNanoSaberPassiveDamage() {
    return nanoSaberPassiveDamage;
  }

  public static void setNanoSaberPassiveDamage(double damage) {
    nanoSaberPassiveDamage = Math.max(0.0, damage);
  }

  public static double getNanoSaberEnergyPerHit() {
    return nanoSaberEnergyPerHit;
  }

  public static void setNanoSaberEnergyPerHit(double energy) {
    nanoSaberEnergyPerHit = Math.max(1.0, energy);
  }

  public static double getNanoSaberArmorBypass() {
    return nanoSaberArmorBypass;
  }

  public static void setNanoSaberArmorBypass(double ratio) {
    nanoSaberArmorBypass = Math.max(0.0, Math.min(1.0, ratio));
  }

  public static void setNanoSaberStats(Map<String, Object> stats) {
    if (stats == null) return;
    if (stats.containsKey("activeDamage")) {
      setNanoSaberActiveDamage(((Number) stats.get("activeDamage")).doubleValue());
    }
    if (stats.containsKey("passiveDamage")) {
      setNanoSaberPassiveDamage(((Number) stats.get("passiveDamage")).doubleValue());
    }
    if (stats.containsKey("energyPerHit")) {
      setNanoSaberEnergyPerHit(((Number) stats.get("energyPerHit")).doubleValue());
    }
    if (stats.containsKey("armorBypassPercentage")) {
      setNanoSaberArmorBypass(((Number) stats.get("armorBypassPercentage")).doubleValue());
    }
  }

  // --- Mining Laser ---

  public static boolean isLaserExplosiveDestruction() {
    return laserExplosiveDestruction;
  }

  public static void setLaserExplosiveDestruction(boolean enable) {
    laserExplosiveDestruction = enable;
  }

  public static void laserSmelting(Object input, Object output) {
    ItemStack outStack = ItemParser.parseOutput(output);
    if (!outStack.isEmpty()) {
      if (input instanceof String str) {
        laserSmeltRecipes.put(str.trim().toLowerCase(), outStack);
      } else if (input instanceof ItemStack inStack) {
        ResourceLocation loc = BuiltInRegistries.ITEM.getKey(inStack.getItem());
        if (loc != null) {
          laserSmeltRecipes.put(loc.toString(), outStack);
        }
      }
    }
  }

  public static ItemStack getLaserSmelting(ItemStack input) {
    if (input == null || input.isEmpty() || laserSmeltRecipes.isEmpty()) {
      return ItemStack.EMPTY;
    }
    for (Map.Entry<String, ItemStack> entry : laserSmeltRecipes.entrySet()) {
      if (ItemParser.matches(input, entry.getKey())) {
        return entry.getValue();
      }
    }
    return ItemStack.EMPTY;
  }

  // --- Quantum Suit ---

  public static boolean isQuantumSuitVoidProtection() {
    return quantumSuitVoidProtection;
  }

  public static void setQuantumSuitVoidProtection(boolean enable) {
    quantumSuitVoidProtection = enable;
  }

  public static double getQuantumSprintMultiplier() {
    return quantumSprintMultiplier;
  }

  public static void setQuantumSprintMultiplier(double mult) {
    quantumSprintMultiplier = Math.max(0.1, mult);
  }

  public static void setQuantumSprintSpeed(double speed) {
    setQuantumSprintMultiplier(speed);
  }

  public static double getQuantumJumpMultiplier() {
    return quantumJumpMultiplier;
  }

  public static void setQuantumJumpMultiplier(double mult) {
    quantumJumpMultiplier = Math.max(0.0, mult);
  }

  // --- Energy Storage Blocks ---

  public static void setStorageCapacity(String id, int capacity) {
    if (id != null) {
      storageCapacities.put(id.trim().toLowerCase(), Math.max(1, capacity));
    }
  }

  public static int getStorageCapacity(BlockEntityType<?> type, int def) {
    if (type == null) return def;
    ResourceLocation loc = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type);
    if (loc != null) {
      String key = loc.getPath().toLowerCase();
      if (storageCapacities.containsKey(key)) {
        return storageCapacities.get(key);
      }
      if (storageCapacities.containsKey(loc.toString())) {
        return storageCapacities.get(loc.toString());
      }
    }
    return def;
  }

  public static void setStorageOutput(String id, int output) {
    if (id != null) {
      storageOutputs.put(id.trim().toLowerCase(), Math.max(1, output));
    }
  }

  public static int getStorageOutput(BlockEntityType<?> type, int def) {
    if (type == null) return def;
    ResourceLocation loc = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type);
    if (loc != null) {
      String key = loc.getPath().toLowerCase();
      if (storageOutputs.containsKey(key)) {
        return storageOutputs.get(key);
      }
      if (storageOutputs.containsKey(loc.toString())) {
        return storageOutputs.get(loc.toString());
      }
    }
    return def;
  }

  // --- Structure Loot Injection ---

  public static void addStructureLoot(
      String tablePattern, Object itemOrStack, float chance, int minCount, int maxCount) {
    if (tablePattern != null && itemOrStack != null) {
      structureLoot
          .computeIfAbsent(tablePattern.trim().toLowerCase(), k -> new CopyOnWriteArrayList<>())
          .add(
              new StructureLootEntry(
                  itemOrStack,
                  Math.max(0.0f, Math.min(1.0f, chance)),
                  Math.max(1, minCount),
                  Math.max(minCount, maxCount)));
    }
  }

  public static void addStructureLoot(String tablePattern, Object itemOrStack, float chance) {
    addStructureLoot(tablePattern, itemOrStack, chance, 1, 1);
  }

  public static void applyStructureLoot(
      ResourceLocation tableId, List<ItemStack> loot, RandomSource random) {
    if (tableId == null || loot == null || structureLoot.isEmpty()) return;
    String idStr = tableId.toString().toLowerCase();
    String path = tableId.getPath().toLowerCase();

    for (Map.Entry<String, List<StructureLootEntry>> entry : structureLoot.entrySet()) {
      String pattern = entry.getKey();
      if (idStr.contains(pattern) || path.contains(pattern)) {
        for (StructureLootEntry e : entry.getValue()) {
          if (random.nextFloat() <= e.chance()) {
            ItemStack base = ItemParser.parseOutput(e.item());
            if (!base.isEmpty()) {
              int count =
                  e.minCount() >= e.maxCount()
                      ? e.minCount()
                      : e.minCount() + random.nextInt(e.maxCount() - e.minCount() + 1);
              ItemStack toAdd = base.copy();
              toAdd.setCount(count);
              loot.add(toAdd);
            }
          }
        }
      }
    }
  }

  // === Recipe Registration Methods ===

  public static IndustrialJS macerator(Object output, Object input) {
    IndustrialJSRecipeRegistry.queueRecipe(Ic2RecipeTypes.MACERATOR, output, input, null);
    return instance;
  }

  public static IndustrialJS compressor(Object output, Object input) {
    IndustrialJSRecipeRegistry.queueRecipe(Ic2RecipeTypes.COMPRESSOR, output, input, null);
    return instance;
  }

  public static IndustrialJS extractor(Object output, Object input) {
    IndustrialJSRecipeRegistry.queueRecipe(Ic2RecipeTypes.EXTRACTOR, output, input, null);
    return instance;
  }

  public static IndustrialJS oreWasher(Object outputs, Object input, int waterAmount) {
    CompoundTag nbt = new CompoundTag();
    nbt.putInt("amount", Math.max(1, waterAmount));
    IndustrialJSRecipeRegistry.queueRecipe(Ic2RecipeTypes.ORE_WASHER, outputs, input, nbt);
    return instance;
  }

  public static IndustrialJS oreWasher(Object outputs, Object input) {
    return oreWasher(outputs, input, 1000);
  }

  public static IndustrialJS thermalCentrifuge(Object outputs, Object input, int minHeat) {
    CompoundTag nbt = new CompoundTag();
    nbt.putInt("minHeat", Math.max(0, minHeat));
    IndustrialJSRecipeRegistry.queueRecipe(Ic2RecipeTypes.CENTRIFUGE, outputs, input, nbt);
    return instance;
  }

  public static IndustrialJS thermalCentrifuge(Object outputs, Object input) {
    return thermalCentrifuge(outputs, input, 500);
  }

  public static IndustrialJS blastFurnace(Object outputs, Object input, int duration, int minHeat) {
    CompoundTag nbt = new CompoundTag();
    nbt.putInt("duration", Math.max(1, duration));
    nbt.putInt("minHeat", Math.max(0, minHeat));
    IndustrialJSRecipeRegistry.queueRecipe(Ic2RecipeTypes.BLAST_FURNACE, outputs, input, nbt);
    return instance;
  }

  public static IndustrialJS blastFurnace(Object outputs, Object input) {
    return blastFurnace(outputs, input, 6000, 1000);
  }

  public static IndustrialJS blockCutter(Object output, Object input, int hardness) {
    CompoundTag nbt = new CompoundTag();
    nbt.putInt("hardness", Math.max(1, hardness));
    IndustrialJSRecipeRegistry.queueRecipe(Ic2RecipeTypes.BLOCK_CUTTER, output, input, nbt);
    return instance;
  }

  public static IndustrialJS blockCutter(Object output, Object input) {
    return blockCutter(output, input, 1);
  }

  public static IndustrialJS metalFormerExtruding(Object output, Object input) {
    IndustrialJSRecipeRegistry.queueRecipe(
        Ic2RecipeTypes.METAL_FORMER_EXTRUDING, output, input, null);
    return instance;
  }

  public static IndustrialJS metalFormerCutting(Object output, Object input) {
    IndustrialJSRecipeRegistry.queueRecipe(
        Ic2RecipeTypes.METAL_FORMER_CUTTING, output, input, null);
    return instance;
  }

  public static IndustrialJS metalFormerRolling(Object output, Object input) {
    IndustrialJSRecipeRegistry.queueRecipe(
        Ic2RecipeTypes.METAL_FORMER_ROLLING, output, input, null);
    return instance;
  }

  public static IndustrialJS molecularTransformer(Object output, Object input, double euCost) {
    IndustrialJSRecipeRegistry.queueMolecularTransformer(output, input, euCost);
    return instance;
  }

  public static IndustrialJS scrapbox(Object item, float weight) {
    IndustrialJSRecipeRegistry.queueScrapbox(item, weight);
    return instance;
  }

  public static IndustrialJS amplifier(Object item, int amplification) {
    IndustrialJSRecipeRegistry.queueAmplifier(item, amplification);
    return instance;
  }

  public static IndustrialJS cannerEnrich(Object outputFluid, Object inputFluid, Object additive) {
    IndustrialJSRecipeRegistry.queueCannerEnrich(outputFluid, inputFluid, additive);
    return instance;
  }

  public static IndustrialJS cannerBottle(Object output, Object container, Object fill) {
    IndustrialJSRecipeRegistry.queueCannerBottle(output, container, fill);
    return instance;
  }

  // Chainable instance helper
  private static final IndustrialJS instance = new IndustrialJS();

  public static IndustrialJS recipes() {
    return instance;
  }

  public static IndustrialJS recipes(Object ignoredEvent) {
    return instance;
  }
}
