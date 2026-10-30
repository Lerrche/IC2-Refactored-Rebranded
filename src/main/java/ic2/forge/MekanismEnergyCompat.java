package ic2.forge;

import ic2.core.IC2;
import ic2.core.util.LogCategory;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.BlockCapability;

/** Optional, side-aware bridge to Mekanism's native Joule energy capability. */
public final class MekanismEnergyCompat {

  /** IC2-to-Mekanism conversion used by the native strict-energy fallback. */
  public static final double JOULES_PER_EU = 10.0;

  private static final BlockCapability STRICT_ENERGY_CAPABILITY;
  private static final Class<?> SIDED_HANDLER_API;
  private static final Method GET_ENERGY_CONTAINER_COUNT;
  private static final Method GET_NEEDED_ENERGY;
  private static final Method INSERT_ENERGY;
  private static final Method GET_SIDED_ENERGY_CONTAINER_COUNT;
  private static final Method GET_SIDED_NEEDED_ENERGY;
  private static final Method INSERT_SIDED_ENERGY;
  private static final Object ACTION_EXECUTE;
  private static final Throwable INITIALIZATION_FAILURE;
  private static final AtomicBoolean FAILURE_LOGGED = new AtomicBoolean();

  static {
    BlockCapability capability = null;
    Class<?> sidedHandlerApi = null;
    Method getEnergyContainerCount = null;
    Method getNeededEnergy = null;
    Method insertEnergy = null;
    Method getSidedEnergyContainerCount = null;
    Method getSidedNeededEnergy = null;
    Method insertSidedEnergy = null;
    Object actionExecute = null;
    Throwable initializationFailure = null;

    try {
      ClassLoader classLoader = MekanismEnergyCompat.class.getClassLoader();
      Class<?> mekanismCapabilities =
          Class.forName("mekanism.common.capabilities.Capabilities", false, classLoader);
      Object strictEnergy = mekanismCapabilities.getField("STRICT_ENERGY").get(null);
      capability =
          (BlockCapability) strictEnergy.getClass().getMethod("block").invoke(strictEnergy);

      Class<?> handlerApi =
          Class.forName("mekanism.api.energy.IStrictEnergyHandler", false, classLoader);
      sidedHandlerApi =
          Class.forName("mekanism.api.energy.ISidedStrictEnergyHandler", false, classLoader);
      Class<?> actionApi = Class.forName("mekanism.api.Action", false, classLoader);

      getEnergyContainerCount = handlerApi.getMethod("getEnergyContainerCount");
      getNeededEnergy = handlerApi.getMethod("getNeededEnergy", int.class);
      insertEnergy = handlerApi.getMethod("insertEnergy", long.class, actionApi);

      getSidedEnergyContainerCount =
          sidedHandlerApi.getMethod("getEnergyContainerCount", Direction.class);
      getSidedNeededEnergy =
          sidedHandlerApi.getMethod("getNeededEnergy", int.class, Direction.class);
      insertSidedEnergy =
          sidedHandlerApi.getMethod("insertEnergy", long.class, Direction.class, actionApi);
      actionExecute = actionApi.getField("EXECUTE").get(null);
    } catch (ReflectiveOperationException | LinkageError | RuntimeException error) {
      initializationFailure = error;
      capability = null;
      sidedHandlerApi = null;
      getEnergyContainerCount = null;
      getNeededEnergy = null;
      insertEnergy = null;
      getSidedEnergyContainerCount = null;
      getSidedNeededEnergy = null;
      insertSidedEnergy = null;
      actionExecute = null;
    }

    STRICT_ENERGY_CAPABILITY = capability;
    SIDED_HANDLER_API = sidedHandlerApi;
    GET_ENERGY_CONTAINER_COUNT = getEnergyContainerCount;
    GET_NEEDED_ENERGY = getNeededEnergy;
    INSERT_ENERGY = insertEnergy;
    GET_SIDED_ENERGY_CONTAINER_COUNT = getSidedEnergyContainerCount;
    GET_SIDED_NEEDED_ENERGY = getSidedNeededEnergy;
    INSERT_SIDED_ENERGY = insertSidedEnergy;
    ACTION_EXECUTE = actionExecute;
    INITIALIZATION_FAILURE = initializationFailure;
  }

