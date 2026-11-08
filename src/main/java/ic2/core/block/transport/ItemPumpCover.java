package ic2.core.block.transport;

import ic2.core.util.Ic2Tooltip;
import ic2.core.util.RotationUtil;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ItemPumpCover extends Item {
  public final PumpCoverTier tier;

  public ItemPumpCover(PumpCoverTier tier, Properties properties) {
    super(properties);
    this.tier = tier;
  }

  @Override
  public @NotNull InteractionResult useOn(UseOnContext context) {
    Level level = context.getLevel();
    BlockPos clickedPos = context.getClickedPos();
    BlockState state = level.getBlockState(clickedPos);
    Player player = context.getPlayer();
    ItemStack hand = context.getItemInHand();

    BlockPos targetPipePos = null;
    Direction targetFace = null;

    if (state.getBlock() instanceof TransportPipeBlock) {
      targetPipePos = clickedPos;
      targetFace =
          RotationUtil.rotateByHit(
              context.getClickedFace(),
              (float) (context.getClickLocation().x - clickedPos.getX()),
              (float) (context.getClickLocation().y - clickedPos.getY()),
              (float) (context.getClickLocation().z - clickedPos.getZ()));
    } else {
      // Check if clicking against a pipe from an adjacent block
      BlockPos adjacentPos = clickedPos.relative(context.getClickedFace());
      if (level.getBlockState(adjacentPos).getBlock() instanceof TransportPipeBlock) {
        targetPipePos = adjacentPos;
        targetFace = context.getClickedFace().getOpposite();
      }
    }

    if (targetPipePos != null && targetFace != null) {
      BlockEntity be = level.getBlockEntity(targetPipePos);
      if (be instanceof TransportPipeBlockEntity pipeEntity) {
        if (!level.isClientSide) {
          if (pipeEntity.installCover(targetFace, hand.copyWithCount(1))) {
            if (player == null || !player.getAbilities().instabuild) {
              hand.shrink(1);
            }
            level.playSound(
                null, targetPipePos, SoundEvents.ANVIL_USE, SoundSource.BLOCKS, 0.5F, 1.5F);
            return InteractionResult.SUCCESS;
          }
        } else {
          return InteractionResult.SUCCESS;
        }
      }
    }
    return super.useOn(context);
  }

  @Override
  public void appendHoverText(
      @NotNull ItemStack stack,
      Item.TooltipContext context,
      @NotNull List<Component> tooltip,
      @NotNull TooltipFlag flag) {
    Ic2Tooltip.add(
        tooltip, Component.translatable("tooltip.ic2.cover.pump.rate", this.tier.ratePerTick));
    Ic2Tooltip.add(
        tooltip, Component.translatable("tooltip.ic2.cover.pump.rate_sec", this.tier.ratePerSec));
  }
}
