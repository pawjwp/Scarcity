package net.pawjwp.scarcity.compat.thirst;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.NamedRenderTypeManager;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.ForgeFaceData;
import net.minecraftforge.client.model.SeparateTransformsModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

// Camel pack model that shows the stored fluid inside
public class CamelPackModel implements IUnbakedGeometry<CamelPackModel> {
    // Worn tank interior geometry
    private static final int TANK_FLOOR = 4;
    private static final int TANK_HEIGHT = 7;
    private static final float TANK_X1 = 6;
    private static final float TANK_X2 = 10;
    private static final float TANK_Z1 = 7.1F;
    private static final float TANK_Z2 = 8.5F;
    private static final int WINDOW_BOTTOM = 5;

    // Inventory tank geometry
    private static final int ICON_HEIGHT = 5;
    private static final float ICON_X1 = 7;
    private static final float ICON_X2 = 9;
    private static final float ICON_BOTTOM = 4;

    // Texture rows sampled by the fluid's top face, which the worn model's front face and the icon continue below
    private static final int SURFACE_ROWS = 2;

    private final BlockModel worn;
    private final BlockModel icon;

    private CamelPackModel(BlockModel worn, BlockModel icon) {
        this.worn = worn;
        this.icon = icon;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker,
            Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides,
            ResourceLocation modelLocation) {
        BakedModel wornBaked = worn.bake(baker, worn, spriteGetter, modelState, modelLocation, true);
        BakedModel iconBaked = icon.bake(baker, icon, spriteGetter, modelState, modelLocation, false);
        TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle"));
        return new SeparateTransformsModel.Baked(context.useAmbientOcclusion(), context.isGui3d(),
                context.useBlockLight(), particle,
                new FluidOverrides(context, particle, wornBaked, iconBaked, modelLocation), wornBaked,
                iconPerspectives(iconBaked)
        );
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
        worn.resolveParents(modelGetter);
        icon.resolveParents(modelGetter);
    }

    // List of display contexts that show the inventory icon instead of the worn model
    private static ImmutableMap<ItemDisplayContext, BakedModel> iconPerspectives(BakedModel icon) {
        return ImmutableMap.of(ItemDisplayContext.GUI, icon, ItemDisplayContext.GROUND, icon, ItemDisplayContext.FIXED, icon);
    }

    // Swaps in a model with the stored fluid drawn into it, baking each fluid and fill height once
    private static final class FluidOverrides extends ItemOverrides {
        private final Map<String, BakedModel> cache = new HashMap<>();
        private final IGeometryBakingContext context;
        private final TextureAtlasSprite particle;
        private final BakedModel worn;
        private final BakedModel icon;
        private final ResourceLocation modelLocation;

        private FluidOverrides(IGeometryBakingContext context, TextureAtlasSprite particle, BakedModel worn, BakedModel icon, ResourceLocation modelLocation) {
            this.context = context;
            this.particle = particle;
            this.worn = worn;
            this.icon = icon;
            this.modelLocation = modelLocation;
        }

        @Override
        public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
            FluidStack fluid = FluidUtil.getFluidContained(stack).orElse(FluidStack.EMPTY);
            if (fluid.isEmpty() || !(stack.getItem() instanceof CamelPackItem item)) {
                return originalModel;
            }

            // The fill height in pixels, rounded up so fluid in the tank is always drawn
            int height = Mth.clamp(
                    Mth.ceil(TANK_HEIGHT * (float) fluid.getAmount() / item.getConfig().capacity().getAsInt()), 1, TANK_HEIGHT
            );
            String key = ForgeRegistries.FLUIDS.getKey(fluid.getFluid()) + "/" + height;
            return cache.computeIfAbsent(key, k -> bakeWithFluid(fluid.getFluid(), height));
        }

