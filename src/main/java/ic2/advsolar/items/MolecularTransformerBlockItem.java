package ic2.advsolar.items;

import com.mojang.blaze3d.vertex.PoseStack;
import ic2.advsolar.client.render.MolecularTransformerRenderer;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

public class MolecularTransformerBlockItem extends BlockItem {
  public MolecularTransformerBlockItem(Block block, Item.Properties properties) {
    super(block, properties);
  }

  @Override
  public void initializeClient(Consumer<IClientItemExtensions> consumer) {
    consumer.accept(
        new IClientItemExtensions() {
          private BlockEntityWithoutLevelRenderer renderer;

          @Override
          public BlockEntityWithoutLevelRenderer getCustomRenderer() {
            if (this.renderer == null) {
              this.renderer =
                  new BlockEntityWithoutLevelRenderer(
                      Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                      Minecraft.getInstance().getEntityModels()) {
                    @Override
                    public void renderByItem(
                        ItemStack stack,
                        ItemDisplayContext displayContext,
                        PoseStack poseStack,
                        MultiBufferSource buffer,
                        int packedLight,
                        int packedOverlay) {
                      MolecularTransformerRenderer.renderItemModel(
                          poseStack, buffer, packedLight, packedOverlay);
                    }
                  };
            }
            return this.renderer;
          }
        });
  }
}
