package ic2.api.upgrade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.EnumSet;
import java.util.Set;
import net.minecraft.world.item.ItemStack;
import org.junit.jupiter.api.Test;

class AddonUpgradeItemTest {

  @Test
  void testSpeedUpgrade() {
    UpgradeDefinition speed = UpgradeDefinition.speed(0.7, 1.3);

    assertEquals(0.7, speed.getProcessTimeMultiplier(ItemStack.EMPTY, null), 1e-6);
    assertEquals(1.3, speed.getEnergyDemandMultiplier(ItemStack.EMPTY, null), 1e-6);
    assertEquals(0, speed.getExtraEnergyStorage(ItemStack.EMPTY, null));
    assertEquals(0, speed.getExtraTier(ItemStack.EMPTY, null));

    assertTrue(speed.isSuitableFor(ItemStack.EMPTY, EnumSet.of(UpgradableProperty.Processing)));
    assertFalse(speed.isSuitableFor(ItemStack.EMPTY, EnumSet.of(UpgradableProperty.Transformer)));
  }

  @Test
  void testEnergyStorageUpgrade() {
    UpgradeDefinition storage = UpgradeDefinition.energyStorage(10000);

    assertEquals(10000, storage.getExtraEnergyStorage(ItemStack.EMPTY, null));
    assertEquals(1.0, storage.getEnergyStorageMultiplier(ItemStack.EMPTY, null), 1e-6);
    assertTrue(
        storage.isSuitableFor(ItemStack.EMPTY, EnumSet.of(UpgradableProperty.EnergyStorage)));
    assertFalse(storage.isSuitableFor(ItemStack.EMPTY, EnumSet.of(UpgradableProperty.Processing)));
  }

  @Test
  void testTransformerUpgrade() {
    UpgradeDefinition transformer = UpgradeDefinition.transformer(2);

    assertEquals(2, transformer.getExtraTier(ItemStack.EMPTY, null));
    assertTrue(
        transformer.isSuitableFor(ItemStack.EMPTY, EnumSet.of(UpgradableProperty.Transformer)));
  }

  @Test
  void testCustomUpgradeBuilder() {
    UpgradeDefinition custom =
        UpgradeDefinition.builder()
            .suitableFor(UpgradableProperty.Processing, UpgradableProperty.EnergyStorage)
            .extraProcessTime(-20)
            .processTimeMultiplier(0.5)
            .extraEnergyDemand(5)
            .energyDemandMultiplier(1.5)
            .extraEnergyStorage(20000)
            .energyStorageMultiplier(1.2)
            .extraTier(1)
            .augmentation(3)
            .rangeAmplification(4)
            .modifiesRedstone(true)
            .build();

    assertNotNull(custom);
    assertEquals(-20, custom.getExtraProcessTime(ItemStack.EMPTY, null));
    assertEquals(0.5, custom.getProcessTimeMultiplier(ItemStack.EMPTY, null), 1e-6);
    assertEquals(5, custom.getExtraEnergyDemand(ItemStack.EMPTY, null));
    assertEquals(1.5, custom.getEnergyDemandMultiplier(ItemStack.EMPTY, null), 1e-6);
    assertEquals(20000, custom.getExtraEnergyStorage(ItemStack.EMPTY, null));
    assertEquals(1.2, custom.getEnergyStorageMultiplier(ItemStack.EMPTY, null), 1e-6);
    assertEquals(1, custom.getExtraTier(ItemStack.EMPTY, null));
    assertEquals(3, custom.getAugmentation(ItemStack.EMPTY, null));
    assertEquals(14, custom.getRangeAmplification(ItemStack.EMPTY, null, 10));
    assertTrue(custom.modifiesRedstoneInput(ItemStack.EMPTY, null));

    Set<UpgradableProperty> suitable = custom.getSuitableProperties();
    assertTrue(suitable.contains(UpgradableProperty.Processing));
    assertTrue(suitable.contains(UpgradableProperty.EnergyStorage));
    assertFalse(suitable.contains(UpgradableProperty.Transformer));
  }

  @Test
  void testInvalidBuilderArguments() {
    assertThrows(
        IllegalArgumentException.class,
        () -> UpgradeDefinition.builder().processTimeMultiplier(0.0));

    assertThrows(
        IllegalArgumentException.class,
        () -> UpgradeDefinition.builder().energyDemandMultiplier(-1.0));

    assertThrows(
        IllegalArgumentException.class,
        () -> UpgradeDefinition.builder().energyStorageMultiplier(-0.5));
  }
}
