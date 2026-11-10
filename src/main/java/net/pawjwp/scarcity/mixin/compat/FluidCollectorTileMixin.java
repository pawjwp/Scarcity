package net.pawjwp.scarcity.mixin.compat;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// Requires enable_thirst_purity_fixes to be enabled in config.
@Pseudo
@Mixin(targets = "com.buuz135.industrial.block.resourceproduction.tile.FluidCollectorTile", remap = false)
public class FluidCollectorTileMixin {

    // Reads the purity of the water before picking it up
    @ModifyExpressionValue(
            method = "work",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/material/FluidState;getType()Lnet/minecraft/world/level/material/Fluid;",
                    remap = true
            ),
            require = 0
    )
    private Fluid scarcity$readSourcePurity(Fluid fluid, @Local BlockPos pointed, @Share("purity") LocalRef<Integer> purity) {
        if (ScarcityConfig.enableThirstPurityFixes) {
            purity.set(ThirstCompat.getSourcePurity(((BlockEntity) (Object) this).getLevel(), pointed));
        }
        return fluid;
    }

    // Adds the water's purity to the picked up water (based on how Thirst handles vanilla buckets)
    @ModifyArg(
            method = "work",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/hrznstudio/titanium/component/fluid/SidedFluidTankComponent;fillForced(Lnet/minecraftforge/fluids/FluidStack;Lnet/minecraftforge/fluids/capability/IFluidHandler$FluidAction;)I"
            ),
            index = 0,
            require = 0
    )
    private FluidStack scarcity$addSourcePurity(FluidStack fluid, @Share("purity") LocalRef<Integer> purity) {
        if (purity.get() != null) {
            ThirstCompat.addPurity(fluid, purity.get());
        }
        return fluid;
    }
}
