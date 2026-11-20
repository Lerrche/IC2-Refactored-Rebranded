package ic2.core.item.tool;

import ic2.core.IC2;
import ic2.core.ref.Ic2ToolMaterials;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;

/**
 * Diamond Chainsaw ported from Electro-Magic Tools (EMT). Functions as a diamond-tier axe and
 * chainsaw with high mining speed (16.0F), shears support, axe stripping/scraping/waxing, and EMT's
 * right-click torch placement.
 */
public class ItemElectricToolDiamondChainsaw extends ItemElectricToolChainsaw {
  public ItemElectricToolDiamondChainsaw(Properties settings) {
    super(settings, 160, Ic2ToolMaterials.DIAMOND_CHAINSAW, 40000, 200, 2);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Player player = context.getPlayer();
    if (player != null && IC2.keyboard.isModeSwitchKeyDown(player)) {
      return InteractionResult.PASS;
    }

    ItemStack stack = context.getItemInHand();
    if (!this.canUse(stack)) {
      return InteractionResult.PASS;
    }

    Level level = context.getLevel();
    BlockPos pos = context.getClickedPos();
    BlockState state = level.getBlockState(pos);

    // 1. Check for axe stripping / scraping / wax removal
    BlockState modifiedState = state.getToolModifiedState(context, ItemAbilities.AXE_STRIP, false);
    var sound = SoundEvents.AXE_STRIP;
    if (modifiedState == null) {
      modifiedState = state.getToolModifiedState(context, ItemAbilities.AXE_SCRAPE, false);
      sound = SoundEvents.AXE_SCRAPE;
    }
    if (modifiedState == null) {
      modifiedState = state.getToolModifiedState(context, ItemAbilities.AXE_WAX_OFF, false);
      sound = SoundEvents.AXE_WAX_OFF;
    }

    if (modifiedState != null) {
      level.playSound(player, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
      if (!level.isClientSide) {
        level.setBlock(pos, modifiedState, 11);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, modifiedState));
        this.consumeEnergy(stack, this.operationEnergyCost, player);
      }
      return InteractionResult.sidedSuccess(level.isClientSide);
    }

    // 2. EMT feature: place torch from player's inventory on right-click
    if (player != null && tryPlaceTorch(player, context)) {
      return InteractionResult.sidedSuccess(level.isClientSide);
    }

    return super.useOn(context);
  }

  private boolean tryPlaceTorch(Player player, UseOnContext context) {
    Level level = context.getLevel();
    BlockPos clickedPos = context.getClickedPos();
    Direction face = context.getClickedFace();
    BlockPos targetPos = clickedPos.relative(face);

    if (!level.getBlockState(targetPos).canBeReplaced()) {
      return false;
    }

    for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
      ItemStack invStack = player.getInventory().getItem(i);
      if (!invStack.isEmpty() && invStack.getItem() instanceof BlockItem blockItem) {
        Block block = blockItem.getBlock();
        if (block == Blocks.TORCH || block == Blocks.SOUL_TORCH) {
          BlockState placeState;
          if (face == Direction.UP) {
            placeState = block.defaultBlockState();
          } else if (face != Direction.DOWN) {
            Block wallTorch = block == Blocks.TORCH ? Blocks.WALL_TORCH : Blocks.SOUL_WALL_TORCH;
            placeState = wallTorch.defaultBlockState().setValue(WallTorchBlock.FACING, face);
          } else {
            return false;
          }

          if (placeState.canSurvive(level, targetPos)) {
            if (!level.isClientSide) {
              level.setBlock(targetPos, placeState, 3);
              level.playSound(
                  null, targetPos, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
              level.gameEvent(
                  GameEvent.BLOCK_PLACE, targetPos, GameEvent.Context.of(player, placeState));
              if (!player.getAbilities().instabuild) {
                invStack.shrink(1);
              }
            }
            return true;
          }
        }
      }
    }
    return false;
  }
}
