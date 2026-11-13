package net.pawjwp.scarcity.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.item.ScarcityItems;
import net.pawjwp.scarcity.tag.ScarcityTags;

// Crafting recipe for a water filter combined with a container of water, which raises the purity of the water by one level and damages the filter
public class WaterFilteringRecipe extends CustomRecipe {
    public WaterFilteringRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    // A container of water from the water_filter_purifiable tag
    public static ItemStack waterContainer(Item item) {
        // if it's a potion, set the potion type to water
        return item == Items.POTION ? PotionUtils.setPotion(new ItemStack(item), Potions.WATER) : new ItemStack(item);
    }

    // The water container in the crafting grid
    private static ItemStack findWater(CraftingContainer container) {
        ItemStack water = ItemStack.EMPTY;
        boolean hasFilter = false;
        for (ItemStack stack : container.getItems()) {
            // The grid must have exactly one water filter and one container of water
            if (stack.isEmpty()) {
                continue;
            }
            if (!hasFilter && stack.is(ScarcityItems.WATER_FILTER.get())) {
                hasFilter = true;
            } else if (water.isEmpty() && stack.is(ScarcityTags.WATER_FILTER_PURIFIABLE)
                    // A potion only counts when it is a water bottle
                    && (!stack.is(Items.POTION) || PotionUtils.getPotion(stack) == Potions.WATER)
                    // Max purity is rejected because it cannot be further purified
                    && ThirstCompat.getPurity(stack) >= 0 && ThirstCompat.getPurity(stack) < ThirstCompat.MAX_PURITY) {
                water = stack;
            } else {
                return ItemStack.EMPTY;
            }
        }
        return hasFilter ? water : ItemStack.EMPTY;
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        return !findWater(container).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack purified = findWater(container).copyWithCount(1);
        ThirstCompat.addPurity(purified, ThirstCompat.getPurity(purified) + 1);
        return purified;
    }

    // Leaves the filter in the grid and nothing else, since the water container is the crafting result
    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        for (int slot = 0; slot < remaining.size(); slot++) {
            if (container.getItem(slot).is(ScarcityItems.WATER_FILTER.get())) {
                ItemStack filter = container.getItem(slot).copy();
                if (!filter.hurt(1, RandomSource.create(), null)) {
                    remaining.set(slot, filter);
                }
            }
        }
        return remaining;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ScarcityRecipeSerializers.WATER_FILTERING.get();
    }
}
