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
import slimeknights.tconstruct.library.fluid.FluidTankBase;

// Requires enable_thirst_purity_merging to be enabled in config.
@Pseudo
@Mixin(value = FluidTankBase.class, remap = false)
public class FluidTankBaseMixin {

    // Merges the two fluid stacks, setting the resulting fluid's purity to the lower of the two purity values
    // Affects Tinkers' Construct tanks, whose fill replaces the Forge FluidTank fill that Thirst Was Reclaimed already merges
    @Inject(method = "fill", at = @At("RETURN"), require = 0)
    private void scarcity$mergeToLowestPurity(FluidStack resource, IFluidHandler.FluidAction action, CallbackInfoReturnable<Integer> cir) {
        if (ScarcityConfig.enableThirstPurityMerging && action.execute() && cir.getReturnValue() > 0) {
            ThirstCompat.mergePurity(((FluidTank) (Object) this).getFluid(), resource, cir.getReturnValue());
        }
    }
}
