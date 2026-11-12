package net.pawjwp.scarcity.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;
import net.pawjwp.scarcity.MultiBottleDispenseBehavior;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {

    // If enable_bottle_pickup_adjustments is true, dispensers fill four bottles and consume the water source
    @WrapOperation(
            method = "dispenseFrom",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/block/DispenserBlock;getDispenseMethod(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/core/dispenser/DispenseItemBehavior;"
            )
    )
    private DispenseItemBehavior scarcity$multiBottleWaterPickup(DispenserBlock dispenser, ItemStack stack, Operation<DispenseItemBehavior> original) {
        DispenseItemBehavior behavior = original.call(dispenser, stack);
        return ScarcityConfig.enableBottlePickupAdjustments && stack.is(Items.GLASS_BOTTLE) ? new MultiBottleDispenseBehavior(behavior) : behavior;
    }
}
