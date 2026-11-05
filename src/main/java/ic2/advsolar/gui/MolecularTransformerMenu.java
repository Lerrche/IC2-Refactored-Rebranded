package ic2.advsolar.gui;

import ic2.advsolar.AdvancedSolarPanelsMod;
import ic2.advsolar.blocks.entity.MolecularTransformerBlockEntity;
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

public class MolecularTransformerMenu extends AbstractContainerMenu {
  public static final int MACHINE_SLOT_COUNT = 2;
  public static final int DATA_COUNT = 6;

  private final Container container;
  private final ContainerData data;

  // Client constructor
  public MolecularTransformerMenu(
      int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
    this(
        containerId,
        playerInventory,
        new SimpleContainer(MACHINE_SLOT_COUNT),
        new SimpleContainerData(DATA_COUNT));
  }

  // Server & full constructor
  public MolecularTransformerMenu(
      int containerId, Inventory playerInventory, Container container, ContainerData data) {
    super(AdvancedSolarPanelsMod.MOLECULAR_TRANSFORMER_MENU.get(), containerId);
    checkContainerSize(container, MACHINE_SLOT_COUNT);
    checkContainerDataCount(data, DATA_COUNT);
    this.container = container;
    this.data = data;

    // Slot 0: Input slot (x=20, y=27 per molecular_transformer.xml)
    this.addSlot(
        new Slot(container, 0, 20, 27) {
          @Override
          public boolean mayPlace(ItemStack stack) {
            return MolecularTransformerBlockEntity.hasRecipeFor(stack);
          }
        });

    // Slot 1: Output slot (x=20, y=68 per molecular_transformer.xml)
    this.addSlot(
        new Slot(container, 1, 20, 68) {
          @Override
          public boolean mayPlace(ItemStack stack) {
            return false;
          }
        });

    // Player inventory (3x9): x=18, y=98, spacing=5 (21px step) per molecular_transformer.xml
    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 9; col++) {
        this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 18 + col * 21, 98 + row * 21));
      }
    }

    // Player hotbar (1x9): x=18, y=165 (hotbaroffset=67 => 98+67=165), 21px step
    for (int col = 0; col < 9; col++) {
      this.addSlot(new Slot(playerInventory, col, 18 + col * 21, 165));
    }

    this.addDataSlots(data);
  }

  public long getProgressEu() {
    int low = this.data.get(0) & 0xFFFF;
    int mid = this.data.get(1) & 0xFFFF;
    int high = this.data.get(2) & 0xFFFF;
    return ((long) high << 32) | ((long) mid << 16) | low;
  }

  public long getRequiredEu() {
    int low = this.data.get(3) & 0xFFFF;
    int mid = this.data.get(4) & 0xFFFF;
    int high = this.data.get(5) & 0xFFFF;
    return ((long) high << 32) | ((long) mid << 16) | low;
  }

  public ItemStack getInputItem() {
    return this.slots.get(0).getItem();
  }

  public ItemStack getOutputItem() {
    return this.slots.get(1).getItem();
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

      if (index < MACHINE_SLOT_COUNT) {
        // From machine to player inventory
        if (!this.moveItemStackTo(stackInSlot, MACHINE_SLOT_COUNT, this.slots.size(), true)) {
          return ItemStack.EMPTY;
        }
      } else {
        // From player inventory to input slot if valid recipe
        if (MolecularTransformerBlockEntity.hasRecipeFor(stackInSlot)) {
          if (!this.moveItemStackTo(stackInSlot, 0, 1, false)) {
            return ItemStack.EMPTY;
          }
        } else if (index < MACHINE_SLOT_COUNT + 27) {
          if (!this.moveItemStackTo(
              stackInSlot, MACHINE_SLOT_COUNT + 27, this.slots.size(), false)) {
            return ItemStack.EMPTY;
          }
        } else if (!this.moveItemStackTo(
            stackInSlot, MACHINE_SLOT_COUNT, MACHINE_SLOT_COUNT + 27, false)) {
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
