package net.pawjwp.scarcity.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {

    @Shadow public abstract void addExhaustion(float exhaustion);

    @Shadow private float exhaustionLevel;

    // Other

    @Inject(method = "tick", at = @At("HEAD"))
    private void scarcity$passiveExhaustion(Player player, CallbackInfo ci) {
        if (!player.level().isClientSide && ScarcityConfig.passiveExhaustionPerTick != 0.0F) {
            this.addExhaustion(ScarcityConfig.passiveExhaustionPerTick);
        }
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "CONSTANT",
                    args = "floatValue=4.0"
            ),
            slice = @Slice(
                    from = @At("HEAD"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z")
            )
    )
    private float scarcity$exhaustionStep(float original) {
        return ScarcityConfig.exhaustionStep;
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
    private int scarcity$fastRegenInterval(int original) {
        return ScarcityConfig.fastRegenIntervalTicks;
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
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isHurt()Z", ordinal = 0),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V", ordinal = 0)
            ),
            index = 0
    )
    private float scarcity$fastRegenExhaustionMult(float original) {
        return original * ScarcityConfig.fastRegenExhaustionMultiplier;
    }


    // Slow regen

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "CONSTANT",
                    args = "intValue=18"
            ),
            require = 0
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
    private int scarcity$slowRegenInterval(int original) {
        return ScarcityConfig.slowRegenIntervalTicks;
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
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V"),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isHurt()Z", ordinal = 1),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V", ordinal = 1)
            ),
            index = 0
    )
    private float scarcity$slowRegenExhaustion(float original) {
        return ScarcityConfig.slowRegenExhaustion;
    }
}
