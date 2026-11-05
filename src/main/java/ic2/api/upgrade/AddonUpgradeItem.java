package ic2.api.upgrade;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Highly configurable machine upgrade base class for IC2R add-ons.
 *
 * <p>Supports common upgrade types (overclocking/speed, energy storage, transformer tier boost, and
 * custom modifiers) using a fluent builder and {@link UpgradeDefinition}.
 */
public class AddonUpgradeItem extends Item implements IFullUpgrade {
  private final UpgradeDefinition definition;

  public AddonUpgradeItem(Item.Properties properties, UpgradeDefinition definition) {
    super(properties);
    this.definition = Objects.requireNonNull(definition, "definition");
  }

  protected AddonUpgradeItem(Builder builder) {
    super(builder.properties);
    this.definition = builder.definitionBuilder.build();
  }

  public UpgradeDefinition getDefinition() {
    return this.definition;
  }

  public static Builder builder(Item.Properties properties) {
    return new Builder(properties);
  }

  public static AddonUpgradeItem createSpeedUpgrade(
      Item.Properties properties, double processTimeMultiplier, double energyDemandMultiplier) {
    return new AddonUpgradeItem(
        properties, UpgradeDefinition.speed(processTimeMultiplier, energyDemandMultiplier));
  }

  public static AddonUpgradeItem createEnergyStorageUpgrade(
      Item.Properties properties, int extraEnergyStorage) {
    return new AddonUpgradeItem(properties, UpgradeDefinition.energyStorage(extraEnergyStorage));
  }

  public static AddonUpgradeItem createTransformerUpgrade(
      Item.Properties properties, int extraTier) {
    return new AddonUpgradeItem(properties, UpgradeDefinition.transformer(extraTier));
  }

  @Override
  public boolean isSuitableFor(ItemStack stack, Set<UpgradableProperty> properties) {
    return this.definition.isSuitableFor(stack, properties);
  }

  @Override
  public boolean onTick(ItemStack stack, IUpgradableBlock block) {
    return false;
  }

  @Override
  public Collection<ItemStack> onProcessEnd(
      ItemStack stack, IUpgradableBlock block, Collection<ItemStack> outputs) {
    return outputs;
  }

  @Override
  public int getExtraProcessTime(ItemStack stack, IUpgradableBlock block) {
    return this.definition.getExtraProcessTime(stack, block);
  }

  @Override
  public double getProcessTimeMultiplier(ItemStack stack, IUpgradableBlock block) {
    return this.definition.getProcessTimeMultiplier(stack, block);
  }

  @Override
  public int getExtraEnergyDemand(ItemStack stack, IUpgradableBlock block) {
    return this.definition.getExtraEnergyDemand(stack, block);
  }

  @Override
  public double getEnergyDemandMultiplier(ItemStack stack, IUpgradableBlock block) {
    return this.definition.getEnergyDemandMultiplier(stack, block);
  }

  @Override
  public int getExtraEnergyStorage(ItemStack stack, IUpgradableBlock block) {
    return this.definition.getExtraEnergyStorage(stack, block);
  }

  @Override
  public double getEnergyStorageMultiplier(ItemStack stack, IUpgradableBlock block) {
    return this.definition.getEnergyStorageMultiplier(stack, block);
  }

  @Override
  public int getExtraTier(ItemStack stack, IUpgradableBlock block) {
    return this.definition.getExtraTier(stack, block);
  }

  @Override
  public int getAugmentation(ItemStack stack, IUpgradableBlock block) {
    return this.definition.getAugmentation(stack, block);
  }

  @Override
  public int getRangeAmplification(ItemStack stack, IUpgradableBlock block, int range) {
    return this.definition.getRangeAmplification(stack, block, range);
  }

  @Override
  public boolean modifiesRedstoneInput(ItemStack stack, IUpgradableBlock block) {
    return this.definition.modifiesRedstoneInput(stack, block);
  }

  @Override
  public int getRedstoneInput(ItemStack stack, IUpgradableBlock block, int input) {
    return input;
  }

  public Set<UpgradableProperty> getSuitableProperties() {
    return this.definition.getSuitableProperties();
  }

  public static class Builder {
    private final Item.Properties properties;
    private final UpgradeDefinition.Builder definitionBuilder = UpgradeDefinition.builder();

    public Builder(Item.Properties properties) {
      this.properties = Objects.requireNonNull(properties, "properties");
    }

    public Builder suitableFor(UpgradableProperty... properties) {
      this.definitionBuilder.suitableFor(properties);
      return this;
    }

    public Builder extraProcessTime(int ticks) {
      this.definitionBuilder.extraProcessTime(ticks);
      return this;
    }

    public Builder processTimeMultiplier(double multiplier) {
      this.definitionBuilder.processTimeMultiplier(multiplier);
      return this;
    }

    public Builder extraEnergyDemand(int extraEuPerTick) {
      this.definitionBuilder.extraEnergyDemand(extraEuPerTick);
      return this;
    }

    public Builder energyDemandMultiplier(double multiplier) {
      this.definitionBuilder.energyDemandMultiplier(multiplier);
      return this;
    }

    public Builder extraEnergyStorage(int extraEu) {
      this.definitionBuilder.extraEnergyStorage(extraEu);
      return this;
    }

    public Builder energyStorageMultiplier(double multiplier) {
      this.definitionBuilder.energyStorageMultiplier(multiplier);
      return this;
    }

    public Builder extraTier(int extraTier) {
      this.definitionBuilder.extraTier(extraTier);
      return this;
    }

    public Builder augmentation(int augmentation) {
      this.definitionBuilder.augmentation(augmentation);
      return this;
    }

    public Builder rangeAmplification(int rangeAmplification) {
      this.definitionBuilder.rangeAmplification(rangeAmplification);
      return this;
    }

    public Builder modifiesRedstone(boolean modifies) {
      this.definitionBuilder.modifiesRedstone(modifies);
      return this;
    }

    public AddonUpgradeItem build() {
      return new AddonUpgradeItem(this);
    }
  }
}
