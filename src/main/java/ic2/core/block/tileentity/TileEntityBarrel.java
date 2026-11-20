package ic2.core.block.tileentity;

import ic2.core.ref.Ic2BlockEntities;
import ic2.core.ref.Ic2Items;
import ic2.core.util.StackUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * TileEntity for the Booze Barrel. Handles water filling, ingredient loading (wheat, hops, sugar
 * cane), fermentation progression over time, tapping with a treetap, and pouring into stone mugs.
 */
public class TileEntityBarrel extends Ic2TileEntity {
  public static final int MAX_WATER = 32000; // 32 buckets / cells
  public static final int MAX_INGREDIENTS = 64;

  public int waterAmount = 0;
  public int wheat = 0;
  public int hops = 0;
  public int sugarCane = 0;
  public int fermentationProgress = 0;
  public boolean hasTap = false;

  public TileEntityBarrel(BlockPos pos, BlockState state) {
    super(Ic2BlockEntities.BARREL, pos, state);
  }

  @Override
  protected void updateEntityServer() {
    super.updateEntityServer();
    if (!this.hasTap
        && this.waterAmount > 0
        && (this.wheat > 0 || this.hops > 0 || this.sugarCane > 0)) {
      this.fermentationProgress++;
      if (this.fermentationProgress % 600 == 0) {
        this.setChanged();
      }
    }
  }

