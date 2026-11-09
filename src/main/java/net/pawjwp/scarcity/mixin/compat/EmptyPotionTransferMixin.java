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
import slimeknights.mantle.fluid.transfer.EmptyPotionTransfer;

// Requires enable_thirst_purity_fixes to be enabled in config.
@Pseudo
@Mixin(value = EmptyPotionTransfer.class, remap = false)
public class EmptyPotionTransferMixin {

    // Water bottles emptied into Tinkers' Construct tanks maintain their purity
    @ModifyReturnValue(method = "getFluid", at = @At("RETURN"), require = 0)
    private FluidStack scarcity$addWaterPurity(FluidStack fluid, @Local(argsOnly = true) ItemStack stack) {
        if (ScarcityConfig.enableThirstPurityFixes) {
            ThirstCompat.copyItemPurityToFluid(stack, fluid);
        }
        return fluid;
    }
}
