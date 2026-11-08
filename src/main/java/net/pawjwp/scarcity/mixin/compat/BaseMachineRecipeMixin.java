package net.pawjwp.scarcity.mixin.compat;

import cofh.thermal.lib.util.recipes.IMachineInventory;
import cofh.thermal.lib.util.recipes.internal.BaseMachineRecipe;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

// Requires enable_thirst_purity_fixes to be enabled in config.
@Pseudo
@Mixin(value = BaseMachineRecipe.class, remap = false)
public class BaseMachineRecipeMixin {

    // Water containers made by Thermal machines like the Fluid Encapsulator maintain the purity of the machine's water
    @ModifyReturnValue(method = "getOutputItems", at = @At("RETURN"), require = 0)
    private List<ItemStack> scarcity$addWaterPurity(List<ItemStack> outputs, @Local(argsOnly = true) IMachineInventory inventory) {
        if (!ScarcityConfig.enableThirstPurityFixes || inventory == null) {
            return outputs;
        }

        for (var tank : inventory.inputTanks()) {
            if (tank.getFluidStack().getFluid().isSame(Fluids.WATER)) {
                return ThirstCompat.withFluidPurity(outputs, tank.getFluidStack());
            }
        }
        return outputs;
    }
}