  @Override
  public ItemInteractionResult onItemUse(
      ItemStack stack, Player player, InteractionHand hand, BlockHitResult hit) {
    if (this.level == null) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    // 1. Water Bucket
    if (stack.is(Items.WATER_BUCKET)) {
      if (this.waterAmount + 1000 <= MAX_WATER) {
        if (!this.level.isClientSide) {
          this.waterAmount += 1000;
          if (!player.getAbilities().instabuild) {
            player.setItemInHand(hand, new ItemStack(Items.BUCKET));
          }
          this.level.playSound(
              null, this.worldPosition, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
          this.setChanged();
          player.displayClientMessage(
              Component.translatable(
                  "ic2.barrel.water_added", this.waterAmount / 1000, MAX_WATER / 1000),
              true);
        }
        return ItemInteractionResult.sidedSuccess(this.level.isClientSide);
      } else {
        if (!this.level.isClientSide) {
          player.displayClientMessage(Component.translatable("ic2.barrel.water_full"), true);
        }
        return ItemInteractionResult.sidedSuccess(this.level.isClientSide);
      }
    }

    // 2. IC2 Water Cell
    if (stack.is(Ic2Items.WATER_CELL)) {
      if (this.waterAmount + 1000 <= MAX_WATER) {
        if (!this.level.isClientSide) {
          this.waterAmount += 1000;
          if (!player.getAbilities().instabuild) {
            stack.shrink(1);
            if (!player.getInventory().add(new ItemStack(Ic2Items.EMPTY_CELL))) {
              player.drop(new ItemStack(Ic2Items.EMPTY_CELL), false);
            }
          }
          this.level.playSound(
              null, this.worldPosition, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
          this.setChanged();
          player.displayClientMessage(
              Component.translatable(
                  "ic2.barrel.water_added", this.waterAmount / 1000, MAX_WATER / 1000),
              true);
        }
        return ItemInteractionResult.sidedSuccess(this.level.isClientSide);
      } else {
        if (!this.level.isClientSide) {
          player.displayClientMessage(Component.translatable("ic2.barrel.water_full"), true);
        }
        return ItemInteractionResult.sidedSuccess(this.level.isClientSide);
      }
    }

    // 3. Wheat
    if (stack.is(Items.WHEAT)) {
      if (this.wheat < MAX_INGREDIENTS) {
        if (!this.level.isClientSide) {
          this.wheat++;
          if (!player.getAbilities().instabuild) stack.shrink(1);
          this.level.playSound(
              null, this.worldPosition, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
          this.setChanged();
          player.displayClientMessage(
              Component.translatable("ic2.barrel.wheat_added", this.wheat), true);
        }
        return ItemInteractionResult.sidedSuccess(this.level.isClientSide);
      }
      return ItemInteractionResult.sidedSuccess(this.level.isClientSide);
    }

    // 4. Hops
    if (stack.is(Ic2Items.HOPS)) {
      if (this.hops < MAX_INGREDIENTS) {
        if (!this.level.isClientSide) {
          this.hops++;
          if (!player.getAbilities().instabuild) stack.shrink(1);
          this.level.playSound(
              null, this.worldPosition, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
          this.setChanged();
          player.displayClientMessage(
              Component.translatable("ic2.barrel.hops_added", this.hops), true);
        }
        return ItemInteractionResult.sidedSuccess(this.level.isClientSide);
      }
      return ItemInteractionResult.sidedSuccess(this.level.isClientSide);
    }

    // 5. Sugar Cane or Sugar
    if (stack.is(Items.SUGAR_CANE) || stack.is(Items.SUGAR)) {
      if (this.sugarCane < MAX_INGREDIENTS) {
        if (!this.level.isClientSide) {
          this.sugarCane++;
          if (!player.getAbilities().instabuild) stack.shrink(1);
          this.level.playSound(
              null, this.worldPosition, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);
          this.setChanged();
          player.displayClientMessage(
              Component.translatable("ic2.barrel.sugarcane_added", this.sugarCane), true);
        }
        return ItemInteractionResult.sidedSuccess(this.level.isClientSide);
      }
      return ItemInteractionResult.sidedSuccess(this.level.isClientSide);
    }

    // 6. Treetap
    if (stack.is(Ic2Items.TREETAP) || stack.is(Ic2Items.ELECTRIC_TREETAP)) {
      if (!this.hasTap) {
        if (!this.level.isClientSide) {
          this.hasTap = true;
          this.setActive(true);
          this.level.setBlockAndUpdate(
              this.worldPosition, this.getBlockState().setValue(Ic2TileEntityBlock.ACTIVE, true));
          if (!player.getAbilities().instabuild && stack.is(Ic2Items.TREETAP)) {
            stack.shrink(1);
          }
          this.level.playSound(
              null, this.worldPosition, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
          this.setChanged();
          player.displayClientMessage(Component.translatable("ic2.barrel.tapped"), true);
        }
        return ItemInteractionResult.sidedSuccess(this.level.isClientSide);
      }
    }

    // 7. Empty Mug
    if (stack.is(Ic2Items.EMPTY_MUG)) {
      if (!this.hasTap) {
        if (!this.level.isClientSide) {
          player.displayClientMessage(Component.translatable("ic2.barrel.needs_tap"), true);
        }
        return ItemInteractionResult.sidedSuccess(this.level.isClientSide);
      }

      if (this.waterAmount < 1000 || (this.wheat == 0 && this.hops == 0 && this.sugarCane == 0)) {
        if (!this.level.isClientSide) {
          player.displayClientMessage(Component.translatable("ic2.barrel.empty"), true);
        }
        return ItemInteractionResult.sidedSuccess(this.level.isClientSide);
      }

      if (!this.level.isClientSide) {
        Item boozeMug = this.getCurrentBoozeMug();
        this.waterAmount -= 1000;
        if (!player.getAbilities().instabuild) {
          stack.shrink(1);
          ItemStack pouredStack = new ItemStack(boozeMug);
          if (!player.getInventory().add(pouredStack)) {
            player.drop(pouredStack, false);
          }
        }
        this.level.playSound(
            null, this.worldPosition, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (this.waterAmount <= 0) {
          this.wheat = 0;
          this.hops = 0;
          this.sugarCane = 0;
          this.fermentationProgress = 0;
        }
        this.setChanged();
        player.displayClientMessage(
            Component.translatable("ic2.barrel.poured", boozeMug.getName(new ItemStack(boozeMug))),
            true);
      }
      return ItemInteractionResult.sidedSuccess(this.level.isClientSide);
    }

    return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
  }

  @Override
  protected InteractionResult onClicked(Player player) {
    if (this.level == null) return InteractionResult.PASS;

    if (this.hasTap) {
      if (!this.level.isClientSide) {
        this.hasTap = false;
        this.setActive(false);
        this.level.setBlockAndUpdate(
            this.worldPosition, this.getBlockState().setValue(Ic2TileEntityBlock.ACTIVE, false));
        StackUtil.dropAsEntity(this.level, this.worldPosition, new ItemStack(Ic2Items.TREETAP));
        this.level.playSound(
            null, this.worldPosition, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        this.setChanged();
        player.displayClientMessage(Component.translatable("ic2.barrel.untapped"), true);
      }
      return InteractionResult.sidedSuccess(this.level.isClientSide);
    }

    return InteractionResult.PASS;
  }

  @Override
  protected InteractionResult onActivated(
      Player player, InteractionHand hand, Direction side, Vec3 hit) {
    if (this.level == null) return InteractionResult.PASS;

    if (!this.level.isClientSide) {
      showStatus(player);
    }
    return InteractionResult.sidedSuccess(this.level.isClientSide);
  }

  private void showStatus(Player player) {
    String stateStr = this.hasTap ? "[Tapped] " : "";
    int waterBuckets = this.waterAmount / 1000;
    if (this.waterAmount <= 0 && this.wheat == 0 && this.hops == 0 && this.sugarCane == 0) {
      player.displayClientMessage(
          Component.translatable("ic2.barrel.status.empty", stateStr), false);
    } else {
      String brewName =
          this.getCurrentBoozeMug().getName(new ItemStack(this.getCurrentBoozeMug())).getString();
      int progressSec = this.fermentationProgress / 20;
      player.displayClientMessage(
          Component.translatable(
              "ic2.barrel.status.info",
              stateStr,
              waterBuckets,
              this.wheat,
              this.hops,
              this.sugarCane,
              brewName,
              progressSec),
          false);
    }
  }

  public Item getCurrentBoozeMug() {
    if (this.sugarCane > 0 && this.wheat == 0 && this.hops == 0) {
      if (this.fermentationProgress < 2400) {
        return Ic2Items.BREW_MUG;
      }
      return Ic2Items.RUM_MUG;
    }

    int totalSolids = this.wheat + this.hops;
    int waterBuckets = Math.max(1, this.waterAmount / 1000);

    if (this.hops > this.wheat * 2 || totalSolids > waterBuckets * 4) {
      return Ic2Items.BLACK_STUFF_MUG;
    }

    if (this.fermentationProgress < 2400) {
      return Ic2Items.BREW_MUG;
    }

    if (this.fermentationProgress < 9600) {
      return Ic2Items.YOUNGSTER_MUG;
    }

    if (this.fermentationProgress >= 24000
        && this.wheat >= 2
        && this.hops >= 2
        && totalSolids >= waterBuckets) {
      return Ic2Items.DRAGON_BLOOD_MUG;
    }

    if (this.wheat >= 2 * this.hops) {
      return Ic2Items.ALE_MUG;
    } else {
      return Ic2Items.BEER_MUG;
    }
  }

  @Override
  protected void onBlockBreak() {
    if (this.level != null && !this.level.isClientSide) {
      if (this.hasTap) {
        StackUtil.dropAsEntity(this.level, this.worldPosition, new ItemStack(Ic2Items.TREETAP));
      }
      if (this.wheat > 0) {
        StackUtil.dropAsEntity(
            this.level, this.worldPosition, new ItemStack(Items.WHEAT, this.wheat));
      }
      if (this.hops > 0) {
        StackUtil.dropAsEntity(
            this.level, this.worldPosition, new ItemStack(Ic2Items.HOPS, this.hops));
      }
      if (this.sugarCane > 0) {
        StackUtil.dropAsEntity(
            this.level, this.worldPosition, new ItemStack(Items.SUGAR_CANE, this.sugarCane));
      }
    }
    super.onBlockBreak();
  }

  @Override
  protected void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
    super.loadAdditional(nbt, registries);
    this.waterAmount = nbt.getInt("WaterAmount");
    this.wheat = nbt.getInt("Wheat");
    this.hops = nbt.getInt("Hops");
    this.sugarCane = nbt.getInt("SugarCane");
    this.fermentationProgress = nbt.getInt("FermentationProgress");
    this.hasTap = nbt.getBoolean("HasTap");
  }

  @Override
  public void saveAdditional(CompoundTag nbt, HolderLookup.Provider registries) {
    super.saveAdditional(nbt, registries);
    nbt.putInt("WaterAmount", this.waterAmount);
    nbt.putInt("Wheat", this.wheat);
    nbt.putInt("Hops", this.hops);
    nbt.putInt("SugarCane", this.sugarCane);
    nbt.putInt("FermentationProgress", this.fermentationProgress);
    nbt.putBoolean("HasTap", this.hasTap);
  }
}
