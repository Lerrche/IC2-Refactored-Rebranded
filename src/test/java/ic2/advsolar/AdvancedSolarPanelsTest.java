package ic2.advsolar;

import static org.junit.jupiter.api.Assertions.*;

import ic2.advsolar.blocks.entity.MolecularTransformerBlockEntity;
import ic2.advsolar.blocks.entity.QuantumGeneratorBlockEntity;
import ic2.advsolar.client.jei.AdvancedSolarJeiPlugin;
import ic2.advsolar.client.jei.MolecularTransformerCategory;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class AdvancedSolarPanelsTest {

  @Test
  @DisplayName("Molecular Transformer contains default transmutation recipes")
  void testMolecularTransformerRecipes() {
    List<MolecularTransformerBlockEntity.TransmutationRecipe> recipes =
        MolecularTransformerBlockEntity.getRecipes();
    assertFalse(recipes.isEmpty(), "Recipes list should not be empty");

    // Verify Coal -> Diamond transmutation is present
    boolean foundCoalToDiamond =
        recipes.stream()
            .anyMatch(
                r ->
                    r.input().test(new ItemStack(Items.COAL))
                        && r.output().getItem() == Items.DIAMOND
                        && r.euCost() == 9_000_000.0);
    assertTrue(foundCoalToDiamond, "Coal to Diamond recipe must be present");

    // Verify Skull -> Wither Skull transmutation
    boolean foundSkull =
        recipes.stream()
            .anyMatch(
                r ->
                    r.input().test(new ItemStack(Items.SKELETON_SKULL))
                        && r.output().getItem() == Items.WITHER_SKELETON_SKULL
                        && r.euCost() == 3_000_000.0);
    assertTrue(foundSkull, "Skeleton Skull to Wither Skull recipe must be present");

    // Verify Wither Skull -> Nether Star transmutation
    boolean foundNetherStar =
        recipes.stream()
            .anyMatch(
                r ->
                    r.input().test(new ItemStack(Items.WITHER_SKELETON_SKULL))
                        && r.output().getItem() == Items.NETHER_STAR
                        && r.euCost() == 250_000_000.0);
    assertTrue(foundNetherStar, "Wither Skull to Nether Star recipe must be present");
  }

  @Test
  @DisplayName("Quantum Generator constants match creative-tier output")
  void testQuantumGeneratorSpecs() {
    assertEquals(32_768.0, QuantumGeneratorBlockEntity.PRODUCTION);
    assertEquals(100_000_000.0, QuantumGeneratorBlockEntity.CAPACITY);
    assertEquals(5, QuantumGeneratorBlockEntity.TIER);
  }

  @Test
  @DisplayName("Molecular Transformer capacity supports Nether Star creation")
  void testMolecularTransformerSpecs() {
    assertEquals(250_000_000.0, MolecularTransformerBlockEntity.CAPACITY);
    assertEquals(32_768.0, MolecularTransformerBlockEntity.MAX_INPUT_EU_PER_TICK);
    assertEquals(5, MolecularTransformerBlockEntity.TIER);
  }

  @Test
  @DisplayName("JEI plugin definitions for Molecular Transformer are valid")
  void testJeiPluginSpecs() {
    assertNotNull(AdvancedSolarJeiPlugin.UID);
    assertEquals("advanced_solar_panels", AdvancedSolarJeiPlugin.UID.getNamespace());
    assertEquals("jei_plugin", AdvancedSolarJeiPlugin.UID.getPath());

    assertNotNull(MolecularTransformerCategory.RECIPE_TYPE);
    assertEquals(
        "advanced_solar_panels", MolecularTransformerCategory.RECIPE_TYPE.getUid().getNamespace());
    assertEquals(
        "molecular_transformer", MolecularTransformerCategory.RECIPE_TYPE.getUid().getPath());
  }
}
