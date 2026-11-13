package net.pawjwp.scarcity.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.pawjwp.scarcity.Scarcity;

public class ScarcityRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Scarcity.MOD_ID);

    public static final RegistryObject<RecipeSerializer<WaterFilteringRecipe>> WATER_FILTERING = RECIPE_SERIALIZERS.register("water_filtering",
            () -> new SimpleCraftingRecipeSerializer<>(WaterFilteringRecipe::new));
    public static final RegistryObject<RecipeSerializer<CamelPackFillRecipe>> CAMEL_PACK_FILL = RECIPE_SERIALIZERS.register("camel_pack_fill",
            () -> new SimpleCraftingRecipeSerializer<>(CamelPackFillRecipe::new));

    public static void register(IEventBus eventBus) {
        RECIPE_SERIALIZERS.register(eventBus);
    }
}
