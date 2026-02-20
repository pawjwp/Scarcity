package net.pawjwp.scarcity.mixin;

import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "dev.ghen.thirst.content.purity.WaterPurity", remap = false)
public class WaterPurityMixin {
    
    // Cancels Thirst's harvestRunningWater method when bottle pickup adjustments are enabled
    @Inject(method = "harvestRunningWater", at = @At("HEAD"), cancellable = true, require = 0)
    private static void scarcity$cancelFlowingWaterBottleFill(
            PlayerInteractEvent.RightClickItem event,
            CallbackInfo ci
    ) {
        if (!ScarcityConfig.enableBottlePickupAdjustments) {
            return;
        }
        if (event.getItemStack().getItem() != Items.GLASS_BOTTLE) {
            return;
        }
        ci.cancel();
    }
}
