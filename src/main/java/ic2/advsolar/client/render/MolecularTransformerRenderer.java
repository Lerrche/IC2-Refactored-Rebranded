package ic2.advsolar.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import ic2.advsolar.blocks.entity.MolecularTransformerBlockEntity;
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
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class MolecularTransformerRenderer
    implements BlockEntityRenderer<MolecularTransformerBlockEntity> {
  private static final ResourceLocation TEXTURE =
      ResourceLocation.fromNamespaceAndPath(
          "advanced_solar_panels", "textures/models/texturemoleculartransformer.png");
  private static final ResourceLocation PLASMA_TEXTURE =
      ResourceLocation.fromNamespaceAndPath("advanced_solar_panels", "textures/models/plazma.png");

  private static MolecularTransformerRenderer instance;

  private final ModelPart root;
  private final ModelPart workZone;

  public MolecularTransformerRenderer() {
    MeshDefinition mesh = new MeshDefinition();
    PartDefinition rootDef = mesh.getRoot();

    // Main machine body and electrode 1 at rot = 0 (origin at y=16)
    PartDefinition main =
        rootDef.addOrReplaceChild(
            "main",
            CubeListBuilder.create()
                // coreBottom: [10x3x10], texOffs(0, 0)
                .texOffs(0, 0)
                .addBox(-5.0F, 4.0F, -5.0F, 10.0F, 3.0F, 10.0F)
                // coreTopPlate: [9x3x9], texOffs(0, 30)
                .texOffs(0, 30)
                .addBox(-5.0F, -7.0F, -4.5F, 9.0F, 3.0F, 9.0F)
                // coreTopElectr: [3x2x3], texOffs(25, 44)
                .texOffs(25, 44)
                .addBox(-2.0F, -8.0F, -1.466667F, 3.0F, 2.0F, 3.0F)
                // firstElTop: [4x3x10], texOffs(20, 16)
                .texOffs(20, 16)
                .addBox(3.0F, -8.0F, -5.0F, 4.0F, 3.0F, 10.0F)
                // firstElBottom: [3x5x6], texOffs(49, 16)
                .texOffs(49, 16)
                .addBox(4.0F, 3.0F, -3.0F, 3.0F, 5.0F, 6.0F),
            PartPose.offset(0.0F, 16.0F, 0.0F));

    // Electrode 2: rotated by -120 deg (-2.094395 rad) around Y
    main.addOrReplaceChild(
        "electrode2",
        CubeListBuilder.create()
            .texOffs(20, 16)
            .addBox(3.0F, -8.0F, -5.0F, 4.0F, 3.0F, 10.0F)
            .texOffs(49, 16)
            .addBox(4.0F, 3.0F, -3.0F, 3.0F, 5.0F, 6.0F),
        PartPose.rotation(0.0F, -2.094395F, 0.0F));

    // Electrode 3: rotated by +120 deg (+2.094395 rad) around Y
    main.addOrReplaceChild(
        "electrode3",
        CubeListBuilder.create()
            .texOffs(20, 16)
            .addBox(3.0F, -8.0F, -5.0F, 4.0F, 3.0F, 10.0F)
            .texOffs(49, 16)
            .addBox(4.0F, 3.0F, -3.0F, 3.0F, 5.0F, 6.0F),
        PartPose.rotation(0.0F, 2.094395F, 0.0F));

    // Translucent central chamber (coreWorkZone)
    rootDef.addOrReplaceChild(
        "workZone",
        CubeListBuilder.create().texOffs(0, 44).addBox(-3.0F, -4.0F, -3.0F, 6.0F, 9.0F, 6.0F),
        PartPose.offset(0.0F, 16.0F, 0.0F));

    ModelPart baked = LayerDefinition.create(mesh, 128, 64).bakeRoot();
    this.root = baked.getChild("main");
    this.workZone = baked.getChild("workZone");
    instance = this;
  }

  public MolecularTransformerRenderer(BlockEntityRendererProvider.Context context) {
    this();
  }

  public static void renderItemModel(
      PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
    if (instance == null) {
      instance = new MolecularTransformerRenderer();
    }
    poseStack.pushPose();
    poseStack.translate(0.5, 1.5, 0.5);
    poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

    instance.root.render(
        poseStack,
        bufferSource.getBuffer(RenderType.entityCutout(TEXTURE)),
        packedLight,
        packedOverlay);

    instance.workZone.render(
        poseStack,
        bufferSource.getBuffer(RenderType.entityTranslucent(TEXTURE)),
        packedLight,
        packedOverlay);

    poseStack.popPose();
  }

  @Override
  public void render(
      MolecularTransformerBlockEntity blockEntity,
      float partialTick,
      PoseStack poseStack,
      MultiBufferSource bufferSource,
      int packedLight,
      int packedOverlay) {
    poseStack.pushPose();
    // Authentic 1.12 TESR coordinate frame: (0.5, 1.5, 0.5) with 180 deg Z flip
    poseStack.translate(0.5, 1.5, 0.5);
    poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

    // 1. Render solid body & 3 electrodes
    this.root.render(
        poseStack,
        bufferSource.getBuffer(RenderType.entityCutout(TEXTURE)),
        packedLight,
        packedOverlay);

    // 2. Render translucent glass workZone chamber
    this.workZone.render(
        poseStack,
        bufferSource.getBuffer(RenderType.entityTranslucent(TEXTURE)),
        packedLight,
        packedOverlay);

    // 3. Render animated plasma core if working
    if (blockEntity.isWorking() || blockEntity.getProgressEu() > 0) {
      poseStack.pushPose();
      poseStack.translate(0.0, 1.0, 0.0);

      long time = blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() : 0;
      float rot = (time + partialTick) * 5.0F;

      poseStack.mulPose(Axis.YP.rotationDegrees(rot));
      poseStack.mulPose(Axis.XP.rotationDegrees(rot * 0.5F));

      VertexConsumer consumer =
          bufferSource.getBuffer(RenderType.entityTranslucent(PLASMA_TEXTURE));
      Matrix4f matrix = poseStack.last().pose();

      float s = 0.16F;
      renderBox(matrix, consumer, -s, -s, -s, s, s, s, 255, 255, 255, 220, 0xF000F0, packedOverlay);

      poseStack.popPose();
    }

    poseStack.popPose();
  }

  private void renderBox(
      Matrix4f matrix,
      VertexConsumer builder,
      float minX,
      float minY,
      float minZ,
      float maxX,
      float maxY,
      float maxZ,
      int r,
      int g,
      int b,
      int a,
      int light,
      int overlay) {
    // Up face (+Y)
    builder
        .addVertex(matrix, minX, maxY, minZ)
        .setColor(r, g, b, a)
        .setUv(0, 0)
        .setOverlay(overlay)
        .setLight(light)
        .setNormal(0, 1, 0);
    builder
        .addVertex(matrix, minX, maxY, maxZ)
        .setColor(r, g, b, a)
        .setUv(0, 1)
        .setOverlay(overlay)
        .setLight(light)
        .setNormal(0, 1, 0);
    builder
        .addVertex(matrix, maxX, maxY, maxZ)
        .setColor(r, g, b, a)
        .setUv(1, 1)
        .setOverlay(overlay)
        .setLight(light)
        .setNormal(0, 1, 0);
    builder
        .addVertex(matrix, maxX, maxY, minZ)
        .setColor(r, g, b, a)
        .setUv(1, 0)
        .setOverlay(overlay)
        .setLight(light)
        .setNormal(0, 1, 0);

    // Down face (-Y)
    builder
        .addVertex(matrix, minX, minY, maxZ)
        .setColor(r, g, b, a)
        .setUv(0, 0)
        .setOverlay(overlay)
        .setLight(light)
        .setNormal(0, -1, 0);
    builder
        .addVertex(matrix, minX, minY, minZ)
        .setColor(r, g, b, a)
        .setUv(0, 1)
        .setOverlay(overlay)
        .setLight(light)
        .setNormal(0, -1, 0);
    builder
        .addVertex(matrix, maxX, minY, minZ)
        .setColor(r, g, b, a)
        .setUv(1, 1)
        .setOverlay(overlay)
        .setLight(light)
        .setNormal(0, -1, 0);
    builder
        .addVertex(matrix, maxX, minY, maxZ)
        .setColor(r, g, b, a)
        .setUv(1, 0)
        .setOverlay(overlay)
        .setLight(light)
        .setNormal(0, -1, 0);

    // North face (-Z)
    builder
        .addVertex(matrix, maxX, maxY, minZ)
        .setColor(r, g, b, a)
        .setUv(0, 0)
        .setOverlay(overlay)
        .setLight(light)
        .setNormal(0, 0, -1);
    builder
        .addVertex(matrix, maxX, minY, minZ)
        .setColor(r, g, b, a)
        .setUv(0, 1)
        .setOverlay(overlay)
        .setLight(light)
        .setNormal(0, 0, -1);
    builder
        .addVertex(matrix, minX, minY, minZ)
        .setColor(r, g, b, a)
        .setUv(1, 1)
        .setOverlay(overlay)
        .setLight(light)
        .setNormal(0, 0, -1);
    builder
        .addVertex(matrix, minX, maxY, minZ)
        .setColor(r, g, b, a)
        .setUv(1, 0)
        .setOverlay(overlay)
        .setLight(light)
        .setNormal(0, 0, -1);

    // South face (+Z)
    builder
        .addVertex(matrix, minX, maxY, maxZ)
        .setColor(r, g, b, a)
        .setUv(0, 0)
        .setOverlay(overlay)
        .setLight(light)
        .setNormal(0, 0, 1);
    builder
        .addVertex(matrix, minX, minY, maxZ)
        .setColor(r, g, b, a)
        .setUv(0, 1)
        .setOverlay(overlay)
        .setLight(light)
        .setNormal(0, 0, 1);
    builder
        .addVertex(matrix, maxX, minY, maxZ)
        .setColor(r, g, b, a)
        .setUv(1, 1)
        .setOverlay(overlay)
        .setLight(light)
        .setNormal(0, 0, 1);
    builder
        .addVertex(matrix, maxX, maxY, maxZ)
        .setColor(r, g, b, a)
        .setUv(1, 0)
        .setOverlay(overlay)
        .setLight(light)
        .setNormal(0, 0, 1);

    // West face (-X)
    builder
        .addVertex(matrix, minX, maxY, minZ)
        .setColor(r, g, b, a)
        .setUv(0, 0)
        .setOverlay(overlay)
        .setLight(light)
        .setNormal(-1, 0, 0);
    builder
        .addVertex(matrix, minX, minY, minZ)
        .setColor(r, g, b, a)
        .setUv(0, 1)
        .setOverlay(overlay)
        .setLight(light)
        .setNormal(-1, 0, 0);
    builder
        .addVertex(matrix, minX, minY, maxZ)
        .setColor(r, g, b, a)
        .setUv(1, 1)
        .setOverlay(overlay)
        .setLight(light)
        .setNormal(-1, 0, 0);
    builder
        .addVertex(matrix, minX, maxY, maxZ)
        .setColor(r, g, b, a)
        .setUv(1, 0)
        .setOverlay(overlay)
        .setLight(light)
        .setNormal(-1, 0, 0);

    // East face (+X)
    builder
        .addVertex(matrix, maxX, maxY, maxZ)
        .setColor(r, g, b, a)
        .setUv(0, 0)
        .setOverlay(overlay)
        .setLight(light)
        .setNormal(1, 0, 0);
    builder
        .addVertex(matrix, maxX, minY, maxZ)
        .setColor(r, g, b, a)
        .setUv(0, 1)
        .setOverlay(overlay)
        .setLight(light)
        .setNormal(1, 0, 0);
    builder
        .addVertex(matrix, maxX, minY, minZ)
        .setColor(r, g, b, a)
        .setUv(1, 1)
        .setOverlay(overlay)
        .setLight(light)
        .setNormal(1, 0, 0);
    builder
        .addVertex(matrix, maxX, maxY, minZ)
        .setColor(r, g, b, a)
        .setUv(1, 0)
        .setOverlay(overlay)
        .setLight(light)
        .setNormal(1, 0, 0);
  }
}
