package net.pawjwp.scarcity.compat.thirst;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;
import java.util.WeakHashMap;

// Camel pack client helpers
public final class CamelPackClient {
    // Average color of each fluid texture
    private static final Map<TextureAtlasSprite, Integer> AVERAGE_COLORS = new WeakHashMap<>();

    // Camel pack bar color, either the fluid's tint or its texture's average color
    public static int barColor(FluidStack fluid) {
        IClientFluidTypeExtensions fluidType = IClientFluidTypeExtensions.of(fluid.getFluid());
        int tint = fluidType.getTintColor(fluid) & 0xFFFFFF;
        if (tint != 0xFFFFFF) {
            return tint;
        }
        TextureAtlasSprite sprite = ForgeHooksClient.getBlockMaterial(fluidType.getStillTexture()).sprite();
        return AVERAGE_COLORS.computeIfAbsent(sprite, CamelPackClient::averageColor);
    }

    // Draws a camel pack on the wearer's back
    public static void renderOnBack(ItemStack stack, LivingEntity wearer, PoseStack poseStack, MultiBufferSource buffer, int light) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        poseStack.pushPose();

        if (wearer.isCrouching()) {
            poseStack.translate(0.0, 3.0 / 16, 0.0);
            poseStack.mulPose(Axis.XP.rotationDegrees(210));
            poseStack.translate(0.0, -6.25 / 16, -2.6 / 16);
        } else {
            poseStack.mulPose(Axis.XP.rotationDegrees(180)); // flip upright + face toward the back
            poseStack.translate(0.0, -6.0 / 16, -3.0 / 16); // 6px down, 3px off the back
        }

        itemRenderer.render(stack, ItemDisplayContext.NONE, false, poseStack, buffer, light,
                OverlayTexture.NO_OVERLAY, itemRenderer.getModel(stack, null, null, 0));

        poseStack.popPose();
    }

    // Average color of a texture's first frame, weighted by each pixel's opacity
    private static int averageColor(TextureAtlasSprite sprite) {
        long red = 0, green = 0, blue = 0, alpha = 0;
        for (int y = 0; y < sprite.contents().height(); y++) {
            for (int x = 0; x < sprite.contents().width(); x++) {
                int pixel = sprite.getPixelRGBA(0, x, y);
                int a = FastColor.ABGR32.alpha(pixel);
                red += (long) FastColor.ABGR32.red(pixel) * a;
                green += (long) FastColor.ABGR32.green(pixel) * a;
                blue += (long) FastColor.ABGR32.blue(pixel) * a;
                alpha += a;
            }
        }
        return alpha == 0 ? 0xFFFFFF : (int) (red / alpha) << 16 | (int) (green / alpha) << 8 | (int) (blue / alpha);
    }
}
