package net.pawjwp.scarcity.tag;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ScarcityTags {

    // Item tags
    public static final TagKey<Item> SEEDS =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath("forge", "seeds"));
    // Water containers shown as purifiable by a water water filter in the crafting grid
    public static final TagKey<Item> WATER_FILTER_PURIFIABLE =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath("scarcity", "water_filter_purifiable"));
    // Water containers given a purity tag when they enter a player's inventory
    public static final TagKey<Item> FORCE_PURITY =
            ItemTags.create(ResourceLocation.fromNamespaceAndPath("scarcity", "force_purity"));
}
