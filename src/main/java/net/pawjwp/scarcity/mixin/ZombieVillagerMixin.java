package net.pawjwp.scarcity.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieVillager.class)
public abstract class ZombieVillagerMixin extends Zombie {

    protected ZombieVillagerMixin(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void scarcity$disableCuring(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!ScarcityConfig.enableZombieVillagerCuring) {
            ItemStack itemstack = player.getItemInHand(hand);
            // Must meet full requirements to disable curing, if not, will allow other mod mixins to continue
            if (itemstack.is(Items.GOLDEN_APPLE) && this.hasEffect(MobEffects.WEAKNESS)) {
                cir.setReturnValue(InteractionResult.PASS);
            }
        }
    }
}