package net.pawjwp.scarcity.mixin.compat;

import net.minecraftforge.fluids.FluidStack;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

// Makes CoFH's fluid hash ignore water purity, so Thermal machines and dynamos treat water of any purity as valid for recipes and fuel
// Requires enable_thirst_purity_fixes to be enabled in config.
@Pseudo
@Mixin(targets = "cofh.core.util.helpers.FluidHelper", remap = false)
public class CoFHFluidHelperMixin {

    // Removes the purity tag from a fluid before it is hashed without affecting other NBT
    @ModifyVariable(method = "fluidHashcode", at = @At("HEAD"), argsOnly = true, require = 0)
    private static FluidStack scarcity$ignoreWaterPurity(FluidStack stack) {
        return ScarcityConfig.enableThirstPurityFixes ? ThirstCompat.withoutPurity(stack) : stack;
    }
}
