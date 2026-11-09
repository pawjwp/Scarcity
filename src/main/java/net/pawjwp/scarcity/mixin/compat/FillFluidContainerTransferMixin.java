package net.pawjwp.scarcity.mixin.compat;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import slimeknights.mantle.fluid.transfer.FillFluidContainerTransfer;

// Requires enable_thirst_purity_fixes to be enabled in config.
@Pseudo
@Mixin(value = FillFluidContainerTransfer.class, remap = false)
public class FillFluidContainerTransferMixin {

    // Water containers filled from Tinkers' Construct tanks maintain the water's purity
    @ModifyReturnValue(method = "getFilled", at = @At("RETURN"), require = 0)
    private ItemStack scarcity$addWaterPurity(ItemStack filled, @Local(argsOnly = true) FluidStack drained) {
        return ScarcityConfig.enableThirstPurityFixes ? ThirstCompat.withFluidPurity(filled, drained) : filled;
    }
}
