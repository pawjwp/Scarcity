package net.pawjwp.scarcity.compat.refurbished;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;

// Water purity fixes for MrCrayfish's Furniture Mod: Refurbished
public final class RefurbishedCompat {

    // Amount of water contained in each bottle
    private static final int BOTTLE_VOLUME = FluidType.BUCKET_VOLUME / 4;

    public static void register() {
        MinecraftForge.EVENT_BUS.addListener(RefurbishedCompat::onRightClickBlock);
    }

    // Fills/empties water bottles in Refurbished Furniture's blocks that have tanks while maintaining purity
    // Replaces Refurbished Furniture's default bottle handling, which ignores purity and treats each bottle as a full bucket
    private static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        ItemStack held = event.getItemStack();
        boolean isGlassBottle = held.is(Items.GLASS_BOTTLE);
        if (!ScarcityConfig.enableThirstPurityFixes || event.getEntity().isSecondaryUseActive()
                || !isGlassBottle && !(held.is(Items.POTION) && PotionUtils.getPotion(held) == Potions.WATER)) {
            return;
        }

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null || !"refurbished_furniture".equals(ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(blockEntity.getType()).getNamespace())) {
            return;
        }
        IFluidHandler tank = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER).orElse(null);
        if (tank == null) {
            return;
        }

        FluidStack water;
        if (isGlassBottle) {
            water = tank.drain(BOTTLE_VOLUME, IFluidHandler.FluidAction.SIMULATE);
            if (water.getAmount() < BOTTLE_VOLUME || !water.getFluid().isSame(Fluids.WATER)) {
                return;
            }
        } else {
            water = new FluidStack(Fluids.WATER, BOTTLE_VOLUME);
            ThirstCompat.copyItemPurityToFluid(held, water);
            if (tank.fill(water, IFluidHandler.FluidAction.SIMULATE) <= 0) {
                return;
            }
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
        if (level.isClientSide()) {
            return;
        }

        Player player = event.getEntity();
        player.awardStat(Stats.ITEM_USED.get(held.getItem()));
        if (isGlassBottle) {
            tank.drain(BOTTLE_VOLUME, IFluidHandler.FluidAction.EXECUTE);
            ItemStack waterBottle = ThirstCompat.withFluidPurity(PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER), water);
            player.setItemInHand(event.getHand(), ItemUtils.createFilledResult(held, player, waterBottle));
            level.gameEvent(null, GameEvent.FLUID_PICKUP, pos);
        } else {
            tank.fill(water, IFluidHandler.FluidAction.EXECUTE);
            player.setItemInHand(event.getHand(), ItemUtils.createFilledResult(held, player, new ItemStack(Items.GLASS_BOTTLE)));
            level.gameEvent(null, GameEvent.FLUID_PLACE, pos);
        }
        level.playSound(null, pos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
    }
}
