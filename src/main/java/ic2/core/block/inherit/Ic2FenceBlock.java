package ic2.core.block.inherit;

import ic2.api.item.ItemWrapper;
import ic2.core.IC2;
import ic2.core.block.machine.tileentity.TileEntityMagnetizer;
import ic2.core.ref.Ic2Blocks;
import ic2.core.util.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class Ic2FenceBlock extends FenceBlock {
  public static final Map<Direction, BooleanProperty> connectProperties = getConnectProperties();
  private static final Map<Player, Long> lastBoostTick = new WeakHashMap<>();
  // Slightly exceeds gravity, so upward speed builds smoothly instead of jumping to full speed.
  private static final double NORMAL_LIFT_ACCELERATION = 0.09;
  private static final double MAX_LIFT_SPEED = 0.35;
  public final boolean canBoost;

  public Ic2FenceBlock(Properties settings, boolean canBoost) {
    super(settings);
    this.canBoost = canBoost;
  }

  private static TileEntityMagnetizer getMagnetizer(
      BlockGetter world, BlockPos pos, Direction side, BlockState state) {
    if (!state.is(Ic2Blocks.MAGNETIZER)) {
      return null;
    }

    if (world.getBlockEntity(pos) instanceof TileEntityMagnetizer magnetizer) {
      // The machine's front must face the iron-fence column.
      if (side != null && side.getOpposite() != magnetizer.getFacing()) {
        return null;
      }
      return magnetizer;
    }

    return null;
  }

  public static boolean hasMetalShoes(Player player) {
    ItemStack shoes = player.getItemBySlot(EquipmentSlot.FEET);
    Item item = shoes.getItem();
    return item == Items.IRON_BOOTS
        || item == Items.GOLDEN_BOOTS
        || item == Items.CHAINMAIL_BOOTS
        || ItemWrapper.isMetalArmor(shoes, player);
  }

  /**
   * Called from PlayerTickEvent.Post. Scanning after vanilla movement means the lift velocity is
   * not immediately replaced by the player's normal travel/ground-collision update.
   */
  public static void onPlayerTick(Player player) {
    Level level = player.level();
    if (level.isClientSide
        || player.isSpectator()
        || (player.isCreative() && player.getAbilities().flying)
        || !hasMetalShoes(player)) {
      return;
    }

    // A player approaches the iron fence from beside it. Check the player's real bounds with a
    // small horizontal/vertical margin instead of requiring the player's block position to be
    // exactly in the fence column.
    AABB searchBounds = player.getBoundingBox().inflate(0.65, 1.0, 0.65);
    int minX = Mth.floor(searchBounds.minX);
    int maxX = Mth.floor(searchBounds.maxX);
    int minY = Mth.floor(searchBounds.minY);
    int maxY = Mth.floor(searchBounds.maxY);
    int minZ = Mth.floor(searchBounds.minZ);
    int maxZ = Mth.floor(searchBounds.maxZ);

    for (int x = minX; x <= maxX; x++) {
      for (int y = minY; y <= maxY; y++) {
        for (int z = minZ; z <= maxZ; z++) {
          BlockPos fencePos = new BlockPos(x, y, z);
          if (!searchBounds.intersects(new AABB(fencePos))) {
            continue;
          }

          BlockState state = level.getBlockState(fencePos);
          if (state.getBlock() instanceof Ic2FenceBlock fence
              && fence.canBoost
              && fence.handleMagnetizerBoost(level, fencePos, player)) {
            return;
          }
        }
      }
    }
  }

  private static Map<Direction, BooleanProperty> getConnectProperties() {
    return CrossCollisionBlock.PROPERTY_BY_DIRECTION;
  }

  @Override
  public BlockState getStateForPlacement(BlockPlaceContext ctx) {
    FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
    return this.getActualState(
        this.defaultBlockState().setValue(WATERLOGGED, fluidState.is(Fluids.WATER)),
        ctx.getLevel(),
        ctx.getClickedPos());
  }

  @Override
  public BlockState updateShape(
      BlockState state,
      Direction direction,
      BlockState neighborState,
      LevelAccessor world,
      BlockPos pos,
      BlockPos neighborPos) {
    if (state.getValue(WATERLOGGED)) {
      world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
    }
    return this.getActualState(state, world, pos);
  }

  public BlockState getActualState(BlockState state, BlockGetter world, BlockPos pos) {
    boolean isPole = true;
    boolean magnetizerConnected = false;
    BlockState ret = state;

    for (Direction facing : Util.HORIZONTAL_DIRS) {
      BlockPos neighborPos = pos.relative(facing);
      BlockState neighborState = world.getBlockState(neighborPos);
      boolean connects = false;

      if (this.isFence(neighborState)) {
        isPole = false;
        connects = true;
      } else if (this.connectsTo(
          neighborState,
          neighborState.isFaceSturdy(world, neighborPos, facing.getOpposite()),
          facing.getOpposite())) {
        connects = true;
      } else if (isPole && getMagnetizer(world, neighborPos, facing, neighborState) != null) {
        magnetizerConnected = true;
        connects = true;
      }

      ret = ret.setValue(connectProperties.get(facing), connects);
    }

    if (!isPole && magnetizerConnected) {
      ret = state;
      for (Direction facing : Util.HORIZONTAL_DIRS) {
        BlockPos neighborPos = pos.relative(facing);
        BlockState neighborState = world.getBlockState(neighborPos);
        boolean connects =
            this.isFence(neighborState)
                || this.connectsTo(
                    neighborState,
                    neighborState.isFaceSturdy(world, neighborPos, facing.getOpposite()),
                    facing.getOpposite());
        ret = ret.setValue(connectProperties.get(facing), connects);
      }
    }

    return ret;
  }

  private boolean isFence(BlockState state) {
    return state.is(BlockTags.FENCES) && !state.is(BlockTags.WOODEN_FENCES);
  }

  private boolean isIc2Fence(BlockState state) {
    return state.getBlock() instanceof Ic2FenceBlock;
  }

  private boolean handleMagnetizerBoost(Level world, BlockPos pos, Player player) {
    if (world.isClientSide
        || player.isSpectator()
        || (player.isCreative() && player.getAbilities().flying)
        || !hasMetalShoes(player)) {
      return false;
    }

    List<TileEntityMagnetizer> connectedMagnetizers = this.getMagnetizers(world, pos);
    if (connectedMagnetizers.isEmpty()) {
      return false;
    }

    long tick = world.getGameTime();
    if (tick == lastBoostTick.getOrDefault(player, Long.MIN_VALUE)) {
      return true;
    }

    List<TileEntityMagnetizer> poweredMagnetizers = new ArrayList<>();
    for (TileEntityMagnetizer magnetizer : connectedMagnetizers) {
      if (magnetizer.canBoost()) {
        poweredMagnetizers.add(magnetizer);
      }
    }
    if (poweredMagnetizers.isEmpty()) {
      return false;
    }

    lastBoostTick.put(player, tick);
    double multiplier = 1.0 / poweredMagnetizers.size();
    for (TileEntityMagnetizer magnetizer : poweredMagnetizers) {
      magnetizer.boost(multiplier);
    }

    Vec3 velocity = player.getDeltaMovement();
    if (player.isShiftKeyDown()) {
      // Sneaking lets the player descend while the fence continues to dampen the fall.
      player.setDeltaMovement(velocity.x, velocity.y < 0.0 ? velocity.y * 0.8 : -0.08, velocity.z);
    } else {
      double maxSpeed = IC2.keyboard.isAltKeyDown(player) ? 0.1 : MAX_LIFT_SPEED;
      // Apply a small lift impulse every tick. Vanilla gravity removes most of it, so the
      // remaining upward speed builds gradually over several seconds instead of jumping
      // immediately to a high elevator speed.
      double liftSpeed = Math.min(velocity.y + NORMAL_LIFT_ACCELERATION, maxSpeed);
      player.setDeltaMovement(velocity.x, liftSpeed, velocity.z);
      player.fallDistance = 0.0F;
      player.setOnGround(false);
      player.hasImpulse = true;
    }

    // A ServerPlayer's own client controls its local movement prediction. Explicitly send the
    // updated velocity so the client does not immediately replace the server-side lift with its
    // pre-boost velocity from the next movement packet. Other tracking clients continue to receive
    // the normal entity motion/position updates from the server.
    if (player instanceof ServerPlayer serverPlayer) {
      serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(serverPlayer));
    }

    for (TileEntityMagnetizer magnetizer : poweredMagnetizers) {
      IC2.network.get(true).updateTileEntityField(magnetizer, "energy");
    }
    return true;
  }

  private List<TileEntityMagnetizer> getMagnetizers(BlockGetter world, BlockPos start) {
    List<TileEntityMagnetizer> magnetizers = new ArrayList<>();
    // Four upgrade slots can each hold a full stack of 64 augmentations.
    final int maxSearchDistance = 20 + 4 * 64;

    // Find machines attached to any segment of this uninterrupted vertical fence pole.
    for (Direction vertical : new Direction[] {Direction.DOWN, Direction.UP}) {
      for (int distance = 0; distance <= maxSearchDistance; distance++) {
        BlockPos fencePos = start.relative(vertical, distance);
        BlockState fenceState = world.getBlockState(fencePos);
        if (!(fenceState.getBlock() instanceof Ic2FenceBlock fence) || !fence.canBoost) {
          break;
        }

        for (Direction side : Util.HORIZONTAL_DIRS) {
          BlockPos neighborPos = fencePos.relative(side);
          BlockState neighborState = world.getBlockState(neighborPos);
          if (this.isIc2Fence(neighborState)) {
            continue;
          }

          TileEntityMagnetizer magnetizer = getMagnetizer(world, neighborPos, side, neighborState);
          if (magnetizer != null
              && distance <= magnetizer.getDistance()
              && !magnetizers.contains(magnetizer)) {
            magnetizers.add(magnetizer);
          }
        }
      }
    }

    return magnetizers;
  }
}
