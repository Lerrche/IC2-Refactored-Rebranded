package ic2.gravisuite;

import static org.junit.jupiter.api.Assertions.*;

import ic2.core.item.armor.jetpack.IBoostingJetpack;
import ic2.core.item.armor.jetpack.IJetpack;
import ic2.gravisuite.items.AdvancedChainsawItem;
import ic2.gravisuite.items.AdvancedDrillItem;
import ic2.gravisuite.items.AdvancedJetpackItem;
import ic2.gravisuite.items.AdvancedNanoChestplateItem;
import ic2.gravisuite.items.GraviChestplateItem;
import ic2.gravisuite.items.GraviToolItem;
import ic2.gravisuite.items.VajraItem;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GraviSuiteItemsTest {

  @Test
  @DisplayName("Vajra specifications match classic 1.12.2 balance")
  void testVajraSpecs() {
    assertEquals(3_000_000.0, VajraItem.MAX_CHARGE);
    assertEquals(30_000.0, VajraItem.TRANSFER_LIMIT);
    assertEquals(3, VajraItem.TIER);
    assertEquals(3_333.0, VajraItem.ENERGY_PER_BLOCK);
    assertEquals(5_000.0, VajraItem.ENERGY_PER_HIT);
    assertEquals(60.0F, VajraItem.INSTANT_MINE_SPEED);
  }

  @Test
  @DisplayName("Advanced Drill has 4 distinct operation modes")
  void testAdvancedDrillModes() {
    AdvancedDrillItem.Mode[] modes = AdvancedDrillItem.Mode.values();
    assertEquals(4, modes.length);

    assertEquals("normal", AdvancedDrillItem.Mode.NORMAL.getId());
    assertEquals(35.0F, AdvancedDrillItem.Mode.NORMAL.getSpeed());
    assertEquals(160.0, AdvancedDrillItem.Mode.NORMAL.getEnergyCost());

    assertEquals("low_power", AdvancedDrillItem.Mode.LOW_POWER.getId());
    assertEquals(16.0F, AdvancedDrillItem.Mode.LOW_POWER.getSpeed());
    assertEquals(80.0, AdvancedDrillItem.Mode.LOW_POWER.getEnergyCost());

    assertEquals("fine", AdvancedDrillItem.Mode.FINE.getId());
    assertEquals(10.0F, AdvancedDrillItem.Mode.FINE.getSpeed());
    assertEquals(50.0, AdvancedDrillItem.Mode.FINE.getEnergyCost());

    assertEquals("big_holes", AdvancedDrillItem.Mode.BIG_HOLES.getId());
    assertEquals(25.0F, AdvancedDrillItem.Mode.BIG_HOLES.getSpeed());
    assertEquals(300.0, AdvancedDrillItem.Mode.BIG_HOLES.getEnergyCost());
  }

  @Test
  @DisplayName("Advanced Chainsaw has 3 operation modes")
  void testAdvancedChainsawModes() {
    AdvancedChainsawItem.Mode[] modes = AdvancedChainsawItem.Mode.values();
    assertEquals(3, modes.length);

    assertEquals("normal", AdvancedChainsawItem.Mode.NORMAL.getId());
    assertEquals("shears", AdvancedChainsawItem.Mode.SHEARS.getId());
    assertEquals("tree_feller", AdvancedChainsawItem.Mode.TREE_FELLER.getId());
  }

  @Test
  @DisplayName("GraviTool has 4 switchable functions")
  void testGraviToolModes() {
    GraviToolItem.Mode[] modes = GraviToolItem.Mode.values();
    assertEquals(4, modes.length);

    assertEquals("wrench", GraviToolItem.Mode.WRENCH.getId());
    assertEquals("hoe", GraviToolItem.Mode.HOE.getId());
    assertEquals("treetap", GraviToolItem.Mode.TREETAP.getId());
    assertEquals("screwdriver", GraviToolItem.Mode.SCREWDRIVER.getId());
  }

  @Test
  @DisplayName("GraviChestPlate specs match 30M EU capacity and tier 3")
  void testGraviChestplateSpecs() {
    assertEquals(30_000_000.0, GraviChestplateItem.MAX_CHARGE);
    assertEquals(30_000.0, GraviChestplateItem.TRANSFER_LIMIT);
    assertEquals(3, GraviChestplateItem.TIER);
    assertEquals(5.0, GraviChestplateItem.FLIGHT_DRAIN_PER_TICK);
    assertEquals(50.0, GraviChestplateItem.BOOST_DRAIN_PER_TICK);
  }

  @Test
  @DisplayName("Advanced Electric Jetpack implements IBoostingJetpack with proper world divisor")
  void testAdvancedJetpackSpecs() {
    AdvancedJetpackItem jetpack = GraviSuiteMod.ADVANCED_JETPACK.get();
    assertTrue(jetpack instanceof IJetpack);
    assertTrue(jetpack instanceof IBoostingJetpack);

    assertEquals(
        1.0F,
        jetpack.getWorldHeightDivisor(ItemStack.EMPTY),
        "Divisor must be 1.0F so flight isn't cut off");
    assertEquals(1.0F, jetpack.getPower(ItemStack.EMPTY));
    assertEquals(0.05F, jetpack.getDropPercentage(ItemStack.EMPTY));
    assertEquals(0.2F, jetpack.getHoverMultiplier(ItemStack.EMPTY, true));
    assertEquals(0.65F, jetpack.getBaseThrust(ItemStack.EMPTY, true));
    assertEquals(0.3F, jetpack.getBaseThrust(ItemStack.EMPTY, false));
  }

  @Test
  @DisplayName("Advanced NanoChestPlate combines NanoSuit armor with jetpack thrust")
  void testAdvancedNanoChestplateSpecs() {
    AdvancedNanoChestplateItem nano = GraviSuiteMod.ADVANCED_NANO_CHESTPLATE.get();
    assertTrue(nano instanceof IBoostingJetpack);
    assertEquals(3_000_000.0, AdvancedNanoChestplateItem.MAX_CHARGE);
    assertEquals(5_000.0, AdvancedNanoChestplateItem.TRANSFER_LIMIT);
    assertEquals(3, AdvancedNanoChestplateItem.TIER);
    assertEquals(5_000, nano.getEnergyPerDamage());
    assertEquals(0.8, nano.getDamageAbsorptionRatio());
  }
}
