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
import slimeknights.tconstruct.smeltery.block.entity.tank.SmelteryTank;

// Requires enable_thirst_purity_merging to be enabled in config.
@Pseudo
@Mixin(value = SmelteryTank.class, remap = false)
public class SmelteryTankMixin {

    // Merges the two fluid stacks, setting the resulting fluid's purity to the lower of the two purity values
    // Affects the Tinkers' Construct smeltery and foundry, which grow the matching fluid when one of their fluids is added to
    @WrapOperation(
            method = "fill",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/fluids/FluidStack;grow(I)V"
            ),
            require = 0
    )
    private void scarcity$mergeToLowestPurity(FluidStack stored, int usable, Operation<Void> original, @Local(argsOnly = true) FluidStack resource) {
        original.call(stored, usable);
        if (ScarcityConfig.enableThirstPurityMerging) {
            ThirstCompat.mergePurity(stored, resource, usable);
        }
    }
}
