package net.pawjwp.scarcity;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.model.DynamicFluidContainerModel;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.pawjwp.scarcity.attribute.ScarcityAttributes;
import net.pawjwp.scarcity.compat.Mods;
import net.pawjwp.scarcity.compat.coldsweat.ColdSweatCompat;
import net.pawjwp.scarcity.compat.coldsweat.InsulatingArmor;
import net.pawjwp.scarcity.compat.curios.CamelPackRenderer;
import net.pawjwp.scarcity.compat.curios.CuriosCompat;
import net.pawjwp.scarcity.compat.curios.InsulationItems;
import net.pawjwp.scarcity.compat.curios.StillsuitRenderer;
import net.pawjwp.scarcity.compat.thirst.CamelPackClient;
import net.pawjwp.scarcity.compat.thirst.CamelPackLayer;
import net.pawjwp.scarcity.compat.thirst.CamelPackModel;
import net.pawjwp.scarcity.compat.thirst.CamelPacks;
import net.pawjwp.scarcity.compat.thirst.PurityTagging;
import net.pawjwp.scarcity.compat.refurbished.RefurbishedCompat;
import net.pawjwp.scarcity.compat.thermal.ThermalClient;
import net.pawjwp.scarcity.compat.thermal.ThermalCompat;
import net.pawjwp.scarcity.compat.tinkers.TinkersClient;
import net.pawjwp.scarcity.compat.tinkers.TinkersCompat;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.pawjwp.scarcity.config.ScarcityConfig;
import net.pawjwp.scarcity.config.ScarcityConfigCondition;
import net.pawjwp.scarcity.item.ScarcityCreativeTabs;
import net.pawjwp.scarcity.item.ScarcityItems;
import net.pawjwp.scarcity.network.ScarcityNetwork;
import net.pawjwp.scarcity.recipe.ScarcityRecipeSerializers;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Scarcity.MOD_ID)
public class Scarcity
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "scarcity";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // public static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public Scarcity(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();

        ScarcityAttributes.register(modEventBus);
        modEventBus.addListener(ScarcityAttributes::onEntityAttributeModification);

        ScarcityCreativeTabs.register(modEventBus);
        ScarcityItems.register(modEventBus);
        ScarcityRecipeSerializers.register(modEventBus);

        if (Mods.TINKERS) {
            TinkersCompat.register(modEventBus);
        }
        if (Mods.THERMAL && Mods.EX_DEORUM) {
            ThermalCompat.register(modEventBus);
        }
        if (Mods.REFURBISHED_FURNITURE && Mods.THIRST) {
            RefurbishedCompat.register();
        }
        if (Mods.THIRST) {
            CamelPacks.register(modEventBus);
            PurityTagging.register();
        }
        if (Mods.CURIOS && (Mods.THIRST || Mods.COLD_SWEAT)) {
            InsulationItems.register(modEventBus);
        }
        if (Mods.COLD_SWEAT) {
            InsulatingArmor.register(modEventBus);
            ColdSweatCompat.register();
        }

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        ScarcityNetwork.register();
        MinecraftForge.EVENT_BUS.addListener(SunlightSensitivity::onStartTracking);
        // Highest priority so other mods' use Scarcity's healing numbers
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, RegenHealAmount::onHeal);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register config
        context.registerConfig(ModConfig.Type.COMMON, ScarcityConfig.COMMON_SPEC);
        modEventBus.addListener(ScarcityConfig::onLoad);
        modEventBus.addListener(ScarcityConfig::onReload);

        CraftingHelper.register(ScarcityConfigCondition.Serializer.INSTANCE);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        if (Mods.THIRST && Mods.CURIOS) {
            event.enqueueWork(CuriosCompat::registerCamelPacks);
        }
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if(event.getTabKey() == ScarcityCreativeTabs.SCARCITY_TAB.getKey()) {
            ScarcityItems.CREATIVE_TAB_ITEMS.forEach(event::accept);
            if (Mods.TINKERS && ScarcityConfig.enableTinkersTools) {
                TinkersCompat.addToolVariants(event::accept);
            }
            if (Mods.THERMAL && Mods.EX_DEORUM && ScarcityConfig.enableMachineSieve) {
                event.accept(ThermalCompat.MACHINE_SIEVE_ITEM.get());
                event.accept(ThermalCompat.SIEVE_HEAVY_AUGMENT.get());
            }
            if (Mods.THIRST) {
                event.accept(CamelPacks.CAMEL_PACK.get());
                event.accept(CamelPacks.ADVANCED_CAMEL_PACK.get());
                event.accept(CamelPacks.UNLIMITED_CAMEL_PACK.get());
            }
            if (Mods.CURIOS && (Mods.THIRST || Mods.COLD_SWEAT)) {
                event.accept(InsulationItems.STILLSUIT.get());
            }
            if (Mods.CURIOS && Mods.COLD_SWEAT) {
                event.accept(InsulationItems.LEATHER_INSULATION.get());
                event.accept(InsulationItems.WOOL_INSULATION.get());
            }
            if (Mods.COLD_SWEAT) {
                event.accept(InsulatingArmor.DESERT_CAP.get());
                event.accept(InsulatingArmor.DESERT_TUNIC.get());
                event.accept(InsulatingArmor.DESERT_PANTS.get());
                event.accept(InsulatingArmor.DESERT_SHOES.get());
                event.accept(InsulatingArmor.SNOW_HAT.get());
                event.accept(InsulatingArmor.SNOW_COAT.get());
                event.accept(InsulatingArmor.SNOW_PANTS.get());
                event.accept(InsulatingArmor.SNOW_BOOTS.get());
            }
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            if (Mods.TINKERS) {
                event.enqueueWork(TinkersClient::registerItemProperties);
            }
            if (Mods.THERMAL && Mods.EX_DEORUM) {
                event.enqueueWork(ThermalClient::registerScreens);
            }
            if (Mods.THIRST) {
                MinecraftForge.EVENT_BUS.addListener(CamelPackClient::renderSlotSigns);
                MinecraftForge.EVENT_BUS.addListener(CamelPackClient::renderClickHint);
            }
            if (Mods.THIRST && Mods.CURIOS) {
                event.enqueueWork(CamelPackRenderer::register);
            }
            if (Mods.CURIOS && (Mods.THIRST || Mods.COLD_SWEAT)) {
                event.enqueueWork(StillsuitRenderer::register);
            }
        }

        @SubscribeEvent
        public static void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event)
        {
            if (Mods.THIRST) {
                event.register("camel_pack", CamelPackModel.Loader.INSTANCE);
            }
        }

        // Tints the fluid drawn in camel packs with the fluid's colour, the same way Forge tints buckets
        @SubscribeEvent
        public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event)
        {
            if (Mods.THIRST) {
                event.register(new DynamicFluidContainerModel.Colors(), CamelPacks.CAMEL_PACK.get(), CamelPacks.ADVANCED_CAMEL_PACK.get());
            }
        }

        // Draws a camel pack worn in the chestplate slot
        @SubscribeEvent
        public static void onAddLayers(EntityRenderersEvent.AddLayers event)
        {
            if (Mods.THIRST && !Mods.CURIOS) {
                for (String skin : event.getSkins()) {
                    PlayerRenderer renderer = event.getSkin(skin);
                    renderer.addLayer(new CamelPackLayer(renderer));
                }
            }
        }
    }
}
