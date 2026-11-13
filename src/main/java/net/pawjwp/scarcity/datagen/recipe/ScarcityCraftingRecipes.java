package net.pawjwp.scarcity.datagen.recipe;

import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.pawjwp.scarcity.Scarcity;
import net.pawjwp.scarcity.compat.tinkers.TinkersCompat;
import net.pawjwp.scarcity.config.ScarcityConfigCondition;
import net.pawjwp.scarcity.item.ScarcityItems;
import net.pawjwp.scarcity.recipe.ScarcityRecipeSerializers;
import slimeknights.tconstruct.library.recipe.modifiers.adding.ModifierRecipeBuilder;
import slimeknights.tconstruct.library.recipe.tinkerstation.building.ToolBuildingRecipeBuilder;
import slimeknights.tconstruct.library.tools.SlotType;
import slimeknights.tconstruct.library.tools.item.IModifiable;

import java.util.function.Consumer;

public class ScarcityCraftingRecipes {
    public static void register(Consumer<FinishedRecipe> consumer) {
        recipesTinkersTools(consumer);
        recipesTinkersModifiers(consumer);
        recipesWaterGear(consumer);
    }

    private static void recipesWaterGear(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ScarcityItems.WATER_FILTER.get())
                .pattern("PGP")
                .pattern("PCP")
                .pattern(" P ")
                .define('P', Items.PAPER)
                .define('G', Tags.Items.GRAVEL)
                .define('C', Items.CHARCOAL)
                .unlockedBy("has_charcoal", InventoryChangeTrigger.TriggerInstance.hasItems(Items.CHARCOAL))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ScarcityItems.INSULATED_HYDRAULIC_FABRIC.get(), 2)
                .pattern("SLS")
                .pattern("LWL")
                .pattern("SLS")
                .define('S', Tags.Items.STRING)
                .define('L', Tags.Items.LEATHER)
                .define('W', ItemTags.WOOL)
                .unlockedBy("has_leather", InventoryChangeTrigger.TriggerInstance.hasItems(Items.LEATHER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ScarcityItems.FILTRATION_POCKET.get())
                .pattern("ICI")
                .pattern("IFI")
                .pattern("III")
                .define('I', ScarcityItems.INSULATED_HYDRAULIC_FABRIC.get())
                .define('C', Tags.Items.INGOTS_COPPER)
                .define('F', ScarcityItems.WATER_FILTER.get())
                .unlockedBy("has_insulated_hydraulic_fabric", InventoryChangeTrigger.TriggerInstance.hasItems(ScarcityItems.INSULATED_HYDRAULIC_FABRIC.get()))
                .save(consumer);

        // Purifies a container by one water purity level with a water filter
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition("thirst"))
                .addRecipe(c -> SpecialRecipeBuilder.special(ScarcityRecipeSerializers.WATER_FILTERING.get()).save(c, "scarcity:water_filtering"))
                .build(consumer, ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "water_filtering"));
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
