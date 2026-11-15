package net.pawjwp.scarcity.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.block.KelpBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(KelpBlock.class)
public class KelpBlockMixin {

    @ModifyReturnValue(method = "canGrowInto", at = @At("RETURN"))
    private boolean scarcity$preventGrowthIntoFlowingWater(boolean original, BlockState state) {
        if (!ScarcityConfig.enableWaterPlantSourcePrevention || !original) return original;
        return state.getFluidState().isSource();
    }
}
