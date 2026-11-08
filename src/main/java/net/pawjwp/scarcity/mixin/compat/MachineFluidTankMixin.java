package net.pawjwp.scarcity.mixin.compat;

import com.enderio.machines.common.io.fluid.MachineFluidTank;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
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
@Mixin(value = MachineFluidTank.class, remap = false)
public class MachineFluidTankMixin {

    @Shadow
    private FluidStack fluid;

    // Merges the two fluid stacks
    // Affects all Ender IO machines and tanks
    @Inject(method = "fill", at = @At("RETURN"), require = 0)
    private void scarcity$mergePurity(FluidStack resource, IFluidHandler.FluidAction action, CallbackInfoReturnable<Integer> cir) {
        if (ScarcityConfig.enableThirstPurityMerging && action.execute() && cir.getReturnValue() > 0) {
            ThirstCompat.mergePurity(fluid, resource, cir.getReturnValue());
        }
    }
}
