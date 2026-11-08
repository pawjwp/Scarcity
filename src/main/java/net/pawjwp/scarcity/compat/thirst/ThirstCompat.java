package net.pawjwp.scarcity.compat.thirst;

import dev.ghen.thirst.content.purity.WaterPurity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
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

    // Adds the purity tag from block to a FluidStack
    public static void addBlockPurity(FluidStack fluid, BlockState state) {
        if (!Mods.THIRST || !fluid.getFluid().isSame(Fluids.WATER)) {
            return;
        }

        int purity = WaterPurity.getBlockPurity(state);
        if (purity >= 0) {
            WaterPurity.addPurity(fluid, purity);
        }
    }

    // Adds the purity tag from an item to its contained fluid, based on how Thirst handles vanilla buckets
    public static void copyItemPurityToFluid(ItemStack item, FluidStack fluid) {
        if (!Mods.THIRST || fluid.isEmpty() || !WaterPurity.hasPurity(item)) {
            return;
        }

        WaterPurity.addPurity(fluid, WaterPurity.getPurity(item));
    }

    // Adds the purity tag from a fluid to the item that contains it, based on how Thirst handles vanilla buckets
    public static void copyFluidPurityToItem(FluidStack fluid, ItemStack item) {
        if (!Mods.THIRST || item.isEmpty() || !WaterPurity.hasPurity(fluid)) {
            return;
        }

        WaterPurity.addPurity(item, WaterPurity.getPurity(fluid));
    }
}
