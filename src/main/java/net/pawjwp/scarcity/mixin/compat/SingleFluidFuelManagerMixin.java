package net.pawjwp.scarcity.mixin.compat;

import net.minecraftforge.fluids.FluidStack;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

// Makes single-fluid dynamos (Compression, Magmatic, Numismatic, etc.) match fuels by fluid type only, ignoring NBT.
// Requires enable_thermal_patches to be enabled in config.
@Pseudo
@Mixin(targets = "cofh.thermal.lib.util.managers.SingleFluidFuelManager", remap = false)
public class SingleFluidFuelManagerMixin {

    @Redirect(
            method = {"getFuel", "addFuel"},
            at = @At(
                    value = "INVOKE",
                    target = "Lcofh/core/util/helpers/FluidHelper;fluidHashcode(Lnet/minecraftforge/fluids/FluidStack;)I",
                    remap = false
            ),
            require = 0
    )
    private static int scarcity$ignoreFluidNBT(FluidStack stack) {
        if (ScarcityConfig.enableThermalPatches) {
            return stack.getFluid().hashCode();
        }
        return stack.getTag() != null ? stack.getFluid().hashCode() + 31 * stack.getTag().hashCode() : stack.getFluid().hashCode();
    }
}