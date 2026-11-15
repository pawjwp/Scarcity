package net.pawjwp.scarcity.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.pawjwp.scarcity.compat.thirst.CamelPackItem;

// Crafting recipe for a camel pack combined with a container of water to fill the camel pack
public class CamelPackFillRecipe extends CustomRecipe {
    public CamelPackFillRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    // The filled camel pack, with the container slot of the fluid container and the item left in that slot
    private record Filled(ItemStack camelPack, int containerSlot, ItemStack leftovers) {
    }

    // Fills the camel pack in the crafting grid
    private static Filled fill(CraftingContainer container) {
        // The grid must have one camel pack and one other item
        int camelPackSlot = -1;
        int containerSlot = -1;
        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack stack = container.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            // camel pack must have a tank
            if (camelPackSlot < 0 && stack.getItem() instanceof CamelPackItem item && item.getConfig().hasTank()) {
                camelPackSlot = slot;
            } else if (containerSlot < 0) {
                containerSlot = slot;
            } else {
                return null;
            }
        }
        if (camelPackSlot < 0 || containerSlot < 0) {
            return null;
        }

        ItemStack camelPack = container.getItem(camelPackSlot).copy();
        ItemStack leftovers = CamelPackItem.fill(camelPack, container.getItem(containerSlot));
        return leftovers == null ? null : new Filled(camelPack, containerSlot, leftovers);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        return fill(container) != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        return fill(container).camelPack;
    }

    // Leaves the emptied fluid container in the grid and nothing else, since the camel pack is the crafting result
    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
        NonNullList<ItemStack> remaining = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);
        Filled filled = fill(container);
        remaining.set(filled.containerSlot, filled.leftovers);
        return remaining;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ScarcityRecipeSerializers.CAMEL_PACK_FILL.get();
    }
}
