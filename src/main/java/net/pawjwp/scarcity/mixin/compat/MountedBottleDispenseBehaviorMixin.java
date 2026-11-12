package net.pawjwp.scarcity.mixin.compat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// Requires enable_thirst_purity_fixes or enable_bottle_pickup_adjustments to be enabled in config.
// Targets the anonymous BOTTLE dispenser behavior for contraptions ($7 in Create 6.0.8)
@Pseudo
@Mixin(targets = "com.simibubi.create.AllMountedDispenseItemBehaviors$7", remap = false)
public class MountedBottleDispenseBehaviorMixin {

    private static final int bottlesPerBucket = 4;

    // Reads the purity of the water before picking it up
    // If enable_bottle_pickup_adjustments is true, it requires four bottles and removes the water source
    @WrapOperation(
            method = "doExecute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;",
                    remap = true
            ),
            require = 0
    )
    private FluidState scarcity$readWater(Level level, BlockPos pos, Operation<FluidState> original, @Local(argsOnly = true) ItemStack stack, @Share("purity") LocalRef<Integer> purity) {
        FluidState fluidState = original.call(level, pos);
        if (!fluidState.is(FluidTags.WATER)) {
            return fluidState;
        }

        if (ScarcityConfig.enableThirstPurityFixes) {
            purity.set(ThirstCompat.getWaterPurity(level, pos));
        }

        // Return empty if the block isn't a source block or there aren't four bottles
        BlockState state = level.getBlockState(pos);
        if (ScarcityConfig.enableBottlePickupAdjustments && (stack.getCount() < bottlesPerBucket || !(state.getBlock() instanceof BucketPickup pickup) || pickup.pickupBlock(level, pos, state).isEmpty())) {
            return Fluids.EMPTY.defaultFluidState();
        }
        return fluidState;
    }

    // Adds the water's purity to the filled bottle (based on how Thirst handles vanilla dispensers)
    // If enable_bottle_pickup_adjustments is true, it passes four bottles
    @ModifyArg(
            method = "doExecute",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/api/contraption/dispenser/MountedDispenseBehavior;placeItemInInventory(Lnet/minecraft/world/item/ItemStack;Lcom/simibubi/create/content/contraptions/behaviour/MovementContext;Lnet/minecraft/core/BlockPos;)V",
                    ordinal = 1
            ),
            index = 0,
            require = 0
    )
    private ItemStack scarcity$fillBottles(ItemStack item, @Share("purity") LocalRef<Integer> purity) {
        if (purity.get() != null) {
            ThirstCompat.addPurity(item, purity.get());
        }
        return ScarcityConfig.enableBottlePickupAdjustments ? item.copyWithCount(bottlesPerBucket) : item;
    }

    // If enable_bottle_pickup_adjustments is true, consume four bottles
    @ModifyArg(
            method = "doExecute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V",
                    ordinal = 1,
                    remap = true
            ),
            require = 0
    )
    private int scarcity$useFourBottles(int amount) {
        return ScarcityConfig.enableBottlePickupAdjustments ? bottlesPerBucket : amount;
    }
}
