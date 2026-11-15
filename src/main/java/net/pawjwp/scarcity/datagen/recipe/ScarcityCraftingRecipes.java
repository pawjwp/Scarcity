package net.pawjwp.scarcity.datagen.recipe;

import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.pawjwp.scarcity.Scarcity;
import net.pawjwp.scarcity.compat.coldsweat.InsulatingArmor;
import net.pawjwp.scarcity.compat.curios.InsulationItems;
import net.pawjwp.scarcity.compat.thirst.CamelPacks;
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
        recipesInsulation(consumer);
        recipesInsulatingArmor(consumer);
    }

    private static void recipesInsulation(Consumer<FinishedRecipe> consumer) {
        modRecipe(consumer, "stillsuit", ShapedRecipeBuilder.shaped(RecipeCategory.MISC, InsulationItems.STILLSUIT.get())
                .pattern("FFF")
                .pattern("PWP")
                .pattern("FPF")
                .define('F', ScarcityItems.INSULATED_HYDRAULIC_FABRIC.get())
                .define('P', ScarcityItems.FILTRATION_POCKET.get())
                .define('W', CamelPacks.ADVANCED_CAMEL_PACK.get())
                .unlockedBy("has_advanced_camel_pack", InventoryChangeTrigger.TriggerInstance.hasItems(CamelPacks.ADVANCED_CAMEL_PACK.get())),
                "curios", "thirst");

        modRecipe(consumer, "leather_insulation", ShapedRecipeBuilder.shaped(RecipeCategory.MISC, InsulationItems.LEATHER_INSULATION.get())
                .pattern("LSL")
                .pattern("LLL")
                .pattern("LLL")
                .define('L', Tags.Items.LEATHER)
                .define('S', Tags.Items.STRING)
                .unlockedBy("has_leather", InventoryChangeTrigger.TriggerInstance.hasItems(Items.LEATHER)),
                "curios", "cold_sweat");

        modRecipe(consumer, "wool_insulation", ShapedRecipeBuilder.shaped(RecipeCategory.MISC, InsulationItems.WOOL_INSULATION.get())
                .pattern("WSW")
                .pattern("WWW")
                .pattern("WWW")
                .define('W', ItemTags.WOOL)
                .define('S', Tags.Items.STRING)
                .unlockedBy("has_wool", InventoryChangeTrigger.TriggerInstance.hasItems(Items.WHITE_WOOL)),
                "curios", "cold_sweat");
    }

    private static void recipesInsulatingArmor(Consumer<FinishedRecipe> consumer) {
        modRecipe(consumer, "desert_cap", ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, InsulatingArmor.DESERT_CAP.get())
                .pattern("LWL")
                .pattern("S S")
                .define('L', Tags.Items.LEATHER)
                .define('S', Tags.Items.STRING)
                .define('W', ItemTags.WOOL)
                .unlockedBy("has_leather", InventoryChangeTrigger.TriggerInstance.hasItems(Items.LEATHER)),
                "cold_sweat");

        modRecipe(consumer, "desert_tunic", ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, InsulatingArmor.DESERT_TUNIC.get())
                .pattern("S S")
                .pattern("LWL")
                .pattern("SSS")
                .define('L', Tags.Items.LEATHER)
                .define('S', Tags.Items.STRING)
                .define('W', ItemTags.WOOL)
                .unlockedBy("has_leather", InventoryChangeTrigger.TriggerInstance.hasItems(Items.LEATHER)),
                "cold_sweat");

        modRecipe(consumer, "desert_pants", ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, InsulatingArmor.DESERT_PANTS.get())
                .pattern("SWS")
                .pattern("L L")
                .pattern("L L")
                .define('L', Tags.Items.LEATHER)
                .define('S', Tags.Items.STRING)
                .define('W', ItemTags.WOOL)
                .unlockedBy("has_leather", InventoryChangeTrigger.TriggerInstance.hasItems(Items.LEATHER)),
                "cold_sweat");

        modRecipe(consumer, "desert_shoes", ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, InsulatingArmor.DESERT_SHOES.get())
                .pattern("L L")
                .pattern("P P")
                .define('L', Tags.Items.LEATHER)
                .define('P', ItemTags.PLANKS)
                .unlockedBy("has_leather", InventoryChangeTrigger.TriggerInstance.hasItems(Items.LEATHER)),
                "cold_sweat");

        modRecipe(consumer, "snow_hat", ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, InsulatingArmor.SNOW_HAT.get())
                .pattern("WWW")
                .pattern("L L")
                .define('L', Tags.Items.LEATHER)
                .define('W', ItemTags.WOOL)
                .unlockedBy("has_wool", InventoryChangeTrigger.TriggerInstance.hasItems(Items.WHITE_WOOL)),
                "cold_sweat");

        modRecipe(consumer, "snow_coat", ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, InsulatingArmor.SNOW_COAT.get())
                .pattern("L L")
                .pattern("WLW")
                .pattern("WLW")
                .define('L', Tags.Items.LEATHER)
                .define('W', ItemTags.WOOL)
                .unlockedBy("has_wool", InventoryChangeTrigger.TriggerInstance.hasItems(Items.WHITE_WOOL)),
                "cold_sweat");

        modRecipe(consumer, "snow_pants", ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, InsulatingArmor.SNOW_PANTS.get())
                .pattern("WWW")
                .pattern("L L")
                .pattern("L L")
                .define('L', Tags.Items.LEATHER)
                .define('W', ItemTags.WOOL)
                .unlockedBy("has_wool", InventoryChangeTrigger.TriggerInstance.hasItems(Items.WHITE_WOOL)),
                "cold_sweat");

        modRecipe(consumer, "snow_boots", ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, InsulatingArmor.SNOW_BOOTS.get())
                .pattern("W W")
                .pattern("L L")
                .define('L', Tags.Items.LEATHER)
                .define('W', ItemTags.WOOL)
                .unlockedBy("has_wool", InventoryChangeTrigger.TriggerInstance.hasItems(Items.WHITE_WOOL)),
                "cold_sweat");
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

        // Fills a camel pack from a water bottle or an item that holds fluid
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition("thirst"))
                .addRecipe(c -> SpecialRecipeBuilder.special(ScarcityRecipeSerializers.CAMEL_PACK_FILL.get()).save(c, "scarcity:camel_pack_fill"))
                .build(consumer, ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "camel_pack_fill"));

        modRecipe(consumer, "camel_pack", ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CamelPacks.CAMEL_PACK.get())
                .pattern("SLS")
                .pattern("LFL")
                .pattern("LBL")
                .define('B', Items.BUCKET)
                .define('F', ScarcityItems.WATER_FILTER.get())
                .define('L', Tags.Items.LEATHER)
                .define('S', Tags.Items.STRING)
                .unlockedBy("has_water_filter", InventoryChangeTrigger.TriggerInstance.hasItems(ScarcityItems.WATER_FILTER.get())),
                "thirst");

        modRecipe(consumer, "advanced_camel_pack", ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CamelPacks.ADVANCED_CAMEL_PACK.get())
                .pattern("FCF")
                .pattern("PBP")
                .pattern("FPF")
                .define('B', Items.BUCKET)
                .define('C', Items.HOPPER)
                .define('F', ScarcityItems.INSULATED_HYDRAULIC_FABRIC.get())
                .define('P', ScarcityItems.FILTRATION_POCKET.get())
                .unlockedBy("has_filtration_pocket", InventoryChangeTrigger.TriggerInstance.hasItems(ScarcityItems.FILTRATION_POCKET.get())),
                "thirst");

        // Upgrades a camel pack and keeps its stored fluid
        ResourceLocation upgradeId = ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "advanced_camel_pack_upgrade");
        ConditionalRecipe.builder()
                .addCondition(new ModLoadedCondition("thirst"))
                .addRecipe(c -> ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CamelPacks.ADVANCED_CAMEL_PACK.get())
                        .pattern("FCF")
                        .pattern("PWP")
                        .pattern("FFF")
                        .define('C', Items.HOPPER)
                        .define('F', ScarcityItems.INSULATED_HYDRAULIC_FABRIC.get())
                        .define('P', ScarcityItems.FILTRATION_POCKET.get())
                        .define('W', CamelPacks.CAMEL_PACK.get())
                        .unlockedBy("has_camel_pack", InventoryChangeTrigger.TriggerInstance.hasItems(CamelPacks.CAMEL_PACK.get()))
                        .save(recipe -> c.accept(new UpgradeRecipe(recipe)), upgradeId))
                .build(consumer, upgradeId);

        modRecipe(consumer, "unlimited_camel_pack", ShapedRecipeBuilder.shaped(RecipeCategory.MISC, CamelPacks.UNLIMITED_CAMEL_PACK.get())
                .pattern("ARA")
                .pattern("SWS")
                .pattern("ATA")
                .define('A', Tags.Items.INGOTS_NETHERITE)
                .define('R', Items.NETHER_STAR)
                .define('S', Items.TOTEM_OF_UNDYING)
                .define('T', Items.CAULDRON)
                .define('W', CamelPacks.ADVANCED_CAMEL_PACK.get())
                .unlockedBy("has_advanced_camel_pack", InventoryChangeTrigger.TriggerInstance.hasItems(CamelPacks.ADVANCED_CAMEL_PACK.get())),
                "thirst");
    }

    // A recipe that only loads when all listed mods are present
    private static void modRecipe(Consumer<FinishedRecipe> consumer, String name, ShapedRecipeBuilder builder, String... modIds) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, name);
        ConditionalRecipe.Builder conditional = ConditionalRecipe.builder();
        for (String modId : modIds) {
            conditional.addCondition(new ModLoadedCondition(modId));
        }
        conditional.addRecipe(builder::save).build(consumer, id);
    }

    // A recipe that uses the camel pack upgrade type
    private record UpgradeRecipe(FinishedRecipe recipe) implements FinishedRecipe {
        @Override
        public void serializeRecipeData(JsonObject json) {
            recipe.serializeRecipeData(json);
        }

        @Override
        public ResourceLocation getId() {
            return recipe.getId();
        }

        @Override
        public RecipeSerializer<?> getType() {
            return ScarcityRecipeSerializers.CAMEL_PACK_UPGRADE.get();
        }

        @Override
        public JsonObject serializeAdvancement() {
            return recipe.serializeAdvancement();
        }

        @Override
        public ResourceLocation getAdvancementId() {
            return recipe.getAdvancementId();
        }
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
