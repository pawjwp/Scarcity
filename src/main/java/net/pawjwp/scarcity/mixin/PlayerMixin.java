package net.pawjwp.scarcity.mixin;

import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Player.class)
public class PlayerMixin {

    @ModifyArg(
            method = "jumpFromGround()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"),
            index = 0
    )
    private float scarcity$jumpExhaustion(float vanilla) {
        Player self = (Player)(Object)this;
        return self.isSprinting() ? ScarcityConfig.exhaustionJumpSprinting : ScarcityConfig.exhaustionJumpWalking;
    }

    @ModifyArg(
            method = "attack(Lnet/minecraft/world/entity/Entity;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V", ordinal = 0),
            index = 0,
            require = 0
    )
    private float scarcity$attackExhaustion(float vanilla) {
        return ScarcityConfig.exhaustionAttackHit;
    }

    @ModifyArg(
            method = "actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"),
            index = 0,
            require = 0
    )
    private float scarcity$damageExhaustion(float vanilla) {
        return vanilla * ScarcityConfig.exhaustionDamageMultiplier;
    }

    /**
     * @author Pawjwp
     * @reason Replace vanilla exhaustion constants with configurable values.
     */
    @Overwrite
    public void checkMovementStatistics(double pDistanceX, double pDistanceY, double pDistanceZ) {
        Player self = (Player) (Object) this;

        if (!self.isPassenger()) {
            if (self.isSwimming()) {
                int i = Math.round((float) Math.sqrt(pDistanceX * pDistanceX + pDistanceY * pDistanceY + pDistanceZ * pDistanceZ) * 100.0F);
                if (i > 0) {
                    self.awardStat(Stats.SWIM_ONE_CM, i);
                    self.causeFoodExhaustion(ScarcityConfig.exhaustionSwimOneBlock * (float) i * 0.01F);
                }
            } else if (self.isEyeInFluid(FluidTags.WATER)) {
                int j = Math.round((float) Math.sqrt(pDistanceX * pDistanceX + pDistanceY * pDistanceY + pDistanceZ * pDistanceZ) * 100.0F);
                if (j > 0) {
                    self.awardStat(Stats.WALK_UNDER_WATER_ONE_CM, j);
                    self.causeFoodExhaustion(ScarcityConfig.exhaustionWalkUnderwaterOneBlock * (float) j * 0.01F);
                }
            } else if (self.isInWater()) {
                int k = Math.round((float) Math.sqrt(pDistanceX * pDistanceX + pDistanceZ * pDistanceZ) * 100.0F);
                if (k > 0) {
                    self.awardStat(Stats.WALK_ON_WATER_ONE_CM, k);
                    self.causeFoodExhaustion(ScarcityConfig.exhaustionWalkOnWaterOneBlock * (float) k * 0.01F);
                }
            } else if (self.onClimbable()) {
                int cm = (int) Math.round(pDistanceY * 100.0D);
                if (pDistanceY > 0.0D) {
                    self.awardStat(Stats.CLIMB_ONE_CM, cm);
                    self.causeFoodExhaustion(ScarcityConfig.exhaustionClimbOneBlock * (float) cm * 0.01F);
                }
            } else if (self.onGround()) {
                int l = Math.round((float) Math.sqrt(pDistanceX * pDistanceX + pDistanceZ * pDistanceZ) * 100.0F);
                if (l > 0) {
                    if (self.isSprinting()) {
                        self.awardStat(Stats.SPRINT_ONE_CM, l);
                        self.causeFoodExhaustion(ScarcityConfig.exhaustionSprintOneBlock * (float) l * 0.01F);
                    } else if (self.isCrouching()) {
                        self.awardStat(Stats.CROUCH_ONE_CM, l);
                        self.causeFoodExhaustion(ScarcityConfig.exhaustionCrouchOneBlock * (float) l * 0.01F);
                    } else {
                        self.awardStat(Stats.WALK_ONE_CM, l);
                        self.causeFoodExhaustion(ScarcityConfig.exhaustionWalkOneBlock * (float) l * 0.01F);
                    }
                }
            } else if (self.isFallFlying()) {
                int i1 = Math.round((float) Math.sqrt(pDistanceX * pDistanceX + pDistanceY * pDistanceY + pDistanceZ * pDistanceZ) * 100.0F);
                self.awardStat(Stats.AVIATE_ONE_CM, i1);
                self.causeFoodExhaustion(ScarcityConfig.exhaustionAviateOneBlock * (float) i1 * 0.01F);
            } else {
                int j1 = Math.round((float) Math.sqrt(pDistanceX * pDistanceX + pDistanceZ * pDistanceZ) * 100.0F);
                if (j1 > 25) {
                    self.awardStat(Stats.FLY_ONE_CM, j1);
                    self.causeFoodExhaustion(ScarcityConfig.exhaustionFlyOneBlock * (float) j1 * 0.01F);
                }
            }

        }
    }
}