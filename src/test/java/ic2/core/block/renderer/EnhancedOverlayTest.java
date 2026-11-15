package ic2.core.block.renderer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ic2.api.item.IEnhancedOverlayProvider;
import ic2.core.block.renderer.EnhancedOverlay.Segment;
import ic2.core.item.tool.ItemToolWrench;
import ic2.core.item.tool.ItemToolWrenchElectric;
import ic2.gravisuite.items.GraviToolItem;
import net.minecraft.core.Direction;
import org.junit.jupiter.api.Test;

class EnhancedOverlayTest {

  @Test
  void testTargetFacingHorizontal() {
    Direction side = Direction.NORTH;

    assertEquals(Direction.NORTH, EnhancedOverlay.getTargetFacing(side, Segment.CENTRE));
    assertEquals(Direction.UP, EnhancedOverlay.getTargetFacing(side, Segment.TOP));
    assertEquals(Direction.DOWN, EnhancedOverlay.getTargetFacing(side, Segment.BOTTOM));
    assertEquals(Direction.WEST, EnhancedOverlay.getTargetFacing(side, Segment.LEFT));
    assertEquals(Direction.EAST, EnhancedOverlay.getTargetFacing(side, Segment.RIGHT));

    assertEquals(Direction.SOUTH, EnhancedOverlay.getTargetFacing(side, Segment.TOP_LEFT));
    assertEquals(Direction.SOUTH, EnhancedOverlay.getTargetFacing(side, Segment.TOP_RIGHT));
    assertEquals(Direction.SOUTH, EnhancedOverlay.getTargetFacing(side, Segment.BOTTOM_LEFT));
    assertEquals(Direction.SOUTH, EnhancedOverlay.getTargetFacing(side, Segment.BOTTOM_RIGHT));
  }

  @Test
  void testTargetFacingVertical() {
    Direction side = Direction.UP;

    assertEquals(Direction.UP, EnhancedOverlay.getTargetFacing(side, Segment.CENTRE));
    assertEquals(Direction.NORTH, EnhancedOverlay.getTargetFacing(side, Segment.TOP));
    assertEquals(Direction.SOUTH, EnhancedOverlay.getTargetFacing(side, Segment.BOTTOM));
    assertEquals(Direction.WEST, EnhancedOverlay.getTargetFacing(side, Segment.LEFT));
    assertEquals(Direction.EAST, EnhancedOverlay.getTargetFacing(side, Segment.RIGHT));

    assertEquals(Direction.DOWN, EnhancedOverlay.getTargetFacing(side, Segment.TOP_LEFT));
    assertEquals(Direction.DOWN, EnhancedOverlay.getTargetFacing(side, Segment.TOP_RIGHT));
    assertEquals(Direction.DOWN, EnhancedOverlay.getTargetFacing(side, Segment.BOTTOM_LEFT));
    assertEquals(Direction.DOWN, EnhancedOverlay.getTargetFacing(side, Segment.BOTTOM_RIGHT));
  }

  @Test
  void testSegmentForHit() {
    assertEquals(Segment.CENTRE, Segment.forHit(0.5, 0.5));
    assertEquals(Segment.TOP, Segment.forHit(0.5, 0.8));
    assertEquals(Segment.BOTTOM, Segment.forHit(0.5, 0.1));
    assertEquals(Segment.LEFT, Segment.forHit(0.1, 0.5));
    assertEquals(Segment.RIGHT, Segment.forHit(0.8, 0.5));
    assertEquals(Segment.TOP_LEFT, Segment.forHit(0.1, 0.8));
    assertEquals(Segment.TOP_RIGHT, Segment.forHit(0.8, 0.8));
    assertEquals(Segment.BOTTOM_LEFT, Segment.forHit(0.1, 0.1));
    assertEquals(Segment.BOTTOM_RIGHT, Segment.forHit(0.8, 0.1));
  }

  @Test
  void testWrenchImplementsOverlayProvider() {
    assertTrue(
        IEnhancedOverlayProvider.class.isAssignableFrom(ItemToolWrench.class),
        "ItemToolWrench must implement IEnhancedOverlayProvider");
    assertTrue(
        IEnhancedOverlayProvider.class.isAssignableFrom(ItemToolWrenchElectric.class),
        "ItemToolWrenchElectric must implement IEnhancedOverlayProvider");
    assertTrue(
        IEnhancedOverlayProvider.class.isAssignableFrom(GraviToolItem.class),
        "GraviToolItem must implement IEnhancedOverlayProvider");
  }
}
