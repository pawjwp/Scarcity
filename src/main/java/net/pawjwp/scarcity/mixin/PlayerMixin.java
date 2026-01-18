package net.pawjwp.scarcity.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.player.Player;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerMixin {

    // Jump, Attack, and Damage Exhaustion

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

    // Movement Exhaustion (rate modification)

    // Swimming (vanilla: 0.01F * i * 0.01F)
    @ModifyExpressionValue(
            method = "checkMovementStatistics",
            at = @At(
                    value = "CONSTANT",
                    args = "floatValue=0.01",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSwimming()Z"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V", ordinal = 0)
            )
    )
    private float scarcity$swimExhaustionRate(float original) {
        return ScarcityConfig.exhaustionSwimOneBlock;
    }

    // Walk underwater (vanilla: 0.01F * j * 0.01F)
    @ModifyExpressionValue(
            method = "checkMovementStatistics",
            at = @At(
                    value = "CONSTANT",
                    args = "floatValue=0.01",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isEyeInFluid(Lnet/minecraft/tags/TagKey;)Z"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V", ordinal = 1)
            )
    )
    private float scarcity$walkUnderwaterExhaustionRate(float original) {
        return ScarcityConfig.exhaustionWalkUnderwaterOneBlock;
    }

    // Walk on water (vanilla: 0.01F * k * 0.01F)
    @ModifyExpressionValue(
            method = "checkMovementStatistics",
            at = @At(
                    value = "CONSTANT",
                    args = "floatValue=0.01",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isInWater()Z"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V", ordinal = 2)
            )
    )
    private float scarcity$walkOnWaterExhaustionRate(float original) {
        return ScarcityConfig.exhaustionWalkOnWaterOneBlock;
    }

    // Sprint (vanilla: 0.1F * l * 0.01F)
    @ModifyExpressionValue(
            method = "checkMovementStatistics",
            at = @At(
                    value = "CONSTANT",
                    args = "floatValue=0.1",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isSprinting()Z"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V", ordinal = 3)
            )
    )
    private float scarcity$sprintExhaustionRate(float original) {
        return ScarcityConfig.exhaustionSprintOneBlock;
    }

    // Crouch (vanilla: 0.0F * l * 0.01F)
    @ModifyExpressionValue(
            method = "checkMovementStatistics",
            at = @At(
                    value = "CONSTANT",
                    args = "floatValue=0.0",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isCrouching()Z"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V", ordinal = 4)
            )
    )
    private float scarcity$crouchExhaustionRate(float original) {
        return ScarcityConfig.exhaustionCrouchOneBlock;
    }

    // Walk (vanilla: 0.0F * l * 0.01F)
    @ModifyExpressionValue(
            method = "checkMovementStatistics",
            at = @At(
                    value = "CONSTANT",
                    args = "floatValue=0.0",
                    ordinal = 0
            ),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V", ordinal = 4),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isFallFlying()Z")
            )
    )
    private float scarcity$walkExhaustionRate(float original) {
        return ScarcityConfig.exhaustionWalkOneBlock;
    }

    // Movement Exhaustion (new calls)

    // Climbing
    @Inject(
            method = "checkMovementStatistics",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;onClimbable()Z",
                    shift = At.Shift.AFTER
            )
    )
    private void scarcity$climbExhaustion(double pDistanceX, double pDistanceY, double pDistanceZ, CallbackInfo ci) {
        Player self = (Player)(Object)this;
        if (self.onClimbable() && pDistanceY > 0.0D && ScarcityConfig.exhaustionClimbOneBlock != 0.0F) {
            int cm = (int) Math.round(pDistanceY * 100.0D);
            self.causeFoodExhaustion(ScarcityConfig.exhaustionClimbOneBlock * (float) cm * 0.01F);
        }
    }

    // Aviating
    @Inject(
            method = "checkMovementStatistics",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;isFallFlying()Z",
                    shift = At.Shift.AFTER
            )
    )
    private void scarcity$aviateExhaustion(double pDistanceX, double pDistanceY, double pDistanceZ, CallbackInfo ci) {
        Player self = (Player)(Object)this;
        if (self.isFallFlying() && ScarcityConfig.exhaustionAviateOneBlock != 0.0F) {
            int i1 = Math.round((float)Math.sqrt(pDistanceX * pDistanceX + pDistanceY * pDistanceY + pDistanceZ * pDistanceZ) * 100.0F);
            self.causeFoodExhaustion(ScarcityConfig.exhaustionAviateOneBlock * (float) i1 * 0.01F);
        }
    }

    // Flying
    @Inject(
            method = "checkMovementStatistics",
            at = @At("TAIL")
    )
    private void scarcity$flyExhaustion(double pDistanceX, double pDistanceY, double pDistanceZ, CallbackInfo ci) {
        Player self = (Player)(Object)this;
        if (!self.isPassenger() && !self.isSwimming() && !self.isEyeInFluid(net.minecraft.tags.FluidTags.WATER) &&
            !self.isInWater() && !self.onClimbable() && !self.onGround() && !self.isFallFlying() &&
            ScarcityConfig.exhaustionFlyOneBlock != 0.0F) {
            int j1 = Math.round((float)Math.sqrt(pDistanceX * pDistanceX + pDistanceZ * pDistanceZ) * 100.0F);
            if (j1 > 25) {
                self.causeFoodExhaustion(ScarcityConfig.exhaustionFlyOneBlock * (float) j1 * 0.01F);
            }
        }
    }
}
