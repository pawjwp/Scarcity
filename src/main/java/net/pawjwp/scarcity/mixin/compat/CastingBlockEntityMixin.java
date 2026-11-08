package net.pawjwp.scarcity.mixin.compat;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import slimeknights.tconstruct.smeltery.block.entity.CastingBlockEntity;

// Requires enable_thirst_purity_fixes to be enabled in config.
@Pseudo
@Mixin(value = CastingBlockEntity.class, remap = false)
public class CastingBlockEntityMixin {

    // Water containers made from Tinker's Construct casting (casting table or basin) maintain the purity of the input fluid
    @ModifyVariable(method = "serverTick", at = @At("STORE"), require = 0)
    private ItemStack scarcity$addWaterPurity(ItemStack output, @Local FluidStack currentFluid) {
        return ScarcityConfig.enableThirstPurityFixes ? ThirstCompat.withFluidPurity(output, currentFluid) : output;
    }
}
