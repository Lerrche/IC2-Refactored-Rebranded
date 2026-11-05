package ic2.advsolar.blocks.entity;

import ic2.advsolar.gui.QuantumGeneratorMenu;
import ic2.api.block.AddonEnergyBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Quantum Generator block entity.
 *
 * <p>Creative-tier infinite energy generator offering configurable output and voltage tiers.
 */
public class QuantumGeneratorBlockEntity extends AddonEnergyBlockEntity implements MenuProvider {
  public static final double DEFAULT_PRODUCTION = 32_768.0;
  public static final double PRODUCTION = DEFAULT_PRODUCTION;
  public static final double CAPACITY = 100_000_000.0;
  public static final int DEFAULT_TIER = 5;
  public static final int TIER = DEFAULT_TIER;

  private int production = (int) DEFAULT_PRODUCTION;
  private int currentTier = DEFAULT_TIER;

  protected final ContainerData dataAccess =
      new ContainerData() {
        @Override
        public int get(int index) {
          return switch (index) {
            case 0 -> production & 0xFFFF;
            case 1 -> (production >> 16) & 0xFFFF;
            case 2 -> currentTier;
            case 3 -> 1; // Always active
            default -> 0;
          };
        }

        @Override
        public void set(int index, int value) {}

        @Override
        public int getCount() {
          return QuantumGeneratorMenu.DATA_COUNT;
        }
      };

  public QuantumGeneratorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state, DEFAULT_TIER, CAPACITY, 0, DEFAULT_PRODUCTION);
  }

  public void adjustProduction(int delta) {
    long newProd = (long) this.production + delta;
    if (newProd < 0) newProd = 0;
    if (newProd > 1_000_000_000) newProd = 1_000_000_000;
    this.production = (int) newProd;
  }

  public void setGeneratorTier(int tier) {
    if (tier >= 1 && tier <= 5) {
      this.currentTier = tier;
    }
  }

  public int getProduction() {
    return this.production;
  }

  public int getCurrentTier() {
    return this.currentTier;
  }

  @Override
  public int getSourceTier() {
    return this.currentTier;
  }

  @Override
  public double getOfferedEnergy() {
    return Math.min(getStoredEu(), this.production);
  }

  @Override
  public void serverTick() {
    if (this.level == null || this.level.isClientSide) return;
    if (getStoredEu() < getCapacityEu() && this.production > 0) {
      addEu(Math.min(this.production, getCapacityEu() - getStoredEu()));
      setChanged();
    }
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
  }

  @Override
  public AbstractContainerMenu createMenu(
      int containerId, Inventory playerInventory, Player player) {
    return new QuantumGeneratorMenu(containerId, playerInventory, this, this.dataAccess);
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    if (tag.contains("production")) {
      this.production = tag.getInt("production");
    }
    if (tag.contains("currentTier")) {
      this.currentTier = Math.max(1, Math.min(5, tag.getInt("currentTier")));
    }
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    tag.putInt("production", this.production);
    tag.putInt("currentTier", this.currentTier);
  }
}
