package net.pawjwp.scarcity.mixin.compat;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// Requires enable_thirst_purity_merging to be enabled in config.
@Mixin(value = FluidHandlerItemStack.class, remap = false)
public class FluidHandlerItemStackMixin {

    // Merges the two fluid stacks, setting the resulting fluid's purity to the lower of the two purity values
    // Affects all items built Forge's FluidHandlerItemStack
    @ModifyArg(
            method = "fill",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/fluids/capability/templates/FluidHandlerItemStack;setFluid(Lnet/minecraftforge/fluids/FluidStack;)V"
            ),
            require = 0
    )
    private FluidStack scarcity$mergeToLowestPurity(FluidStack fluid, @Local(argsOnly = true) FluidStack resource, @Local int fillAmount) {
        if (ScarcityConfig.enableThirstPurityMerging) {
            ThirstCompat.mergePurity(fluid, resource, fillAmount);
        }
        return fluid;
    }
}
