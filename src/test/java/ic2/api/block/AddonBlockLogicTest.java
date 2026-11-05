package ic2.api.block;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AddonBlockLogicTest {

  @Test
  void testFePerEuConstant() {
    assertEquals(2.0, AddonEnergyBlockEntity.FE_PER_EU, 1e-9);
  }

  @Test
  void testMachineProgressMath() {
    int duration = 200;
    int progress = 50;
    int percent = Math.min(100, progress * 100 / Math.max(1, duration));
    assertEquals(25, percent);

    progress = 200;
    percent = Math.min(100, progress * 100 / Math.max(1, duration));
    assertEquals(100, percent);
  }

  @Test
  void testEuFeEnergyEquivalence() {
    double eu = 128.0;
    int fe = (int) Math.floor(eu * AddonEnergyBlockEntity.FE_PER_EU);
    assertEquals(256, fe);

    double backToEu = fe / AddonEnergyBlockEntity.FE_PER_EU;
    assertEquals(128.0, backToEu, 1e-9);
  }
}
