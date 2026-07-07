package net.pawjwp.scarcity.mixin.compat;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.pawjwp.scarcity.compat.Mods;
import net.pawjwp.scarcity.compat.tinkers.TinkersCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thedarkcolour.exdeorum.loot.CrookLootModifier;

// Overrides Ex Deorum's tag-based crook detection for Scarcity's Crooks to support the following functionality
// - Broken tools should not longer be treated as valid crooks even though they would still have the tag
@Pseudo
@Mixin(value = CrookLootModifier.class, remap = false)
public class CrookLootModifierMixin {

    @Inject(method = "doApply", at = @At("HEAD"), cancellable = true, require = 0)
    private void scarcity$skipBrokenCrooks(ObjectArrayList<ItemStack> generatedLoot, LootContext context,
                                           CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir) {
        ItemStack tool = context.getParamOrNull(LootContextParams.TOOL);

        if (tool != null && Mods.TINKERS && TinkersCompat.isBroken(tool)) {
            cir.setReturnValue(generatedLoot);
        }
    }
}