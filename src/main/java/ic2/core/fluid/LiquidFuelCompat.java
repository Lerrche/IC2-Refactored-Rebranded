package ic2.core.fluid;

import ic2.api.recipe.ISemiFluidFuelManager.BurnProperty;
import ic2.core.IC2;
import ic2.core.init.IC2Config;
import ic2.core.util.LogCategory;
import ic2.integration.adastra.AdAstraCompat;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

/**
 * Compatibility system that automatically detects and resolves liquid fuels from mod tags, naming
 * conventions, and furnace burn values across Ad Astra and other tech mods for use in the Semifluid
 * Generator.
 */
public final class LiquidFuelCompat {

  public enum FuelCategory {
    CRYO_FUEL(5, 64.0),
    REFINED_FUEL(5, 32.0),
    OIL(10, 16.0),
    BIOFUEL(10, 24.0),
    HYDROGEN(4, 32.0),
    CREOSOTE(8, 32.0),
    BIOGAS(20, 32.0),
    BIOMASS(40, 32.0),
    GENERIC(10, 16.0);

    public final int amount;
    public final double basePower;

    FuelCategory(int amount, double basePower) {
      this.amount = amount;
      this.basePower = basePower;
    }
  }

  // Common convention tags (c:, forge:, neoforge:)
  public static final TagKey<Fluid> C_OIL = fluidTag("c", "oil");
  public static final TagKey<Fluid> C_CRUDE_OIL = fluidTag("c", "crude_oil");
  public static final TagKey<Fluid> C_FUELS_OIL = fluidTag("c", "fuels/oil");
  public static final TagKey<Fluid> C_FUELS_CRUDE_OIL = fluidTag("c", "fuels/crude_oil");
  public static final TagKey<Fluid> FORGE_OIL = fluidTag("forge", "oil");
  public static final TagKey<Fluid> FORGE_CRUDE_OIL = fluidTag("forge", "crude_oil");
  public static final TagKey<Fluid> NEOFORGE_OIL = fluidTag("neoforge", "oil");

  public static final TagKey<Fluid> C_FUELS = fluidTag("c", "fuels");
  public static final TagKey<Fluid> C_FUEL = fluidTag("c", "fuel");
  public static final TagKey<Fluid> C_FUELS_FUEL = fluidTag("c", "fuels/fuel");
  public static final TagKey<Fluid> C_FUELS_GASOLINE = fluidTag("c", "fuels/gasoline");
  public static final TagKey<Fluid> C_FUELS_DIESEL = fluidTag("c", "fuels/diesel");
  public static final TagKey<Fluid> C_FUELS_KEROSENE = fluidTag("c", "fuels/kerosene");
  public static final TagKey<Fluid> C_FUELS_JET_FUEL = fluidTag("c", "fuels/jet_fuel");
  public static final TagKey<Fluid> C_FUELS_REFINED_FUEL = fluidTag("c", "fuels/refined_fuel");
  public static final TagKey<Fluid> C_DIESEL = fluidTag("c", "diesel");
  public static final TagKey<Fluid> C_GASOLINE = fluidTag("c", "gasoline");
  public static final TagKey<Fluid> C_KEROSENE = fluidTag("c", "kerosene");
  public static final TagKey<Fluid> FORGE_FUEL = fluidTag("forge", "fuel");
  public static final TagKey<Fluid> NEOFORGE_FUEL = fluidTag("neoforge", "fuel");

  public static final TagKey<Fluid> C_BIOFUEL = fluidTag("c", "biofuel");
  public static final TagKey<Fluid> C_FUELS_BIOFUEL = fluidTag("c", "fuels/biofuel");
  public static final TagKey<Fluid> C_BIODIESEL = fluidTag("c", "biodiesel");
  public static final TagKey<Fluid> C_FUELS_BIODIESEL = fluidTag("c", "fuels/biodiesel");
  public static final TagKey<Fluid> C_ETHANOL = fluidTag("c", "ethanol");
  public static final TagKey<Fluid> C_FUELS_ETHANOL = fluidTag("c", "fuels/ethanol");
  public static final TagKey<Fluid> C_BIOETHANOL = fluidTag("c", "bioethanol");
  public static final TagKey<Fluid> C_SEED_OIL = fluidTag("c", "seed_oil");
  public static final TagKey<Fluid> C_PLANTOIL = fluidTag("c", "plantoil");

