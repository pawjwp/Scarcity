package net.pawjwp.scarcity.mixin;

import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Player.class)
public class PlayerMixin {

    // Jumping
    private static float EXHAUSTION_JUMP_SPRINTING = 0.2F; // vanilla: 0.2
    private static float EXHAUSTION_JUMP_WALKING = 0.05F; // vanilla: 0.05

    // Attacking
    private static float EXHAUSTION_ATTACK_HIT = 0.1F; // vanilla: 0.1

    // Movement (per cm)
    private static float EXHAUSTION_SWIM_ONE_CM              = 0.0001F; // vanilla: 0.0001
    private static float EXHAUSTION_WALK_UNDERWATER_ONE_CM   = 0.0001F; // vanilla: 0.0001
    private static float EXHAUSTION_WALK_ON_WATER_ONE_CM     = 0.0001F; // vanilla: 0.0001
    private static float EXHAUSTION_CLIMB_ONE_CM             = 0.0F; // vanilla: 0.0
    private static float EXHAUSTION_SPRINT_ONE_CM            = 0.001F; // vanilla: 0.001
    private static float EXHAUSTION_WALK_ONE_CM              = 0.0F; // vanilla: 0.0
    private static float EXHAUSTION_CROUCH_ONE_CM            = 0.0F; // vanilla: 0.0
    private static float EXHAUSTION_AVIATE_ONE_CM            = 0.0F; // vanilla: 0.0
    private static float EXHAUSTION_FLY_ONE_CM               = 0.0F; // vanilla: 0.0

    // Damage multiplier
    private static float EXHAUSTION_DAMAGE_MULTIPLIER = 1.0F; // vanilla: 1.0


    @ModifyArg(
            method = "jumpFromGround()V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"),
            index = 0
    )
    private float scarcity$jumpExhaustion(float vanilla) {
        Player self = (Player)(Object)this;
        return self.isSprinting() ? EXHAUSTION_JUMP_SPRINTING : EXHAUSTION_JUMP_WALKING;
    }

    @ModifyArg(
            method = "attack(Lnet/minecraft/world/entity/Entity;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V", ordinal = 0),
            index = 0,
            require = 0
    )
    private float scarcity$attackExhaustion(float vanilla) {
        return EXHAUSTION_ATTACK_HIT;
    }

    @ModifyArg(
            method = "actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"),
            index = 0,
            require = 0
    )
    private float scarcity$damageExhaustion(float vanilla) {
        return vanilla * EXHAUSTION_DAMAGE_MULTIPLIER;
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
                    self.causeFoodExhaustion(EXHAUSTION_SWIM_ONE_CM * (float) i);
                }
            } else if (self.isEyeInFluid(FluidTags.WATER)) {
                int j = Math.round((float) Math.sqrt(pDistanceX * pDistanceX + pDistanceY * pDistanceY + pDistanceZ * pDistanceZ) * 100.0F);
                if (j > 0) {
                    self.awardStat(Stats.WALK_UNDER_WATER_ONE_CM, j);
                    self.causeFoodExhaustion(EXHAUSTION_WALK_UNDERWATER_ONE_CM * (float) j);
                }
            } else if (self.isInWater()) {
                int k = Math.round((float) Math.sqrt(pDistanceX * pDistanceX + pDistanceZ * pDistanceZ) * 100.0F);
                if (k > 0) {
                    self.awardStat(Stats.WALK_ON_WATER_ONE_CM, k);
                    self.causeFoodExhaustion(EXHAUSTION_WALK_ON_WATER_ONE_CM * (float) k);
                }
            } else if (self.onClimbable()) {
                int cm = (int) Math.round(pDistanceY * 100.0D);
                if (pDistanceY > 0.0D) {
                    self.awardStat(Stats.CLIMB_ONE_CM, cm);
                    self.causeFoodExhaustion(EXHAUSTION_CLIMB_ONE_CM * (float) cm);
                }
            } else if (self.onGround()) {
                int l = Math.round((float) Math.sqrt(pDistanceX * pDistanceX + pDistanceZ * pDistanceZ) * 100.0F);
                if (l > 0) {
                    if (self.isSprinting()) {
                        self.awardStat(Stats.SPRINT_ONE_CM, l);
                        self.causeFoodExhaustion(EXHAUSTION_SPRINT_ONE_CM * (float) l);
                    } else if (self.isCrouching()) {
                        self.awardStat(Stats.CROUCH_ONE_CM, l);
                        self.causeFoodExhaustion(EXHAUSTION_CROUCH_ONE_CM * (float) l);
                    } else {
                        self.awardStat(Stats.WALK_ONE_CM, l);
                        self.causeFoodExhaustion(EXHAUSTION_WALK_ONE_CM * (float) l);
                    }
                }
            } else if (self.isFallFlying()) {
                int i1 = Math.round((float) Math.sqrt(pDistanceX * pDistanceX + pDistanceY * pDistanceY + pDistanceZ * pDistanceZ) * 100.0F);
                self.awardStat(Stats.AVIATE_ONE_CM, i1);
                self.causeFoodExhaustion(EXHAUSTION_AVIATE_ONE_CM * (float) i1);
            } else {
                int j1 = Math.round((float) Math.sqrt(pDistanceX * pDistanceX + pDistanceZ * pDistanceZ) * 100.0F);
                if (j1 > 25) {
                    self.awardStat(Stats.FLY_ONE_CM, j1);
                    self.causeFoodExhaustion(EXHAUSTION_FLY_ONE_CM * (float) j1);
                }
            }

        }
    }
}