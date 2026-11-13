package net.pawjwp.scarcity.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.pawjwp.scarcity.compat.thirst.CamelPackItem;

// Recipe that copies stored fluid in a camel pack ingredient into the returned camel pack
public class CamelPackUpgradeRecipe extends ShapedRecipe {
    public CamelPackUpgradeRecipe(ShapedRecipe recipe) {
        super(recipe.getId(), recipe.getGroup(), recipe.category(), recipe.getWidth(), recipe.getHeight(),
                recipe.getIngredients(), recipe.getResultItem(RegistryAccess.EMPTY), recipe.showNotification());
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack result = super.assemble(container, registryAccess);
        for (ItemStack stack : container.getItems()) {
            if (stack.getItem() instanceof CamelPackItem && stack.hasTag() && stack.getTag().contains(FluidHandlerItemStack.FLUID_NBT_KEY)) {
                // Copy the stored fluid so its purity is kept
                result.getOrCreateTag().put(FluidHandlerItemStack.FLUID_NBT_KEY, stack.getTag().getCompound(FluidHandlerItemStack.FLUID_NBT_KEY).copy());
            }
        }
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ScarcityRecipeSerializers.CAMEL_PACK_UPGRADE.get();
    }

    // Reads and writes the recipe in the same format as a vanilla shaped recipe
    public static class Serializer implements RecipeSerializer<CamelPackUpgradeRecipe> {
        @Override
        public CamelPackUpgradeRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new CamelPackUpgradeRecipe(RecipeSerializer.SHAPED_RECIPE.fromJson(id, json));
        }

        @Override
        public CamelPackUpgradeRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new CamelPackUpgradeRecipe(RecipeSerializer.SHAPED_RECIPE.fromNetwork(id, buffer));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CamelPackUpgradeRecipe recipe) {
            RecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe);
        }
    }
}
