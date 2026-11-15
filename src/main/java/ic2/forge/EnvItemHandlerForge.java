package ic2.forge;

import com.mojang.authlib.GameProfile;
import ic2.core.block.personal.IPersonalBlock;
import ic2.core.block.personal.TileEntityPersonalChest;
import ic2.core.block.tileentity.TileEntityInventory;
import ic2.core.item.EnvItemHandler;
import ic2.core.util.DelegatingInventory;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class EnvItemHandlerForge implements EnvItemHandler {
  @Nullable private static Container getStorage(
      BlockEntity target,
      Direction side,
      GameProfile profile,
      @Nullable Set<Container> personalInventories) {
    if (target instanceof TileEntityPersonalChest personalChest) {
      if (profile != null && !personalChest.permitsAccess(profile)) {
        return null;
      }
      Container privilegedInventory =
          new DelegatingInventory(personalChest) {
            @Override
            public int getContainerSize() {
              return personalChest.contentSlot.size();
            }

            @Override
            public ItemStack getItem(int index) {
              return personalChest.contentSlot.get(index);
            }

            @Override
            public ItemStack removeItem(int index, int amount) {
              ItemStack stack = this.getItem(index);
              if (StackUtil.isEmpty(stack)) {
                return StackUtil.emptyStack;
              }

              if (amount >= StackUtil.getSize(stack)) {
                this.setItem(index, StackUtil.emptyStack);
                return stack;
              }

              if (amount != 0) {
                if (amount < 0) {
                  int space =
                      Math.min(
                              personalChest.contentSlot.getStackSizeLimit(),
                              stack.getMaxStackSize())
                          - StackUtil.getSize(stack);
                  amount = Math.max(amount, -space);
                }

                stack = StackUtil.decSize(stack, amount);
                this.setItem(index, stack);
              }

              return StackUtil.copyWithSize(stack, amount);
            }

            @Override
            public ItemStack removeItemNoUpdate(int index) {
              ItemStack ret = this.getItem(index);
              if (!StackUtil.isEmpty(ret)) {
                this.setItem(index, StackUtil.emptyStack);
              }

              return ret;
            }

            @Override
            public void setItem(int index, ItemStack stack) {
              personalChest.contentSlot.put(index, stack);
              personalChest.setChanged();
            }

            @Override
            public int getMaxStackSize() {
              return personalChest.contentSlot.getStackSizeLimit();
            }

            @Override
            public boolean canPlaceItem(int index, ItemStack stack) {
              return personalChest.contentSlot.accepts(stack);
            }
          };

      if (personalInventories != null) {
        personalInventories.add(privilegedInventory);
      }

      return privilegedInventory;
    } else if (target instanceof IPersonalBlock personalTarget) {
      GameProfile targetOwner = personalTarget.getOwner();
      if (profile == null) {
        profile = targetOwner;
      }
      if (profile != null) {
        Container privilegedInventory = personalTarget.getPrivilegedInventory(profile);
        if (privilegedInventory == target && targetOwner != null) {
          privilegedInventory = personalTarget.getPrivilegedInventory(targetOwner);
        }
        if (privilegedInventory != target) {
          if (personalInventories != null) {
            personalInventories.add(privilegedInventory);
          }

          return privilegedInventory;
        }
      }
      return target instanceof TileEntityInventory tileEntityInventory ? tileEntityInventory : null;
    } else if (target instanceof RandomizableContainerBlockEntity containerBlockEntity) {
      BlockState state = target.getBlockState();
      if (state.getBlock() instanceof ChestBlock chestBlock) {
        Level level = target.getLevel();
        if (level != null) {
          Container combined =
              ChestBlock.getContainer(chestBlock, state, level, target.getBlockPos(), true);
          if (combined != null) {
            return combined;
          }
        }
      }

      return containerBlockEntity;
    } else {
      return target instanceof TileEntityInventory tileEntityInventory ? tileEntityInventory : null;
    }
  }

  @Nullable private static EnvItemHandlerForge.HandlerForge getStorage(
      BlockEntity source, Direction dir, @Nullable Set<Container> personalInventories) {
    BlockEntity target = source.getLevel().getBlockEntity(source.getBlockPos().relative(dir));
    GameProfile profile =
        source instanceof IPersonalBlock ? ((IPersonalBlock) source).getOwner() : null;
    return EnvItemHandlerForge.HandlerForge.ofNullable(
        getStorage(target, dir.getOpposite(), profile, personalInventories), dir.getOpposite());
  }

  private ItemStack extractItemFrom(
      EnvItemHandlerForge.HandlerForge storage, int amount, boolean simulate) {
    for (int i = 0; i < storage.getSlots(); i++) {
      ItemStack itemStack = storage.getStackInSlot(i);
      if (!itemStack.isEmpty() && storage.canExtractItem(i, itemStack)) {
        return storage.extractItem(i, amount, simulate);
      }
    }

    return ItemStack.EMPTY;
  }

  private ItemStack extractItemFrom(
      EnvItemHandlerForge.HandlerForge storage,
      int amount,
      Predicate<ItemStack> filter,
      boolean simulate) {
    for (int i = 0; i < storage.getSlots(); i++) {
      ItemStack itemStack = storage.getStackInSlot(i);
      if (!itemStack.isEmpty() && storage.canExtractItem(i, itemStack) && filter.test(itemStack)) {
        return storage.extractItem(i, amount, simulate);
      }
    }

    return ItemStack.EMPTY;
  }

  private int insertItemTo(
      EnvItemHandlerForge.HandlerForge storage, ItemStack stack, boolean simulate) {
    ItemStack remaining = stack.copy();
    int initialCount = remaining.getCount();

    for (int i = 0; i < storage.getSlots(); i++) {
      ItemStack itemStack = storage.getStackInSlot(i);
      if (storage.canInsertItem(i, remaining)
          && (itemStack.isEmpty()
              || ItemStack.isSameItemSameComponents(itemStack, remaining)
                  && itemStack.getCount() < storage.getSlotLimit(i))) {
        ItemStack toInsert = remaining.copy();
        ItemStack remainStack = storage.insertItem(i, toInsert, simulate);
        int inserted = remaining.getCount() - remainStack.getCount();
        remaining.shrink(inserted);
        if (remaining.isEmpty()) {
          break;
        }
      }
    }

    return initialCount - remaining.getCount();
  }

  @Override
  public int fetch(BlockEntity source, ItemStack stack, boolean simulate) {
    if (stack.isEmpty()) {
      return 0;
    }

    int remainingNeeded = stack.getCount();
    int totalFetched = 0;

    for (EnvItemHandler.AdjacentInventory inv : this.getAdjacentInventories(source)) {
      EnvItemHandlerForge.HandlerForge storage = (EnvItemHandlerForge.HandlerForge) inv;
      for (int i = 0; i < storage.getSlots(); i++) {
        ItemStack slotStack = storage.getStackInSlot(i);
        if (!slotStack.isEmpty()
            && storage.canExtractItem(i, slotStack)
            && ItemStack.isSameItemSameComponents(slotStack, stack)) {
          int toExtract = Math.min(remainingNeeded, slotStack.getCount());
          ItemStack extracted = storage.extractItem(i, toExtract, simulate);
          if (!extracted.isEmpty()) {
            totalFetched += extracted.getCount();
            remainingNeeded -= extracted.getCount();
            if (remainingNeeded <= 0) {
              return totalFetched;
            }
          }
        }
      }
    }

    return totalFetched;
  }

  @Override
  public int deposit(
      BlockEntity te, Direction side, ItemStack stack, GameProfile accessor, boolean simulate) {
    return this.deposit(
        EnvItemHandlerForge.HandlerForge.ofNullable(getStorage(te, side, accessor, null), side),
        stack,
        simulate);
  }

  @Override
  public int deposit(EnvItemHandler.AdjacentInventory inv, ItemStack stack, boolean simulate) {
    return this.deposit((EnvItemHandlerForge.HandlerForge) inv, stack, simulate);
  }

  private int deposit(EnvItemHandlerForge.HandlerForge storage, ItemStack stack, boolean simulate) {
    return storage == null ? 0 : this.insertItemTo(storage, stack, simulate);
  }

  @Override
  public int distribute(BlockEntity source, ItemStack stack, boolean simulate) {
    int totalDeposited = 0;
    ItemStack remaining = stack.copy();

    for (EnvItemHandler.AdjacentInventory inv : this.getAdjacentInventories(source)) {
      int deposited = this.deposit(inv, remaining.copy(), simulate);
      if (deposited > 0) {
        totalDeposited += deposited;
        remaining.shrink(deposited);
        if (remaining.isEmpty()) {
          break;
        }
      }
    }

    return totalDeposited;
  }

  @Nullable @Override
  public EnvItemHandler.AdjacentInventory getAdjacentInventory(BlockEntity source, Direction side) {
    return getStorage(source, side, null);
  }

  @Override
  public List<? extends EnvItemHandler.AdjacentInventory> getAdjacentInventories(
      BlockEntity source) {
    List<EnvItemHandlerForge.HandlerForge> inventories = new ArrayList<>();
    Set<Container> personalInventories = new HashSet<>();

    for (Direction dir : Util.ALL_DIRS) {
      EnvItemHandlerForge.HandlerForge maybeHandler = getStorage(source, dir, personalInventories);
      if (maybeHandler != null) {
        inventories.add(maybeHandler);
      }
    }

    inventories.sort(
        Comparator.<EnvItemHandlerForge.HandlerForge, Boolean>comparing(
                storage -> !personalInventories.contains(storage.getDelegate()))
            .thenComparingInt(
                storage -> {
                  int slotsEstimate = 0;
                  slotsEstimate += storage.getSlots();
                  return -slotsEstimate;
                }));
    return inventories;
  }

  @Override
  public EnvItemHandler.AdjacentInventory wrapInventory(BlockEntity inventory, Direction side) {
    return new EnvItemHandlerForge.HandlerForge((Container) inventory, side);
  }

  @Override
  public int transfer(
      EnvItemHandler.AdjacentInventory from, EnvItemHandler.AdjacentInventory to, int maxAmount) {
    EnvItemHandlerForge.HandlerForge fromStorage = (EnvItemHandlerForge.HandlerForge) from;
    EnvItemHandlerForge.HandlerForge toStorage = (EnvItemHandlerForge.HandlerForge) to;
    ItemStack extractedItemStack = this.extractItemFrom(fromStorage, maxAmount, true);
    if (extractedItemStack.getCount() > 0
        && this.insertItemTo(toStorage, extractedItemStack, true) > 0) {
      this.extractItemFrom(fromStorage, maxAmount, false);
      this.insertItemTo(toStorage, extractedItemStack, false);
      return extractedItemStack.getCount();
    } else {
      return 0;
    }
  }

  @Override
  public int transfer(
      EnvItemHandler.AdjacentInventory from,
      EnvItemHandler.AdjacentInventory to,
      int maxAmount,
      Predicate<ItemStack> filter) {
    EnvItemHandlerForge.HandlerForge fromStorage = (EnvItemHandlerForge.HandlerForge) from;
    EnvItemHandlerForge.HandlerForge toStorage = (EnvItemHandlerForge.HandlerForge) to;
    ItemStack extractedItemStack = this.extractItemFrom(fromStorage, maxAmount, filter, true);
    if (extractedItemStack.getCount() > 0
        && this.insertItemTo(toStorage, extractedItemStack, true) > 0) {
      this.extractItemFrom(fromStorage, maxAmount, filter, false);
      this.insertItemTo(toStorage, extractedItemStack, false);
      return extractedItemStack.getCount();
    } else {
      return 0;
    }
  }

  static class HandlerForge extends ItemStackHandler
      implements EnvItemHandler.AdjacentInventory {
    final Direction side;
    Container inventory;

    HandlerForge(Container inventory, Direction side) {
      super(inventory.getContainerSize());
      this.side = side;
      this.inventory = inventory;

      for (int i = 0; i < inventory.getContainerSize(); i++) {
        ItemStack item = inventory.getItem(i);
        this.stacks.set(i, item.isEmpty() ? ItemStack.EMPTY : item.copy());
      }
    }

    static EnvItemHandlerForge.HandlerForge ofNullable(Container inventory, Direction side) {
      return inventory == null ? null : new EnvItemHandlerForge.HandlerForge(inventory, side);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
      super.setStackInSlot(slot, stack.copy());
      this.inventory.setItem(slot, stack.copy());
    }

    public int size() {
      return this.inventory.getContainerSize();
    }

    @NotNull @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
      ItemStack remainStack = super.insertItem(slot, stack.copy(), simulate);
      if (!simulate) {
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
          this.inventory.setItem(i, this.getStackInSlot(i).copy());
        }
      }

      return remainStack;
    }

    @NotNull @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
      ItemStack extractedStack = super.extractItem(slot, amount, simulate);
      if (!simulate) {
        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
          this.inventory.setItem(i, this.getStackInSlot(i).copy());
        }
      }

      return extractedStack;
    }

    public boolean canInsertItem(int slot, @NotNull ItemStack stack) {
      return !(this.inventory instanceof WorldlyContainer sidedInventory
          && !sidedInventory.canPlaceItemThroughFace(slot, stack, this.getSide()));
    }

    public boolean canExtractItem(int slot, @NotNull ItemStack stack) {
      return !(this.inventory instanceof WorldlyContainer sidedInventory
          && !sidedInventory.canTakeItemThroughFace(slot, stack, this.getSide()));
    }

    @Override
    public Direction getSide() {
      return this.side;
    }

    private Container getDelegate() {
      return this.inventory;
    }
  }
}
