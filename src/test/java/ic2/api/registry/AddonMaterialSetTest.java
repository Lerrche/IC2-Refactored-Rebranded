package ic2.api.registry;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AddonMaterialSetTest {

  @Test
  void testMaterialNameValidation() {
    Ic2AddonContent content = Ic2AddonContent.create("test_addon");

    assertThrows(
        IllegalArgumentException.class,
        () -> AddonMaterialSet.builder(content, "Invalid Material"));
    assertThrows(
        IllegalArgumentException.class, () -> AddonMaterialSet.builder(content, "INVALID"));
    assertThrows(IllegalArgumentException.class, () -> AddonMaterialSet.builder(content, ""));

    assertDoesNotThrow(() -> AddonMaterialSet.builder(content, "refined_iron"));
  }

  @Test
  void testFormValidationAndDuplicateRejection() {
    Ic2AddonContent content = Ic2AddonContent.create("test_addon");
    AddonMaterialSet.Builder builder = AddonMaterialSet.builder(content, "lead");

    assertThrows(IllegalArgumentException.class, () -> builder.itemForm("Bad Form"));
    assertThrows(IllegalArgumentException.class, () -> builder.itemForm(""));

    builder.itemForm("ingot");
    assertThrows(IllegalArgumentException.class, () -> builder.itemForm("ingot"));
  }

  @Test
  void testBuildMaterialSet() {
    Ic2AddonContent content = Ic2AddonContent.create("test_addon");
    AddonMaterialSet set =
        AddonMaterialSet.builder(content, "silver")
            .itemForm("ingot")
            .itemForm("dust")
            .itemForm("plate")
            .build();

    assertEquals("silver", set.material());
    assertEquals(3, set.items().size());
    assertNotNull(set.item("ingot"));
    assertNotNull(set.item("dust"));
    assertNotNull(set.item("plate"));
    assertThrows(NullPointerException.class, () -> set.item("nonexistent"));
  }
}
