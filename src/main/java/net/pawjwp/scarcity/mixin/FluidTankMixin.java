package net.pawjwp.scarcity.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import net.pawjwp.scarcity.config.ScarcityConfig.PurityMergeMethod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Requires thirst_purity_merge_method to be set to anything other than LOWEST in config.
// Uses a higher priority than Thirst Was Reclaimed's FluidTank mixin, so this mixin's merge runs after Thirst Was Reclaimed's lowest purity merge
@Mixin(value = FluidTank.class, priority = 1100, remap = false)
public class FluidTankMixin {

    @Shadow
    protected FluidStack fluid;

    // Saves the stored fluid's purity before filling, since Thirst Was Reclaimed lowers it after filling
    @Inject(method = "fill", at = @At("HEAD"))
    private void scarcity$savePurity(FluidStack resource, IFluidHandler.FluidAction action, CallbackInfoReturnable<Integer> cir, @Share("purity") LocalIntRef purity) {
        if (ScarcityConfig.thirstPurityMergeMethod != PurityMergeMethod.LOWEST && action.execute()) {
            purity.set(ThirstCompat.getPurity(fluid));
        }
    }

    // Merges the two fluid stacks
    // Affects all tanks that use Forge's FluidTank fill, replacing Thirst Was Reclaimed's lowest purity merge
    @Inject(method = "fill", at = @At("RETURN"))
    private void scarcity$mergePurity(FluidStack resource, IFluidHandler.FluidAction action, CallbackInfoReturnable<Integer> cir, @Share("purity") LocalIntRef purity) {
        if (ScarcityConfig.thirstPurityMergeMethod != PurityMergeMethod.LOWEST && action.execute() && cir.getReturnValue() > 0) {
            ThirstCompat.mergePurity(fluid, purity.get(), resource, cir.getReturnValue());
        }
    }
}
