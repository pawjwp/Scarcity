package net.pawjwp.scarcity.mixin.compat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraftforge.fluids.FluidStack;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

// Requires enable_thirst_purity_merging to be enabled in config.
@Pseudo
@Mixin(targets = "mekanism.common.capabilities.fluid.BasicFluidTank", remap = false)
public class BasicFluidTankMixin {

    // Merges the two fluid stacks, setting the resulting fluid's purity to the lower of the two purity values
    // Affects all Mekanism fluid tanks
    @WrapOperation(
            method = "insert",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/fluids/FluidStack;grow(I)V"
            ),
            require = 0
    )
    private void scarcity$mergeToLowestPurity(FluidStack stored, int toAdd, Operation<Void> original, @Local(argsOnly = true) FluidStack stack) {
        original.call(stored, toAdd);
        if (ScarcityConfig.enableThirstPurityMerging) {
            ThirstCompat.mergePurity(stored, stack, toAdd);
        }
    }
}
