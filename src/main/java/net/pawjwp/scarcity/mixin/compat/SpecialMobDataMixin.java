package net.pawjwp.scarcity.mixin.compat;

import net.minecraft.world.entity.Mob;
import net.pawjwp.scarcity.SunlightSensitivity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// SpecialMobs overrides fireImmune() and setRemainingFireTicks()
// This overrides any built-in fire immunity when ScarcitySunlightSensitivity is true.
@Pseudo
@Mixin(targets = "fathertoast.specialmobs.common.entity.SpecialMobData", remap = false)
public class SpecialMobDataMixin {

    @Shadow
    private Mob theEntity;

    @Inject(method = "isImmuneToFire", at = @At("HEAD"), cancellable = true, require = 0)
    private void scarcity$overrideFireImmunity(CallbackInfoReturnable<Boolean> cir) {
        if (SunlightSensitivity.getState(theEntity) == SunlightSensitivity.State.SENSITIVE) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "isImmuneToBurning", at = @At("HEAD"), cancellable = true, require = 0)
    private void scarcity$overrideBurnImmunity(CallbackInfoReturnable<Boolean> cir) {
        if (SunlightSensitivity.getState(theEntity) == SunlightSensitivity.State.SENSITIVE) {
            cir.setReturnValue(false);
        }
    }
}
