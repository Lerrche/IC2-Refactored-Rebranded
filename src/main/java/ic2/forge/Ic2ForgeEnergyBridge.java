package ic2.forge;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.info.ILocatable;
import ic2.core.block.tileentity.Ic2TileEntity;
import ic2.core.block.wiring.AbstractCableBlock;
import ic2.core.energy.EnergyBridgeMath;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/** Bridges IC2's EU network directly to external FE and Mekanism energy receivers. */
public final class Ic2ForgeEnergyBridge {
  private static final Map<Level, Map<BlockPos, ForgeEnergySink>> sinks = new HashMap<>();
  /** Positions whose block entities/capabilities may not be ready until the next server tick. */
  private static final Map<Level, Set<BlockPos>> pendingPositions = new HashMap<>();
  private static final int EXTERNAL_SINK_TIER = 4;

  private Ic2ForgeEnergyBridge() {}

  private static boolean isAe2Block(Level world, BlockPos pos) {
    ResourceLocation id = BuiltInRegistries.BLOCK.getKey(world.getBlockState(pos).getBlock());
    return id != null && "ae2".equals(id.getNamespace());
  }

  private static boolean isExternalEnergyTarget(Level world, BlockPos pos) {
    if (!world.hasChunkAt(pos)) {
      return false;
    }
    BlockEntity blockEntity = world.getBlockEntity(pos);
    return !(blockEntity instanceof Ic2TileEntity)
        && !(blockEntity instanceof IEnergyTile)
        && !(world.getBlockState(pos).getBlock() instanceof AbstractCableBlock)
        && !isAe2Block(world, pos);
  }

  /**
   * Returns the faces of the target block that are physically touching IC2 cables. The direction
   * is from the target block toward the cable, matching IC2's sink-side direction convention.
   */
  private static EnumSet<Direction> getConnectedCableSides(Level world, BlockPos targetPos) {
    EnumSet<Direction> connectedSides = EnumSet.noneOf(Direction.class);
    for (Direction side : Direction.values()) {
      BlockPos neighborPos = targetPos.relative(side);
      if (world.hasChunkAt(neighborPos)
          && world.getBlockState(neighborPos).getBlock() instanceof AbstractCableBlock) {
        connectedSides.add(side);
      }
    }
    return connectedSides;
  }

  private static boolean hasAdjacentIc2Cable(Level world, BlockPos pos) {
    return !getConnectedCableSides(world, pos).isEmpty();
  }

  /** Performs the one-time registration check only on faces touching IC2 cables. */
  private static boolean hasReceivingCapability(
      Level world, BlockPos pos, EnumSet<Direction> connectedSides) {
    if (!isExternalEnergyTarget(world, pos)) {
      return false;
    }
    for (Direction side : connectedSides) {
      IEnergyStorage storage =
          world.getCapability(Capabilities.EnergyStorage.BLOCK, pos, side);
      if (storage != null && storage.canReceive()) {
        return true;
      }
      Object handler = MekanismEnergyCompat.getHandler(world, pos, side);
      if (MekanismEnergyCompat.canReceiveFrom(handler, side)) {
        return true;
      }
    }
    return false;
  }

  private static void queuePosition(Level world, BlockPos pos) {
    pendingPositions.computeIfAbsent(world, level -> new HashSet<>()).add(pos.immutable());
  }

  /** Called directly by an IC2 cable when it is placed or loaded. */
  public static void onCableAdded(Level world, BlockPos cablePos) {
    if (world == null || world.isClientSide || EnergyNet.instance == null) {
      return;
    }
    for (Direction direction : Direction.values()) {
      BlockPos targetPos = cablePos.relative(direction).immutable();
      registerSink(world, targetPos);
      // A block entity can be created after the cable's onLoad callback has run.
      if (!isSinkRegistered(world, targetPos)) {
        queuePosition(world, targetPos);
      }
    }
  }

