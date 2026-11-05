package ic2.api.upgrade;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.world.item.ItemStack;

/**
 * Pure data and logic definition for IC2 Rebranded machine upgrades.
 *
 * <p>Can be used directly with {@link AddonUpgradeItem} or attached to any custom item that
 * implements {@link IFullUpgrade}.
 */
public final class UpgradeDefinition {
  private final Set<UpgradableProperty> suitableProperties;
  private final int extraProcessTime;
  private final double processTimeMultiplier;
  private final int extraEnergyDemand;
  private final double energyDemandMultiplier;
  private final int extraEnergyStorage;
  private final double energyStorageMultiplier;
  private final int extraTier;
  private final int augmentation;
  private final int rangeAmplification;
  private final boolean modifiesRedstone;

  private UpgradeDefinition(Builder builder) {
    this.suitableProperties =
        builder.suitableProperties.isEmpty()
            ? Collections.emptySet()
            : Collections.unmodifiableSet(EnumSet.copyOf(builder.suitableProperties));
    this.extraProcessTime = builder.extraProcessTime;
    this.processTimeMultiplier = builder.processTimeMultiplier;
    this.extraEnergyDemand = builder.extraEnergyDemand;
    this.energyDemandMultiplier = builder.energyDemandMultiplier;
    this.extraEnergyStorage = builder.extraEnergyStorage;
    this.energyStorageMultiplier = builder.energyStorageMultiplier;
    this.extraTier = builder.extraTier;
    this.augmentation = builder.augmentation;
    this.rangeAmplification = builder.rangeAmplification;
    this.modifiesRedstone = builder.modifiesRedstone;
  }

  public static Builder builder() {
    return new Builder();
  }

  /** Creates a preconfigured speed / overclocking upgrade definition. */
  public static UpgradeDefinition speed(
      double processTimeMultiplier, double energyDemandMultiplier) {
    return builder()
        .suitableFor(UpgradableProperty.Processing)
        .processTimeMultiplier(processTimeMultiplier)
        .energyDemandMultiplier(energyDemandMultiplier)
        .build();
  }

  /** Creates a preconfigured energy storage upgrade definition. */
  public static UpgradeDefinition energyStorage(int extraEnergyStorage) {
    return builder()
        .suitableFor(UpgradableProperty.EnergyStorage)
        .extraEnergyStorage(extraEnergyStorage)
        .build();
  }

  /** Creates a preconfigured transformer (tier boost) upgrade definition. */
  public static UpgradeDefinition transformer(int extraTier) {
    return builder().suitableFor(UpgradableProperty.Transformer).extraTier(extraTier).build();
  }

  public boolean isSuitableFor(ItemStack stack, Set<UpgradableProperty> properties) {
    if (this.suitableProperties.isEmpty()) {
      return true;
    }
    for (UpgradableProperty prop : this.suitableProperties) {
      if (properties.contains(prop)) {
        return true;
      }
    }
    return false;
  }

  public int getExtraProcessTime(ItemStack stack, IUpgradableBlock block) {
    return this.extraProcessTime;
  }

  public double getProcessTimeMultiplier(ItemStack stack, IUpgradableBlock block) {
    return this.processTimeMultiplier;
  }

  public int getExtraEnergyDemand(ItemStack stack, IUpgradableBlock block) {
    return this.extraEnergyDemand;
  }

  public double getEnergyDemandMultiplier(ItemStack stack, IUpgradableBlock block) {
    return this.energyDemandMultiplier;
  }

  public int getExtraEnergyStorage(ItemStack stack, IUpgradableBlock block) {
    return this.extraEnergyStorage;
  }

  public double getEnergyStorageMultiplier(ItemStack stack, IUpgradableBlock block) {
    return this.energyStorageMultiplier;
  }

  public int getExtraTier(ItemStack stack, IUpgradableBlock block) {
    return this.extraTier;
  }

  public int getAugmentation(ItemStack stack, IUpgradableBlock block) {
    return this.augmentation;
  }

  public int getRangeAmplification(ItemStack stack, IUpgradableBlock block, int range) {
    return range + this.rangeAmplification;
  }

  public boolean modifiesRedstoneInput(ItemStack stack, IUpgradableBlock block) {
    return this.modifiesRedstone;
  }

  public Set<UpgradableProperty> getSuitableProperties() {
    return this.suitableProperties;
  }

  public static class Builder {
    private final Set<UpgradableProperty> suitableProperties =
        EnumSet.noneOf(UpgradableProperty.class);
    private int extraProcessTime = 0;
    private double processTimeMultiplier = 1.0;
    private int extraEnergyDemand = 0;
    private double energyDemandMultiplier = 1.0;
    private int extraEnergyStorage = 0;
    private double energyStorageMultiplier = 1.0;
    private int extraTier = 0;
    private int augmentation = 0;
    private int rangeAmplification = 0;
    private boolean modifiesRedstone = false;

    public Builder suitableFor(UpgradableProperty... properties) {
      Collections.addAll(this.suitableProperties, properties);
      return this;
    }

    public Builder extraProcessTime(int ticks) {
      this.extraProcessTime = ticks;
      return this;
    }

    public Builder processTimeMultiplier(double multiplier) {
      if (!Double.isFinite(multiplier) || multiplier <= 0.0) {
        throw new IllegalArgumentException("processTimeMultiplier must be finite and positive");
      }
      this.processTimeMultiplier = multiplier;
      return this;
    }

    public Builder extraEnergyDemand(int extraEuPerTick) {
      this.extraEnergyDemand = extraEuPerTick;
      return this;
    }

    public Builder energyDemandMultiplier(double multiplier) {
      if (!Double.isFinite(multiplier) || multiplier < 0.0) {
        throw new IllegalArgumentException(
            "energyDemandMultiplier must be finite and non-negative");
      }
      this.energyDemandMultiplier = multiplier;
      return this;
    }

    public Builder extraEnergyStorage(int extraEu) {
      this.extraEnergyStorage = extraEu;
      return this;
    }

    public Builder energyStorageMultiplier(double multiplier) {
      if (!Double.isFinite(multiplier) || multiplier < 0.0) {
        throw new IllegalArgumentException(
            "energyStorageMultiplier must be finite and non-negative");
      }
      this.energyStorageMultiplier = multiplier;
      return this;
    }

    public Builder extraTier(int extraTier) {
      this.extraTier = extraTier;
      return this;
    }

    public Builder augmentation(int augmentation) {
      this.augmentation = augmentation;
      return this;
    }

    public Builder rangeAmplification(int rangeAmplification) {
      this.rangeAmplification = rangeAmplification;
      return this;
    }

    public Builder modifiesRedstone(boolean modifies) {
      this.modifiesRedstone = modifies;
      return this;
    }

    public UpgradeDefinition build() {
      return new UpgradeDefinition(this);
    }
  }
}
