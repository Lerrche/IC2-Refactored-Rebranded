package ic2.api.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Configurable electric mining tool base. Add-ons can override {@link #canMine} and the block break
 * hooks to implement drills, chainsaws, tunnellers, area miners or custom mining effects.
 */
public class AddonElectricMiningTool extends AddonElectricItem
    implements IMiningDrill, BlockBreakableItem {
  private final int energyPerBlock;
  private final int baseBreakTime;
  private final float miningSpeed;

  public AddonElectricMiningTool(
      net.minecraft.world.item.Item.Properties itemProperties,
      double capacity,
      double transferLimit,
      int tier,
      boolean canProvideEnergy,
      int energyPerBlock,
      int breakTime,
      float miningSpeed) {
    super(itemProperties, capacity, transferLimit, tier, canProvideEnergy);
    if (energyPerBlock < 0) throw new IllegalArgumentException("energyPerBlock cannot be negative");
    if (breakTime < 1) throw new IllegalArgumentException("breakTime must be positive");
    if (!Float.isFinite(miningSpeed) || miningSpeed < 0)
      throw new IllegalArgumentException("miningSpeed must be finite and non-negative");
    this.energyPerBlock = energyPerBlock;
    this.baseBreakTime = breakTime;
    this.miningSpeed = miningSpeed;
  }

  public boolean canMine(ItemStack stack, Level level, BlockPos pos, BlockState state) {
    return !state.isAir() && state.getDestroySpeed(level, pos) >= 0.0F;
  }

  @Override
  public int energyUse(ItemStack stack, Level level, BlockPos pos, BlockState state) {
    return this.energyPerBlock;
  }

  @Override
  public int breakTime(ItemStack stack, Level level, BlockPos pos, BlockState state) {
    if (!canMine(stack, level, pos, state)) return Integer.MAX_VALUE;
    float hardness = state.getDestroySpeed(level, pos);
    if (hardness < 0) return Integer.MAX_VALUE;
    return Math.max(
        1,
        Math.round(
            this.baseBreakTime * Math.max(0.1F, hardness) / Math.max(0.1F, this.miningSpeed)));
  }

  @Override
  public boolean breakBlock(ItemStack stack, Level level, BlockPos pos, BlockState state) {
    return canMine(stack, level, pos, state)
        && (this.energyPerBlock == 0 || ElectricItem.manager.use(stack, this.energyPerBlock, null));
  }

  @Override
  public InteractionResult onBlockStartBreak(
      Player player, Level level, InteractionHand hand, BlockPos pos, Direction face) {
    return canMine(player.getItemInHand(hand), level, pos, level.getBlockState(pos))
        ? InteractionResult.PASS
        : InteractionResult.FAIL;
  }

  @Override
  public boolean beforeBlockBreak(
      Level level,
      Player player,
      BlockPos pos,
      BlockState state,
      @Nullable BlockEntity blockEntity) {
    ItemStack stack = player.getMainHandItem();
    return canMine(stack, level, pos, state)
        && (this.energyPerBlock == 0
            || ElectricItem.manager.use(stack, this.energyPerBlock, player));
  }

  @Override
  public void afterBlockBreak(
      Level level,
      Player player,
      BlockPos pos,
      BlockState state,
      @Nullable BlockEntity blockEntity) {}

  public int getEnergyPerBlock() {
    return this.energyPerBlock;
  }

  public float getMiningSpeed() {
    return this.miningSpeed;
  }
}
