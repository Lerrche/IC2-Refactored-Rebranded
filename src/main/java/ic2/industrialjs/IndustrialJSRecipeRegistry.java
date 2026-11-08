package ic2.industrialjs;

import ic2.advsolar.blocks.entity.MolecularTransformerBlockEntity;
import ic2.api.recipe.ICannerBottleRecipeManager;
import ic2.api.recipe.ICannerEnrichRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.Recipes;
import ic2.core.IC2;
import ic2.core.block.machine.CannerBottleRecipeManager;
import ic2.core.block.machine.CannerEnrichRecipeManager;
import ic2.core.fluid.Ic2FluidStack;
import ic2.core.recipe.BasicMachineRecipeManager;
import ic2.core.recipe.MatterAmplifierRecipeManager;
import ic2.core.util.LogCategory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;

public class IndustrialJSRecipeRegistry {

  public record QueuedMachineRecipe(Object input, Object output, CompoundTag metadata) {}

  public record QueuedMolecularRecipe(Object input, Object output, double euCost) {}

  public record QueuedScrapboxDrop(Object item, float weight) {}

  public record QueuedAmplifier(Object item, int amplification) {}

  public record QueuedCannerEnrich(Object outputFluid, Object inputFluid, Object additive) {}

  public record QueuedCannerBottle(Object output, Object container, Object fill) {}

  private static final Map<RecipeType<?>, List<QueuedMachineRecipe>> QUEUED_RECIPES =
      new HashMap<>();
  private static final List<QueuedMolecularRecipe> QUEUED_MOLECULAR = new ArrayList<>();
  private static final List<QueuedScrapboxDrop> QUEUED_SCRAPBOX = new ArrayList<>();
  private static final List<QueuedAmplifier> QUEUED_AMPLIFIERS = new ArrayList<>();
  private static final List<QueuedCannerEnrich> QUEUED_CANNER_ENRICH = new ArrayList<>();
  private static final List<QueuedCannerBottle> QUEUED_CANNER_BOTTLE = new ArrayList<>();

  public static synchronized void queueRecipe(
      RecipeType<?> type, Object output, Object input, CompoundTag metadata) {
    QUEUED_RECIPES
        .computeIfAbsent(type, k -> new ArrayList<>())
        .add(new QueuedMachineRecipe(input, output, metadata));
  }

  public static synchronized void queueMolecularTransformer(
      Object output, Object input, double euCost) {
    QUEUED_MOLECULAR.add(new QueuedMolecularRecipe(input, output, euCost));
  }

  public static synchronized void queueScrapbox(Object item, float weight) {
    QUEUED_SCRAPBOX.add(new QueuedScrapboxDrop(item, weight));
  }

  public static synchronized void queueAmplifier(Object item, int amplification) {
    QUEUED_AMPLIFIERS.add(new QueuedAmplifier(item, amplification));
  }

  public static synchronized void queueCannerEnrich(
      Object outputFluid, Object inputFluid, Object additive) {
    QUEUED_CANNER_ENRICH.add(new QueuedCannerEnrich(outputFluid, inputFluid, additive));
  }

  public static synchronized void queueCannerBottle(
      Object output, Object container, Object fill) {
    QUEUED_CANNER_BOTTLE.add(new QueuedCannerBottle(output, container, fill));
  }

  public static synchronized void clear() {
    QUEUED_RECIPES.clear();
    QUEUED_MOLECULAR.clear();
    QUEUED_SCRAPBOX.clear();
    QUEUED_AMPLIFIERS.clear();
    QUEUED_CANNER_ENRICH.clear();
    QUEUED_CANNER_BOTTLE.clear();
  }

  public static synchronized void applyTo(
      RecipeType<?> recipeType, BasicMachineRecipeManager manager) {
    List<QueuedMachineRecipe> list = QUEUED_RECIPES.get(recipeType);
    if (list == null || list.isEmpty()) {
      return;
    }

    int added = 0;
    for (QueuedMachineRecipe q : list) {
      IRecipeInput input = ItemParser.parseInput(q.input());
      if (input == null) {
        IC2.log.warn(
            LogCategory.General,
            "[IndustrialJS] Could not resolve input for recipe: %s",
            q.input());
        continue;
      }

      Collection<ItemStack> outputs = ItemParser.parseOutputs(q.output());
      if (outputs.isEmpty()) {
        IC2.log.warn(
            LogCategory.General,
            "[IndustrialJS] Could not resolve output for recipe: %s",
            q.output());
        continue;
      }

      manager.addRecipe(input, outputs, q.metadata(), false);
      added++;
    }

    if (added > 0) {
      IC2.log.info(
          LogCategory.General,
          "[IndustrialJS] Injected %d custom recipes for %s",
          added,
          recipeType);
    }
  }

