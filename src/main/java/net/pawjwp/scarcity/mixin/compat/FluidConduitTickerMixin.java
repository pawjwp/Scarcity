package net.pawjwp.scarcity.mixin.compat;

import com.enderio.api.conduit.ColoredRedstoneProvider;
import com.enderio.api.conduit.ConduitGraph;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.conduit.ConduitType;
import com.enderio.api.conduit.ticker.CapabilityAwareConduitTicker;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduitData;
import com.enderio.conduits.common.conduit.type.fluid.FluidConduitTicker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

// Makes Ender IO fluid conduits transfer fluids with preserved NBT
// Requires enable_enderio_patches to be enabled
@Pseudo
@Mixin(value = FluidConduitTicker.class, remap = false)
public abstract class FluidConduitTickerMixin extends CapabilityAwareConduitTicker<FluidConduitData, IFluidHandler> {

    @Shadow
    @Final
    private boolean lockFluids;

    @Shadow
    private int getScaledFluidRate(CapabilityConnection extractingConnection) {
        throw new AssertionError();
    }

    @Shadow
    private int doFluidTransfer(FluidStack fluid, CapabilityConnection extract, List<CapabilityConnection> inserts) {
        throw new AssertionError();
    }

    // Replaces EnderIO's extract loop so each transfer uses a copy of the extracting tank's fluid stack and preserves NBT.
    @Inject(method = "tickCapabilityGraph", at = @At("HEAD"), cancellable = true, require = 0)
    private void scarcity$transferFluidWithNBT(ServerLevel level, ConduitType<FluidConduitData> type, List<CapabilityConnection> inserts,
                                               List<CapabilityConnection> extracts, ConduitGraph<FluidConduitData> graph,
                                               ColoredRedstoneProvider coloredRedstoneProvider, CallbackInfo ci) {
        if (!ScarcityConfig.enableEnderIOPatches) {
            return;
        }
        ci.cancel();

        for (CapabilityConnection extract : extracts) {
            IFluidHandler extractHandler = extract.capability;
            FluidConduitData fluidConduitData = extract.data.castTo(FluidConduitData.class);

            final int fluidRate = getScaledFluidRate(extract);

            if (fluidConduitData.lockedFluid() != null) {
                int remaining = fluidRate;

                for (int i = 0; i < extractHandler.getTanks() && remaining > 0; i++) {
                    if (extractHandler.getFluidInTank(i).getFluid() != fluidConduitData.lockedFluid()) {
                        continue;
                    }

                    remaining = doFluidTransfer(new FluidStack(extractHandler.getFluidInTank(i), remaining), extract, inserts);
                }
            } else {
                int remaining = fluidRate;

                for (int i = 0; i < extractHandler.getTanks() && remaining > 0; i++) {

                    if (extractHandler.getFluidInTank(i).isEmpty()) {
                        continue;
                    }

                    Fluid fluid = extractHandler.getFluidInTank(i).getFluid();
                    remaining = doFluidTransfer(new FluidStack(extractHandler.getFluidInTank(i), remaining), extract, inserts);

                    if (lockFluids && remaining < fluidRate) {
                        for (ConduitNode<FluidConduitData> node : graph.getNodes()) {
                            if (fluid instanceof FlowingFluid flowing) {
                                fluid = flowing.getSource();
                            }

                            node.getConduitData().setLockedFluid(fluid);
                        }
                        break;
                    }
                }
            }
        }
    }
}
