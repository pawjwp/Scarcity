package net.pawjwp.scarcity.mixin.compat;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

// Requires enable_thirst_purity_fixes to be enabled in config.
@Pseudo
@Mixin(targets = "com.simibubi.create.content.fluids.pipes.VanillaFluidTargets", remap = false)
public class VanillaFluidTargetsMixin {

    // Ensure that a cauldron's water purity is maintained when drained with Create pipes
    @ModifyReturnValue(method = "drainBlock", at = @At("RETURN"), require = 0)
    private static FluidStack scarcity$keepCauldronPurity(FluidStack drained, @Local(argsOnly = true) BlockState state) {
        if (ScarcityConfig.enableThirstPurityFixes) {
            ThirstCompat.addBlockPurity(drained, state);
        }
        return drained;
    }
}
