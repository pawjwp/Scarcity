package net.pawjwp.scarcity.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.pawjwp.scarcity.RegenHealAmount;
import net.pawjwp.scarcity.attribute.ScarcityAttributes;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FoodData.class, priority = 900)
public abstract class FoodDataMixin {

    @Shadow
    public abstract void addExhaustion(float exhaustion);

    @Shadow
    private float exhaustionLevel;

    // Sets extreme negative hunger exhaustion to the configured floor
    @Inject(method = "tick", at = @At("HEAD"))
    private void scarcity$clampExhaustionFloor(Player player, CallbackInfo ci) {
        if (ScarcityConfig.negativeExhaustionThreshold > -40.0F
                && this.exhaustionLevel < ScarcityConfig.negativeExhaustionThreshold) {
            this.exhaustionLevel = ScarcityConfig.negativeExhaustionThreshold;
        }
    }

    // Other

    // Passive exhaustion
    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z"
            )
    )
    private void scarcity$passiveExhaustion(Player player, CallbackInfo ci) {
        if (!player.level().isClientSide && ScarcityConfig.passiveExhaustionPerTick != 0.0F) {
            this.addExhaustion(ScarcityConfig.passiveExhaustionPerTick);
        }
    }

    // Exhaustion multiplier
    @ModifyVariable(method = "addExhaustion", at = @At("HEAD"), argsOnly = true)
    private float scarcity$exhaustionMultiplier(float exhaustion) {
        return exhaustion * ScarcityConfig.exhaustionMultiplier;
    }


    // Regeneration

    // Applies fast or slow regeneration healing using the configured scaling
    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;heal(F)V")
    )
    private void scarcity$regenHeal(Player player, float amount, Operation<Void> original,
                                    @Share("healed") LocalBooleanRef healed,
                                    @Share("exhaustionBeforeHeal") LocalFloatRef exhaustionBeforeHeal) {
        float health = player.getHealth();
        exhaustionBeforeHeal.set(this.exhaustionLevel);
        RegenHealAmount.player = player;
        RegenHealAmount.amount = amount;
        original.call(player, amount);
        RegenHealAmount.player = null;
        healed.set(player.getHealth() > health);
    }

    // Applies exhaustion when the player is healed
    @WrapOperation(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V")
    )
    private void scarcity$regenExhaustion(FoodData instance, float exhaustion, Operation<Void> original,
                                          @Share("healed") LocalBooleanRef healed,
                                          @Share("exhaustionBeforeHeal") LocalFloatRef exhaustionBeforeHeal) {
        if (healed.get()) {
            original.call(instance, exhaustion);
        } else {
            this.exhaustionLevel = exhaustionBeforeHeal.get();
        }
    }


    // Fast regen

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "CONSTANT",
                    args = "intValue=20"
            ),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isHurt()Z", ordinal = 0),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V", ordinal = 0)
            )
    )
    private int scarcity$fastRegenFoodThreshold(int original) {
        return ScarcityConfig.fastRegenFoodThreshold;
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "CONSTANT",
                    args = "intValue=10"
            ),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isHurt()Z", ordinal = 0),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V", ordinal = 0)
            )
    )
    private int scarcity$fastRegenInterval(int original, @Local(argsOnly = true) Player player) {
        double speed = player.getAttributeValue(ScarcityAttributes.FAST_REGEN_SPEED.get())
                * player.getAttributeValue(ScarcityAttributes.REGEN_SPEED.get());
        if (speed <= 0.0) return Integer.MAX_VALUE;
        return Math.max(1, (int)(ScarcityConfig.fastRegenIntervalTicks / speed));
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "CONSTANT",
                    args = "floatValue=6.0"
            ),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isHurt()Z", ordinal = 0),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V", ordinal = 0)
            )
    )
    private float scarcity$fastRegenSaturationCap(float original) {
        return ScarcityConfig.fastRegenSaturationCap;
    }

    @ModifyArg(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V", ordinal = 0)
    )
    private float scarcity$fastRegenExhaustion(float exhaustion) {
        return exhaustion * ScarcityConfig.fastRegenExhaustionMultiplier;
    }


    // Slow regen

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "CONSTANT",
                    args = "intValue=18"
            )
    )
    private int scarcity$slowRegenFoodThreshold(int original) {
        return ScarcityConfig.slowRegenFoodThreshold;
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "CONSTANT",
                    args = "intValue=80"
            ),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isHurt()Z", ordinal = 1),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V", ordinal = 1)
            )
    )
    private int scarcity$slowRegenInterval(int original, @Local(argsOnly = true) Player player) {
        double speed = player.getAttributeValue(ScarcityAttributes.SLOW_REGEN_SPEED.get())
                * player.getAttributeValue(ScarcityAttributes.REGEN_SPEED.get());
        if (speed <= 0.0) return Integer.MAX_VALUE;
        return Math.max(1, (int)(ScarcityConfig.slowRegenIntervalTicks / speed));
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "CONSTANT",
                    args = "floatValue=1.0"
            ),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isHurt()Z", ordinal = 1),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V", ordinal = 1)
            )
    )
    private float scarcity$slowRegenHealAmount(float original) {
        return ScarcityConfig.slowRegenHealAmount;
    }

    @ModifyArg(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V", ordinal = 1)
    )
    private float scarcity$slowRegenExhaustion(float exhaustion) {
        return exhaustion * ScarcityConfig.slowRegenExhaustionMultiplier;
    }
}