  /** Deferred cleanup is limited to the six neighbors of the removed cable. */
  public static void onCableRemoved(Level world, BlockPos cablePos) {
    if (world == null || world.isClientSide) {
      return;
    }
    for (Direction direction : Direction.values()) {
      queuePosition(world, cablePos.relative(direction));
    }
  }

  private static boolean isSinkRegistered(Level world, BlockPos pos) {
    Map<BlockPos, ForgeEnergySink> worldSinks = sinks.get(world);
    return worldSinks != null && worldSinks.containsKey(pos);
  }

  private static void registerSink(Level world, BlockPos pos) {
    if (world.isClientSide || EnergyNet.instance == null || !world.hasChunkAt(pos)) {
      return;
    }

    EnumSet<Direction> connectedSides = getConnectedCableSides(world, pos);
    if (connectedSides.isEmpty() || !isExternalEnergyTarget(world, pos)) {
      unregisterSink(world, pos);
      return;
    }

    Map<BlockPos, ForgeEnergySink> worldSinks =
        sinks.computeIfAbsent(world, level -> new HashMap<>());
    ForgeEnergySink existing = worldSinks.get(pos);
    if (existing != null) {
      existing.setConnectedSides(connectedSides);
      return;
    }
    if (!hasReceivingCapability(world, pos, connectedSides)) {
      return;
    }

    // Capability discovery happens only when a sink is first registered, and only on faces
    // physically touching IC2 cables. The live sink then refreshes those cached faces once per
    // tick so side-configuration changes are picked up without rescanning unrelated faces.
    ForgeEnergySink sink = new ForgeEnergySink(world, pos.immutable(), connectedSides);
    worldSinks.put(pos.immutable(), sink);
    EnergyNet.instance.addLocatableTile(sink);
  }

  private static void unregisterSink(Level world, BlockPos pos) {
    if (world.isClientSide || EnergyNet.instance == null) {
      return;
    }
    Map<BlockPos, ForgeEnergySink> worldSinks = sinks.get(world);
    if (worldSinks == null) {
      return;
    }
    ForgeEnergySink sink = worldSinks.remove(pos);
    if (sink != null) {
      EnergyNet.instance.removeTile(sink);
    }
    if (worldSinks.isEmpty()) {
      sinks.remove(world);
    }
  }

  private static void processPendingPositions(ServerLevel world) {
    Set<BlockPos> queued = pendingPositions.remove(world);
    if (queued == null || queued.isEmpty()) {
      return;
    }
    for (BlockPos pos : queued) {
      if (world.hasChunkAt(pos)) {
        registerSink(world, pos);
      }
    }
  }

