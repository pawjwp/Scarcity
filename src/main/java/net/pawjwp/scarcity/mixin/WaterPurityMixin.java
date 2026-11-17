package net.pawjwp.scarcity.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.FluidStack;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Target either Thirst was Taken or Thirst was Reclaimed
@Pseudo
@Mixin(targets = {"dev.ghen.thirst.content.purity.WaterPurity", "cn.mlus.thirst.content.purity.WaterPurity"}, remap = false)
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

    // Uses thirst_untagged_water_purity for items without a purity tag instead of Thirst's default
    @ModifyReturnValue(method = "getPurity(Lnet/minecraft/world/item/ItemStack;)I", at = @At("RETURN"), require = 0)
    private static int scarcity$untaggedItemPurity(int purity, @Local(argsOnly = true) ItemStack item) {
        if (ScarcityConfig.thirstUntaggedWaterPurity < 0 || !ThirstCompat.isPurityEnabled() || item.hasTag() && item.getTag().contains("Purity")
                || "toughasnails".equals(item.getItem().getCreatorModId(item))) {
            return purity;
        }
        return ScarcityConfig.thirstUntaggedWaterPurity;
    }

    // Uses thirst_untagged_water_purity for fluids without a purity tag instead of Thirst's own default
    @ModifyReturnValue(method = "getPurity(Lnet/minecraftforge/fluids/FluidStack;)I", at = @At("RETURN"), require = 0)
    private static int scarcity$untaggedFluidPurity(int purity, @Local(argsOnly = true) FluidStack fluid) {
        if (ScarcityConfig.thirstUntaggedWaterPurity < 0 || !ThirstCompat.isPurityEnabled() || fluid.hasTag() && fluid.getTag().contains("Purity")) {
            return purity;
        }
        return ScarcityConfig.thirstUntaggedWaterPurity;
    }
}
