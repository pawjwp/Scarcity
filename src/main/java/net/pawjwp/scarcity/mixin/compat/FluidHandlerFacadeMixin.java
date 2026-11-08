package net.pawjwp.scarcity.mixin.compat;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

// Requires enable_thirst_purity_fixes to be enabled in config.
@Pseudo
@Mixin(targets = "appeng.me.storage.ExternalStorageFacade$FluidHandlerFacade", remap = false)
public class FluidHandlerFacadeMixin {

    // Makes storage buses only extract when the tank has the same NBT/purity value that was requested
    // This prevents it from turning dirty water into clean water on extract
    @WrapOperation(
            method = "extractExternal",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/fluids/capability/IFluidHandler;drain(Lnet/minecraftforge/fluids/FluidStack;Lnet/minecraftforge/fluids/capability/IFluidHandler$FluidAction;)Lnet/minecraftforge/fluids/FluidStack;"
            ),
            require = 0
    )
    private FluidStack scarcity$drainOnlyRequestedFluid(IFluidHandler handler, FluidStack requested, IFluidHandler.FluidAction action, Operation<FluidStack> original) {
        if (ScarcityConfig.enableThirstPurityFixes) {
            FluidStack available = original.call(handler, requested, IFluidHandler.FluidAction.SIMULATE);
            if (!available.isEmpty() && !FluidStack.areFluidStackTagsEqual(available, requested)) {
                return FluidStack.EMPTY;
            }
        }
        return original.call(handler, requested, action);
    }
}
