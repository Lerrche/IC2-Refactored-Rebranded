package ic2.core.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import ic2.core.recipe.v2.RecipeManagerGetter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

class RecipeManagerGetterTest {

  private static RecipeManager createRecipeManager() {
    try {
      Field f = Unsafe.class.getDeclaredField("theUnsafe");
      f.setAccessible(true);
      Unsafe unsafe = (Unsafe) f.get(null);
      return (RecipeManager) unsafe.allocateInstance(RecipeManager.class);
    } catch (Exception e) {
      throw new RuntimeException("Failed to allocate RecipeManager", e);
    }
  }

  @Test
  @DisplayName("RecipeManagerGetter handles null world and null RecipeManager safely")
  void testNullSafety() {
    AtomicInteger callCount = new AtomicInteger();
    RecipeManagerGetter<String> getter = new RecipeManagerGetter<>(rm -> {
      callCount.incrementAndGet();
      return "result";
    });

    assertNull(getter.get((Level) null));
    assertNull(getter.get((RecipeManager) null));
    assertEquals(0, callCount.get());
  }

  @Test
  @DisplayName("RecipeManagerGetter caches per RecipeManager and does not re-invoke factory")
  void testCaching() {
    AtomicInteger callCount = new AtomicInteger();
    RecipeManagerGetter<String> getter = new RecipeManagerGetter<>(rm -> {
      callCount.incrementAndGet();
      return "manager_" + callCount.get();
    });

    RecipeManager rm1 = createRecipeManager();
    RecipeManager rm2 = createRecipeManager();

    String first = getter.get(rm1);
    assertNotNull(first);
    assertEquals("manager_1", first);
    assertEquals(1, callCount.get());

    // Subsequent calls with rm1 must return the cached instance without calling factory
    for (int i = 0; i < 100; i++) {
      String next = getter.get(rm1);
      assertSame(first, next);
    }
    assertEquals(1, callCount.get(), "Factory should only be invoked once for rm1");

    // rm2 gets its own cached entry
    String second = getter.get(rm2);
    assertNotNull(second);
    assertEquals("manager_2", second);
    assertEquals(2, callCount.get());

    for (int i = 0; i < 50; i++) {
      String next = getter.get(rm2);
      assertSame(second, next);
    }
    assertEquals(2, callCount.get(), "Factory should only be invoked once for rm2");
  }

  @Test
  @DisplayName("RecipeManagerGetter clear() purges cached managers")
  void testClear() {
    AtomicInteger callCount = new AtomicInteger();
    RecipeManagerGetter<String> getter = new RecipeManagerGetter<>(rm -> "val_" + callCount.incrementAndGet());

    RecipeManager rm = createRecipeManager();

    assertEquals("val_1", getter.get(rm));
    assertEquals(1, callCount.get());

    getter.clear();

    assertEquals("val_2", getter.get(rm));
    assertEquals(2, callCount.get());
  }

  @Test
  @DisplayName("RecipeManagerGetter is thread-safe under concurrent access")
  void testConcurrentAccess() throws InterruptedException {
    AtomicInteger callCount = new AtomicInteger();
    RecipeManagerGetter<String> getter = new RecipeManagerGetter<>(rm -> {
      callCount.incrementAndGet();
      try {
        Thread.sleep(5);
      } catch (InterruptedException ignored) {}
      return "result";
    });

    RecipeManager rm = createRecipeManager();

    int threadCount = 10;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch startLatch = new CountDownLatch(1);
    CountDownLatch doneLatch = new CountDownLatch(threadCount);

    for (int i = 0; i < threadCount; i++) {
      executor.submit(() -> {
        try {
          startLatch.await();
          for (int j = 0; j < 50; j++) {
            assertEquals("result", getter.get(rm));
          }
        } catch (Exception e) {
          throw new RuntimeException(e);
        } finally {
          doneLatch.countDown();
        }
      });
    }

    startLatch.countDown();
    doneLatch.await();
    executor.shutdown();

    assertEquals(1, callCount.get());
  }
}
