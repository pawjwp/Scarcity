package net.pawjwp.scarcity.mixin.compat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

// Requires enable_thirst_purity_fixes to be enabled in config.
// Targets the anonymous BOTTLE dispenser behavior for contraptions ($7 in Create 6.0.8)
@Pseudo
@Mixin(targets = "com.simibubi.create.AllMountedDispenseItemBehaviors$7", remap = false)
public class MountedBottleDispenseBehaviorMixin {

    // Reads the purity of the water before picking it up
    @WrapOperation(
            method = "doExecute",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;",
                    remap = true
            ),
            require = 0
    )
    private FluidState scarcity$readWaterPurity(Level level, BlockPos pos, Operation<FluidState> original, @Share("purity") LocalRef<Integer> purity) {
        if (ScarcityConfig.enableThirstPurityFixes) {
            purity.set(ThirstCompat.getWaterPurity(level, pos));
        }
        return original.call(level, pos);
    }

    // Adds the water's purity to the filled bottle (based on how Thirst handles vanilla dispensers)
    @ModifyArg(
            method = "doExecute",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/simibubi/create/api/contraption/dispenser/MountedDispenseBehavior;placeItemInInventory(Lnet/minecraft/world/item/ItemStack;Lcom/simibubi/create/content/contraptions/behaviour/MovementContext;Lnet/minecraft/core/BlockPos;)V"
            ),
            index = 0,
            require = 0
    )
    private ItemStack scarcity$addBottlePurity(ItemStack item, @Share("purity") LocalRef<Integer> purity) {
        if (purity.get() != null) {
            ThirstCompat.addPurity(item, purity.get());
        }
        return item;
    }
}
