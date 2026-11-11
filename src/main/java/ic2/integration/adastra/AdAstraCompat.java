package ic2.integration.adastra;

import ic2.core.IC2;
import ic2.core.fluid.LiquidFuelCompat;
import ic2.core.util.LogCategory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.fml.ModList;

/** Dedicated bridge for Ad Astra fluids (Oil, Rocket Fuel, Cryo Fuel, Hydrogen). */
public final class AdAstraCompat {
  public static final String MOD_ID = "ad_astra";

  private AdAstraCompat() {}

  public static void init() {
    if (!ModList.get().isLoaded(MOD_ID)) {
      return;
    }

    try {
      registerIfPresent("oil", LiquidFuelCompat.FuelCategory.OIL);
      registerIfPresent("fuel", LiquidFuelCompat.FuelCategory.REFINED_FUEL);
      registerIfPresent("cryo_fuel", LiquidFuelCompat.FuelCategory.CRYO_FUEL);
      registerIfPresent("hydrogen", LiquidFuelCompat.FuelCategory.HYDROGEN);

      IC2.log.info(
          LogCategory.General,
          "Ad Astra fluid compatibility loaded: Oil, Fuel, Cryo Fuel, and Hydrogen registered for Semifluid Generator.");
    } catch (Throwable t) {
      IC2.log.warn(
          LogCategory.General, "Failed to initialize Ad Astra compatibility: %s", t.getMessage());
    }
  }

  private static void registerIfPresent(String name, LiquidFuelCompat.FuelCategory category) {
    ResourceLocation stillLoc = ResourceLocation.fromNamespaceAndPath(MOD_ID, name);
    ResourceLocation flowLoc = ResourceLocation.fromNamespaceAndPath(MOD_ID, "flowing_" + name);
    Fluid still = BuiltInRegistries.FLUID.get(stillLoc);
    Fluid flowing = BuiltInRegistries.FLUID.get(flowLoc);
    if (still != null && still != Fluids.EMPTY) {
      LiquidFuelCompat.registerCustomFuel(still, category);
    }
    if (flowing != null && flowing != Fluids.EMPTY) {
      LiquidFuelCompat.registerCustomFuel(flowing, category);
    }
  }
}
