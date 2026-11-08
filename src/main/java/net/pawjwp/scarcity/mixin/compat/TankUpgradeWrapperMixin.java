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
@Mixin(targets = "net.p3pp3rf1y.sophisticatedcore.upgrades.tank.TankUpgradeWrapper", remap = false)
public class TankUpgradeWrapperMixin {

    // Merges the two fluid stacks, setting the resulting fluid's purity to the lower of the two purity values
    // Affects Sophisticated Backpacks and Storage tank upgrades
    // Runs when the stored amount increases, before its contents are saved the item's NBT
    @WrapOperation(
            method = "fill",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/fluids/FluidStack;setAmount(I)V"
            ),
            require = 0
    )
    private void scarcity$mergeToLowestPurity(FluidStack stored, int amount, Operation<Void> original, @Local(argsOnly = true) FluidStack resource) {
        int addedAmount = amount - stored.getAmount();
        original.call(stored, amount);
        if (ScarcityConfig.enableThirstPurityMerging) {
            ThirstCompat.mergePurity(stored, resource, addedAmount);
        }
    }
}
