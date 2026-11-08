package net.pawjwp.scarcity.mixin.compat;

import cofh.core.common.item.FluidContainerItem;
import cofh.lib.api.item.IFluidContainerItem;
import cofh.lib.common.fluid.FluidContainerItemWrapper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static cofh.lib.util.constants.NBTTags.TAG_FLUID;

// Requires enable_thirst_purity_merging to be enabled in config.
@Pseudo
@Mixin(value = FluidContainerItemWrapper.class, remap = false)
public class FluidContainerItemWrapperMixin {

    @Shadow
    @Final
    protected ItemStack container;

    @Shadow
    @Final
    protected IFluidContainerItem item;

    // Merges the two fluid stacks, setting the resulting fluid's purity to the lower of the two purity values
    // Affects Thermal items that hold fluid like the Fluid Reservoir
    // Saves its fluid in NBT so it can be read again
    @Inject(method = "fill", at = @At("RETURN"), require = 0)
    private void scarcity$mergeToLowestPurity(FluidStack resource, IFluidHandler.FluidAction action, CallbackInfoReturnable<Integer> cir) {
        if (ScarcityConfig.enableThirstPurityMerging && action.execute() && cir.getReturnValue() > 0 && item instanceof FluidContainerItem) {
            CompoundTag fluidTag = item.getOrCreateTankTag(container).getCompound(TAG_FLUID);
            FluidStack stored = FluidStack.loadFluidStackFromNBT(fluidTag);
            ThirstCompat.mergePurity(stored, resource, cir.getReturnValue());
            stored.writeToNBT(fluidTag);
        }
    }
}
