package net.pawjwp.scarcity.compat.thirst;

import dev.ghen.thirst.content.purity.WaterPurity;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.pawjwp.scarcity.ScarcityThirstAccess;
import net.pawjwp.scarcity.compat.Mods;
import net.pawjwp.scarcity.config.ScarcityConfig;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

// Water purity helper functions for Thirst Was Taken and Thirst Was Reclaimed
// Uses Thirst Was Taken's WaterPurity class, which Thirst Was Reclaimed allows as a bridge to its own
public final class ThirstCompat {
    public static final int MAX_PURITY = WaterPurity.MAX_PURITY;

    // Gets the purity of a water container item
    public static int getPurity(ItemStack item) {
        return Mods.THIRST ? WaterPurity.getPurity(item) : MAX_PURITY;
    }

    // Merges two fluid stacks, setting the resulting fluid's purity using the method of merging set by the config
    public static void mergePurity(FluidStack stored, FluidStack incoming, int addedAmount) {
        mergePurity(stored, getPurity(stored), incoming, addedAmount);
    }

    // Merges two fluid stacks like above, using the stored fluid's purity from before the incoming fluid was added
    public static void mergePurity(FluidStack stored, int storedPurity, FluidStack incoming, int addedAmount) {
        if (!Mods.THIRST || !stored.getFluid().isSame(Fluids.WATER) || !incoming.getFluid().isSame(Fluids.WATER)) {
            return;
        }

        int purity = mergedPurity(storedPurity, WaterPurity.getPurity(incoming), addedAmount, stored.getAmount());
        if (purity != WaterPurity.getPurity(stored)) {
            WaterPurity.addPurity(stored, purity);
        }
    }

    // Gets the purity of merged water, calculated without FluidStacks
    public static int mergedPurity(int storedPurity, int incomingPurity, int addedAmount, int totalAmount) {
        double average = storedPurity + (incomingPurity - storedPurity) * (double) Math.min(addedAmount, totalAmount) / totalAmount;
        int roundedDown = (int) average;
        double remainder = average - roundedDown;
        return switch (ScarcityConfig.thirstPurityMergeMethod) {
            case LOWEST -> Math.min(storedPurity, incomingPurity); // The default, and what Thirst Was Reclaimed uses in its overrides to the Forge FluidTank
            case NEAREST -> (int) Math.round(average);
            case FLOOR -> roundedDown;
            case CEILING -> (int) Math.ceil(average);
            case RANDOM -> ThreadLocalRandom.current().nextDouble() < remainder ? roundedDown + 1 : roundedDown;
            case THRESHOLD -> remainder > ScarcityConfig.thirstPurityRoundingThreshold ? roundedDown + 1 : roundedDown;
        };
    }

    // Gets the purity of a fluid, using the default purity if the tag is missing
    public static int getPurity(FluidStack fluid) {
        return Mods.THIRST ? WaterPurity.getPurity(fluid) : WaterPurity.MAX_PURITY;
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

    // Gets the purity of a water block, including flowing water
    public static Integer getWaterPurity(Level level, BlockPos pos) {
        if (!Mods.THIRST || !level.getFluidState(pos).is(FluidTags.WATER)) {
            return null; // null if not water
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

    // Adds a purity tag to water
    public static void addPurity(FluidStack fluid, int purity) {
        if (!Mods.THIRST || !fluid.getFluid().isSame(Fluids.WATER)) {
            return;
        }

        WaterPurity.addPurity(fluid, purity);
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

    // Gets a player's thirst data
    public static ScarcityThirstAccess getThirst(Player player) {
        return (ScarcityThirstAccess) player.getCapability(PlayerThirstCapability.INSTANCE).orElse(null);
    }

    // The Thirst mods' player thirst capability
    private static class PlayerThirstCapability {
        static final Capability<?> INSTANCE = find();

        private static Capability<?> find() {
            for (String modPackage : List.of("cn.mlus", "dev.ghen")) {
                try {
                    return (Capability<?>) Class.forName(modPackage + ".thirst.foundation.common.capability.ModCapabilities").getField("PLAYER_THIRST").get(null);
                } catch (ReflectiveOperationException ignored) {
                }
            }
            throw new IllegalStateException("Thirst's player thirst capability was not found");
        }
    }
}
