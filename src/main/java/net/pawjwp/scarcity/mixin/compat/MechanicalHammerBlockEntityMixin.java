package net.pawjwp.scarcity.mixin.compat;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.pawjwp.scarcity.compat.Mods;
import net.pawjwp.scarcity.compat.tinkers.TinkersCompat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thedarkcolour.exdeorum.blockentity.MechanicalHammerBlockEntity;

// Keeps Tinkers tools inside Ex Deorum's mechanical hammer after getting broken
// This prevents them from being destroyed like vanilla tools
// Broken tools grant no speed bonus and do not continue to take damage
@Pseudo
@Mixin(value = MechanicalHammerBlockEntity.class, remap = false)
public class MechanicalHammerBlockEntityMixin {

    @Shadow
    private float efficiency;

    @Inject(method = "damageHammer", at = @At("HEAD"), cancellable = true, require = 0)
    private void scarcity$damageTinkersTool(RandomSource rand, CallbackInfo ci) {
        var self = (MechanicalHammerBlockEntity) (Object) this;
        ItemStack hammer = self.inventory.getStackInSlot(MechanicalHammerBlockEntity.HAMMER_SLOT);

        if (Mods.TINKERS && TinkersCompat.isTinkersTool(hammer)) {
            TinkersCompat.damageTool(hammer);
            if (TinkersCompat.isBroken(hammer)) {
                this.efficiency = 1f;
            }
            self.setChanged();
            ci.cancel();
        }
    }

    @Inject(method = "onHammerChanged", at = @At("TAIL"), require = 0)
    private void scarcity$ignoreBrokenTools(CallbackInfo ci) {
        var self = (MechanicalHammerBlockEntity) (Object) this;
        ItemStack hammer = self.inventory.getStackInSlot(MechanicalHammerBlockEntity.HAMMER_SLOT);

        if (Mods.TINKERS && TinkersCompat.isBroken(hammer)) {
            this.efficiency = 1f;
        }
    }
}