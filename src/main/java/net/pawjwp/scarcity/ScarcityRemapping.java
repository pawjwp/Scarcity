package net.pawjwp.scarcity;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.MissingMappingsEvent;

import java.util.Map;

// Remaps items from their old IDs to new ones
@Mod.EventBusSubscriber(modid = Scarcity.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ScarcityRemapping {

    // old_namespace:old_id, new_namespace:new_id
    private static final Map<String, String> REMAPS = Map.ofEntries(
            Map.entry("kubejs:crook", "scarcity:crook"),
            Map.entry("kubejs:crushing_hammer", "scarcity:crushing_hammer"),

            Map.entry("kubejs:filter", "scarcity:water_filter"),
            Map.entry("kubejs:insulated_hydraulic_fabric", "scarcity:insulated_hydraulic_fabric"),
            Map.entry("kubejs:filtration_pocket", "scarcity:filtration_pocket"),

            Map.entry("kubejs:camel_pack", "scarcity:camel_pack"),
            Map.entry("kubejs:advanced_camel_pack", "scarcity:advanced_camel_pack"),
            Map.entry("kubejs:unlimited_camel_pack", "scarcity:unlimited_camel_pack"),

            Map.entry("kubejs:stillsuit", "scarcity:stillsuit"),
            Map.entry("kubejs:leather_insulation", "scarcity:leather_insulation"),
            Map.entry("kubejs:wool_insulation", "scarcity:wool_insulation")
    );

    @SubscribeEvent
    public static void onMissingMappings(MissingMappingsEvent event) {
        REMAPS.forEach((from, to) -> {
            ResourceLocation oldId = ResourceLocation.parse(from);
            ResourceLocation newId = ResourceLocation.parse(to);
            // Skips items that are not registered because their mod is missing, since looking them up would return air
            if (!ForgeRegistries.ITEMS.containsKey(newId)) {
                return;
            }
            for (MissingMappingsEvent.Mapping<Item> mapping : event.getMappings(Registries.ITEM, oldId.getNamespace())) {
                if (mapping.getKey().equals(oldId)) {
                    mapping.remap(ForgeRegistries.ITEMS.getValue(newId));
                }
            }
        });
    }
}
