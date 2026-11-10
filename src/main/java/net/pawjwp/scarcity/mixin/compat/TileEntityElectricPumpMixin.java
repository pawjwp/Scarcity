package net.pawjwp.scarcity.mixin.compat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// Requires enable_thirst_purity_fixes to be enabled in config.
@Pseudo
@Mixin(targets = "mekanism.common.tile.machine.TileEntityElectricPump", remap = false)
public class TileEntityElectricPumpMixin {

    // Reads the purity of the water before picking it up
    @WrapOperation(
            method = "suck(Lnet/minecraft/core/BlockPos;ZZ)Z",
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
            method = "suck(Lnet/minecraft/core/BlockPos;ZZ)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lmekanism/common/tile/machine/TileEntityElectricPump;suck(Lnet/minecraftforge/fluids/FluidStack;Lnet/minecraft/core/BlockPos;Z)V"
            ),
            index = 0,
            require = 0
    )
    private FluidStack scarcity$addSourcePurity(FluidStack fluid, @Local(argsOnly = true) BlockPos pos, @Share("purity") LocalRef<Integer> purity) {
        if (!ScarcityConfig.enableThirstPurityFixes) {
            return fluid;
        }

        // Read the water left behind by Mekanism's pumpWaterSources config
        Integer sourcePurity = purity.get() != null ? purity.get() : ThirstCompat.getSourcePurity(((BlockEntity) (Object) this).getLevel(), pos);
        if (sourcePurity != null) {
            ThirstCompat.addPurity(fluid, sourcePurity);
        }
        return fluid;
    }
}
