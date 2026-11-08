package net.pawjwp.scarcity.mixin.compat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
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
import org.spongepowered.asm.mixin.injection.ModifyArg;
import thedarkcolour.exdeorum.item.PorcelainBucket;

// Requires enable_thirst_purity_fixes to be enabled in config.
@Pseudo
@Mixin(PorcelainBucket.class)
public class PorcelainBucketMixin {

    // Reads the purity of the water source block before the bucket picks it up
    @WrapOperation(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/BucketPickup;pickupBlock(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;"
            ),
            require = 0
    )
    private ItemStack scarcity$readSourcePurity(BucketPickup pickup, LevelAccessor level, BlockPos pos, BlockState state, Operation<ItemStack> original, @Share("purity") LocalRef<Integer> purity) {
        if (ScarcityConfig.enableThirstPurityFixes && level instanceof Level sourceLevel) {
            purity.set(ThirstCompat.getSourcePurity(sourceLevel, pos));
        }
        return original.call(pickup, level, pos, state);
    }

    // Adds the water source's purity to the filled bucket (based on how Thirst handles vanilla buckets)
    @ModifyArg(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemUtils;createFilledResult(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"
            ),
            index = 2,
            require = 0
    )
    private ItemStack scarcity$addSourcePurity(ItemStack result, @Share("purity") LocalRef<Integer> purity) {
        if (purity.get() != null) {
            ThirstCompat.addPurity(result, purity.get());
        }
        return result;
    }
}
