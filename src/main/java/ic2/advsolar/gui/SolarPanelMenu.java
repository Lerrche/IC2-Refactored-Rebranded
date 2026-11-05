package ic2.advsolar.gui;

import ic2.advsolar.AdvancedSolarPanelsMod;
import ic2.api.item.ElectricItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SolarPanelMenu extends AbstractContainerMenu {
  public static final int CHARGE_SLOT_COUNT = 4;
  public static final int DATA_COUNT = 8;

  private final Container container;
  private final ContainerData data;

  // Client constructor
  public SolarPanelMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
    this(
        containerId,
        playerInventory,
        new SimpleContainer(CHARGE_SLOT_COUNT),
        new SimpleContainerData(DATA_COUNT));
  }

  // Server & full constructor
  public SolarPanelMenu(
      int containerId, Inventory playerInventory, Container container, ContainerData data) {
    super(AdvancedSolarPanelsMod.SOLAR_PANEL_MENU.get(), containerId);
    checkContainerSize(container, CHARGE_SLOT_COUNT);
    checkContainerDataCount(data, DATA_COUNT);
    this.container = container;
    this.data = data;

    // 4 Charge slots
    for (int i = 0; i < CHARGE_SLOT_COUNT; i++) {
      this.addSlot(
          new Slot(container, i, 17 + i * 18, 59) {
            @Override
            public boolean mayPlace(ItemStack stack) {
              return ElectricItem.manager != null
                  && ElectricItem.manager.charge(stack, 1.0, Integer.MAX_VALUE, true, true) > 0;
            }
          });
    }

    // Player inventory (3x9)
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 9; col++) {
        this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 17 + col * 18, 86 + row * 18));
      }
    }

    // Player hotbar (1x9)
    for (int col = 0; col < 9; col++) {
      this.addSlot(new Slot(playerInventory, col, 17 + col * 18, 144));
    }

    this.addDataSlots(data);
  }

  public int getStoredEu() {
    int low = this.data.get(0) & 0xFFFF;
    int high = this.data.get(1) & 0xFFFF;
    return (high << 16) | low;
  }

  public int getMaxStorageEu() {
    int low = this.data.get(2) & 0xFFFF;
    int high = this.data.get(3) & 0xFFFF;
    return (high << 16) | low;
  }

  public boolean isSunVisible() {
    return this.data.get(4) != 0;
  }

  public int getDayProduction() {
    return this.data.get(5);
  }

  public int getNightProduction() {
    return this.data.get(6);
  }

  public int getOutputEuPerTick() {
    return this.data.get(7);
  }

  @Override
  public boolean stillValid(Player player) {
    return this.container.stillValid(player);
  }

  @Override
  public ItemStack quickMoveStack(Player player, int index) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.slots.get(index);
    if (slot != null && slot.hasItem()) {
      ItemStack stackInSlot = slot.getItem();
      itemstack = stackInSlot.copy();

      if (index < CHARGE_SLOT_COUNT) {
        // From charge slots to player inventory
        if (!this.moveItemStackTo(stackInSlot, CHARGE_SLOT_COUNT, this.slots.size(), true)) {
          return ItemStack.EMPTY;
        }
      } else {
        // From player inventory to charge slots if electric
        if (ElectricItem.manager != null
            && ElectricItem.manager.charge(stackInSlot, 1.0, Integer.MAX_VALUE, true, true) > 0) {
          if (!this.moveItemStackTo(stackInSlot, 0, CHARGE_SLOT_COUNT, false)) {
            return ItemStack.EMPTY;
          }
        } else if (index < CHARGE_SLOT_COUNT + 27) {
          if (!this.moveItemStackTo(
              stackInSlot, CHARGE_SLOT_COUNT + 27, this.slots.size(), false)) {
            return ItemStack.EMPTY;
          }
        } else if (!this.moveItemStackTo(
            stackInSlot, CHARGE_SLOT_COUNT, CHARGE_SLOT_COUNT + 27, false)) {
          return ItemStack.EMPTY;
        }
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
