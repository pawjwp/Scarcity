package net.pawjwp.scarcity.mixin.compat;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Requires enable_thirst_purity_fixes to be enabled in config.
@Pseudo
@Mixin(targets = "thedarkcolour.exdeorum.item.PorcelainBucket$CapabilityProvider", remap = false)
public class PorcelainBucketCapabilityProviderMixin {

    @Shadow
    private ItemStack container;

    // Adds the bucket's purity to the water when emptied
    @ModifyReturnValue(method = "getFluid", at = @At("RETURN"), require = 0)
    private FluidStack scarcity$addBucketPurity(FluidStack fluid) {
        if (ScarcityConfig.enableThirstPurityFixes) {
            ThirstCompat.copyItemPurityToFluid(container, fluid);
        }
        return fluid;
    }

    // Adds the water's purity to the bucket when filled
    @Inject(method = "setFluid", at = @At("TAIL"), require = 0)
    private void scarcity$addFluidPurity(FluidStack fluidStack, CallbackInfo ci) {
        if (ScarcityConfig.enableThirstPurityFixes) {
            ThirstCompat.copyFluidPurityToItem(fluidStack, container);
        }
    }
}
