package net.pawjwp.scarcity.compat.thirst;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.FastColor;
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
