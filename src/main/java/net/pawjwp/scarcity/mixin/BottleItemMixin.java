package net.pawjwp.scarcity.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BottleItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import net.pawjwp.scarcity.compat.thirst.ThirstCompat;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(BottleItem.class)
public abstract class BottleItemMixin extends Item {

    public BottleItemMixin(Properties properties) {
        super(properties);
    }

    // Fills four bottles from a water source and removes the source
    @Inject(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/player/Player;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V",
                    ordinal = 1,
                    shift = At.Shift.BEFORE
            ),
            cancellable = true,
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    private void scarcity$multiBottleWaterPickup(
            Level level,
            Player player,
            InteractionHand hand,
            CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir,
            List<AreaEffectCloud> list,
            ItemStack itemstack,
            BlockHitResult hitResult,
            BlockPos blockPos
    ) {
        // If config is disabled or the player is in creative mode, work like vanilla
        if (!ScarcityConfig.enableBottlePickupAdjustments || player.getAbilities().instabuild) {
            return;
        }

        int bottlesPerBucket = 4;

        if (itemstack.getCount() < bottlesPerBucket) {
            cir.setReturnValue(InteractionResultHolder.pass(itemstack));
            cir.cancel();
            return;
        }

        BlockState blockState = level.getBlockState(blockPos);
        if (!(blockState.getBlock() instanceof BucketPickup bucketPickup)) {
            cir.setReturnValue(InteractionResultHolder.fail(itemstack));
            cir.cancel();
            return;
        }

        Integer purity = ThirstCompat.getSourcePurity(level, blockPos);
        ItemStack picked = bucketPickup.pickupBlock(level, blockPos, blockState);
        if (picked.isEmpty()) {
            cir.setReturnValue(InteractionResultHolder.fail(itemstack));
            cir.cancel();
            return;
        }

        level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
        ItemStack waterBottles = PotionUtils.setPotion(
                new ItemStack(Items.POTION, bottlesPerBucket),
                Potions.WATER
        );
        // Apply purity to picked up bottles
        if (purity != null) {
            ThirstCompat.addPurity(waterBottles, purity);
        }

        // Fills all four bottles at once
        player.awardStat(Stats.ITEM_USED.get(this), bottlesPerBucket);
        ItemStack currentStack = itemstack;
        currentStack.shrink(bottlesPerBucket);
        if (currentStack.isEmpty()) {
            currentStack = waterBottles.split(waterBottles.getMaxStackSize());
        }
        if (!waterBottles.isEmpty() && !player.getInventory().add(waterBottles)) {
            player.drop(waterBottles, false);
        }

        level.gameEvent(player, GameEvent.FLUID_PICKUP, blockPos);

        cir.setReturnValue(InteractionResultHolder.sidedSuccess(currentStack, level.isClientSide()));
        cir.cancel();
    }
}
