package net.pawjwp.scarcity.datagen.recipe;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.pawjwp.scarcity.Scarcity;
import net.pawjwp.scarcity.compat.tinkers.TinkersCompat;
import net.pawjwp.scarcity.config.ScarcityConfigCondition;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.item.IModifiable;

import java.util.function.Consumer;

public class ScarcityCraftingRecipes {
    public static void register(Consumer<FinishedRecipe> consumer) {
        recipesTinkersTools(consumer);
        recipesTinkersModifiers(consumer);
    }

    private static void recipesTinkersTools(Consumer<FinishedRecipe> consumer) {
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition("tconstruct"))
                .addCondition(new ScarcityConfigCondition(ScarcityConfigCondition.TINKERS_TOOLS))
                .addRecipe(c -> ToolBuildingRecipeBuilder.toolBuildingRecipe((IModifiable) TinkersCompat.CROOK.get()).save(c))
                .build(consumer, ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "tools/crook"));

        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition("tconstruct"))
                .addCondition(new ScarcityConfigCondition(ScarcityConfigCondition.TINKERS_TOOLS))
                .addRecipe(c -> ToolBuildingRecipeBuilder.toolBuildingRecipe((IModifiable) TinkersCompat.CRUSHING_HAMMER.get()).save(c))
                .build(consumer, ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "tools/crushing_hammer"));
    }

    private static void recipesTinkersModifiers(Consumer<FinishedRecipe> consumer) {
        // Applies Heavy-Hammering to a Crushing Hammer, letting it break compressed blocks like a compressed hammer
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition("tconstruct"))
                .addCondition(new ScarcityConfigCondition(ScarcityConfigCondition.TINKERS_TOOLS))
                .addCondition(new ScarcityConfigCondition(ScarcityConfigCondition.EX_DEORUM_TOOL_OVERRIDES))
                .addRecipe(c -> ModifierRecipeBuilder.modifier(TinkersCompat.HEAVY_HAMMERING)
                        .setTools(Ingredient.of(TinkersCompat.CRUSHING_HAMMER.get()))
                        .addInput(Items.ANVIL)
                        .setMaxLevel(1)
                        .setSlots(SlotType.UPGRADE, 1)
                        .save(c))
                .build(consumer, ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "tools/modifiers/heavy_hammering"));
    }
}
