package net.pawjwp.scarcity.datagen;

import net.pawjwp.scarcity.Scarcity;
import net.minecraft.data.DataGenerator;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = Scarcity.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(event.includeServer(), new ScarcityRecipes(packOutput));

        generator.addProvider(event.includeClient(), new ScarcityItemModels(packOutput, existingFileHelper));

        var blockTags = new ScarcityBlockTags(packOutput, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new ScarcityItemTags(packOutput, lookupProvider, blockTags.contentsGetter(), existingFileHelper));

        // Tinkers data gen providers
        generator.addProvider(event.includeServer(), new ScarcityToolDefinitions(packOutput));
        generator.addProvider(event.includeServer(), new ScarcityStationLayouts(packOutput));
        generator.addProvider(event.includeServer(), new ScarcityModifiers(packOutput));
    }
}