  public static final TagKey<Fluid> C_HYDROGEN = fluidTag("c", "hydrogen");
  public static final TagKey<Fluid> FORGE_HYDROGEN = fluidTag("forge", "hydrogen");

  public static final TagKey<Fluid> C_CREOSOTE = fluidTag("c", "creosote");
  public static final TagKey<Fluid> FORGE_CREOSOTE = fluidTag("forge", "creosote");

  public static final TagKey<Fluid> C_BIOGAS = fluidTag("c", "biogas");
  public static final TagKey<Fluid> C_BIOMASS = fluidTag("c", "biomass");

  // Ad Astra mod tags
  public static final TagKey<Fluid> AD_ASTRA_OIL = fluidTag("ad_astra", "oil");
  public static final TagKey<Fluid> AD_ASTRA_FUEL = fluidTag("ad_astra", "fuel");
  public static final TagKey<Fluid> AD_ASTRA_EFFICIENT_FUEL =
      fluidTag("ad_astra", "efficient_fuel");
  public static final TagKey<Fluid> AD_ASTRA_CRYO_FUEL = fluidTag("ad_astra", "cryo_fuel");
  public static final TagKey<Fluid> AD_ASTRA_HYDROGEN = fluidTag("ad_astra", "hydrogen");
  public static final TagKey<Fluid> AD_ASTRA_TIER_1_ROCKET_FUEL =
      fluidTag("ad_astra", "tier_1_rocket_fuel");
  public static final TagKey<Fluid> AD_ASTRA_TIER_2_ROCKET_FUEL =
      fluidTag("ad_astra", "tier_2_rocket_fuel");
  public static final TagKey<Fluid> AD_ASTRA_TIER_3_ROCKET_FUEL =
      fluidTag("ad_astra", "tier_3_rocket_fuel");
  public static final TagKey<Fluid> AD_ASTRA_TIER_4_ROCKET_FUEL =
      fluidTag("ad_astra", "tier_4_rocket_fuel");
  public static final TagKey<Fluid> AD_ASTRA_TIER_1_ROVER_FUEL =
      fluidTag("ad_astra", "tier_1_rover_fuel");

  private static final Map<Fluid, BurnProperty> OVERRIDES = new ConcurrentHashMap<>();

  private LiquidFuelCompat() {}

  public static void init() {
    AdAstraCompat.init();
    IC2.log.debug(LogCategory.General, "Liquid fuel compatibility initialized.");
  }

  public static void registerCustomFuel(Fluid fluid, FuelCategory category) {
    if (fluid == null || fluid == Fluids.EMPTY) {
      return;
    }
    BurnProperty prop = createBurnProperty(category);
    if (prop != null) {
      OVERRIDES.put(fluid, prop);
      if (fluid instanceof FlowingFluid flowingFluid) {
        Fluid source = flowingFluid.getSource();
        Fluid flowing = flowingFluid.getFlowing();
        if (source != null) OVERRIDES.put(source, prop);
        if (flowing != null) OVERRIDES.put(flowing, prop);
      }
    }
  }

  public static void registerCustomFuel(Fluid fluid, int amount, double power) {
    if (fluid == null || fluid == Fluids.EMPTY) {
      return;
    }
    BurnProperty prop = new BurnProperty(amount, power);
    OVERRIDES.put(fluid, prop);
    if (fluid instanceof FlowingFluid flowingFluid) {
      Fluid source = flowingFluid.getSource();
      Fluid flowing = flowingFluid.getFlowing();
      if (source != null) OVERRIDES.put(source, prop);
      if (flowing != null) OVERRIDES.put(flowing, prop);
    }
  }

  public static BurnProperty resolve(Fluid fluid) {
    if (fluid == null || fluid == Fluids.EMPTY) {
      return null;
    }

    if (OVERRIDES.containsKey(fluid)) {
      return OVERRIDES.get(fluid);
    }

    if (isExcluded(fluid)) {
      return null;
    }

    if (!IC2Config.balance.energy.generator.autoDetectLiquidFuels.get()) {
      return null;
    }

    FuelCategory category = categorize(fluid);
    if (category == null) {
      category = checkBucketBurnTime(fluid);
    }

    if (category != null) {
      return createBurnProperty(category);
    }

    return null;
  }

