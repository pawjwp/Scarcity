package net.pawjwp.scarcity.tag;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ScarcityTags {

    // Item tags
    public static final TagKey<Item> SEEDS =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "seeds"));
    // Water containers shown as purifiable by a water water filter in the crafting grid
    public static final TagKey<Item> WATER_FILTER_PURIFIABLE =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath("scarcity", "water_filter_purifiable"));
}
