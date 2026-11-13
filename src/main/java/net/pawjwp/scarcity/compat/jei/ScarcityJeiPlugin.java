package net.pawjwp.scarcity.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraftforge.common.crafting.StrictNBTIngredient;
import net.minecraftforge.registries.ForgeRegistries;
import net.pawjwp.scarcity.Scarcity;
import net.pawjwp.scarcity.compat.Mods;
import net.pawjwp.scarcity.compat.thermal.MachineSieveScreen;
import net.pawjwp.scarcity.compat.thermal.ThermalCompat;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.item.ScarcityItems;
import net.pawjwp.scarcity.recipe.WaterFilteringRecipe;
import net.pawjwp.scarcity.tag.ScarcityTags;
import thedarkcolour.exdeorum.compat.XeiSieveRecipe;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class ScarcityJeiPlugin implements IModPlugin {
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "jei");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    // Shows a water filtering recipe for each purifiable water container at each purity, since the real recipe isn't shown in JEI
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (!Mods.THIRST) {
            return;
        }

        List<CraftingRecipe> recipes = new ArrayList<>();
        for (Item item : ForgeRegistries.ITEMS.tags().getTag(ScarcityTags.WATER_FILTER_PURIFIABLE)) {
            for (int purity = 0; purity < ThirstCompat.MAX_PURITY; purity++) {
                ItemStack input = WaterFilteringRecipe.waterContainer(item);
                ItemStack output = input.copy();
                ThirstCompat.addPurity(input, purity);
                ThirstCompat.addPurity(output, purity + 1);
                recipes.add(new ShapelessRecipe(
                        ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "water_filtering/" + ForgeRegistries.ITEMS.getKey(item).getPath() + "_" + purity),
                        "", CraftingBookCategory.MISC, output,
                        NonNullList.of(Ingredient.EMPTY, Ingredient.of(ScarcityItems.WATER_FILTER.get()), StrictNBTIngredient.of(input))));
            }
        }
        registration.addRecipes(RecipeTypes.CRAFTING, recipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        if (Mods.THERMAL && Mods.EX_DEORUM) {
            SieveJeiSupport.registerCatalysts(registration);
        }
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        if (Mods.THERMAL && Mods.EX_DEORUM) {
            SieveJeiSupport.registerGuiHandlers(registration);
        }
    }

    // Thermal/Ex Deorum classes only load when both mods are present
    private static class SieveJeiSupport {
        // JEI recipe types compare by id and recipe class, so these match Ex Deorum's registered categories
        private static final RecipeType<XeiSieveRecipe> SIEVE = RecipeType.create("exdeorum", "sieve", XeiSieveRecipe.class);
        private static final RecipeType<XeiSieveRecipe> COMPRESSED_SIEVE = RecipeType.create("exdeorum", "compressed_sieve", XeiSieveRecipe.class);

        static void registerCatalysts(IRecipeCatalystRegistration registration) {
            var machine = new ItemStack(ThermalCompat.MACHINE_SIEVE_ITEM.get());
            registration.addRecipeCatalyst(machine, SIEVE);
            registration.addRecipeCatalyst(machine, COMPRESSED_SIEVE);
        }

        static void registerGuiHandlers(IGuiHandlerRegistration registration) {
            registration.addRecipeClickArea(MachineSieveScreen.class,
                    MachineSieveScreen.PROGRESS_X, MachineSieveScreen.PROGRESS_Y, 24, 16,
                    SIEVE, COMPRESSED_SIEVE);
        }
    }
}