  private static void unregisterChunk(Level world, ChunkPos chunkPos) {
    Map<BlockPos, ForgeEnergySink> worldSinks = sinks.get(world);
    if (worldSinks == null || EnergyNet.instance == null) {
      return;
    }
    Iterator<Map.Entry<BlockPos, ForgeEnergySink>> it = worldSinks.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<BlockPos, ForgeEnergySink> entry = it.next();
      BlockPos pos = entry.getKey();
      if ((pos.getX() >> 4) == chunkPos.x && (pos.getZ() >> 4) == chunkPos.z) {
        EnergyNet.instance.removeTile(entry.getValue());
        it.remove();
      }
    }
    if (worldSinks.isEmpty()) {
      sinks.remove(world);
    }
  }

  private static void unregisterWorld(Level world) {
    Map<BlockPos, ForgeEnergySink> worldSinks = sinks.remove(world);
    pendingPositions.remove(world);
    if (worldSinks == null || EnergyNet.instance == null) {
      return;
    }
    for (ForgeEnergySink sink : worldSinks.values()) {
      EnergyNet.instance.removeTile(sink);
    }
  }

  public static final class EventHandler {
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
      if (!(event.getLevel() instanceof Level world) || world.isClientSide) {
        return;
      }
      BlockPos placedPos = event.getPos();
      if (world.getBlockState(placedPos).getBlock() instanceof AbstractCableBlock) {
        onCableAdded(world, placedPos);
      } else if (hasAdjacentIc2Cable(world, placedPos)) {
        registerSink(world, placedPos);
        if (!isSinkRegistered(world, placedPos)) {
          queuePosition(world, placedPos);
        }
      }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
      if (event.getLevel() instanceof Level world && !world.isClientSide) {
        unregisterSink(world, event.getPos());
      }
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
      if (event.getChunk() instanceof LevelChunk chunk && !chunk.getLevel().isClientSide) {
        unregisterChunk(chunk.getLevel(), chunk.getPos());
      }
    }

    @SubscribeEvent
    public void onWorldTick(LevelTickEvent.Post event) {
      if (event.getLevel() instanceof ServerLevel world) {
        processPendingPositions(world);
      }
    }

    @SubscribeEvent
    public void onWorldUnload(LevelEvent.Unload event) {
      if (event.getLevel() instanceof Level world && !world.isClientSide) {
        unregisterWorld(world);
      }
    }
  }

  private static long euToMekanismJoules(double amount) {
    if (!Double.isFinite(amount) || amount <= 0.0) {
      return 0L;
    }
    double joules = amount * MekanismEnergyCompat.JOULES_PER_EU;
    if (!Double.isFinite(joules) || joules >= (double) Long.MAX_VALUE) {
      return Long.MAX_VALUE;
    }
    return (long) Math.floor(joules);
  }

  private static final class ForgeEnergySink implements ILocatable, IEnergySink {
    private final Level world;
    private final BlockPos pos;
    private EnumSet<Direction> connectedSides;

    // Capabilities are refreshed once per world tick and only for faces that touch IC2 cables.
    // They are deliberately not cached across ticks, so Mekanism side-configuration changes and
    // NeoForge capability invalidations are picked up promptly.
    private long capabilityCacheTick = Long.MIN_VALUE;
    private final Map<Direction, Object> mekanismHandlers = new EnumMap<>(Direction.class);
    private final Map<Direction, IEnergyStorage> receivingStorages =
        new EnumMap<>(Direction.class);

    private ForgeEnergySink(Level world, BlockPos pos, EnumSet<Direction> connectedSides) {
      this.world = world;
      this.pos = pos.immutable();
      this.connectedSides = EnumSet.copyOf(connectedSides);
    }

    private void setConnectedSides(EnumSet<Direction> sides) {
      if (!connectedSides.equals(sides)) {
        connectedSides = EnumSet.copyOf(sides);
        invalidateCapabilityCache();
      }
    }

    private void invalidateCapabilityCache() {
      capabilityCacheTick = Long.MIN_VALUE;
      mekanismHandlers.clear();
      receivingStorages.clear();
    }

    private void refreshCapabilities() {
      long gameTime = world.getGameTime();
      if (capabilityCacheTick == gameTime) {
        return;
      }
      capabilityCacheTick = gameTime;
      mekanismHandlers.clear();
      receivingStorages.clear();

      if (!isExternalEnergyTarget(world, pos)) {
        return;
      }

      for (Direction side : connectedSides) {
        Object handler = MekanismEnergyCompat.getHandler(world, pos, side);
        if (handler != null) {
          mekanismHandlers.put(side, handler);
        }

        IEnergyStorage storage =
            world.getCapability(Capabilities.EnergyStorage.BLOCK, pos, side);
        if (storage != null) {
          receivingStorages.put(side, storage);
        }
      }
    }

    private Object getMekanismHandler(Direction side) {
      refreshCapabilities();
      return mekanismHandlers.get(side);
    }

    private IEnergyStorage getReceivingStorage(Direction side) {
      refreshCapabilities();
      IEnergyStorage storage = receivingStorages.get(side);
      return storage != null && storage.canReceive() ? storage : null;
    }

    private boolean canReceiveFrom(Direction side) {
      if (!connectedSides.contains(side)) {
        return false;
      }
      if (getReceivingStorage(side) != null) {
        return true;
      }
      return MekanismEnergyCompat.canReceiveFrom(getMekanismHandler(side), side);
    }

    @Override
    public boolean acceptsEnergyFrom(IEnergyEmitter emitter, Direction direction) {
      // The cable-side topology is stable independently of a machine's current input-side
      // configuration. Actual capability acceptance is checked again when energy is injected.
      // This also means changing a Mekanism side from output/none to input does not require
      // breaking and replacing the IC2 cable.
      return direction == null ? !connectedSides.isEmpty() : connectedSides.contains(direction);
    }

    @Override
    public double getDemandedEnergy() {
      refreshCapabilities();

      double feDemand = 0.0;
      long mekanismDemand = 0L;
      for (Direction side : connectedSides) {
        IEnergyStorage storage = getReceivingStorage(side);
        if (storage != null) {
          long freeFe =
              Math.max(0L, (long) storage.getMaxEnergyStored() - storage.getEnergyStored());
          feDemand = Math.max(feDemand, EnergyBridgeMath.feToEu(freeFe));
        }

        Object handler = getMekanismHandler(side);
        if (MekanismEnergyCompat.canReceiveFrom(handler, side)) {
          mekanismDemand =
              Math.max(
                  mekanismDemand,
                  MekanismEnergyCompat.getDemandedJoules(handler, side));
        }
      }

      // Mekanism's FE wrapper is preferred when it is usable because it applies Mekanism's own
      // FE/Joule conversion. The native strict-energy handler remains a fallback.
      if (feDemand > 0.0) {
        return feDemand;
      }
      return mekanismDemand / MekanismEnergyCompat.JOULES_PER_EU;
    }

    @Override
    public int getSinkTier() {
      return EXTERNAL_SINK_TIER;
    }

    @Override
    public double injectEnergy(Direction directionFrom, double amount, double voltage) {
      if (amount <= 0.0) {
        return Math.max(0.0, amount);
      }

      refreshCapabilities();
      if (directionFrom != null) {
        return connectedSides.contains(directionFrom)
            ? injectFromSide(directionFrom, amount)
            : amount;
      }

      // Direction can be unknown for some IC2 paths. In that case, try only faces actually
      // connected to IC2 cables instead of probing all six block faces.
      double remaining = amount;
      for (Direction side : connectedSides) {
        remaining = injectFromSide(side, remaining);
        if (remaining <= 0.0) {
          return 0.0;
        }
      }
      return remaining;
    }

    private double injectFromSide(Direction side, double amount) {
      if (amount <= 0.0) {
        return 0.0;
      }

      // First use the public FE capability. Mekanism's FE wrapper performs its own correct
      // conversion and respects the machine's configured input side.
      IEnergyStorage storage = getReceivingStorage(side);
      if (storage != null) {
        long availableFe = EnergyBridgeMath.euToFeFloor(amount);
        long freeFe =
            Math.max(0L, (long) storage.getMaxEnergyStored() - storage.getEnergyStored());
        int toSend = EnergyBridgeMath.clampToIntEnergy(Math.min(availableFe, freeFe));
        if (toSend > 0) {
          int acceptedFe = Math.min(toSend, Math.max(0, storage.receiveEnergy(toSend, false)));
          if (acceptedFe > 0) {
            double acceptedEu = Math.min(amount, EnergyBridgeMath.feToEu(acceptedFe));
            return Math.max(0.0, amount - acceptedEu);
          }
        }
      }

      // If FE is unavailable or declined the packet, use Mekanism's native Joule capability.
      Object handler = getMekanismHandler(side);
      if (!MekanismEnergyCompat.canReceiveFrom(handler, side)) {
        return amount;
      }
      long offeredJoules = euToMekanismJoules(amount);
      if (offeredJoules <= 0L) {
        return amount;
      }
      long acceptedJoules = MekanismEnergyCompat.insertJoules(handler, offeredJoules, side);
      double acceptedEu =
          Math.min(amount, acceptedJoules / MekanismEnergyCompat.JOULES_PER_EU);
      return Math.max(0.0, amount - acceptedEu);
    }

    @Override
    public Level getWorldObj() {
      return world;
    }

    @Override
    public BlockPos getPosition() {
      return pos;
    }
  }
}
