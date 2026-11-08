package net.pawjwp.scarcity.mixin.compat;

import cofh.lib.api.item.IFluidContainerItem;
import cofh.thermal.core.util.recipes.machine.BottlerRecipeNBT;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

import static cofh.lib.util.constants.NBTTags.TAG_FLUID;

// Requires enable_thermal_patches to be enabled in config.
@Pseudo
@Mixin(value = BottlerRecipeNBT.class, remap = false)
public class BottlerRecipeNBTMixin {

    // Copies a Fluid Encapsulator's input fluid NBT to a fluid-containing output (like a Florb)
    @WrapOperation(
            method = "getOutputItems",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;setTag(Lnet/minecraft/nbt/CompoundTag;)V",
                    remap = true
            ),
            require = 0
    )
    private void scarcity$keepStoredFluid(ItemStack item, CompoundTag tag, Operation<Void> original) {
        if (ScarcityConfig.enableThermalPatches && item.getItem() instanceof IFluidContainerItem container) {
            container.getOrCreateTankTag(item).getCompound(TAG_FLUID).put("Tag", tag);
            return;
        }
        original.call(item, tag);
    }
}
