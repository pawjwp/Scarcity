package net.pawjwp.scarcity.compat.thirst;

import dev.ghen.thirst.content.purity.WaterPurity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.pawjwp.scarcity.compat.Mods;

import java.util.List;
import java.util.stream.Collectors;

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

    // Adds the purity tag from an item to its contained fluid (based on how Thirst handles vanilla buckets)
    public static void copyItemPurityToFluid(ItemStack item, FluidStack fluid) {
        if (!Mods.THIRST || fluid.isEmpty() || !WaterPurity.hasPurity(item)) {
            return;
        }

        WaterPurity.addPurity(fluid, WaterPurity.getPurity(item));
    }

    // Adds the purity tag from a fluid to the item that contains it (based on how Thirst handles vanilla buckets)
    public static void copyFluidPurityToItem(FluidStack fluid, ItemStack item) {
        if (!Mods.THIRST || item.isEmpty() || !WaterPurity.hasPurity(fluid)) {
            return;
        }

        WaterPurity.addPurity(item, WaterPurity.getPurity(fluid));
    }

    // Gets the purity of a water source block that is about to be picked up (based on how Thirst handles vanilla buckets)
    public static Integer getSourcePurity(Level level, BlockPos pos) {
        FluidState fluidState = level.getFluidState(pos);
        if (!Mods.THIRST || !fluidState.is(FluidTags.WATER) || !fluidState.isSource()) {
            return null; // null if not a water source
        }

        return WaterPurity.getBlockPurity(level, pos);
    }

    // Adds a purity tag to an item
    public static void addPurity(ItemStack item, int purity) {
        if (!Mods.THIRST) {
            return;
        }

        WaterPurity.addPurity(item, purity);
    }

    // Gets a copy of a fluid without the purity tag for matching in recipes
    public static FluidStack withoutPurity(FluidStack fluid) {
        if (!fluid.hasTag() || !fluid.getTag().contains("Purity")) {
            return fluid;
        }

        FluidStack fluidWithoutPurity = fluid.copy();
        fluidWithoutPurity.getTag().remove("Purity");
        return fluidWithoutPurity;
    }

    // Applies a fluid's purity to an item when it is a water container (based on how Thirst fills its containers)
    public static ItemStack withFluidPurity(ItemStack item, FluidStack fluid) {
        if (!Mods.THIRST || !WaterPurity.hasPurity(fluid) || !WaterPurity.isWaterFilledContainer(item)) {
            return item;
        }

        return WaterPurity.addPurity(item.copy(), WaterPurity.getPurity(fluid));
    }

    // Applies a fluid's purity to a list of items (based on how Thirst fills its containers)
    public static List<ItemStack> withFluidPurity(List<ItemStack> items, FluidStack fluid) {
        if (!Mods.THIRST || !WaterPurity.hasPurity(fluid)) {
            return items;
        }

        return items.stream().map(item -> withFluidPurity(item, fluid)).collect(Collectors.toList());
    }
}
