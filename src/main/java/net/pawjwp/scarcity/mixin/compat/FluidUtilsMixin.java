package net.pawjwp.scarcity.mixin.compat;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// Requires enable_thirst_purity_fixes to be enabled in config.
@Pseudo
@Mixin(targets = "me.desht.pneumaticcraft.common.util.FluidUtils", remap = false)
public class FluidUtilsMixin {

    // Adds the water's purity to the picked up water (based on how Thirst handles vanilla buckets)
    @ModifyArg(
            method = "tryPickupFluid",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/fluids/capability/templates/FluidTank;setFluid(Lnet/minecraftforge/fluids/FluidStack;)V"
            ),
            require = 0
    )
    private static FluidStack scarcity$addSourcePurity(FluidStack fluid, @Local(argsOnly = true) Level world, @Local(argsOnly = true) BlockPos pos) {
        if (ScarcityConfig.enableThirstPurityFixes) {
            Integer purity = ThirstCompat.getSourcePurity(world, pos);
            if (purity != null) {
                ThirstCompat.addPurity(fluid, purity);
            }
        }
        return fluid;
    }
}
