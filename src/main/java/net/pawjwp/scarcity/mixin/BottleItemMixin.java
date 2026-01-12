package net.pawjwp.scarcity.mixin;

import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    @Shadow
    protected abstract ItemStack turnBottleIntoItem(ItemStack bottleStack,
                                                    Player player,
                                                    ItemStack filledBottleStack);

    @Inject(
            method = "use",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;gameEvent(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/level/gameevent/GameEvent;Lnet/minecraft/core/BlockPos;)V",
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
        if (!ScarcityConfig.enableBottlePickupAdjustments) {
            return;
        }

        if (itemstack.getCount() < 4) {
            cir.setReturnValue(InteractionResultHolder.pass(itemstack));
            cir.cancel();
            return;
        }

        // Turn 4 empty bottles into water bottles
        ItemStack currentStack = itemstack;

        for (int i = 0; i < 4; i++) {
            ItemStack waterPotion = PotionUtils.setPotion(
                    new ItemStack(Items.POTION),
                    Potions.WATER
            );
            currentStack = this.turnBottleIntoItem(currentStack, player, waterPotion);
        }

        if (!level.isClientSide) {
            level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 11);
            level.gameEvent(player, GameEvent.FLUID_PICKUP, blockPos);
        }

        // Return the new stack
        cir.setReturnValue(InteractionResultHolder.sidedSuccess(currentStack, level.isClientSide()));
        cir.cancel();
    }
}
