package net.pawjwp.scarcity.mixin.compat;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraftforge.fluids.FluidStack;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Requires enable_thirst_purity_merging to be enabled in config.
@Pseudo
@Mixin(targets = "mekanism.common.capabilities.fluid.BasicFluidTank", remap = false)
public class BasicFluidTankMixin {

    @Shadow
    protected FluidStack stored;

    // Merges the two fluid stacks, setting the resulting fluid's purity to the lower of the two purity values
    // Affects all Mekanism fluid tanks
    @Inject(
            method = "insert",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/fluids/FluidStack;grow(I)V",
                    shift = At.Shift.AFTER
            ),
            require = 0
    )
    private void scarcity$mergeToLowestPurity(CallbackInfoReturnable<FluidStack> cir, @Local(argsOnly = true) FluidStack stack) {
        if (ScarcityConfig.enableThirstPurityMerging) {
            ThirstCompat.mergeToLowestPurity(stored, stack);
        }
    }
}
