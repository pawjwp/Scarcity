package net.pawjwp.scarcity.mixin.compat;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.Entity;
import net.pawjwp.scarcity.BurnRule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

// Override fire immunity for SpecialMobs zombified piglin variants by intercepting
// fireImmune(), setRemainingFireTicks(), and isSunSensitive()
@Pseudo
@Mixin(targets = "fathertoast.specialmobs.common.entity.zombifiedpiglin._SpecialZombifiedPiglinEntity", remap = false)
public abstract class SpecialZombifiedPiglinEntityMixin {

    @ModifyExpressionValue(
            method = {"fireImmune", "isSunSensitive"},
            at = @At(
                    value = "INVOKE",
                    target = "Lfathertoast/specialmobs/common/entity/SpecialMobData;isImmuneToFire()Z",
                    remap = false
            ),
            require = 0
    )
    private boolean scarcity$overrideImmuneToFire(boolean original) {
        return switch (BurnRule.currentOutcome((Entity) (Object) this)) {
            case BURN -> false;
            case NO_BURN -> true;
            case NO_RULE -> original;
        };
    }

    @ModifyExpressionValue(
            method = {"setRemainingFireTicks", "isSunSensitive"},
            at = @At(
                    value = "INVOKE",
                    target = "Lfathertoast/specialmobs/common/entity/SpecialMobData;isImmuneToBurning()Z",
                    remap = false
            ),
            require = 0
    )
    private boolean scarcity$overrideImmuneToBurning(boolean original) {
        return switch (BurnRule.currentOutcome((Entity) (Object) this)) {
            case BURN -> false;
            case NO_BURN -> true;
            case NO_RULE -> original;
        };
    }
}
