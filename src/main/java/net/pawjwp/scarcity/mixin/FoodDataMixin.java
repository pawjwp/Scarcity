package net.pawjwp.scarcity.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.pawjwp.scarcity.Scarcity;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FoodData.class, priority = 900)
public abstract class FoodDataMixin {

    @Shadow
    public abstract void addExhaustion(float exhaustion);

    @Shadow
    private float exhaustionLevel;

    // Clamp values

    @Unique
    private static Boolean scarcity$thirstLoaded = null;

    @Unique
    private static boolean scarcity$isThirstLoaded() {
        if (scarcity$thirstLoaded == null) {
            try {
                Class.forName("dev.ghen.thirst.foundation.common.capability.ModCapabilities");
                scarcity$thirstLoaded = true;
            } catch (ClassNotFoundException e) {
                scarcity$thirstLoaded = false;
            }
        }
        return scarcity$thirstLoaded;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void scarcity$clampExhaustionFloor(Player player, CallbackInfo ci) {
        // Clamp vanilla exhaustion
        if (ScarcityConfig.negativeExhaustionThreshold > -40.0F
                && this.exhaustionLevel < ScarcityConfig.negativeExhaustionThreshold) {
            this.exhaustionLevel = ScarcityConfig.negativeExhaustionThreshold;
        }

        // Clamp Thirst exhaustion if Thirst is loaded
        if (scarcity$isThirstLoaded()) {
            scarcity$clampThirstExhaustion(player);
        }
    }

    @Unique
    private void scarcity$clampThirstExhaustion(Player player) {
        try {
            // Get ModCapabilities class and PLAYER_THIRST field
            Class<?> modCapabilitiesClass = Class.forName("dev.ghen.thirst.foundation.common.capability.ModCapabilities");
            Object playerThirstCap = modCapabilitiesClass.getField("PLAYER_THIRST").get(null);

            // Get thirst capability
            net.minecraftforge.common.util.LazyOptional<?> lazyOptional = player.getCapability((net.minecraftforge.common.capabilities.Capability<?>) playerThirstCap);

            lazyOptional.ifPresent(thirst -> {
                try {
                    // Get exhaustion
                    java.lang.reflect.Method getExhaustion = thirst.getClass().getMethod("getExhaustion");
                    float thirstExhaustion = (float) getExhaustion.invoke(thirst);

                    if (ScarcityConfig.negativeExhaustionThreshold > -40.0F
                            && thirstExhaustion < ScarcityConfig.negativeExhaustionThreshold) {
                        java.lang.reflect.Method setExhaustion = thirst.getClass().getMethod("setExhaustion", float.class);
                        setExhaustion.invoke(thirst, ScarcityConfig.negativeExhaustionThreshold);
                    }
                } catch (Exception e) {
                    Scarcity.LOGGER.warn("Scarcity failed to clamp Thirst exhaustion", e);
                }
            });
        } catch (Exception e) {
            Scarcity.LOGGER.warn("Scarcity failed to access Thirst capability", e);
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

    // Exhaustion step
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
}