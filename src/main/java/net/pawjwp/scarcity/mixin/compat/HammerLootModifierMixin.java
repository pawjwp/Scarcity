package net.pawjwp.scarcity.mixin.compat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.pawjwp.scarcity.compat.Mods;
import net.pawjwp.scarcity.compat.tinkers.TinkersCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import thedarkcolour.exdeorum.loot.HammerLootModifier;
import thedarkcolour.exdeorum.recipe.RecipeUtil;
import thedarkcolour.exdeorum.recipe.hammer.HammerRecipe;

// Overrides Ex Deorum's tag-based hammer detection for Scarcity's Crushing Hammers to support the following functionality
// - Broken tools should not longer be treated as valid hammers even though they would still have the tag
// - The heavy hammering modifier changes a hammer from a normal hammer to a compressed one, which is not possible with tags
@Pseudo
@Mixin(value = HammerLootModifier.class, remap = false)
public class HammerLootModifierMixin {

    @WrapOperation(
            method = "doApply",
            at = @At(
                    value = "INVOKE",
                    target = "Lthedarkcolour/exdeorum/loot/HammerLootModifier;getRecipe(Lnet/minecraft/world/item/Item;)Lthedarkcolour/exdeorum/recipe/hammer/HammerRecipe;",
                    remap = false
            ),
            require = 0
    )
    private HammerRecipe scarcity$detectHammerById(HammerLootModifier instance, Item itemForm, Operation<HammerRecipe> original,
                                                   ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);

        if (tool != null && Mods.TINKERS && ScarcityConfig.enableExDeorumToolOverrides) {
            if (TinkersCompat.isBroken(tool)) {
                return null;
            }
            if (TinkersCompat.isCrushingHammer(tool)) {
                return TinkersCompat.hasHeavyHammering(tool)
                        ? RecipeUtil.getCompressedHammerRecipe(itemForm)
                        : RecipeUtil.getHammerRecipe(itemForm);
            }
        }

        return original.call(instance, itemForm);
    }
}