package ic2.advsolar.blocks.entity;

import ic2.advsolar.gui.MolecularTransformerMenu;
import ic2.api.block.AddonEnergyBlockEntity;
import ic2.api.energy.tile.IEnergyEmitter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Molecular Transformer block entity for Advanced Solar Panels.
 *
 * <p>Consumes huge amounts of energy to transmute matter into rare and valuable substances.
 */
public class MolecularTransformerBlockEntity extends AddonEnergyBlockEntity
    implements MenuProvider, Container {
  public static final double MAX_INPUT_EU_PER_TICK = 32_768.0;
  public static final double CAPACITY = 250_000_000.0;
  public static final int TIER = 5;

  public record TransmutationRecipe(Ingredient input, ItemStack output, double euCost) {}

  private static final List<TransmutationRecipe> RECIPES = new ArrayList<>();

  static {
    RECIPES.add(
        new TransmutationRecipe(
            Ingredient.of(Items.COAL), new ItemStack(Items.DIAMOND), 9_000_000.0));
    RECIPES.add(
        new TransmutationRecipe(
            Ingredient.of(Items.CHARCOAL), new ItemStack(Items.COAL), 50_000.0));
    RECIPES.add(
        new TransmutationRecipe(
            Ingredient.of(Items.SKELETON_SKULL),
            new ItemStack(Items.WITHER_SKELETON_SKULL),
            3_000_000.0));
    RECIPES.add(
        new TransmutationRecipe(
            Ingredient.of(Items.IRON_INGOT), new ItemStack(Items.GLOWSTONE_DUST, 2), 500_000.0));
    RECIPES.add(
        new TransmutationRecipe(
            Ingredient.of(Items.FLINT), new ItemStack(Items.GUNPOWDER), 70_000.0));
    RECIPES.add(
        new TransmutationRecipe(Ingredient.of(Items.SAND), new ItemStack(Items.CLAY), 50_000.0));
    RECIPES.add(
        new TransmutationRecipe(
            Ingredient.of(Items.DIRT), new ItemStack(Items.CLAY_BALL), 25_000.0));
    RECIPES.add(
        new TransmutationRecipe(
            Ingredient.of(Items.NETHERRACK), new ItemStack(Items.GUNPOWDER), 70_000.0));
    RECIPES.add(
        new TransmutationRecipe(
            Ingredient.of(Items.GUNPOWDER), new ItemStack(Items.REDSTONE), 70_000.0));
    RECIPES.add(
        new TransmutationRecipe(
            Ingredient.of(Items.ROTTEN_FLESH), new ItemStack(Items.LEATHER), 50_000.0));
    RECIPES.add(
        new TransmutationRecipe(
            Ingredient.of(Items.WITHER_SKELETON_SKULL),
            new ItemStack(Items.NETHER_STAR),
            250_000_000.0));
    RECIPES.add(
        new TransmutationRecipe(
            Ingredient.of(Items.YELLOW_WOOL), new ItemStack(Items.GLOWSTONE), 500_000.0));
    RECIPES.add(
        new TransmutationRecipe(
            Ingredient.of(Items.BLUE_WOOL), new ItemStack(Items.LAPIS_BLOCK), 500_000.0));
    RECIPES.add(
        new TransmutationRecipe(
            Ingredient.of(Items.RED_WOOL), new ItemStack(Items.REDSTONE_BLOCK), 500_000.0));
  }

  public static void registerRecipe(Ingredient input, ItemStack output, double euCost) {
    RECIPES.add(new TransmutationRecipe(input, output, euCost));
  }

  public static void addRecipe(TransmutationRecipe recipe) {
    if (recipe != null) {
      RECIPES.add(recipe);
    }
  }

  public static List<TransmutationRecipe> getRecipes() {
    return Collections.unmodifiableList(RECIPES);
  }

  public static boolean hasRecipeFor(ItemStack stack) {
    if (stack == null || stack.isEmpty()) return false;
    for (TransmutationRecipe r : RECIPES) {
      if (r.input().test(stack)) return true;
    }
    return false;
  }

  public static TransmutationRecipe findRecipe(ItemStack stack) {
    if (stack == null || stack.isEmpty()) return null;
    for (TransmutationRecipe recipe : RECIPES) {
      if (recipe.input().test(stack)) return recipe;
    }
    return null;
  }

  private final NonNullList<ItemStack> items =
      NonNullList.withSize(MolecularTransformerMenu.MACHINE_SLOT_COUNT, ItemStack.EMPTY);
  private double progressEu = 0.0;
  private double requiredEu = 0.0;
  private int lastEuInput = 0;

  protected final ContainerData dataAccess =
      new ContainerData() {
        @Override
        public int get(int index) {
          long prog = (long) Math.max(0, Math.floor(progressEu));
          long req = (long) Math.max(0, Math.floor(requiredEu));
          return switch (index) {
            case 0 -> (int) (prog & 0xFFFF);
            case 1 -> (int) ((prog >> 16) & 0xFFFF);
            case 2 -> (int) ((prog >> 32) & 0xFFFF);
            case 3 -> (int) (req & 0xFFFF);
            case 4 -> (int) ((req >> 16) & 0xFFFF);
            case 5 -> (int) ((req >> 32) & 0xFFFF);
            default -> 0;
          };
        }

        @Override
        public void set(int index, int value) {}

        @Override
        public int getCount() {
          return MolecularTransformerMenu.DATA_COUNT;
        }
      };

  private final IItemHandler itemHandler =
      new IItemHandler() {
        @Override
        public int getSlots() {
          return 2;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
          return items.get(slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
          if (slot != 0 || stack.isEmpty()) return stack;
          if (findRecipe(stack) == null) return stack;
          ItemStack current = items.get(0);
          if (!current.isEmpty()
              && (!ItemStack.isSameItemSameComponents(current, stack)
                  || current.getCount() >= current.getMaxStackSize())) {
            return stack;
          }
          int maxInsert =
              current.isEmpty()
                  ? stack.getMaxStackSize()
                  : current.getMaxStackSize() - current.getCount();
          int insertCount = Math.min(stack.getCount(), maxInsert);
          if (!simulate && insertCount > 0) {
            if (current.isEmpty()) {
              items.set(0, stack.copyWithCount(insertCount));
            } else {
              current.grow(insertCount);
            }
            setChanged();
          }
          if (insertCount >= stack.getCount()) return ItemStack.EMPTY;
          ItemStack remaining = stack.copy();
          remaining.shrink(insertCount);
          return remaining;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
          if (slot != 1 || items.get(1).isEmpty() || amount <= 0) return ItemStack.EMPTY;
          ItemStack out = items.get(1);
          int extractCount = Math.min(amount, out.getCount());
          ItemStack result = out.copyWithCount(extractCount);
          if (!simulate) {
            out.shrink(extractCount);
            setChanged();
          }
          return result;
        }

        @Override
        public int getSlotLimit(int slot) {
          return 64;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
          return slot == 0 && findRecipe(stack) != null;
        }
      };

  public MolecularTransformerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
    super(type, pos, state, TIER, CAPACITY, MAX_INPUT_EU_PER_TICK, 0);
  }

  public IItemHandler getItemHandler() {
    return this.itemHandler;
  }

  public double getProgressEu() {
    return this.progressEu;
  }

  public double getRequiredEu() {
    return this.requiredEu;
  }

  public boolean isWorking() {
    return this.requiredEu > 0 && !this.items.get(0).isEmpty();
  }

  @Override
  public boolean acceptsEnergyFrom(IEnergyEmitter emitter, Direction side) {
    return isWorking() && this.progressEu < this.requiredEu;
  }

  @Override
  public double getDemandedEnergy() {
    if (!isWorking()) return 0;
    return Math.min(MAX_INPUT_EU_PER_TICK, Math.max(0, this.requiredEu - this.progressEu));
  }

  @Override
  public double injectEnergy(Direction from, double amount, double voltage) {
    if (!isWorking() || amount <= 0) return amount;
    double needed = this.requiredEu - this.progressEu;
    double accepted = Math.min(amount, Math.min(MAX_INPUT_EU_PER_TICK, needed));
    if (accepted > 0) {
      this.progressEu += accepted;
      this.lastEuInput = (int) accepted;
      setChanged();
    }
    return Math.max(0, amount - accepted);
  }

  @Override
  public void serverTick() {
    if (this.level == null || this.level.isClientSide) return;

    ItemStack input = this.items.get(0);
    if (!input.isEmpty()) {
      TransmutationRecipe recipe = findRecipe(input);
      if (recipe != null) {
        this.requiredEu =
            recipe.euCost() * ic2.industrialjs.IndustrialJS.getMolecularTransformerMultiplier();
        ItemStack recipeOut = recipe.output();
        ItemStack currentOut = this.items.get(1);

        boolean canFit =
            currentOut.isEmpty()
                || (ItemStack.isSameItemSameComponents(currentOut, recipeOut)
                    && currentOut.getCount() + recipeOut.getCount()
                        <= currentOut.getMaxStackSize());

        if (canFit) {
          // If buffer stored energy exists, feed it to progress
          if (this.getStoredEu() > 0) {
            double needed = this.requiredEu - this.progressEu;
            double step = Math.min(needed, Math.min(this.getStoredEu(), MAX_INPUT_EU_PER_TICK));
            if (step > 0) {
              this.removeEu(step);
              this.progressEu += step;
              setChanged();
            }
          }

          if (this.progressEu >= this.requiredEu) {
            this.progressEu = 0;
            input.shrink(1);
            if (currentOut.isEmpty()) {
              this.items.set(1, recipeOut.copy());
            } else {
              currentOut.grow(recipeOut.getCount());
            }
            setChanged();
          }
        }
      } else {
        this.requiredEu = 0;
        this.progressEu = 0;
      }
    } else {
      this.requiredEu = 0;
      this.progressEu = 0;
    }
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
  }

  @Override
  public AbstractContainerMenu createMenu(
      int containerId, Inventory playerInventory, Player player) {
    return new MolecularTransformerMenu(containerId, playerInventory, this, this.dataAccess);
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
    this.progressEu = tag.getDouble("progressEu");
    this.requiredEu = tag.getDouble("requiredEu");
    this.items.clear();
    ContainerHelper.loadAllItems(tag, this.items, registries);
  }

  @Override
  protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    tag.putDouble("progressEu", this.progressEu);
    tag.putDouble("requiredEu", this.requiredEu);
    ContainerHelper.saveAllItems(tag, this.items, registries);
  }
}
