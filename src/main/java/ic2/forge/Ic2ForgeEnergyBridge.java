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
  private static final int MAX_PENDING_POSITIONS_PER_TICK = 64;

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
   * Checks whether an adjacent position contains an IC2 emitter registered in the energy net.
   * getSubTile also handles emitters that are sub-tiles of a multiblock, such as reactor chambers.
   */
  private static boolean isIc2EmitterAt(Level world, BlockPos pos) {
    if (EnergyNet.instance == null || !world.hasChunkAt(pos)) {
      return false;
    }

    IEnergyTile subTile = EnergyNet.instance.getSubTile(world, pos);
    if (subTile instanceof IEnergyEmitter) {
      return true;
    }

    return EnergyNet.instance.getTile(world, pos) instanceof IEnergyEmitter;
  }

  /**
   * Returns faces touching either an IC2 cable or an IC2 energy emitter. The direction is from the
   * external target toward the IC2 energy source, matching IC2's sink-side convention.
   */
  private static EnumSet<Direction> getConnectedEnergySides(Level world, BlockPos targetPos) {
    EnumSet<Direction> connectedSides = EnumSet.noneOf(Direction.class);
    for (Direction side : Direction.values()) {
      BlockPos neighborPos = targetPos.relative(side);
      if (!world.hasChunkAt(neighborPos)) {
        continue;
      }

      if (world.getBlockState(neighborPos).getBlock() instanceof AbstractCableBlock
          || isIc2EmitterAt(world, neighborPos)) {
        connectedSides.add(side);
      }
    }
    return connectedSides;
  }

  private static boolean hasAdjacentIc2EnergySource(Level world, BlockPos pos) {
    return !getConnectedEnergySides(world, pos).isEmpty();
  }

  private static void queueAdjacentPositions(Level world, BlockPos pos) {
    for (Direction direction : Direction.values()) {
      queuePosition(world, pos.relative(direction));
    }
  }

  /**
   * Queues only loaded neighbors when a source disappears; unloaded targets cannot have live sinks.
   */
  private static void queueAdjacentLoadedPositions(Level world, BlockPos pos) {
    Set<BlockPos> queued = pendingPositions.get(world);
    for (Direction direction : Direction.values()) {
      BlockPos neighborPos = pos.relative(direction).immutable();
      if (world.hasChunkAt(neighborPos)) {
        queuePosition(world, neighborPos);
      } else if (queued != null) {
        // The source is gone, so an unloaded target no longer needs a deferred registration.
        queued.remove(neighborPos);
      }
    }
    if (queued != null && queued.isEmpty()) {
      pendingPositions.remove(world);
    }
  }

  /** Performs the one-time registration check only on faces touching IC2 energy sources. */
  private static boolean hasReceivingCapability(
      Level world, BlockPos pos, EnumSet<Direction> connectedSides) {
    if (!isExternalEnergyTarget(world, pos)) {
      return false;
    }
    for (Direction side : connectedSides) {
      IEnergyStorage storage = world.getCapability(Capabilities.EnergyStorage.BLOCK, pos, side);
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
    queueAdjacentLoadedPositions(world, cablePos);
  }

  private static boolean isSinkRegistered(Level world, BlockPos pos) {
    Map<BlockPos, ForgeEnergySink> worldSinks = sinks.get(world);
    return worldSinks != null && worldSinks.containsKey(pos);
  }

  private static void registerSink(Level world, BlockPos pos) {
    if (world.isClientSide || EnergyNet.instance == null || !world.hasChunkAt(pos)) {
      return;
    }

    EnumSet<Direction> connectedSides = getConnectedEnergySides(world, pos);
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
    // physically touching IC2 sources. The live sink then refreshes those cached faces once per
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
    Set<BlockPos> queued = pendingPositions.get(world);
    if (queued == null || queued.isEmpty()) {
      return;
    }

    // A chunk can contain many IC2 emitters (or cables). Process only a small number of
    // adjacent positions per tick so loading a large base cannot stall the world join.
    Iterator<BlockPos> iterator = queued.iterator();
    int processed = 0;
    while (iterator.hasNext() && processed < MAX_PENDING_POSITIONS_PER_TICK) {
      BlockPos pos = iterator.next();
      // Keep positions in unloaded chunks queued. The source chunk may load before the
      // receiving machine's chunk; its pending registration must survive until that chunk loads.
      if (!world.hasChunkAt(pos)) {
        continue;
      }

      iterator.remove();
      registerSink(world, pos);
      processed++;
    }

    if (queued.isEmpty()) {
      pendingPositions.remove(world);
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
        return;
      }

      // Register an external machine placed next to an existing IC2 cable or energy emitter.
      if (hasAdjacentIc2EnergySource(world, placedPos)) {
        registerSink(world, placedPos);
        if (!isSinkRegistered(world, placedPos)) {
          queuePosition(world, placedPos);
        }
      }

      // IC2 generators add themselves to the energy net during their load callback. Defer the
      // adjacent sink checks until the next server tick so that registration has completed.
      BlockEntity placedEntity = world.getBlockEntity(placedPos);
      if (placedEntity instanceof IEnergyEmitter
          || placedEntity instanceof Ic2TileEntity
          || placedEntity instanceof IEnergyTile) {
        // Queue IC2 tile positions even if their EnergyNet registration completes after this event.
        queueAdjacentPositions(world, placedPos);
      }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
      if (event.getLevel() instanceof Level world && !world.isClientSide) {
        BlockPos brokenPos = event.getPos();
        BlockEntity blockEntity = world.getBlockEntity(brokenPos);
        boolean wasEnergySource =
            blockEntity instanceof IEnergyEmitter
                || (blockEntity instanceof Ic2TileEntity && isIc2EmitterAt(world, brokenPos));

        unregisterSink(world, brokenPos);
        // Only a removed IC2 energy source can invalidate neighboring external sinks.
        if (wasEnergySource) {
          queueAdjacentLoadedPositions(world, brokenPos);
        }
      }
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
      if (event.getChunk() instanceof LevelChunk chunk && !chunk.getLevel().isClientSide) {
        Level world = chunk.getLevel();
        for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
          // Do not inspect every third-party machine in the chunk. Only IC2 energy tiles can
          // create new direct-adjacency registrations; their six neighbors are checked later.
          if (blockEntity instanceof IEnergyEmitter) {
            queueAdjacentPositions(world, blockEntity.getBlockPos());
          } else if (blockEntity instanceof Ic2TileEntity
              && isIc2EmitterAt(world, blockEntity.getBlockPos())) {
            queueAdjacentPositions(world, blockEntity.getBlockPos());
          }
        }
      }
    }

    @SubscribeEvent
    public void onChunkUnload(ChunkEvent.Unload event) {
      if (event.getChunk() instanceof LevelChunk chunk && !chunk.getLevel().isClientSide) {
        Level world = chunk.getLevel();
        for (BlockEntity blockEntity : chunk.getBlockEntities().values()) {
          if (blockEntity instanceof IEnergyEmitter) {
            queueAdjacentLoadedPositions(world, blockEntity.getBlockPos());
          } else if (blockEntity instanceof Ic2TileEntity
              && isIc2EmitterAt(world, blockEntity.getBlockPos())) {
            queueAdjacentLoadedPositions(world, blockEntity.getBlockPos());
          }
        }
        unregisterChunk(world, chunk.getPos());
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

    // Capabilities are refreshed once per world tick and only for faces touching IC2 sources.
    // They are deliberately not cached across ticks, so Mekanism side-configuration changes and
    // NeoForge capability invalidations are picked up promptly.
    private long capabilityCacheTick = Long.MIN_VALUE;
    private final Map<Direction, Object> mekanismHandlers = new EnumMap<>(Direction.class);
    private final Map<Direction, IEnergyStorage> receivingStorages = new EnumMap<>(Direction.class);

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

        IEnergyStorage storage = world.getCapability(Capabilities.EnergyStorage.BLOCK, pos, side);
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
      // The IC2-side topology is stable independently of a machine's current input-side
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
              Math.max(mekanismDemand, MekanismEnergyCompat.getDemandedJoules(handler, side));
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
      // connected to IC2 sources instead of probing all six block faces.
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
        long freeFe = Math.max(0L, (long) storage.getMaxEnergyStored() - storage.getEnergyStored());
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
      double acceptedEu = Math.min(amount, acceptedJoules / MekanismEnergyCompat.JOULES_PER_EU);
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
