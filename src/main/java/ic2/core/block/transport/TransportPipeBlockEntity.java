package ic2.core.block.transport;

import ic2.api.transport.IFluidPipe;
import ic2.api.transport.IItemTransportTile;
import ic2.core.ref.Ic2BlockEntities;
import ic2.core.util.CauldronUtil;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TransportPipeBlockEntity extends BlockEntity
    implements IFluidPipe, IItemTransportTile {
  private final PipeType pipeType;
  private final PipeSize pipeSize;
  private final int fluidCapacity;
  private final int maxStackSize;

  private byte disconnectedMask = 0; // bit (1 << side.ordinal()) is 1 if disconnected by wrench
  private final ItemStack[] covers = new ItemStack[6];

  private final FluidTank fluidTank;
  private final ItemStackHandler itemHandler;

  public TransportPipeBlockEntity(
      PipeType type, PipeSize size, BlockPos pos, BlockState state) {
    super(Ic2BlockEntities.TRANSPORT_PIPE, pos, state);
    this.pipeType = type;
    this.pipeSize = size;
    this.fluidCapacity = Math.max(1000, (int) (type.transferRate * size.multiplier));
    this.maxStackSize = size.maxStackSize;

    Arrays.fill(this.covers, ItemStack.EMPTY);

    this.fluidTank =
        new FluidTank(this.fluidCapacity) {
          @Override
          protected void onContentsChanged() {
            setChanged();
          }
        };

    this.itemHandler =
        new ItemStackHandler(1) {
          @Override
          public int getSlotLimit(int slot) {
            return TransportPipeBlockEntity.this.maxStackSize;
          }

          @Override
          protected void onContentsChanged(int slot) {
            setChanged();
          }
        };
  }

  // Factory constructor for registry reflection / deserialization
  public TransportPipeBlockEntity(BlockPos pos, BlockState state) {
    this(
        state.getBlock() instanceof TransportPipeBlock tb ? tb.type : PipeType.BRONZE,
        state.getBlock() instanceof TransportPipeBlock tb ? tb.size : PipeSize.SMALL,
        pos,
        state);
  }

  public PipeType getPipeType() {
    return this.pipeType;
  }

  public PipeSize getPipeSize() {
    return this.pipeSize;
  }

  @Override
  public BlockEntity getBlockEntity() {
    return this;
  }

  @Override
  public boolean isConnected(Direction side) {
    if (side == null) {
      return false;
    }
    return isFaceConnected(side);
  }

  public boolean isFaceConnected(Direction side) {
    if ((this.disconnectedMask & (1 << side.ordinal())) != 0) {
      return false;
    }
    if (this.level == null) {
      return false;
    }

    BlockPos neighborPos = this.worldPosition.relative(side);
    BlockState neighborState = this.level.getBlockState(neighborPos);

    if (CauldronUtil.isCauldron(neighborState)) {
      return true;
    }

    BlockEntity neighborBe = this.level.getBlockEntity(neighborPos);

    if (neighborBe instanceof TransportPipeBlockEntity neighborPipe) {
      return (neighborPipe.disconnectedMask & (1 << side.getOpposite().ordinal())) == 0;
    }

    if (hasCover(side)) {
      return true;
    }

    // Check fluid or item capability from neighbor
    IFluidHandler fh =
        this.level.getCapability(Capabilities.FluidHandler.BLOCK, neighborPos, side.getOpposite());
    if (fh != null) {
      return true;
    }

    IItemHandler ih =
        this.level.getCapability(Capabilities.ItemHandler.BLOCK, neighborPos, side.getOpposite());
    return ih != null;
  }

  @Override
  public void flipConnection(Direction side) {
    this.disconnectedMask ^= (byte) (1 << side.ordinal());
    this.setChanged();

    if (this.level != null && !this.level.isClientSide) {
      BlockState state = this.getBlockState();
      boolean connected = isFaceConnected(side);
      BlockState updated =
          state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(side), connected);
      this.level.setBlock(this.worldPosition, updated, 3);
      this.level.sendBlockUpdated(this.worldPosition, state, updated, 3);

      // Also notify neighbor pipe so it updates its connection state
      BlockPos neighborPos = this.worldPosition.relative(side);
      BlockEntity neighborBe = this.level.getBlockEntity(neighborPos);
      if (neighborBe instanceof TransportPipeBlockEntity neighborPipe) {
        BlockState nState = neighborPipe.getBlockState();
        BlockState nUpdated =
            nState.setValue(
                PipeBlock.PROPERTY_BY_DIRECTION.get(side.getOpposite()),
                neighborPipe.isFaceConnected(side.getOpposite()));
        this.level.setBlock(neighborPos, nUpdated, 3);
        this.level.sendBlockUpdated(neighborPos, nState, nUpdated, 3);
      }
    }
  }

  public boolean hasCover(Direction side) {
    return !this.covers[side.ordinal()].isEmpty();
  }

  public ItemStack getCover(Direction side) {
    return this.covers[side.ordinal()];
  }

  public boolean installCover(Direction side, ItemStack cover) {
    if (cover.isEmpty()) {
      return false;
    }
    int idx = side.ordinal();
    if (!this.covers[idx].isEmpty()) {
      return false;
    }
    this.covers[idx] = cover;
    // ensure connection is not masked
    this.disconnectedMask &= (byte) ~(1 << idx);
    this.setChanged();

    if (this.level != null) {
      BlockState state = this.getBlockState();
      BlockState updated =
          state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(side), true);
      this.level.setBlock(this.worldPosition, updated, 3);
      this.level.sendBlockUpdated(this.worldPosition, state, updated, 3);
    }
    return true;
  }

  public ItemStack removeCover(Direction side) {
    int idx = side.ordinal();
    ItemStack existing = this.covers[idx];
    if (!existing.isEmpty()) {
      this.covers[idx] = ItemStack.EMPTY;
      this.setChanged();
      if (this.level != null) {
        BlockState state = this.getBlockState();
        BlockState updated =
            state.setValue(PipeBlock.PROPERTY_BY_DIRECTION.get(side), isFaceConnected(side));
        this.level.setBlock(this.worldPosition, updated, 3);
        this.level.sendBlockUpdated(this.worldPosition, state, updated, 3);
      }
      return existing;
    }
    return ItemStack.EMPTY;
  }

  public void dropInventoryAndCovers() {
    if (this.level == null || this.level.isClientSide) {
      return;
    }
    ItemStack items = this.itemHandler.getStackInSlot(0);
    if (!items.isEmpty()) {
      Block.popResource(this.level, this.worldPosition, items.copy());
      this.itemHandler.setStackInSlot(0, ItemStack.EMPTY);
    }
    for (int i = 0; i < 6; i++) {
      if (!this.covers[i].isEmpty()) {
        Block.popResource(this.level, this.worldPosition, this.covers[i].copy());
        this.covers[i] = ItemStack.EMPTY;
      }
    }
    this.setChanged();
  }

  // Capability getters
  public @Nullable IFluidHandler getFluidHandler(@Nullable Direction side) {
    if (side == null || isFaceConnected(side)) {
      return this.fluidTank;
    }
    return null;
  }

  public @Nullable IItemHandler getItemHandler(@Nullable Direction side) {
    if (side == null || isFaceConnected(side)) {
      return this.itemHandler;
    }
    return null;
  }

  // --- IFluidPipe ---
  @Override
  public int getTransferRate() {
    return this.pipeType.transferRate;
  }

  @Override
  public int getCurrentInnerCapacity() {
    return this.fluidTank.getFluidAmount();
  }

  @Override
  public int getMaxInnerCapacity() {
    return this.fluidCapacity;
  }

  @Override
  public IFluidHandler getFluidTank() {
    return this.fluidTank;
  }

  // --- IItemTransportTile ---
  @Override
  public int getMaxStackSizeAllowed() {
    return this.maxStackSize;
  }

  @Override
  public ItemStack getContents() {
    return this.itemHandler.getStackInSlot(0);
  }

  @Override
  public void setContents(ItemStack stack) {
    this.itemHandler.setStackInSlot(0, stack);
  }

  @Override
  public int putItems(ItemStack stack, Direction from, boolean simulate) {
    if (stack.isEmpty()) {
      return 0;
    }
    if (from != null && !isFaceConnected(from)) {
      return 0;
    }
    ItemStack remaining = this.itemHandler.insertItem(0, stack, simulate);
    return stack.getCount() - remaining.getCount();
  }

  // --- Network Routing Helpers ---
  private Direction lastReceivedItemFace = null;

  public static class FluidSinkTarget {
    public final BlockPos pos;
    public final Direction face;
    public final int distance;

    public FluidSinkTarget(BlockPos pos, Direction face, int distance) {
      this.pos = pos;
      this.face = face;
      this.distance = distance;
    }
  }

  public static class ItemSinkTarget {
    public final BlockPos pos;
    public final Direction face;
    public final int distance;

    public ItemSinkTarget(BlockPos pos, Direction face, int distance) {
      this.pos = pos;
      this.face = face;
      this.distance = distance;
    }
  }

  private List<FluidSinkTarget> findConnectedFluidSinks(Level level, BlockPos startPos) {
    List<FluidSinkTarget> sinks = new ArrayList<>();
    Set<BlockPos> visited = new HashSet<>();
    Queue<BlockPos> queue = new ArrayDeque<>();
    queue.add(startPos);
    visited.add(startPos);

    int maxPipes = 128;
    int count = 0;

    while (!queue.isEmpty() && count < maxPipes) {
      BlockPos curPos = queue.poll();
      count++;
      BlockEntity curBe = level.getBlockEntity(curPos);
      if (!(curBe instanceof TransportPipeBlockEntity curPipe)) {
        continue;
      }

      for (Direction dir : Direction.values()) {
        if (!curPipe.isFaceConnected(dir) || curPipe.hasCover(dir)) {
          continue;
        }

        BlockPos neighborPos = curPos.relative(dir);
        Direction neighborFace = dir.getOpposite();

        if (visited.contains(neighborPos)) {
          continue;
        }

        BlockEntity neighborBe = level.getBlockEntity(neighborPos);
        if (neighborBe instanceof TransportPipeBlockEntity nextPipe) {
          if (nextPipe.isFaceConnected(neighborFace)) {
            visited.add(neighborPos);
            queue.add(neighborPos);
          }
        } else {
          // External block - check fluid capability
          IFluidHandler fh =
              level.getCapability(Capabilities.FluidHandler.BLOCK, neighborPos, neighborFace);
          if (fh != null) {
            sinks.add(new FluidSinkTarget(neighborPos, neighborFace, count));
          }
        }
      }
    }
    return sinks;
  }

  private List<ItemSinkTarget> findConnectedItemSinks(Level level, BlockPos startPos) {
    List<ItemSinkTarget> sinks = new ArrayList<>();
    Set<BlockPos> visited = new HashSet<>();
    Queue<BlockPos> queue = new ArrayDeque<>();
    queue.add(startPos);
    visited.add(startPos);

    int maxPipes = 128;
    int count = 0;

    while (!queue.isEmpty() && count < maxPipes) {
      BlockPos curPos = queue.poll();
      count++;
      BlockEntity curBe = level.getBlockEntity(curPos);
      if (!(curBe instanceof TransportPipeBlockEntity curPipe)) {
        continue;
      }

      for (Direction dir : Direction.values()) {
        if (!curPipe.isFaceConnected(dir) || curPipe.hasCover(dir)) {
          continue;
        }

        BlockPos neighborPos = curPos.relative(dir);
        Direction neighborFace = dir.getOpposite();

        if (visited.contains(neighborPos)) {
          continue;
        }

        BlockEntity neighborBe = level.getBlockEntity(neighborPos);
        if (neighborBe instanceof TransportPipeBlockEntity nextPipe) {
          if (nextPipe.isFaceConnected(neighborFace)) {
            visited.add(neighborPos);
            queue.add(neighborPos);
          }
        } else {
          // External block - check item capability
          IItemHandler ih =
              level.getCapability(Capabilities.ItemHandler.BLOCK, neighborPos, neighborFace);
          if (ih != null) {
            sinks.add(new ItemSinkTarget(neighborPos, neighborFace, count));
          }
        }
      }
    }
    return sinks;
  }

  // --- Server Tick ---
  public void serverTick(Level level, BlockPos pos, BlockState state) {
    if (level == null || level.isClientSide) {
      return;
    }

    // 1. Process Pump covers
    for (Direction dir : Direction.values()) {
      ItemStack cover = this.covers[dir.ordinal()];
      if (cover.getItem() instanceof ItemPumpCover pumpCover) {
        PumpCoverTier tier = pumpCover.tier;
        BlockPos neighborPos = pos.relative(dir);
        Direction neighborFace = dir.getOpposite();

        // Pull Fluid from Cauldron (if adjacent)
        BlockState neighborState = level.getBlockState(neighborPos);
        if (CauldronUtil.isCauldron(neighborState)) {
          int space = this.fluidTank.getCapacity() - this.fluidTank.getFluidAmount();
          if (space > 0) {
            FluidStack drainedSim =
                CauldronUtil.drainCauldronNeo(level, neighborPos, neighborState, space, true);
            if (!drainedSim.isEmpty()
                && this.fluidTank.fill(drainedSim, IFluidHandler.FluidAction.SIMULATE) > 0) {
              FluidStack drainedExec =
                  CauldronUtil.drainCauldronNeo(level, neighborPos, neighborState, space, false);
              this.fluidTank.fill(drainedExec, IFluidHandler.FluidAction.EXECUTE);
            }
          }
        }

        // Pull Fluid from FluidHandler
        IFluidHandler neighborFluid =
            level.getCapability(Capabilities.FluidHandler.BLOCK, neighborPos, neighborFace);
        if (neighborFluid != null) {
          int space = this.fluidTank.getCapacity() - this.fluidTank.getFluidAmount();
          int toDrain = Math.min(tier.ratePerTick, space);
          if (toDrain > 0) {
            FluidStack drainedSim =
                neighborFluid.drain(toDrain, IFluidHandler.FluidAction.SIMULATE);
            if (!drainedSim.isEmpty()
                && this.fluidTank.fill(drainedSim, IFluidHandler.FluidAction.SIMULATE) > 0) {
              int accepted = this.fluidTank.fill(drainedSim, IFluidHandler.FluidAction.EXECUTE);
              neighborFluid.drain(accepted, IFluidHandler.FluidAction.EXECUTE);
            }
          }
        }

        // Pull Item (if empty)
        if (this.itemHandler.getStackInSlot(0).isEmpty()) {
          IItemHandler neighborItem =
              level.getCapability(Capabilities.ItemHandler.BLOCK, neighborPos, neighborFace);
          if (neighborItem != null) {
            for (int s = 0; s < neighborItem.getSlots(); s++) {
              ItemStack sim = neighborItem.extractItem(s, this.maxStackSize, true);
              if (!sim.isEmpty()) {
                ItemStack insertedSim = this.itemHandler.insertItem(0, sim, true);
                int acceptedCount = sim.getCount() - insertedSim.getCount();
                if (acceptedCount > 0) {
                  ItemStack extracted = neighborItem.extractItem(s, acceptedCount, false);
                  this.itemHandler.insertItem(0, extracted, false);
                  this.lastReceivedItemFace = dir;
                  break;
                }
              }
            }
          }
        }
      }
    }

    // 1b. Passive Cauldron extraction for connected pipes (even without a pump cover)
    for (Direction dir : Direction.values()) {
      if (!isFaceConnected(dir) || hasCover(dir)) {
        continue;
      }
      BlockPos neighborPos = pos.relative(dir);
      BlockState neighborState = level.getBlockState(neighborPos);
      if (CauldronUtil.isCauldron(neighborState) && CauldronUtil.hasDrainableFluid(neighborState)) {
        int space = this.fluidTank.getCapacity() - this.fluidTank.getFluidAmount();
        if (space > 0) {
          FluidStack drainedSim =
              CauldronUtil.drainCauldronNeo(level, neighborPos, neighborState, space, true);
          if (!drainedSim.isEmpty()
              && this.fluidTank.fill(drainedSim, IFluidHandler.FluidAction.SIMULATE) > 0) {
            FluidStack drainedExec =
                CauldronUtil.drainCauldronNeo(level, neighborPos, neighborState, space, false);
            this.fluidTank.fill(drainedExec, IFluidHandler.FluidAction.EXECUTE);
          }
        }
      }
    }

    // 2. Transfer Fluid along network / downstream
    if (this.fluidTank.getFluidAmount() > 0) {
      int transferLimit = this.pipeType.getTransferRatePerTick();
      int availableFluid = Math.min(this.fluidTank.getFluidAmount(), transferLimit);

      // 2a. First, route directly to network fluid sinks
      List<FluidSinkTarget> sinks = findConnectedFluidSinks(level, pos);
      if (!sinks.isEmpty() && availableFluid > 0) {
        for (FluidSinkTarget sinkTarget : sinks) {
          if (availableFluid <= 0) break;
          IFluidHandler sink =
              level.getCapability(Capabilities.FluidHandler.BLOCK, sinkTarget.pos, sinkTarget.face);
          if (sink == null) continue;

          FluidStack drainedSim = this.fluidTank.drain(availableFluid, IFluidHandler.FluidAction.SIMULATE);
          if (drainedSim.isEmpty()) break;

          int accepted = sink.fill(drainedSim, IFluidHandler.FluidAction.EXECUTE);
          if (accepted > 0) {
            this.fluidTank.drain(accepted, IFluidHandler.FluidAction.EXECUTE);
            availableFluid -= accepted;
          }
        }
      }

      // 2b. If fluid remains and sinks couldn't accept all of it, push to adjacent connected pipes with space
      if (availableFluid > 0 && this.fluidTank.getFluidAmount() > 0) {
        for (Direction dir : Direction.values()) {
          if (!isFaceConnected(dir) || hasCover(dir)) {
            continue;
          }
          BlockPos targetPos = pos.relative(dir);
          BlockEntity targetBe = level.getBlockEntity(targetPos);
          if (targetBe instanceof TransportPipeBlockEntity otherPipe
              && otherPipe.isFaceConnected(dir.getOpposite())) {
            int otherSpace = otherPipe.fluidTank.getCapacity() - otherPipe.fluidTank.getFluidAmount();
            if (otherSpace > 0) {
              int toMove = Math.min(availableFluid, otherSpace);
              FluidStack drained = this.fluidTank.drain(toMove, IFluidHandler.FluidAction.SIMULATE);
              if (!drained.isEmpty()) {
                int filled = otherPipe.fluidTank.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                if (filled > 0) {
                  this.fluidTank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                  availableFluid -= filled;
                  if (availableFluid <= 0) break;
                }
              }
            }
          }
        }
      }
    }

    // 3. Transfer Items along network / downstream
    ItemStack currentItem = this.itemHandler.getStackInSlot(0);
    if (!currentItem.isEmpty()) {
      // 3a. First, route to connected network item sinks
      List<ItemSinkTarget> sinks = findConnectedItemSinks(level, pos);
      if (!sinks.isEmpty()) {
        for (ItemSinkTarget sinkTarget : sinks) {
          IItemHandler sink =
              level.getCapability(Capabilities.ItemHandler.BLOCK, sinkTarget.pos, sinkTarget.face);
          if (sink == null) continue;

          for (int s = 0; s < sink.getSlots(); s++) {
            ItemStack remaining = sink.insertItem(s, currentItem, false);
            this.itemHandler.setStackInSlot(0, remaining);
            currentItem = remaining;
            if (currentItem.isEmpty()) break;
          }
          if (currentItem.isEmpty()) break;
        }
      }

      // 3b. If item remains, advance to adjacent connected pipe that is empty
      if (!currentItem.isEmpty()) {
        for (Direction dir : Direction.values()) {
          if (!isFaceConnected(dir) || hasCover(dir) || dir == this.lastReceivedItemFace) {
            continue;
          }
          BlockPos targetPos = pos.relative(dir);
          BlockEntity targetBe = level.getBlockEntity(targetPos);
          if (targetBe instanceof TransportPipeBlockEntity otherPipe
              && otherPipe.isFaceConnected(dir.getOpposite())) {
            if (otherPipe.itemHandler.getStackInSlot(0).isEmpty()) {
              ItemStack remaining = otherPipe.itemHandler.insertItem(0, currentItem, false);
              otherPipe.lastReceivedItemFace = dir.getOpposite();
              this.itemHandler.setStackInSlot(0, remaining);
              currentItem = remaining;
              if (currentItem.isEmpty()) break;
            }
          }
        }
      }
    } else {
      this.lastReceivedItemFace = null;
    }
  }

  // --- Serialization ---
  @Override
  protected void saveAdditional(
      @NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
    super.saveAdditional(tag, registries);
    tag.putByte("disconnectedMask", this.disconnectedMask);

    CompoundTag fluidTag = new CompoundTag();
    this.fluidTank.writeToNBT(registries, fluidTag);
    tag.put("fluid", fluidTag);

    CompoundTag itemTag = this.itemHandler.serializeNBT(registries);
    tag.put("inventory", itemTag);

    ListTag coversTag = new ListTag();
    for (int i = 0; i < 6; i++) {
      CompoundTag cTag = new CompoundTag();
      if (!this.covers[i].isEmpty()) {
        coversTag.add(this.covers[i].save(registries, cTag));
      } else {
        coversTag.add(cTag);
      }
    }
    tag.put("covers", coversTag);
  }

  @Override
  protected void loadAdditional(
      @NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries) {
    super.loadAdditional(tag, registries);
    if (tag.contains("disconnectedMask")) {
      this.disconnectedMask = tag.getByte("disconnectedMask");
    }
    if (tag.contains("fluid")) {
      this.fluidTank.readFromNBT(registries, tag.getCompound("fluid"));
    }
    if (tag.contains("inventory")) {
      this.itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
    }
    if (tag.contains("covers", Tag.TAG_LIST)) {
      ListTag coversTag = tag.getList("covers", Tag.TAG_COMPOUND);
      for (int i = 0; i < Math.min(6, coversTag.size()); i++) {
        CompoundTag cTag = coversTag.getCompound(i);
        if (!cTag.isEmpty()) {
          this.covers[i] = ItemStack.parseOptional(registries, cTag);
        } else {
          this.covers[i] = ItemStack.EMPTY;
        }
      }
    }
  }

  @Override
  public @NotNull CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries) {
    CompoundTag tag = super.getUpdateTag(registries);
    saveAdditional(tag, registries);
    return tag;
  }

  @Override
  public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
  }
}
