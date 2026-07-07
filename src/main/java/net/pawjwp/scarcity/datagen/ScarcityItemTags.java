package net.pawjwp.scarcity.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.pawjwp.scarcity.Scarcity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ScarcityItemTags extends ItemTagsProvider {
    private static final List<String> TOOL_TAGS = List.of(
            "forge:tools",
            "minecraft:tools",
            "tconstruct:modifiable",
            "tconstruct:modifiable/aoe",
            "tconstruct:modifiable/bonus_slots",
            "tconstruct:modifiable/durability",
            "tconstruct:modifiable/harvest",
            "tconstruct:modifiable/harvest/primary",
            "tconstruct:modifiable/held",
            "tconstruct:modifiable/interactable",
            "tconstruct:modifiable/interactable/charge",
            "tconstruct:modifiable/interactable/right",
            "tconstruct:modifiable/melee",
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