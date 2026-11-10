package net.pawjwp.scarcity.mixin.compat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

// Requires enable_thirst_purity_fixes to be enabled in config.
// Targets the anonymous BUCKET dispenser behavior for contraptions ($5 in Create 6.0.8)
@Pseudo
@Mixin(targets = "com.simibubi.create.AllMountedDispenseItemBehaviors$5", remap = false)
public class MountedBucketDispenseBehaviorMixin {

    // Adds the water's purity to buckets filled by dispensers on contraptions (based on how Thirst handles vanilla dispensers)
    @WrapOperation(
            method = "execute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/BucketPickup;pickupBlock(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;",
                    remap = true
            ),
            require = 0
    )
    private ItemStack scarcity$addBucketPurity(BucketPickup pickup, LevelAccessor level, BlockPos pos, BlockState state, Operation<ItemStack> original) {
        Integer purity = ScarcityConfig.enableThirstPurityFixes && level instanceof Level sourceLevel ? ThirstCompat.getSourcePurity(sourceLevel, pos) : null;
        ItemStack bucket = original.call(pickup, level, pos, state);
        if (purity != null) {
            ThirstCompat.addPurity(bucket, purity);
        }
        return bucket;
    }
}