  public static FuelCategory categorize(Fluid fluid) {
    if (fluid == null || fluid == Fluids.EMPTY) {
      return null;
    }

    ResourceLocation key = BuiltInRegistries.FLUID.getKey(fluid);
    if (key == null) {
      return null;
    }

    String ns = key.getNamespace();
    String path = key.getPath().toLowerCase(Locale.ROOT);
    if (path.startsWith("flowing_")) {
      path = path.substring(8);
    }

    // Direct Ad Astra fluid registry IDs
    if (ns.equals("ad_astra")) {
      if (path.equals("cryo_fuel")) return FuelCategory.CRYO_FUEL;
      if (path.equals("fuel")) return FuelCategory.REFINED_FUEL;
      if (path.equals("oil")) return FuelCategory.OIL;
      if (path.equals("hydrogen")) return FuelCategory.HYDROGEN;
    }

    // Cryo Fuel (highest tier rocket fuel)
    if (fluid.is(AD_ASTRA_CRYO_FUEL)
        || fluid.is(AD_ASTRA_EFFICIENT_FUEL)
        || path.equals("cryo_fuel")
        || path.endsWith("_cryo_fuel")) {
      return FuelCategory.CRYO_FUEL;
    }

    // Biofuels / Bioethanol / Biodiesel / Plant & Seed Oils
    if (fluid.is(C_BIOFUEL)
        || fluid.is(C_FUELS_BIOFUEL)
        || fluid.is(C_BIODIESEL)
        || fluid.is(C_FUELS_BIODIESEL)
        || fluid.is(C_ETHANOL)
        || fluid.is(C_FUELS_ETHANOL)
        || fluid.is(C_BIOETHANOL)
        || fluid.is(C_SEED_OIL)
        || fluid.is(C_PLANTOIL)
        || path.contains("biofuel")
        || path.contains("biodiesel")
        || path.contains("bioethanol")
        || path.contains("ethanol")
        || path.contains("seed_oil")
        || path.contains("tree_oil")
        || path.contains("plant_oil")
        || path.contains("plantoil")) {
      return FuelCategory.BIOFUEL;
    }

    // Refined Fuels / Rocket Fuels / Diesel / Gasoline / Kerosene
    if (fluid.is(AD_ASTRA_FUEL)
        || fluid.is(AD_ASTRA_TIER_1_ROCKET_FUEL)
        || fluid.is(AD_ASTRA_TIER_2_ROCKET_FUEL)
        || fluid.is(AD_ASTRA_TIER_3_ROCKET_FUEL)
        || fluid.is(AD_ASTRA_TIER_4_ROCKET_FUEL)
        || fluid.is(AD_ASTRA_TIER_1_ROVER_FUEL)
        || fluid.is(C_FUELS)
        || fluid.is(C_FUEL)
        || fluid.is(C_FUELS_FUEL)
        || fluid.is(C_FUELS_GASOLINE)
        || fluid.is(C_FUELS_DIESEL)
        || fluid.is(C_FUELS_KEROSENE)
        || fluid.is(C_FUELS_JET_FUEL)
        || fluid.is(C_FUELS_REFINED_FUEL)
        || fluid.is(C_DIESEL)
        || fluid.is(C_GASOLINE)
        || fluid.is(C_KEROSENE)
        || fluid.is(FORGE_FUEL)
        || fluid.is(NEOFORGE_FUEL)
        || path.equals("fuel")
        || path.endsWith("_fuel")
        || path.contains("gasoline")
        || path.contains("diesel")
        || path.contains("kerosene")
        || path.contains("jet_fuel")
        || path.contains("refined_fuel")
        || path.contains("rocket_fuel")) {
      return FuelCategory.REFINED_FUEL;
    }

    // Oil / Crude Oil / Petroleum
    if (fluid.is(AD_ASTRA_OIL)
        || fluid.is(C_OIL)
        || fluid.is(C_CRUDE_OIL)
        || fluid.is(C_FUELS_OIL)
        || fluid.is(C_FUELS_CRUDE_OIL)
        || fluid.is(FORGE_OIL)
        || fluid.is(FORGE_CRUDE_OIL)
        || fluid.is(NEOFORGE_OIL)
        || path.equals("oil")
        || path.contains("crude_oil")
        || path.contains("petroleum")
        || path.endsWith("_oil")) {
      return FuelCategory.OIL;
    }

    // Hydrogen
    if (fluid.is(AD_ASTRA_HYDROGEN)
        || fluid.is(C_HYDROGEN)
        || fluid.is(FORGE_HYDROGEN)
        || path.contains("hydrogen")) {
      return FuelCategory.HYDROGEN;
    }

    // Creosote
    if (fluid.is(C_CREOSOTE) || fluid.is(FORGE_CREOSOTE) || path.contains("creosote")) {
      return FuelCategory.CREOSOTE;
    }

    // Biogas & Biomass
    if (fluid.is(C_BIOGAS) || path.contains("biogas")) {
      return FuelCategory.BIOGAS;
    }
    if (fluid.is(C_BIOMASS) || path.contains("biomass")) {
      return FuelCategory.BIOMASS;
    }

    return null;
  }

