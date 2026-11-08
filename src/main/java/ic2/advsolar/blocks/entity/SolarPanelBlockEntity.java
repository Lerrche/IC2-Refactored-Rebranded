package ic2.advsolar.blocks.entity;

import ic2.advsolar.gui.SolarPanelMenu;
import ic2.api.block.AddonEnergyBlockEntity;
import ic2.api.item.ElectricItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * High-performance solar panel block entity for Advanced Solar Panels.
 *
 * <p>Produces day power during clear daytime and night power during night or overcast weather as
 * long as sky is unobstructed. Also charges items placed in its internal charging slots.
 */
public class SolarPanelBlockEntity extends AddonEnergyBlockEntity
    implements MenuProvider, Container {
  private final double dayProduction;
  private final double nightProduction;
  private boolean sunIsVisible;
  private final NonNullList<ItemStack> items =
      NonNullList.withSize(SolarPanelMenu.CHARGE_SLOT_COUNT, ItemStack.EMPTY);

  protected final ContainerData dataAccess =
      new ContainerData() {
        @Override
        public int get(int index) {
          int stored = (int) Math.min(Integer.MAX_VALUE, Math.round(getStoredEu()));
          int max = (int) Math.min(Integer.MAX_VALUE, Math.round(getCapacityEu()));
          return switch (index) {
            case 0 -> stored & 0xFFFF;
            case 1 -> (stored >> 16) & 0xFFFF;
            case 2 -> max & 0xFFFF;
            case 3 -> (max >> 16) & 0xFFFF;
            case 4 -> sunIsVisible ? 1 : 0;
            case 5 -> (int) dayProduction;
            case 6 -> (int) nightProduction;
            case 7 -> (int) getOutputLimitEuPerTick();
            default -> 0;
          };
        }

        @Override
        public void set(int index, int value) {}

        @Override
        public int getCount() {
          return SolarPanelMenu.DATA_COUNT;
        }
      };

  public SolarPanelBlockEntity(
      BlockEntityType<?> type,
      BlockPos pos,
      BlockState state,
      double dayProduction,
      double nightProduction,
      double capacityEu,
      int tier,
      double outputEuPerTick) {
    super(type, pos, state, tier, capacityEu, 0, outputEuPerTick);
    this.dayProduction = dayProduction;
    this.nightProduction = nightProduction;
  }

  @Override
  public void serverTick() {
    if (this.level == null || this.level.isClientSide) return;

    boolean canSeeSky = this.level.canSeeSky(this.worldPosition.above());
    this.sunIsVisible =
        canSeeSky && this.level.isDay() && !this.level.isRaining() && !this.level.isThundering();

    if (canSeeSky && this.getCapacityEu() > this.getStoredEu()) {
      double mult =
          this.sunIsVisible
              ? ic2.industrialjs.IndustrialJS.getSolarDayMultiplier()
              : ic2.industrialjs.IndustrialJS.getSolarNightMultiplier();
      double generated = (this.sunIsVisible ? this.dayProduction : this.nightProduction) * mult;
      if (generated > 0) {
        addEu(Math.min(generated, this.getCapacityEu() - this.getStoredEu()));
        setChanged();
      }
    }

    // Charge items inside the 4 charging slots using stored EU
    if (this.getStoredEu() > 0 && ElectricItem.manager != null) {
      for (int i = 0; i < this.items.size(); i++) {
        ItemStack stack = this.items.get(i);
        if (!stack.isEmpty() && this.getStoredEu() > 0) {
          double maxTransfer = Math.min(this.getStoredEu(), this.getOutputLimitEuPerTick());
          double accepted =
              ElectricItem.manager.charge(stack, maxTransfer, this.getSourceTier(), false, false);
          if (accepted > 0) {
            this.removeEu(accepted);
            setChanged();
          }
        }
      }
    }
  }

  public boolean isSunIsVisible() {
    return this.sunIsVisible;
  }

  public double getDayProduction() {
    return this.dayProduction;
  }

  public double getNightProduction() {
    return this.nightProduction;
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
  }

  @Override
  public AbstractContainerMenu createMenu(
      int containerId, Inventory playerInventory, Player player) {
    return new SolarPanelMenu(containerId, playerInventory, this, this.dataAccess);
  }

  // Container implementation
  @Override
  public int getContainerSize() {
    return this.items.size();
  }

  @Override
  public boolean isEmpty() {
    for (ItemStack item : this.items) {
      if (!item.isEmpty()) return false;
    }
    return true;
  }

  @Override
  public ItemStack getItem(int slot) {
    return this.items.get(slot);
  }

  @Override
  public ItemStack removeItem(int slot, int amount) {
    ItemStack result = ContainerHelper.removeItem(this.items, slot, amount);
    if (!result.isEmpty()) setChanged();
    return result;
  }

  @Override
  public ItemStack removeItemNoUpdate(int slot) {
    return ContainerHelper.takeItem(this.items, slot);
  }

  @Override
  public void setItem(int slot, ItemStack stack) {
    this.items.set(slot, stack);
    setChanged();
  }

  @Override
  public boolean stillValid(Player player) {
    return Container.stillValidBlockEntity(this, player);
  }

  @Override
  public void clearContent() {
    this.items.clear();
    setChanged();
  }

  @Override
  protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    this.sunIsVisible = tag.getBoolean("sunIsVisible");
    this.items.clear();
    ContainerHelper.loadAllItems(tag, this.items, registries);
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    tag.putBoolean("sunIsVisible", this.sunIsVisible);
    ContainerHelper.saveAllItems(tag, this.items, registries);
  }
}
