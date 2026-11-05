package ic2.api.block;

import com.mojang.serialization.MapCodec;
import java.util.Objects;
import java.util.function.BiFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Simple vanilla block base for add-on energy machines, batteries and generators. Supply a factory
 * that constructs the registered block entity type. Custom shapes and interactions can be added by
 * subclassing this class.
 */
public class AddonEnergyBlock extends BaseEntityBlock {
  protected final BiFunction<BlockPos, BlockState, ? extends BlockEntity> factory;
  private final MapCodec<AddonEnergyBlock> codec;

  public AddonEnergyBlock(
      BlockBehaviour.Properties properties,
      BiFunction<BlockPos, BlockState, ? extends BlockEntity> factory) {
    super(properties);
    this.factory = Objects.requireNonNull(factory, "factory");
    this.codec =
        simpleCodec(blockProperties -> new AddonEnergyBlock(blockProperties, this.factory));
  }

  /**
   * Helper function returning light emission when a boolean block state property is true.
   *
   * @param property The boolean property to check on the block state
   * @param lightLevel The light level to emit when active (1 to 15)
   * @return A ToIntFunction suitable for {@link BlockBehaviour.Properties#lightLevel}
   */
  public static java.util.function.ToIntFunction<BlockState> litWhenActive(
      net.minecraft.world.level.block.state.properties.BooleanProperty property, int lightLevel) {
    return state -> (state.hasProperty(property) && state.getValue(property)) ? lightLevel : 0;
  }

  /** Keep the supplied block-entity factory when the standard block codec rebuilds this block. */
  @Override
  protected MapCodec<? extends BaseEntityBlock> codec() {
    return this.codec;
  }

  @Override
  public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
    return this.factory.apply(pos, state);
  }

  @Override
  public RenderShape getRenderShape(BlockState state) {
    return RenderShape.MODEL;
  }

  @Nullable @Override
  public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level level, BlockState state, BlockEntityType<T> type) {
    if (level.isClientSide) return null;
    return (tickLevel, pos, blockState, blockEntity) -> {
      if (blockEntity instanceof IAddonServerTickable tickable) tickable.serverTick();
    };
  }
}