  public static boolean isExcluded(Fluid fluid) {
    if (fluid == Fluids.EMPTY
        || fluid == Fluids.WATER
        || fluid == Fluids.FLOWING_WATER
        || fluid == Fluids.LAVA
        || fluid == Fluids.FLOWING_LAVA) {
      return true;
    }

    ResourceLocation key = BuiltInRegistries.FLUID.getKey(fluid);
    if (key == null) {
      return true;
    }

    String ns = key.getNamespace();
    String path = key.getPath().toLowerCase(Locale.ROOT);

    // IC2 non-fuel fluids
    if (ns.equals("ic2")) {
      if (path.contains("uu_matter")
          || path.contains("coolant")
          || path.contains("pahoehoe")
          || path.contains("construction_foam")
          || path.contains("weed_ex")
          || path.contains("steam")
          || path.contains("air")
          || path.contains("oxygen")) {
        return true;
      }
    }

    // Non-fuel liquids from Minecraft and tech mods
    if (path.contains("oxygen")
        || path.contains("water")
        || path.contains("lava")
        || path.contains("milk")
        || path.contains("potion")
        || path.contains("foam")
        || path.contains("coolant")
        || path.contains("acid")) {
      return true;
    }

    return false;
  }

  private static FuelCategory checkBucketBurnTime(Fluid fluid) {
    try {
      Item bucket = fluid.getBucket();
      if (bucket != null && bucket != Items.AIR && bucket != Items.BUCKET) {
        ItemStack stack = new ItemStack(bucket);
        int burnTime = IC2.envProxy.getBurnTime(stack);
        if (burnTime > 0) {
          return FuelCategory.GENERIC;
        }
      }
    } catch (Throwable ignored) {
    }
    return null;
  }

  public static BurnProperty createBurnProperty(FuelCategory category) {
    var gen = IC2Config.balance.energy.generator;
    double multiplier;

    switch (category) {
      case CRYO_FUEL -> multiplier = gen.semiFluidCryoFuel.get();
      case REFINED_FUEL -> multiplier = gen.semiFluidFuel.get();
      case OIL -> multiplier = gen.semiFluidOil.get();
      case BIOFUEL -> multiplier = gen.semiFluidBiofuel.get();
      case HYDROGEN -> multiplier = gen.semiFluidHydrogen.get();
      case CREOSOTE -> multiplier = gen.semiFluidCreosote.get();
      case BIOGAS -> multiplier = gen.semiFluidBiogas.get();
      case BIOMASS -> multiplier = gen.semiFluidBiomass.get();
      case GENERIC -> multiplier = gen.semiFluidFuel.get();
      default -> multiplier = 1.0;
    }

    if (multiplier <= 0.0) {
      return null;
    }

    int power = (int) Math.round(category.basePower * multiplier);
    if (power <= 0) {
      return null;
    }

    return new BurnProperty(category.amount, power);
  }

  private static TagKey<Fluid> fluidTag(String namespace, String path) {
    return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(namespace, path));
  }
}