  private MekanismEnergyCompat() {}

  private static void logFailure(Throwable error) {
    if (error != null
        && ModList.get().isLoaded("mekanism")
        && FAILURE_LOGGED.compareAndSet(false, true)) {
      IC2.log.warn(
          LogCategory.General,
          error,
          "Mekanism strict-energy integration could not initialize or invoke its API. "
              + "The IC2 bridge will continue using the FE capability when available.");
    }
  }

  public static boolean isAvailable() {
    if (INITIALIZATION_FAILURE != null) {
      logFailure(INITIALIZATION_FAILURE);
    }
    return STRICT_ENERGY_CAPABILITY != null
        && GET_ENERGY_CONTAINER_COUNT != null
        && GET_NEEDED_ENERGY != null
        && INSERT_ENERGY != null
        && ACTION_EXECUTE != null;
  }

  /** Returns Mekanism's strict-energy handler exposed on the requested machine side. */
  public static Object getHandler(Level world, BlockPos pos, Direction side) {
    if (!isAvailable() || world == null || pos == null || !world.hasChunkAt(pos)) {
      return null;
    }
    try {
      return world.getCapability(STRICT_ENERGY_CAPABILITY, pos, side);
    } catch (RuntimeException error) {
      logFailure(error);
      return null;
    }
  }

  /** Returns whether the requested face actually exposes one or more energy containers. */
  public static boolean canReceiveFrom(Object handler, Direction side) {
    return getEnergyContainerCount(handler, side) > 0;
  }

  private static int getEnergyContainerCount(Object handler, Direction side) {
    if (!isAvailable() || handler == null) {
      return 0;
    }
    try {
      if (SIDED_HANDLER_API != null
          && SIDED_HANDLER_API.isInstance(handler)
          && GET_SIDED_ENERGY_CONTAINER_COUNT != null) {
        return Math.max(
            0, ((Number) GET_SIDED_ENERGY_CONTAINER_COUNT.invoke(handler, side)).intValue());
      }
      return Math.max(0, ((Number) GET_ENERGY_CONTAINER_COUNT.invoke(handler)).intValue());
    } catch (ReflectiveOperationException | RuntimeException error) {
      logFailure(error);
      return 0;
    }
  }

  /** Returns the Joule demand visible from the specified side. */
  public static long getDemandedJoules(Object handler, Direction side) {
    if (!isAvailable() || handler == null) {
      return 0L;
    }
    try {
      int containers = getEnergyContainerCount(handler, side);
      long demand = 0L;
      for (int container = 0; container < containers; container++) {
        long needed;
        if (SIDED_HANDLER_API != null
            && SIDED_HANDLER_API.isInstance(handler)
            && GET_SIDED_NEEDED_ENERGY != null) {
          needed = ((Number) GET_SIDED_NEEDED_ENERGY.invoke(handler, container, side)).longValue();
        } else {
          needed = ((Number) GET_NEEDED_ENERGY.invoke(handler, container)).longValue();
        }
        if (needed > 0L) {
          if (Long.MAX_VALUE - demand < needed) {
            return Long.MAX_VALUE;
          }
          demand += needed;
        }
      }
      return demand;
    } catch (ReflectiveOperationException | RuntimeException error) {
      logFailure(error);
      return 0L;
    }
  }

  /** Inserts Joules from the specified side and returns the amount actually accepted. */
  public static long insertJoules(Object handler, long amount, Direction side) {
    if (!isAvailable() || handler == null || amount <= 0L) {
      return 0L;
    }
    try {
      long remainder;
      if (SIDED_HANDLER_API != null
          && SIDED_HANDLER_API.isInstance(handler)
          && INSERT_SIDED_ENERGY != null) {
        remainder =
            ((Number) INSERT_SIDED_ENERGY.invoke(handler, amount, side, ACTION_EXECUTE)).longValue();
      } else {
        remainder = ((Number) INSERT_ENERGY.invoke(handler, amount, ACTION_EXECUTE)).longValue();
      }
      remainder = Math.max(0L, Math.min(amount, remainder));
      return amount - remainder;
    } catch (ReflectiveOperationException | RuntimeException error) {
      logFailure(error);
      return 0L;
    }
  }
}
