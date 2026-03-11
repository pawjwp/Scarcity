package net.pawjwp.scarcity.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.SeagrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.material.FluidState;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SeagrassBlock.class)
public class SeagrassBlockMixin {

    @Inject(method = "getStateForPlacement", at = @At("HEAD"), cancellable = true)
    private void scarcity$preventPlacementInFlowingWater(BlockPlaceContext context, CallbackInfoReturnable<BlockState> cir) {
        if (!ScarcityConfig.enableWaterPlantSourcePrevention) return;
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        if (fluidState.is(FluidTags.WATER) && !fluidState.isSource()) {
            cir.setReturnValue(null);
        }
    }

    @Inject(method = "performBonemeal", at = @At("HEAD"), cancellable = true)
    private void scarcity$preventBonemealInFlowingWater(ServerLevel level, RandomSource random, BlockPos pos, BlockState state, CallbackInfo ci) {
        if (!ScarcityConfig.enableWaterPlantSourcePrevention) return;
        FluidState fluidAbove = level.getFluidState(pos.above());
        if (fluidAbove.is(FluidTags.WATER) && !fluidAbove.isSource()) {
            ci.cancel();
        }
    }
}
