package ic2.core.block.personal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class PersonalChestRenderer implements BlockEntityRenderer<TileEntityPersonalChest> {
  public static final ResourceLocation CHEST_TEXTURE =
      ResourceLocation.fromNamespaceAndPath("ic2", "textures/entity/chest/personal_chest.png");

  private final ModelPart lid;
  private final ModelPart bottom;
  private final ModelPart lock;

  public PersonalChestRenderer(BlockEntityRendererProvider.Context context) {
    MeshDefinition mesh = new MeshDefinition();
    PartDefinition root = mesh.getRoot();
    root.addOrReplaceChild(
        "bottom",
        CubeListBuilder.create().texOffs(0, 19).addBox(1.0F, 0.0F, 1.0F, 14.0F, 9.0F, 14.0F),
        PartPose.ZERO);
    root.addOrReplaceChild(
        "lid",
        CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F),
        PartPose.offset(0.0F, 9.0F, 1.0F));
    root.addOrReplaceChild(
        "lock",
        CubeListBuilder.create().texOffs(0, 0).addBox(7.0F, -2.0F, 14.0F, 2.0F, 4.0F, 1.0F),
        PartPose.offset(0.0F, 9.0F, 1.0F));

    ModelPart baked = LayerDefinition.create(mesh, 64, 64).bakeRoot();
    this.bottom = baked.getChild("bottom");
    this.lid = baked.getChild("lid");
    this.lock = baked.getChild("lock");
  }

  @Override
  public void render(
      TileEntityPersonalChest te,
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource buffers,
      int light,
      int overlay) {
    Direction facing = te.getFacing();

    poseStack.pushPose();
    poseStack.translate(0.5F, 0.5F, 0.5F);
    float f = facing.toYRot();
    poseStack.mulPose(Axis.YP.rotationDegrees(-f));
    poseStack.translate(-0.5F, -0.5F, -0.5F);

    float openNess = te.getLidAngle(partialTick);
    openNess = 1.0F - openNess;
    openNess = 1.0F - openNess * openNess * openNess;

    this.lid.xRot = -(openNess * ((float) Math.PI / 2.0F));
    this.lock.xRot = this.lid.xRot;

    VertexConsumer consumer = buffers.getBuffer(RenderType.entityCutout(CHEST_TEXTURE));
    this.bottom.render(poseStack, consumer, light, overlay);
    this.lid.render(poseStack, consumer, light, overlay);
    this.lock.render(poseStack, consumer, light, overlay);

    poseStack.popPose();
  }
}