        private BakedModel bakeWithFluid(Fluid fluid, int height) {
            TextureAtlasSprite sprite = ForgeHooksClient.getBlockMaterial(IClientFluidTypeExtensions.of(fluid).getStillTexture()).sprite();
            // Draw glowing fluids at full brightness
            int light = fluid.getFluidType().getLightLevel() > 0 ? 15 : 0;
            ForgeFaceData faceData = new ForgeFaceData(0xFFFFFFFF, light, light, light == 0);

            // 3D fluid textures placed inside the camel pack model
            // Top face draws from the top of the fluid texture, the front face draws from immediately below that, top-down
            Map<Direction, BlockElementFace> wornFaces = new HashMap<>();
            wornFaces.put(Direction.NORTH, fluidFace(new float[]{16 - TANK_X2, SURFACE_ROWS, 16 - TANK_X1, SURFACE_ROWS + height}));
            wornFaces.put(Direction.UP, fluidFace(new float[]{TANK_X2, SURFACE_ROWS, TANK_X1, 0}));
            // Draw unshaded to appear brighter
            BlockElement volume = new BlockElement(new Vector3f(TANK_X1, TANK_FLOOR, TANK_Z1), new Vector3f(TANK_X2, TANK_FLOOR + height, TANK_Z2), wornFaces, null, false, faceData);

            // Simplified in the camel pack icon, drawn slightly in front of the icon and with a minimum height of 1
            int iconRows = Mth.clamp(TANK_FLOOR + height - WINDOW_BOTTOM, 1, ICON_HEIGHT);
            float iconTop = ICON_BOTTOM + iconRows;
            Map<Direction, BlockElementFace> iconFaces = Map.of(
                Direction.SOUTH, fluidFace(new float[]{ICON_X1, SURFACE_ROWS, ICON_X2, SURFACE_ROWS + iconRows}),
                Direction.NORTH, fluidFace(new float[]{ICON_X2, SURFACE_ROWS, ICON_X1, SURFACE_ROWS + iconRows})
            );
            BlockElement strip = new BlockElement(
                new Vector3f(ICON_X1, ICON_BOTTOM, 7.49F),
                new Vector3f(ICON_X2, iconTop, 8.51F),
                iconFaces, null, false, faceData
            );

            // Draw the icon's fluid opaque and the worn fluid transparent
            BakedModel wornWithFluid = withQuads(worn, bakeElement(volume, sprite), NamedRenderTypeManager.get(ResourceLocation.parse("translucent")));
            BakedModel iconWithFluid = withQuads(icon, bakeElement(strip, sprite), NamedRenderTypeManager.get(ResourceLocation.parse("cutout")));
            return new SeparateTransformsModel.Baked(
                context.useAmbientOcclusion(),
                context.isGui3d(),
                context.useBlockLight(),
                particle,
                ItemOverrides.EMPTY,
                wornWithFluid,
                iconPerspectives(iconWithFluid)
            );
        }

        // A fluid face tinted with the fluid's color, if applicable
        private static BlockElementFace fluidFace(float[] uv) {
            return new BlockElementFace(null, 1, "fluid", new BlockFaceUV(uv, 0));
        }

        private List<BakedQuad> bakeElement(BlockElement element, TextureAtlasSprite sprite) {
            return UnbakedGeometryHelper.bakeElements(List.of(element), material -> sprite, BlockModelRotation.X0_Y0, modelLocation);
        }

        // Copy of a model with fluid quads added
        private BakedModel withQuads(BakedModel model, List<BakedQuad> fluidQuads, RenderTypeGroup fluidRenderType) {
            RandomSource random = RandomSource.create();
            List<BakedQuad> quads = new ArrayList<>(model.getQuads(null, null, random, ModelData.EMPTY, null));
            for (Direction side : Direction.values()) {
                quads.addAll(model.getQuads(null, side, random, ModelData.EMPTY, null));
            }
            return CompositeModel.Baked.builder(model.useAmbientOcclusion(), model.isGui3d(), model.usesBlockLight(), particle, ItemOverrides.EMPTY, model.getTransforms())
                    .addQuads(NamedRenderTypeManager.get(ResourceLocation.parse("cutout")), quads)
                    .addQuads(fluidRenderType, fluidQuads)
                    .build();
        }
    }

    public static final class Loader implements IGeometryLoader<CamelPackModel> {
        public static final Loader INSTANCE = new Loader();

        private Loader() {
        }

        @Override
        public CamelPackModel read(JsonObject json, JsonDeserializationContext context) {
            return new CamelPackModel(
                    context.deserialize(GsonHelper.getAsJsonObject(json, "worn"), BlockModel.class),
                    context.deserialize(GsonHelper.getAsJsonObject(json, "icon"), BlockModel.class));
        }
    }
}
