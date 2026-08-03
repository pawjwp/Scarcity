package net.pawjwp.scarcity.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.pawjwp.scarcity.Scarcity;
import net.pawjwp.scarcity.compat.Mods;
import net.pawjwp.scarcity.compat.thermal.MachineSieveScreen;
import net.pawjwp.scarcity.compat.thermal.ThermalCompat;
import thedarkcolour.exdeorum.compat.XeiSieveRecipe;

@JeiPlugin
public class ScarcityJeiPlugin implements IModPlugin {
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "jei");

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
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
