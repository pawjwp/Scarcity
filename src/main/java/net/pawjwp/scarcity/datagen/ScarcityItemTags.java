package net.pawjwp.scarcity.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.pawjwp.scarcity.Scarcity;
import net.pawjwp.scarcity.tag.ScarcityTags;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ScarcityItemTags extends ItemTagsProvider {
    // Shared by both tools. modifiable/harvest, /melee, /held, /interactable, and modifiable itself are left out
    // since Tinkers derives them automatically from the leaf tags below (e.g. modifiable/held from interactable/right).
    private static final List<String> TOOL_TAGS = List.of(
            "forge:tools",
            "minecraft:tools",
            "tconstruct:modifiable/bonus_slots",
            "tconstruct:modifiable/durability",
            "tconstruct:modifiable/harvest/primary",
            "tconstruct:modifiable/interactable/right",
            "tconstruct:modifiable/melee/weapon",
            "tconstruct:modifiable/multipart",
            "tconstruct:modifiable/small",
            "tleveling:tinker_mining",
            "tleveling:tinker_tools"
    );
    private static final List<String> CROOK_TAGS = List.of(
            "exdeorum:crooks"
    );
    private static final List<String> CRUSHING_HAMMER_TAGS = List.of(
            "exdeorum:hammers",
            "minecraft:breaks_decorated_pots",
            "minecraft:pickaxes",
            "tconstruct:modifiable/aoe",
            "tconstruct:modifiable/loot_capable_tool"
    );

    public ScarcityItemTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                            CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, Scarcity.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        var crook = ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "crook");
        var crushingHammer = ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "crushing_hammer");

        tag(ScarcityTags.WATER_FILTER_PURIFIABLE).add(Items.POTION, Items.WATER_BUCKET);
        tag(ScarcityTags.FORCE_PURITY).add(Items.POTION, Items.WATER_BUCKET)
                .addOptional(ResourceLocation.parse("thirst:terracotta_water_bowl"))
                .addOptional(ResourceLocation.parse("exdeorum:porcelain_water_bucket"));

        // Curios slots that a camel pack can be worn in
        for (String slot : List.of("back", "belt")) {
            tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", slot)))
                    .addOptional(ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "camel_pack"))
                    .addOptional(ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "advanced_camel_pack"))
                    .addOptional(ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "unlimited_camel_pack"));
        }

        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("curios", "insulation")))
                .addOptional(ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "stillsuit"))
                .addOptional(ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "leather_insulation"))
                .addOptional(ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "wool_insulation"));

        for (String tag : TOOL_TAGS) {
            tag(ItemTags.create(ResourceLocation.parse(tag))).addOptional(crook).addOptional(crushingHammer);
        }
        for (String tag : CROOK_TAGS) {
            tag(ItemTags.create(ResourceLocation.parse(tag))).addOptional(crook);
        }
        for (String tag : CRUSHING_HAMMER_TAGS) {
            tag(ItemTags.create(ResourceLocation.parse(tag))).addOptional(crushingHammer);
        }
    }
}