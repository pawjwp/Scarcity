package net.pawjwp.scarcity.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockEntity.class)
public class FallingBlockEntityMixin {

    @Inject(method = "callOnBrokenAfterFall", at = @At("HEAD"))
    private void scarcity$chanceBreakCollidedBlock(Block fallingBlock, BlockPos pos, CallbackInfo ci) {
        Level level = ((FallingBlockEntity)(Object)this).level();
        if (level.isClientSide || !ScarcityConfig.enableFallingBlockBreakingAdjustments) return;

        double hardness = level.getBlockState(pos).getDestroySpeed(level, pos);
        if (hardness < 0) return; // negative hardness is unbreakable

        // blocks with a hardness of 0 always break, high hardness blocks rarely break
        double breakChance = (1.0 / (1.0 + hardness));

        if (level.random.nextDouble() <= breakChance) {
            level.destroyBlock(pos, true);
        }
    }
}
