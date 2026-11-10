package net.pawjwp.scarcity.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.wrappers.BucketPickupHandlerWrapper;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

// Requires enable_thirst_purity_fixes to be enabled in config.
@Mixin(value = BucketPickupHandlerWrapper.class, remap = false)
public class BucketPickupHandlerWrapperMixin {

    @Shadow
    @Final
    protected Level world;

    @Shadow
    @Final
    protected BlockPos blockPos;

    // Reads the purity of the water before picking it up
    @WrapOperation(
            method = {
                    "drain(Lnet/minecraftforge/fluids/FluidStack;Lnet/minecraftforge/fluids/capability/IFluidHandler$FluidAction;)Lnet/minecraftforge/fluids/FluidStack;",
                    "drain(ILnet/minecraftforge/fluids/capability/IFluidHandler$FluidAction;)Lnet/minecraftforge/fluids/FluidStack;"
            },
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/BucketPickup;pickupBlock(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;",
                    remap = true
            )
    )
    private ItemStack scarcity$readSourcePurity(BucketPickup pickup, LevelAccessor level, BlockPos pos, BlockState state, Operation<ItemStack> original, @Share("purity") LocalRef<Integer> purity) {
        if (ScarcityConfig.enableThirstPurityFixes) {
            purity.set(ThirstCompat.getSourcePurity(world, blockPos));
        }
        return original.call(pickup, level, pos, state);
    }

    // Adds the water's purity to the picked up water (based on how Thirst handles vanilla buckets)
    @ModifyReturnValue(
            method = {
                    "drain(Lnet/minecraftforge/fluids/FluidStack;Lnet/minecraftforge/fluids/capability/IFluidHandler$FluidAction;)Lnet/minecraftforge/fluids/FluidStack;",
                    "drain(ILnet/minecraftforge/fluids/capability/IFluidHandler$FluidAction;)Lnet/minecraftforge/fluids/FluidStack;"
            },
            at = @At("RETURN")
    )
    private FluidStack scarcity$addSourcePurity(FluidStack fluid, @Share("purity") LocalRef<Integer> purity) {
        if (!ScarcityConfig.enableThirstPurityFixes) {
            return fluid;
        }

        // Read purity since the source block isn't removed by the simulated drain
        Integer sourcePurity = purity.get() != null ? purity.get() : ThirstCompat.getSourcePurity(world, blockPos);
        if (sourcePurity != null) {
            ThirstCompat.addPurity(fluid, sourcePurity);
        }
        return fluid;
    }
}
