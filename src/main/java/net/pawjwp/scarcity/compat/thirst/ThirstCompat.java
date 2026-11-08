package net.pawjwp.scarcity.compat.thirst;

import dev.ghen.thirst.content.purity.WaterPurity;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.pawjwp.scarcity.compat.Mods;

// Water purity helper functions for Thirst Was Taken and Thirst Was Reclaimed
// Uses Thirst Was Taken's WaterPurity class, which Thirst Was Reclaimed allows as a bridge to its own
public final class ThirstCompat {
    // Merges two fluid stacks, setting the resulting fluid's purity to the lower of the two purity values
    // Matches Thirst Was Reclaimed's overrides to Forge's FluidTank
    public static void mergePurity(FluidStack stored, FluidStack incoming, int addedAmount) {
        if (!Mods.THIRST || !stored.getFluid().isSame(Fluids.WATER) || !incoming.getFluid().isSame(Fluids.WATER)) {
            return;
        }

        int incomingPurity = WaterPurity.getPurity(incoming);
        if (incomingPurity < WaterPurity.getPurity(stored)) {
            WaterPurity.addPurity(stored, incomingPurity);
        }
    }
}
