package net.pawjwp.scarcity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;

// Dispenser behavior for filling four glass bottles from a water source and consuming it
public record MultiBottleDispenseBehavior(DispenseItemBehavior wrapped) implements DispenseItemBehavior {

    private static final int bottlesPerBucket = 4;

    @Override
    public ItemStack dispense(BlockSource source, ItemStack stack) {
        Level level = source.getLevel();
        Direction facing = source.getBlockState().getValue(DispenserBlock.FACING);
        BlockPos pos = source.getPos().relative(facing);
        FluidState fluidState = level.getFluidState(pos);
        if (!fluidState.is(FluidTags.WATER)) {
            return wrapped.dispense(source, stack);
        }

        BlockState state = level.getBlockState(pos);
        Integer purity = ThirstCompat.getSourcePurity(level, pos);
        if (stack.getCount() < bottlesPerBucket || !(state.getBlock() instanceof BucketPickup pickup) || pickup.pickupBlock(level, pos, state).isEmpty()) {
            level.levelEvent(LevelEvent.SOUND_DISPENSER_FAIL, source.getPos(), 0);
            return stack;
        }

        level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
        level.levelEvent(LevelEvent.SOUND_DISPENSER_DISPENSE, source.getPos(), 0);
        level.levelEvent(LevelEvent.PARTICLES_SHOOT, source.getPos(), facing.get3DDataValue());

        ItemStack waterBottles = PotionUtils.setPotion(new ItemStack(Items.POTION, bottlesPerBucket), Potions.WATER);
        if (purity != null) {
            ThirstCompat.addPurity(waterBottles, purity);
        }

        // Fills all four bottles at once
        stack.shrink(bottlesPerBucket);
        ItemStack result = stack.isEmpty() ? waterBottles.split(waterBottles.getMaxStackSize()) : stack;

        DispenserBlockEntity dispenser = source.getEntity();
        for (int slot = 0; slot < dispenser.getContainerSize() && !waterBottles.isEmpty(); slot++) {
            ItemStack slotStack = dispenser.getItem(slot);
            if (slotStack != stack && ItemStack.isSameItemSameTags(slotStack, waterBottles)) {
                int moved = Math.min(waterBottles.getCount(), slotStack.getMaxStackSize() - slotStack.getCount());
                slotStack.grow(moved);
                waterBottles.shrink(moved);
            }
        }
        for (int slot = 0; slot < dispenser.getContainerSize() && !waterBottles.isEmpty(); slot++) {
            ItemStack slotStack = dispenser.getItem(slot);
            if (slotStack != stack && slotStack.isEmpty()) {
                dispenser.setItem(slot, waterBottles.split(waterBottles.getMaxStackSize()));
            }
        }
        // Ejects the remaining water bottles
        if (!waterBottles.isEmpty()) {
            DefaultDispenseItemBehavior.spawnItem(level, waterBottles, 6, facing, DispenserBlock.getDispensePosition(source));
        }
        return result;
    }
}
