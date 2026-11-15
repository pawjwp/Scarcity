package net.pawjwp.scarcity.mixin.compat;

import net.minecraft.world.entity.Mob;
import net.pawjwp.scarcity.SunlightSensitivity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// SpecialMobs mobs override fireImmune() and setRemainingFireTicks() to check if SpecialMobData considers them immune.
// This makes SpecialMobData return not immune when ScarcitySunlightSensitivity is true.
@Pseudo
@Mixin(targets = "fathertoast.specialmobs.common.entity.SpecialMobData", remap = false)
public class SpecialMobDataMixin {

    @Shadow
    @Final
    private Mob theEntity;

    @Shadow
    private boolean isImmuneToFire;

    @Shadow
    private boolean isImmuneToBurning;

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

    // Saves the mob's normal immunities instead of the overridden ones, so it will return to normal behavior if the tag is removed
    @ModifyArg(method = "writeToNBT", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/CompoundTag;putBoolean(Ljava/lang/String;Z)V", remap = true), index = 1, require = 0)
    private boolean scarcity$saveImmunity(String key, boolean value) {
        return switch (key) {
            case "FireImmune" -> isImmuneToFire;
            case "BurningImmune" -> isImmuneToBurning;
            default -> value;
        };
    }
}
