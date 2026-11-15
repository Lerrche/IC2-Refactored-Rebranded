package ic2.core.block.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import ic2.api.tile.IWrenchAble;
import ic2.core.util.RotationUtil;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class EnhancedOverlay {
  private static final Map<Direction, EnhancedOverlay> SIDE_TO_OVERLAY =
      new EnumMap<>(Direction.class);
  private final Map<Segment, RawSegment> segmentMap;

  private EnhancedOverlay(Map<Segment, RawSegment> segmentMap) {
    this.segmentMap = segmentMap;
  }

  public static EnhancedOverlay forFace(Direction face) {
    return SIDE_TO_OVERLAY.get(face);
  }

  public static void transformToFace(
      PoseStack matrix, BlockPos pos, Direction face, double camX, double camY, double camZ) {
    matrix.translate(
        (double) pos.getX() - camX + 0.5,
        (double) pos.getY() - camY + 0.5,
        (double) pos.getZ() - camZ + 0.5);
    switch (face) {
      case DOWN:
      default:
        break;
      case UP:
        matrix.mulPose(Axis.XP.rotationDegrees(180.0F));
        break;
      case NORTH:
        matrix.mulPose(Axis.XP.rotationDegrees(90.0F));
        break;
      case SOUTH:
        matrix.mulPose(Axis.XP.rotationDegrees(-90.0F));
        break;
      case WEST:
        matrix.mulPose(Axis.ZP.rotationDegrees(-90.0F));
        break;
      case EAST:
        matrix.mulPose(Axis.ZP.rotationDegrees(90.0F));
        break;
    }
    matrix.translate(0.0, -0.501, 0.0);
  }

  public static Direction getTargetFacing(Direction side, Segment segment) {
    switch (segment) {
      case CENTRE:
        return side;
      case TOP:
        return side.getAxis().isVertical() ? Direction.NORTH : Direction.UP;
      case BOTTOM:
        return side.getAxis().isVertical() ? Direction.SOUTH : Direction.DOWN;
      case LEFT:
        return side.getAxis().isVertical() ? Direction.WEST : side.getCounterClockWise();
      case RIGHT:
        return side.getAxis().isVertical() ? Direction.EAST : side.getClockWise();
      case TOP_LEFT:
      case TOP_RIGHT:
      case BOTTOM_LEFT:
      case BOTTOM_RIGHT:
        return side.getOpposite();
      default:
        throw new IllegalStateException("Unexpected segment: " + segment);
    }
  }

  public static boolean render(
      Player player,
      BlockHitResult target,
      PoseStack matrix,
      MultiBufferSource buffers,
      Camera camera) {
    Level world = player.level();
    BlockPos blockPos = target.getBlockPos();
    Direction side = target.getDirection();
    BlockState state = world.getBlockState(blockPos);
    if (!(state.getBlock() instanceof IWrenchAble wrenchAble)) {
      return false;
    }

    Direction hoveredSpin = RotationUtil.rotateByRay(target);
    Vec3 camPos = camera.getPosition();
    double camX = camPos.x;
    double camY = camPos.y;
    double camZ = camPos.z;

    // -------------------------------------------------------------
    // PASS 1: Render All Quads
    // -------------------------------------------------------------
    VertexConsumer quadBuffer = buffers.getBuffer(RenderType.debugQuads());

    // 1. Quads on the targeted face (sector highlights)
    EnhancedOverlay overlay = forFace(side);
    if (overlay != null) {
      matrix.pushPose();
      transformToFace(matrix, blockPos, side, camX, camY, camZ);
      Matrix4f facePose = matrix.last().pose();

      for (Segment segment : Segment.values()) {
        Direction spin = getTargetFacing(side, segment);
        if (wrenchAble.canSetFacing(world, blockPos, spin, player)) {
          int r, g, b, a;
          if (hoveredSpin == spin) {
            r = 0;
            g = 255;
            b = 0;
            a = 150;
          } else {
            r = 0;
            g = 80;
            b = 255;
            a = 100;
          }
          RawSegment raw = overlay.segmentMap.get(segment);
          if (raw != null) {
            raw.draw(facePose, quadBuffer, r, g, b, a);
          }
        }
      }
      matrix.popPose();
    }

    // 2. Quad on the destination face (if valid)
    if (hoveredSpin != null && wrenchAble.canSetFacing(world, blockPos, hoveredSpin, player)) {
      matrix.pushPose();
      transformToFace(matrix, blockPos, hoveredSpin, camX, camY, camZ);
      Matrix4f destPose = matrix.last().pose();
      drawFaceQuad(destPose, quadBuffer, 0, 255, 0, 90);
      matrix.popPose();
    }

    if (buffers instanceof MultiBufferSource.BufferSource bufferSource) {
      bufferSource.endBatch(RenderType.debugQuads());
    }

    // -------------------------------------------------------------
    // PASS 2: Render All Lines
    // -------------------------------------------------------------
    VertexConsumer lineBuffer = buffers.getBuffer(RenderType.lines());

    // 1. Grid lines on the targeted face
    matrix.pushPose();
    transformToFace(matrix, blockPos, side, camX, camY, camZ);
    Matrix4f facePose = matrix.last().pose();

    // 3x3 Grid lines
    drawLine(facePose, lineBuffer, 0.5F, -0.25F, -0.5F, -0.25F, 0, 0, 0, 180);
    drawLine(facePose, lineBuffer, 0.5F, 0.25F, -0.5F, 0.25F, 0, 0, 0, 180);
    drawLine(facePose, lineBuffer, 0.25F, -0.5F, 0.25F, 0.5F, 0, 0, 0, 180);
    drawLine(facePose, lineBuffer, -0.25F, -0.5F, -0.25F, 0.5F, 0, 0, 0, 180);
    // Outer border lines
    drawLine(facePose, lineBuffer, 0.5F, -0.5F, -0.5F, -0.5F, 0, 0, 0, 180);
    drawLine(facePose, lineBuffer, 0.5F, 0.5F, -0.5F, 0.5F, 0, 0, 0, 180);
    drawLine(facePose, lineBuffer, 0.5F, -0.5F, 0.5F, 0.5F, 0, 0, 0, 180);
    drawLine(facePose, lineBuffer, -0.5F, -0.5F, -0.5F, 0.5F, 0, 0, 0, 180);
    matrix.popPose();

    // 2. Border lines on the destination face
    if (hoveredSpin != null && wrenchAble.canSetFacing(world, blockPos, hoveredSpin, player)) {
      matrix.pushPose();
      transformToFace(matrix, blockPos, hoveredSpin, camX, camY, camZ);
      Matrix4f destPose = matrix.last().pose();

      drawLine(destPose, lineBuffer, 0.5F, -0.5F, -0.5F, -0.5F, 0, 180, 0, 220);
      drawLine(destPose, lineBuffer, 0.5F, 0.5F, -0.5F, 0.5F, 0, 180, 0, 220);
      drawLine(destPose, lineBuffer, 0.5F, -0.5F, 0.5F, 0.5F, 0, 180, 0, 220);
      drawLine(destPose, lineBuffer, -0.5F, -0.5F, -0.5F, 0.5F, 0, 180, 0, 220);
      matrix.popPose();
    }

    if (buffers instanceof MultiBufferSource.BufferSource bufferSource) {
      bufferSource.endBatch(RenderType.lines());
    }

    return true;
  }

  private static void drawLine(
      Matrix4f pose,
      VertexConsumer buffer,
      float x1,
      float z1,
      float x2,
      float z2,
      int r,
      int g,
      int b,
      int a) {
    float nx = x2 - x1;
    float nz = z2 - z1;
    float len = (float) Math.sqrt(nx * nx + nz * nz);
    if (len > 1e-4F) {
      nx /= len;
      nz /= len;
    }
    buffer.addVertex(pose, x1, 0.0F, z1).setColor(r, g, b, a).setNormal(nx, 0.0F, nz);
    buffer.addVertex(pose, x2, 0.0F, z2).setColor(r, g, b, a).setNormal(nx, 0.0F, nz);
  }

  private static void drawFaceQuad(
      Matrix4f pose, VertexConsumer buffer, int r, int g, int b, int a) {
    buffer.addVertex(pose, 0.5F, 0.0F, -0.5F).setColor(r, g, b, a);
    buffer.addVertex(pose, -0.5F, 0.0F, -0.5F).setColor(r, g, b, a);
    buffer.addVertex(pose, -0.5F, 0.0F, 0.5F).setColor(r, g, b, a);
    buffer.addVertex(pose, 0.5F, 0.0F, 0.5F).setColor(r, g, b, a);
  }

  static {
    Map<Segment, RawSegment> segmentMap = new EnumMap<>(Segment.class);
    SIDE_TO_OVERLAY.put(Direction.DOWN, new EnhancedOverlay(segmentMap));
    segmentMap.put(Segment.TOP_LEFT, RawSegment.C_BOX);
    segmentMap.put(Segment.TOP, RawSegment.B_BAR);
    segmentMap.put(Segment.TOP_RIGHT, RawSegment.A_BOX);
    segmentMap.put(Segment.LEFT, RawSegment.F_BAR);
    segmentMap.put(Segment.CENTRE, RawSegment.E_CENTRE);
    segmentMap.put(Segment.RIGHT, RawSegment.D_BAR);
    segmentMap.put(Segment.BOTTOM_LEFT, RawSegment.I_BOX);
    segmentMap.put(Segment.BOTTOM, RawSegment.H_BAR);
    segmentMap.put(Segment.BOTTOM_RIGHT, RawSegment.G_BOX);

    segmentMap = new EnumMap<>(Segment.class);
    SIDE_TO_OVERLAY.put(Direction.UP, new EnhancedOverlay(segmentMap));
    segmentMap.put(Segment.TOP_LEFT, RawSegment.I_BOX);
    segmentMap.put(Segment.TOP, RawSegment.H_BAR);
    segmentMap.put(Segment.TOP_RIGHT, RawSegment.G_BOX);
    segmentMap.put(Segment.LEFT, RawSegment.F_BAR);
    segmentMap.put(Segment.CENTRE, RawSegment.E_CENTRE);
    segmentMap.put(Segment.RIGHT, RawSegment.D_BAR);
    segmentMap.put(Segment.BOTTOM_LEFT, RawSegment.C_BOX);
    segmentMap.put(Segment.BOTTOM, RawSegment.B_BAR);
    segmentMap.put(Segment.BOTTOM_RIGHT, RawSegment.A_BOX);

    segmentMap = new EnumMap<>(Segment.class);
    SIDE_TO_OVERLAY.put(Direction.NORTH, new EnhancedOverlay(segmentMap));
    segmentMap.put(Segment.TOP_LEFT, RawSegment.A_BOX);
    segmentMap.put(Segment.TOP, RawSegment.B_BAR);
    segmentMap.put(Segment.TOP_RIGHT, RawSegment.C_BOX);
    segmentMap.put(Segment.LEFT, RawSegment.D_BAR);
    segmentMap.put(Segment.CENTRE, RawSegment.E_CENTRE);
    segmentMap.put(Segment.RIGHT, RawSegment.F_BAR);
    segmentMap.put(Segment.BOTTOM_LEFT, RawSegment.G_BOX);
    segmentMap.put(Segment.BOTTOM, RawSegment.H_BAR);
    segmentMap.put(Segment.BOTTOM_RIGHT, RawSegment.I_BOX);

    segmentMap = new EnumMap<>(Segment.class);
    SIDE_TO_OVERLAY.put(Direction.SOUTH, new EnhancedOverlay(segmentMap));
    segmentMap.put(Segment.TOP_LEFT, RawSegment.I_BOX);
    segmentMap.put(Segment.TOP, RawSegment.H_BAR);
    segmentMap.put(Segment.TOP_RIGHT, RawSegment.G_BOX);
    segmentMap.put(Segment.LEFT, RawSegment.F_BAR);
    segmentMap.put(Segment.CENTRE, RawSegment.E_CENTRE);
    segmentMap.put(Segment.RIGHT, RawSegment.D_BAR);
    segmentMap.put(Segment.BOTTOM_LEFT, RawSegment.C_BOX);
    segmentMap.put(Segment.BOTTOM, RawSegment.B_BAR);
    segmentMap.put(Segment.BOTTOM_RIGHT, RawSegment.A_BOX);

    segmentMap = new EnumMap<>(Segment.class);
    SIDE_TO_OVERLAY.put(Direction.WEST, new EnhancedOverlay(segmentMap));
    segmentMap.put(Segment.TOP_LEFT, RawSegment.C_BOX);
    segmentMap.put(Segment.TOP, RawSegment.F_BAR);
    segmentMap.put(Segment.TOP_RIGHT, RawSegment.I_BOX);
    segmentMap.put(Segment.LEFT, RawSegment.B_BAR);
    segmentMap.put(Segment.CENTRE, RawSegment.E_CENTRE);
    segmentMap.put(Segment.RIGHT, RawSegment.H_BAR);
    segmentMap.put(Segment.BOTTOM_LEFT, RawSegment.A_BOX);
    segmentMap.put(Segment.BOTTOM, RawSegment.D_BAR);
    segmentMap.put(Segment.BOTTOM_RIGHT, RawSegment.G_BOX);

    segmentMap = new EnumMap<>(Segment.class);
    SIDE_TO_OVERLAY.put(Direction.EAST, new EnhancedOverlay(segmentMap));
    segmentMap.put(Segment.TOP_LEFT, RawSegment.G_BOX);
    segmentMap.put(Segment.TOP, RawSegment.D_BAR);
    segmentMap.put(Segment.TOP_RIGHT, RawSegment.A_BOX);
    segmentMap.put(Segment.LEFT, RawSegment.H_BAR);
    segmentMap.put(Segment.CENTRE, RawSegment.E_CENTRE);
    segmentMap.put(Segment.RIGHT, RawSegment.B_BAR);
    segmentMap.put(Segment.BOTTOM_LEFT, RawSegment.I_BOX);
    segmentMap.put(Segment.BOTTOM, RawSegment.F_BAR);
    segmentMap.put(Segment.BOTTOM_RIGHT, RawSegment.C_BOX);
  }

  private enum RawSegment {
    A_BOX {
      @Override
      void draw(Matrix4f pose, VertexConsumer buffer, int r, int g, int b, int a) {
        buffer.addVertex(pose, 0.5F, 0.0F, -0.5F).setColor(r, g, b, a);
        buffer.addVertex(pose, 0.25F, 0.0F, -0.5F).setColor(r, g, b, a);
        buffer.addVertex(pose, 0.25F, 0.0F, -0.25F).setColor(r, g, b, a);
        buffer.addVertex(pose, 0.5F, 0.0F, -0.25F).setColor(r, g, b, a);
      }
    },
    B_BAR {
      @Override
      void draw(Matrix4f pose, VertexConsumer buffer, int r, int g, int b, int a) {
        buffer.addVertex(pose, -0.25F, 0.0F, -0.5F).setColor(r, g, b, a);
        buffer.addVertex(pose, 0.25F, 0.0F, -0.5F).setColor(r, g, b, a);
        buffer.addVertex(pose, 0.25F, 0.0F, -0.25F).setColor(r, g, b, a);
        buffer.addVertex(pose, -0.25F, 0.0F, -0.25F).setColor(r, g, b, a);
      }
    },
    C_BOX {
      @Override
      void draw(Matrix4f pose, VertexConsumer buffer, int r, int g, int b, int a) {
        buffer.addVertex(pose, -0.5F, 0.0F, -0.5F).setColor(r, g, b, a);
        buffer.addVertex(pose, -0.25F, 0.0F, -0.5F).setColor(r, g, b, a);
        buffer.addVertex(pose, -0.25F, 0.0F, -0.25F).setColor(r, g, b, a);
        buffer.addVertex(pose, -0.5F, 0.0F, -0.25F).setColor(r, g, b, a);
      }
    },
    D_BAR {
      @Override
      void draw(Matrix4f pose, VertexConsumer buffer, int r, int g, int b, int a) {
        buffer.addVertex(pose, 0.5F, 0.0F, -0.25F).setColor(r, g, b, a);
        buffer.addVertex(pose, 0.25F, 0.0F, -0.25F).setColor(r, g, b, a);
        buffer.addVertex(pose, 0.25F, 0.0F, 0.25F).setColor(r, g, b, a);
        buffer.addVertex(pose, 0.5F, 0.0F, 0.25F).setColor(r, g, b, a);
      }
    },
    E_CENTRE {
      @Override
      void draw(Matrix4f pose, VertexConsumer buffer, int r, int g, int b, int a) {
        buffer.addVertex(pose, 0.25F, 0.0F, -0.25F).setColor(r, g, b, a);
        buffer.addVertex(pose, -0.25F, 0.0F, -0.25F).setColor(r, g, b, a);
        buffer.addVertex(pose, -0.25F, 0.0F, 0.25F).setColor(r, g, b, a);
        buffer.addVertex(pose, 0.25F, 0.0F, 0.25F).setColor(r, g, b, a);
      }
    },
    F_BAR {
      @Override
      void draw(Matrix4f pose, VertexConsumer buffer, int r, int g, int b, int a) {
        buffer.addVertex(pose, -0.5F, 0.0F, -0.25F).setColor(r, g, b, a);
        buffer.addVertex(pose, -0.25F, 0.0F, -0.25F).setColor(r, g, b, a);
        buffer.addVertex(pose, -0.25F, 0.0F, 0.25F).setColor(r, g, b, a);
        buffer.addVertex(pose, -0.5F, 0.0F, 0.25F).setColor(r, g, b, a);
      }
    },
    G_BOX {
      @Override
      void draw(Matrix4f pose, VertexConsumer buffer, int r, int g, int b, int a) {
        buffer.addVertex(pose, 0.5F, 0.0F, 0.5F).setColor(r, g, b, a);
        buffer.addVertex(pose, 0.25F, 0.0F, 0.5F).setColor(r, g, b, a);
        buffer.addVertex(pose, 0.25F, 0.0F, 0.25F).setColor(r, g, b, a);
        buffer.addVertex(pose, 0.5F, 0.0F, 0.25F).setColor(r, g, b, a);
      }
    },
    H_BAR {
      @Override
      void draw(Matrix4f pose, VertexConsumer buffer, int r, int g, int b, int a) {
        buffer.addVertex(pose, -0.25F, 0.0F, 0.5F).setColor(r, g, b, a);
        buffer.addVertex(pose, 0.25F, 0.0F, 0.5F).setColor(r, g, b, a);
        buffer.addVertex(pose, 0.25F, 0.0F, 0.25F).setColor(r, g, b, a);
        buffer.addVertex(pose, -0.25F, 0.0F, 0.25F).setColor(r, g, b, a);
      }
    },
    I_BOX {
      @Override
      void draw(Matrix4f pose, VertexConsumer buffer, int r, int g, int b, int a) {
        buffer.addVertex(pose, -0.5F, 0.0F, 0.5F).setColor(r, g, b, a);
        buffer.addVertex(pose, -0.25F, 0.0F, 0.5F).setColor(r, g, b, a);
        buffer.addVertex(pose, -0.25F, 0.0F, 0.25F).setColor(r, g, b, a);
        buffer.addVertex(pose, -0.5F, 0.0F, 0.25F).setColor(r, g, b, a);
      }
    };

    abstract void draw(Matrix4f pose, VertexConsumer buffer, int r, int g, int b, int a);
  }

  public enum Segment {
    TOP_LEFT,
    TOP,
    TOP_RIGHT,
    LEFT,
    CENTRE,
    RIGHT,
    BOTTOM_LEFT,
    BOTTOM,
    BOTTOM_RIGHT;

    public static Segment forHit(Direction face, double x, double y, double z) {
      switch (face) {
        case DOWN:
        case UP:
          return forHit(x, 1.0 - z);
        case NORTH:
          return forHit(1.0 - x, y);
        case SOUTH:
          return forHit(x, y);
        case WEST:
          return forHit(z, y);
        case EAST:
          return forHit(1.0 - z, y);
        default:
          throw new IllegalArgumentException("Unexpected face: " + face);
      }
    }

    public static Segment forHit(double hitX, double hitY) {
      if (hitX <= 0.25) {
        if (hitY <= 0.25) {
          return BOTTOM_LEFT;
        } else {
          return hitY >= 0.75 ? TOP_LEFT : LEFT;
        }
      } else if (hitX > 0.25 && hitX < 0.75) {
        if (hitY <= 0.25) {
          return BOTTOM;
        } else {
          return hitY >= 0.75 ? TOP : CENTRE;
        }
      } else if (hitX >= 0.75) {
        if (hitY <= 0.25) {
          return BOTTOM_RIGHT;
        } else {
          return hitY >= 0.75 ? TOP_RIGHT : RIGHT;
        }
      } else {
        throw new IllegalArgumentException("Unexpected hit values: [" + hitX + ", " + hitY + ']');
      }
    }

    public static Segment forRayTrace(BlockHitResult ray) {
      Vec3 hit = ray.getLocation();
      BlockPos pos = ray.getBlockPos();
      return forHit(ray.getDirection(), hit.x - pos.getX(), hit.y - pos.getY(), hit.z - pos.getZ());
    }
  }
}
