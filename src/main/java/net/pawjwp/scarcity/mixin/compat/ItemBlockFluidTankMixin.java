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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Requires enable_thirst_purity_fixes to be enabled in config.
@Pseudo
@Mixin(targets = {
        "mekanism.common.item.block.machine.ItemBlockFluidTank",
        "mekanism.common.item.block.machine.ItemBlockFluidTank$FluidTankItemDispenseBehavior"
}, remap = false)
public class ItemBlockFluidTankMixin {

    // Reads the purity of the water before picking it up
    @WrapOperation(
            method = "*",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/BucketPickup;pickupBlock(Lnet/minecraft/world/level/LevelAccessor;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/world/item/ItemStack;",
                    remap = true
            ),
            require = 0
    )
    private ItemStack scarcity$readSourcePurity(BucketPickup pickup, LevelAccessor level, BlockPos pos, BlockState state, Operation<ItemStack> original, @Share("purity") LocalRef<Integer> purity) {
        if (ScarcityConfig.enableThirstPurityFixes && level instanceof Level sourceLevel) {
            purity.set(ThirstCompat.getSourcePurity(sourceLevel, pos));
        }
        return original.call(pickup, level, pos, state);
    }

    // Adds the water's purity to the picked up water (based on how Thirst handles vanilla buckets)
    @ModifyArg(
            method = "*",
            at = @At(
                    value = "INVOKE",
                    target = "Lmekanism/common/item/block/machine/ItemBlockFluidTank;uncheckedGrow(Lmekanism/api/fluid/IExtendedFluidTank;Lnet/minecraftforge/fluids/FluidStack;)V"
            ),
            index = 1,
            require = 0
    )
    private FluidStack scarcity$addSourcePurity(FluidStack fluid, @Share("purity") LocalRef<Integer> purity) {
        if (purity.get() != null) {
            ThirstCompat.addPurity(fluid, purity.get());
        }
        return fluid;
    }

    // Merges the new water with the water in the tank
    // Requires enable_thirst_purity_merging to be enabled in config
    // Set purity before increasing the amount in the tank
    @Inject(method = "uncheckedGrow", at = @At("HEAD"), require = 0, expect = 0)
    private void scarcity$mergePurity(@Coerce IFluidTank tank, FluidStack incoming, CallbackInfo ci) {
        FluidStack stored = tank.getFluid();
        if (!ScarcityConfig.enableThirstPurityMerging || stored.isEmpty()) {
            return;
        }

        // Set purity before increasing the amount in the tank
        FluidStack merged = new FluidStack(stored, stored.getAmount() + incoming.getAmount());
        ThirstCompat.mergePurity(merged, incoming, incoming.getAmount());
        int purity = ThirstCompat.getPurity(merged);
        if (purity != ThirstCompat.getPurity(stored)) {
            ThirstCompat.addPurity(stored, purity);
        }
    }
}
