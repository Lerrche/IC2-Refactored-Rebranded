package ic2.advsolar.gui;

import ic2.advsolar.AdvancedSolarPanelsMod;
import ic2.advsolar.blocks.entity.QuantumGeneratorBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class QuantumGeneratorMenu extends AbstractContainerMenu {
  public static final int DATA_COUNT = 4;

  private final ContainerData data;
  private final QuantumGeneratorBlockEntity blockEntity;

  // Client constructor
  public QuantumGeneratorMenu(
      int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
    this(containerId, playerInventory, null, new SimpleContainerData(DATA_COUNT));
  }

  // Server & full constructor
  public QuantumGeneratorMenu(
      int containerId,
      Inventory playerInventory,
      QuantumGeneratorBlockEntity be,
      ContainerData data) {
    super(AdvancedSolarPanelsMod.QUANTUM_GENERATOR_MENU.get(), containerId);
    checkContainerDataCount(data, DATA_COUNT);
    this.blockEntity = be;
    this.data = data;

    // Player inventory (3x9)
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 9; col++) {
        this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 110 + row * 18));
      }
    }

    // Player hotbar (1x9)
    for (int col = 0; col < 9; col++) {
      this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 168));
    }

    this.addDataSlots(data);
  }

  public int getProduction() {
    int low = this.data.get(0) & 0xFFFF;
    int high = this.data.get(1) & 0xFFFF;
    return (high << 16) | low;
  }

  public int getTier() {
    return this.data.get(2);
  }

  public boolean isActive() {
    return this.data.get(3) != 0;
  }

  @Override
  public boolean clickMenuButton(Player player, int id) {
    if (this.blockEntity == null) return false;
    switch (id) {
      // Normal production buttons
      case 0 -> this.blockEntity.adjustProduction(-100);
      case 1 -> this.blockEntity.adjustProduction(-10);
      case 2 -> this.blockEntity.adjustProduction(-1);
      case 3 -> this.blockEntity.adjustProduction(1);
      case 4 -> this.blockEntity.adjustProduction(10);
      case 5 -> this.blockEntity.adjustProduction(100);

      // Shift production buttons
      case 10 -> this.blockEntity.adjustProduction(-500);
      case 11 -> this.blockEntity.adjustProduction(-50);
      case 12 -> this.blockEntity.adjustProduction(-5);
      case 13 -> this.blockEntity.adjustProduction(5);
      case 14 -> this.blockEntity.adjustProduction(50);
      case 15 -> this.blockEntity.adjustProduction(500);

      // Tier buttons
      case 20 -> this.blockEntity.setGeneratorTier(1);
      case 21 -> this.blockEntity.setGeneratorTier(2);
      case 22 -> this.blockEntity.setGeneratorTier(3);
      case 23 -> this.blockEntity.setGeneratorTier(4);
      case 24 -> this.blockEntity.setGeneratorTier(5);
      case 25 -> this.blockEntity.setGeneratorTier(5); // Max tier
      default -> {
        return false;
      }
    }
    this.blockEntity.setChanged();
    return true;
  }

  @Override
  public boolean stillValid(Player player) {
    return this.blockEntity == null || !this.blockEntity.isRemoved();
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.slots.get(index);
    if (slot != null && slot.hasItem()) {
      ItemStack stackInSlot = slot.getItem();
      itemstack = stackInSlot.copy();
      if (index < 27) {
        if (!this.moveItemStackTo(stackInSlot, 27, this.slots.size(), false)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.moveItemStackTo(stackInSlot, 0, 27, false)) {
        return ItemStack.EMPTY;
      }
      if (stackInSlot.isEmpty()) {
        slot.setByPlayer(ItemStack.EMPTY);
      } else {
        slot.setChanged();
      }
    }
    return itemstack;
  }
}
