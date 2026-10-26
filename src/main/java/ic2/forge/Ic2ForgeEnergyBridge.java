package ic2.forge;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.info.ILocatable;
import ic2.core.block.tileentity.Ic2TileEntity;
import ic2.core.block.wiring.AbstractCableBlock;
import ic2.core.energy.EnergyBridgeMath;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
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

/** Bridges IC2's EU network to receiving NeoForge Energy Storage capabilities. */
public final class Ic2ForgeEnergyBridge {
  private static final Map<Level, Map<BlockPos, ForgeEnergySink>> sinks = new HashMap<>();
  private static final Map<Level, Queue<ChunkPos>> pendingChunks = new HashMap<>();
  private static final int CHUNKS_PER_TICK = 32;
  private static final int EXTERNAL_SINK_TIER = 4;

  private Ic2ForgeEnergyBridge() {}

  private static boolean isAe2Block(Level world, BlockPos pos) {
    ResourceLocation id = BuiltInRegistries.BLOCK.getKey(world.getBlockState(pos).getBlock());
    return id != null && "ae2".equals(id.getNamespace());
  }

  private static IEnergyStorage getReceivingStorage(Level world, BlockPos pos, Direction side) {
    if (!world.hasChunkAt(pos)) {
      return null;
    }
    BlockEntity blockEntity = world.getBlockEntity(pos);
    if ((blockEntity instanceof Ic2TileEntity)
        || (blockEntity instanceof IEnergyTile)
        || world.getBlockState(pos).getBlock() instanceof AbstractCableBlock
        || isAe2Block(world, pos)) {
      return null;
    }

    IEnergyStorage storage = world.getCapability(Capabilities.EnergyStorage.BLOCK, pos, side);
    return storage != null && storage.canReceive() ? storage : null;
  }

  private static IEnergyStorage getAnyReceivingStorage(Level world, BlockPos pos) {
    if (!world.hasChunkAt(pos)) {
      return null;
    }
    BlockEntity blockEntity = world.getBlockEntity(pos);
    if ((blockEntity instanceof Ic2TileEntity)
        || (blockEntity instanceof IEnergyTile)
        || world.getBlockState(pos).getBlock() instanceof AbstractCableBlock
        || isAe2Block(world, pos)) {
      return null;
    }
    IEnergyStorage storage = world.getCapability(Capabilities.EnergyStorage.BLOCK, pos, null);
    if (storage != null && storage.canReceive()) {
      return storage;
    }
    for (Direction side : Direction.values()) {
      storage = getReceivingStorage(world, pos, side);
      if (storage != null) {
        return storage;
      }
    }
    return null;
  }

  private static boolean hasReceivingStorage(Level world, BlockPos pos) {
    if (!world.hasChunkAt(pos)) {
      return false;
    }
    BlockEntity blockEntity = world.getBlockEntity(pos);
    return !(blockEntity instanceof Ic2TileEntity)
        && !(blockEntity instanceof IEnergyTile)
        && !(world.getBlockState(pos).getBlock() instanceof AbstractCableBlock)
        && !isAe2Block(world, pos)
        && getAnyReceivingStorage(world, pos) != null;
  }

  private static void registerSink(Level world, BlockPos pos) {
    if (world.isClientSide || EnergyNet.instance == null) {
      return;
    }
    Map<BlockPos, ForgeEnergySink> worldSinks = sinks.get(world);
    if (worldSinks != null && worldSinks.containsKey(pos)) {
      return;
    }
    if (!hasReceivingStorage(world, pos)) {
      return;
    }
    if (worldSinks == null) {
      worldSinks = sinks.computeIfAbsent(world, level -> new HashMap<>());
    }
    ForgeEnergySink sink = new ForgeEnergySink(world, pos);
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

  private static void processPendingChunks(ServerLevel world) {
    Queue<ChunkPos> queue = pendingChunks.get(world);
    if (queue == null || queue.isEmpty()) {
      return;
    }
    int processed = 0;
    while (processed < CHUNKS_PER_TICK && !queue.isEmpty()) {
      ChunkPos chunkPos = queue.poll();
      BlockPos checkPos = new BlockPos(chunkPos.x << 4, 0, chunkPos.z << 4);
      if (!world.isLoaded(checkPos)) {
        continue;
      }
      LevelChunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
      for (BlockPos pos : chunk.getBlockEntities().keySet()) {
        registerSink(world, pos);
        for (Direction direction : Direction.values()) {
          registerSink(world, pos.relative(direction));
        }
      }
      processed++;
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
    pendingChunks.remove(world);
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
      if (event.getEntity() == null) {
        return;
      }
      Level world = event.getEntity().level();
      if (!world.isClientSide) {
        registerSink(world, event.getPos());
      }
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
      unregisterSink((Level) event.getLevel(), event.getPos());
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkEvent.Load event) {
      if (event.getChunk() instanceof LevelChunk chunk && !chunk.getLevel().isClientSide) {
        Level world = chunk.getLevel();
        pendingChunks.computeIfAbsent(world, level -> new ArrayDeque<>()).add(chunk.getPos());
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
        processPendingChunks(world);
      }
    }

    @SubscribeEvent
    public void onWorldUnload(LevelEvent.Unload event) {
      if (event.getLevel() instanceof Level world && !world.isClientSide) {
        unregisterWorld(world);
      }
    }
  }

  private record ForgeEnergySink(Level world, BlockPos pos) implements ILocatable, IEnergySink {
    private IEnergyStorage getStorageFromSide(Direction side) {
      return getReceivingStorage(world, pos, side);
    }

    @Override
    public boolean acceptsEnergyFrom(IEnergyEmitter emitter, Direction direction) {
      return direction == null
          ? getAnyReceivingStorage(world, pos) != null
          : getStorageFromSide(direction) != null;
    }

    @Override
    public double getDemandedEnergy() {
      IEnergyStorage storage = getAnyReceivingStorage(world, pos);
      if (storage == null) {
        return 0.0;
      }
      long freeFe = Math.max(0L, (long) storage.getMaxEnergyStored() - storage.getEnergyStored());
      return EnergyBridgeMath.feToEu(freeFe);
    }

    @Override
    public int getSinkTier() {
      // FE does not define voltage tiers. Use a high but finite IC2 sink tier for the adapter.
      return EXTERNAL_SINK_TIER;
    }

    @Override
    public double injectEnergy(Direction directionFrom, double amount, double voltage) {
      if (amount <= 0.0) {
        return Math.max(0.0, amount);
      }
      IEnergyStorage storage =
          directionFrom == null
              ? getAnyReceivingStorage(world, pos)
              : getStorageFromSide(directionFrom);
      if (storage == null || !storage.canReceive()) {
        return amount;
      }

      long availableFe = EnergyBridgeMath.euToFeFloor(amount);
      long freeFe = Math.max(0L, (long) storage.getMaxEnergyStored() - storage.getEnergyStored());
      int toSend = EnergyBridgeMath.clampToIntEnergy(Math.min(availableFe, freeFe));
      if (toSend <= 0) {
        return amount;
      }

      int acceptedFe = Math.min(toSend, Math.max(0, storage.receiveEnergy(toSend, false)));
      double acceptedEu = Math.min(amount, EnergyBridgeMath.feToEu(acceptedFe));
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
