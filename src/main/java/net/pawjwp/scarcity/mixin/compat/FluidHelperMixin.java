package net.pawjwp.scarcity.mixin.compat;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thedarkcolour.exdeorum.blockentity.helper.FluidHelper;

// Requires enable_thirst_purity_merging to be enabled in config.
@Pseudo
@Mixin(value = FluidHelper.class, remap = false)
public class FluidHelperMixin {

    // Merges the two fluid stacks, setting the resulting fluid's purity to the lower of the two purity values
    // Affects Ex Deorum barrels and crucibles
    @Inject(method = "fill", at = @At("RETURN"), require = 0)
    private void scarcity$mergeToLowestPurity(FluidStack resource, IFluidHandler.FluidAction action, CallbackInfoReturnable<Integer> cir) {
        if (ScarcityConfig.enableThirstPurityMerging && action.execute() && cir.getReturnValue() > 0) {
            ThirstCompat.mergePurity(((FluidTank) (Object) this).getFluid(), resource, cir.getReturnValue());
        }
    }
}
