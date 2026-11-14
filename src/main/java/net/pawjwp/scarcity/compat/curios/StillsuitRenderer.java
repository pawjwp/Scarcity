package net.pawjwp.scarcity.compat.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.pawjwp.scarcity.Scarcity;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

// Renders stillsuit in a Curios slot over the wearer's model
public class StillsuitRenderer implements ICurioRenderer {
    private static final ResourceLocation WIDE_TEXTURE = ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "textures/models/armor/stillsuit_wide.png");
    private static final ResourceLocation THIN_TEXTURE = ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "textures/models/armor/stillsuit_thin.png");

    public static void register() {
        CuriosRendererRegistry.register(InsulationItems.STILLSUIT.get(), StillsuitRenderer::new);
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack poseStack,
                                                                          RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer,
                                                                          int light, float limbSwing, float limbSwingAmount, float partialTicks,
                                                                          float ageInTicks, float netHeadYaw, float headPitch) {
        ResourceLocation texture = WIDE_TEXTURE;
        if (slotContext.entity() instanceof AbstractClientPlayer player && player.getModelName().equals("slim")) {
            texture = THIN_TEXTURE;
        }

        RenderType renderType = RenderType.entityCutoutNoCullZOffset(texture);
        if (renderTypeBuffer instanceof MultiBufferSource.BufferSource || renderTypeBuffer instanceof OutlineBufferSource) {
            renderType = RenderType.entityTranslucent(texture);
        }

        poseStack.pushPose();

        renderLayerParent.getModel().renderToBuffer(poseStack, renderTypeBuffer.getBuffer(renderType), light,
                OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }
}
