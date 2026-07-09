package net.pawjwp.scarcity.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import net.pawjwp.scarcity.datagen.recipe.ScarcityCraftingRecipes;

import java.util.function.Consumer;

public class ScarcityRecipes extends RecipeProvider implements IConditionBuilder {
    public ScarcityRecipes(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ScarcityCraftingRecipes.register(consumer);
    }
}
