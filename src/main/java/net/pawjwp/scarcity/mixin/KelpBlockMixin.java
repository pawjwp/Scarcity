package net.pawjwp.scarcity.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.KelpBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.FluidState;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KelpBlock.class)
public class KelpBlockMixin {

    @ModifyReturnValue(method = "canGrowInto", at = @At("RETURN"))
    private boolean scarcity$preventGrowthIntoFlowingWater(boolean original, BlockState state) {
        if (!ScarcityConfig.enableWaterPlantSourcePrevention || !original) return original;
        return state.getFluidState().isSource();
    }

    @Inject(method = "getStateForPlacement", at = @At("HEAD"), cancellable = true, require = 0)
    private void scarcity$preventPlacementInFlowingWater(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        if (!ScarcityConfig.enableWaterPlantSourcePrevention) return;
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        if (fluidState.is(FluidTags.WATER) && !fluidState.isSource()) {
            cir.setReturnValue(null);
        }
    }
}
