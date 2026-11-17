package net.pawjwp.scarcity.mixin.compat;

import com.enderio.machines.common.blockentity.FluidTankBlockEntity;
import com.enderio.machines.common.io.fluid.TankAccess;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.IntStream;

import static com.enderio.machines.common.blockentity.FluidTankBlockEntity.FLUID_DRAIN_INPUT;
import static com.enderio.machines.common.blockentity.FluidTankBlockEntity.FLUID_DRAIN_OUTPUT;
import static com.enderio.machines.common.blockentity.FluidTankBlockEntity.FLUID_FILL_INPUT;
import static com.enderio.machines.common.blockentity.FluidTankBlockEntity.FLUID_FILL_OUTPUT;

// Makes the Ender IO Fluid Tank keep fluid NBT when it empties/drains fluid containers
// Requires enable_enderio_patches to be enabled
@Pseudo
@Mixin(value = FluidTankBlockEntity.class, remap = false)
public class FluidTankBlockEntityMixin {

    @Shadow
    @Final
    private static TankAccess TANK;

    // Accepts a fluid container in the fill input slot only when it contains a fluid to drain
    @ModifyExpressionValue(method = "acceptItemFill", at = @At(value = "INVOKE", target = "Ljava/util/Optional;isPresent()Z"), require = 0)
    private boolean scarcity$acceptFilledContainers(boolean hasFluidHandler, @Local(argsOnly = true) ItemStack item) {
        if (!ScarcityConfig.enableEnderIOPatches) {
            return hasFluidHandler;
        }
        return item.copyWithCount(1).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM)
                .map(fluidHandlerCap -> !fluidHandlerCap.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE).isEmpty())
                .orElse(false);
    }

    // Accepts a fluid container in the drain input slot only when the container is able to hold the tank's fluid, including NBT
    @ModifyExpressionValue(method = "acceptItemDrain", at = @At(value = "INVOKE", target = "Ljava/util/Optional;isPresent()Z"), require = 0)
    private boolean scarcity$acceptFillableContainers(boolean hasFluidHandler, @Local(argsOnly = true) ItemStack item) {
        if (!ScarcityConfig.enableEnderIOPatches) {
            return hasFluidHandler;
        }
        FluidStack fluid = TANK.getFluid((FluidTankBlockEntity) (Object) this);
        return item.copyWithCount(1).getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM)
                .map(fluidHandlerCap -> fluid.isEmpty() || fluidHandlerCap.fill(new FluidStack(fluid, Integer.MAX_VALUE), IFluidHandler.FluidAction.SIMULATE) > 0)
                .orElse(false);
    }

    @Inject(method = "fillInternal", at = @At("HEAD"), cancellable = true, require = 0)
    private void scarcity$fillInternal(CallbackInfo ci) {
        if (!ScarcityConfig.enableEnderIOPatches) {
            return;
        }
        ci.cancel();

        var self = (FluidTankBlockEntity) (Object) this;
        ItemStack inputItem = FLUID_FILL_INPUT.getItemStack(self);
        if (inputItem.isEmpty()) {
            return;
        }

        // Only ever handle a single item at a time.
        ItemStack singleInputItem = inputItem.copyWithCount(1);
        IFluidHandlerItem fluidHandlerItem = singleInputItem.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if (fluidHandlerItem == null) {
            return;
        }

        // See what fluid is available to drain from the item.
        FluidStack availableFluid = fluidHandlerItem.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);

        // See if we can insert this fluid into the block's tank.
        int filled = TANK.fill(self, availableFluid, IFluidHandler.FluidAction.SIMULATE);
        if (filled <= 0) {
            return;
        }

        // Drain the accepted amount from the item.
        // We act here because we're acting on a copy of the input.
        FluidStack drainedFluid = fluidHandlerItem.drain(new FluidStack(availableFluid, filled), IFluidHandler.FluidAction.EXECUTE);
        if (drainedFluid.isEmpty()) {
            return;
        }

        // Get the resulting emptied container
        ItemStack resultStack = fluidHandlerItem.getContainer();

        // If this is the only input, and it's not fully empty we will retain it in the input slot, so long as the
        //  destination tank is not full
        if (inputItem.getCount() == 1 && TANK.getFluidAmount(self) < TANK.getCapacity(self)
                && IntStream.range(0, fluidHandlerItem.getTanks()).anyMatch(i -> !fluidHandlerItem.getFluidInTank(i).isEmpty())) {
            TANK.fill(self, drainedFluid, IFluidHandler.FluidAction.EXECUTE);
            FLUID_FILL_INPUT.setStackInSlot(self, resultStack);
        } else if (!self.getInventory().extractItem(FLUID_FILL_INPUT.getIndex(), 1, true).isEmpty() &&
            FLUID_FILL_OUTPUT.insertItem(self, resultStack, true).isEmpty()) {

            TANK.fill(self, drainedFluid, IFluidHandler.FluidAction.EXECUTE);
            self.getInventory().extractItem(FLUID_FILL_INPUT.getIndex(), 1, false);
            FLUID_FILL_OUTPUT.insertItem(self, resultStack, false);
        }
    }

    @Inject(method = "drainInternal", at = @At("HEAD"), cancellable = true, require = 0)
    private void scarcity$drainInternal(CallbackInfo ci) {
        if (!ScarcityConfig.enableEnderIOPatches) {
            return;
        }
        ci.cancel();

        var self = (FluidTankBlockEntity) (Object) this;
        ItemStack inputItem = FLUID_DRAIN_INPUT.getItemStack(self);
        if (inputItem.isEmpty()) {
            return;
        }

        // Only ever handle a single item at a time.
        ItemStack singleInputItem = inputItem.copyWithCount(1);
        IFluidHandlerItem fluidHandlerItem = singleInputItem.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if (fluidHandlerItem == null) {
            return;
        }

        // See what fluid the tank can drain into the item
        FluidStack availableFluid = TANK.drain(self, Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);

        // See if we can insert this fluid into the container
        int filled = fluidHandlerItem.fill(availableFluid, IFluidHandler.FluidAction.EXECUTE);
        if (filled <= 0) {
            return;
        }

        // Get the resulting filled item
        ItemStack resultStack = fluidHandlerItem.getContainer();

        // If this is the only input, and it isn't completely full, and the destination still has fluid, we will retain it
        if (inputItem.getCount() == 1 && !TANK.isEmpty(self)
                && IntStream.range(0, fluidHandlerItem.getTanks()).anyMatch(i -> fluidHandlerItem.getFluidInTank(i).getAmount() < fluidHandlerItem.getTankCapacity(i))) {
            TANK.drain(self, new FluidStack(availableFluid, filled), IFluidHandler.FluidAction.EXECUTE);
            FLUID_DRAIN_INPUT.setStackInSlot(self, resultStack);
        } else if (!self.getInventory().extractItem(FLUID_DRAIN_INPUT.getIndex(), 1, true).isEmpty() &&
            FLUID_DRAIN_OUTPUT.insertItem(self, resultStack, true).isEmpty()) {

            TANK.drain(self, new FluidStack(availableFluid, filled), IFluidHandler.FluidAction.EXECUTE);
            self.getInventory().extractItem(FLUID_DRAIN_INPUT.getIndex(), 1, false);
            FLUID_DRAIN_OUTPUT.insertItem(self, resultStack, false);
        }
    }
}
