package net.pawjwp.scarcity.mixin.compat;

import net.minecraftforge.fluids.FluidStack;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Makes the Refinery match recipes by fluid type only, ignoring NBT.
// Requires enable_thermal_patches to be enabled in config.
@Pseudo
@Mixin(targets = "cofh.thermal.core.util.managers.machine.RefineryRecipeManager", remap = false)
public class RefineryRecipeManagerMixin {

    @Redirect(
            method = {"getRecipe", "addRecipe"},
            at = @At(
                    value = "INVOKE",
                    target = "Lcofh/core/util/helpers/FluidHelper;fluidHashcode(Lnet/minecraftforge/fluids/FluidStack;)I",
                    remap = false
            ),
            require = 0
    )
    private int scarcity$ignoreFluidNBT(FluidStack stack) {
        if (ScarcityConfig.enableThermalPatches) {
            return stack.getFluid().hashCode();
        }
        return stack.getTag() != null ? stack.getFluid().hashCode() + 31 * stack.getTag().hashCode() : stack.getFluid().hashCode();
    }
}
