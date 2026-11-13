package net.pawjwp.scarcity.compat.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.pawjwp.scarcity.compat.thirst.CamelPacks;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import top.theillusivec4.curios.api.client.ICurioRenderer;

// Renders a camel pack worn in a Curios slot, only loaded on the client if Curios and Thirst are present
public class CamelPackRenderer implements ICurioRenderer {

    // register the Curios renderer
    public static void register() {
        CuriosRendererRegistry.register(CamelPacks.CAMEL_PACK.get(), CamelPackRenderer::new);
        CuriosRendererRegistry.register(CamelPacks.ADVANCED_CAMEL_PACK.get(), CamelPackRenderer::new);
        CuriosRendererRegistry.register(CamelPacks.UNLIMITED_CAMEL_PACK.get(), CamelPackRenderer::new);
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack,
                                                                          RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer,
                                                                          int light, float limbSwing, float limbSwingAmount, float partialTicks,
                                                                          float ageInTicks, float netHeadYaw, float headPitch) {
        LivingEntity wearer = slotContext.entity();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        matrixStack.pushPose();

        ICurioRenderer.translateIfSneaking(matrixStack, wearer);

        if (wearer.isCrouching()) {
            // SNEAKING
            matrixStack.mulPose(Axis.XP.rotationDegrees(210));
            matrixStack.translate(0.0, -6.25 / 16, -2.6 / 16);
        } else {
            // STANDING
            matrixStack.mulPose(Axis.XP.rotationDegrees(180)); // flip upright + face toward the back
            matrixStack.translate(0.0, -6.0 / 16, -3.0 / 16); // 6px down, 3px off the back
        }

        itemRenderer.render(stack, ItemDisplayContext.NONE, false, matrixStack, renderTypeBuffer, light,
                OverlayTexture.NO_OVERLAY, itemRenderer.getModel(stack, null, null, 0));

        matrixStack.popPose();
    }
}