  public static synchronized void applyMolecularTransformer() {
    if (QUEUED_MOLECULAR.isEmpty()) {
      return;
    }

    int added = 0;
    for (QueuedMolecularRecipe q : QUEUED_MOLECULAR) {
      Ingredient ingredient = ItemParser.parseIngredient(q.input());
      if (ingredient.isEmpty()) {
        IC2.log.warn(
            LogCategory.General,
            "[IndustrialJS] Could not resolve input for Molecular Transformer: %s",
            q.input());
        continue;
      }

      ItemStack output = ItemParser.parseOutput(q.output());
      if (output.isEmpty()) {
        IC2.log.warn(
            LogCategory.General,
            "[IndustrialJS] Could not resolve output for Molecular Transformer: %s",
            q.output());
        continue;
      }

      MolecularTransformerBlockEntity.addRecipe(
          new MolecularTransformerBlockEntity.TransmutationRecipe(ingredient, output, q.euCost()));
      added++;
    }

    if (added > 0) {
      IC2.log.info(
          LogCategory.General,
          "[IndustrialJS] Injected %d custom recipes for Molecular Transformer",
          added);
    }
  }

  public static synchronized void applyScrapbox() {
    if (QUEUED_SCRAPBOX.isEmpty() || Recipes.scrapboxDrops == null) {
      return;
    }

    for (QueuedScrapboxDrop q : QUEUED_SCRAPBOX) {
      ItemStack stack = ItemParser.parseOutput(q.item());
      if (!stack.isEmpty()) {
        Recipes.scrapboxDrops.addDrop(stack, q.weight());
      }
    }
  }

  public static synchronized void applyAmplifiers(MatterAmplifierRecipeManager manager) {
    if (QUEUED_AMPLIFIERS.isEmpty() || manager == null) {
      return;
    }

    int added = 0;
    for (QueuedAmplifier q : QUEUED_AMPLIFIERS) {
      IRecipeInput input = ItemParser.parseInput(q.item());
      if (input != null && q.amplification() > 0) {
        manager.addRecipe(input, q.amplification(), null, true);
        added++;
      }
    }

    if (added > 0) {
      IC2.log.info(
          LogCategory.General,
          "[IndustrialJS] Injected %d custom amplifiers for Matter Fabricator",
          added);
    }
  }

  public static synchronized void applyCannerEnrich(CannerEnrichRecipeManager manager) {
    if (QUEUED_CANNER_ENRICH.isEmpty() || manager == null) {
      return;
    }

    int added = 0;
    for (QueuedCannerEnrich q : QUEUED_CANNER_ENRICH) {
      Ic2FluidStack out = ItemParser.parseFluid(q.outputFluid());
      Ic2FluidStack in = ItemParser.parseFluid(q.inputFluid());
      IRecipeInput additive = ItemParser.parseInput(q.additive());
      if (out != null && in != null && additive != null) {
        manager.addRecipe(new ICannerEnrichRecipeManager.Input(in, additive), out, null, true);
        added++;
      } else {
        IC2.log.warn(
            LogCategory.General,
            "[IndustrialJS] Could not resolve canner enrich recipe: %s + %s -> %s",
            q.inputFluid(),
            q.additive(),
            q.outputFluid());
      }
    }

    if (added > 0) {
      IC2.log.info(
          LogCategory.General,
          "[IndustrialJS] Injected %d custom recipes for Canner Enrich",
          added);
    }
  }

  public static synchronized void applyCannerBottle(CannerBottleRecipeManager manager) {
    if (QUEUED_CANNER_BOTTLE.isEmpty() || manager == null) {
      return;
    }

    int added = 0;
    for (QueuedCannerBottle q : QUEUED_CANNER_BOTTLE) {
      ItemStack out = ItemParser.parseOutput(q.output());
      IRecipeInput container = ItemParser.parseInput(q.container());
      IRecipeInput fill = ItemParser.parseInput(q.fill());
      if (!out.isEmpty() && container != null && fill != null) {
        manager.addRecipe(new ICannerBottleRecipeManager.Input(container, fill), out, null, true);
        added++;
      } else {
        IC2.log.warn(
            LogCategory.General,
            "[IndustrialJS] Could not resolve canner bottle recipe: %s + %s -> %s",
            q.container(),
            q.fill(),
            q.output());
      }
    }

    if (added > 0) {
      IC2.log.info(
          LogCategory.General,
          "[IndustrialJS] Injected %d custom recipes for Canner Bottle",
          added);
    }
  }
}
