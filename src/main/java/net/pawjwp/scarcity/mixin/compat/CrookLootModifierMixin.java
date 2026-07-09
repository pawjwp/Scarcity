package net.pawjwp.scarcity.mixin.compat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.pawjwp.scarcity.compat.Mods;
import net.pawjwp.scarcity.compat.tinkers.TinkersCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thedarkcolour.exdeorum.loot.CrookLootModifier;

import java.util.List;

// Overrides Ex Deorum's tag-based crook detection for Scarcity's Crooks to support the following functionality
// - Broken tools should no longer be treated as valid crooks even though they would still have the tag
// - Makes Tinkers' Luck modifier work properly for leaf loot drops
@Pseudo
@Mixin(value = CrookLootModifier.class, remap = false)
public class CrookLootModifierMixin {

    @Inject(method = "doApply", at = @At("HEAD"), cancellable = true, require = 0)
    private void scarcity$skipBrokenCrooks(ObjectArrayList<ItemStack> generatedLoot, LootContext context,
                                           CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir) {
        ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);

        if (tool != null && Mods.TINKERS && ScarcityConfig.enableExDeorumToolOverrides && TinkersCompat.isBroken(tool)) {
            cir.setReturnValue(generatedLoot);
        }
    }

    @WrapOperation(
            method = "doApply",
            at = @At(
                    value = "INVOKE",
                    target = "Lthedarkcolour/exdeorum/loot/CrookLootModifier;reRollDrops(Lnet/minecraft/world/level/storage/loot/LootContext;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)Ljava/util/List;",
                    remap = false
            ),
            require = 0
    )
    private List<ItemStack> scarcity$carryModifierFortune(LootContext context, ItemStack nonCrook, BlockState state,
                                                          Operation<List<ItemStack>> original) {
        if (Mods.TINKERS && ScarcityConfig.enableExDeorumToolOverrides && !nonCrook.isEnchanted()) {
            ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);

            if (tool != null && TinkersCompat.isTinkersTool(tool)) {
                int fortune = tool.getEnchantmentLevel(Enchantments.BLOCK_FORTUNE);
                if (fortune > 0) {
                    nonCrook.enchant(Enchantments.BLOCK_FORTUNE, fortune);
                }
            }
        }
        return original.call(context, nonCrook, state);
    }
}
