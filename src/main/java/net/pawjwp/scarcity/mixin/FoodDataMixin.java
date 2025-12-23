package net.pawjwp.scarcity.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Constant;

@Mixin(FoodData.class)
public abstract class FoodDataMixin {

    // Fast Regeneration
    private static int FAST_REGEN_FOOD_THRESHOLD = 20; // default: 20
    private static int FAST_REGEN_INTERVAL_TICKS = 10; // default: 10
    private static float FAST_REGEN_SATURATION_CAP = 6.0F; // default: 6.0
    private static float FAST_REGEN_EXHAUSTION_MULT = 1.0F; // default: 1.0

    // Slow Regeneration
    private static int SLOW_REGEN_FOOD_THRESHOLD = 18; // default: 18, Combat tests: 7
    private static int SLOW_REGEN_INTERVAL_TICKS = 80; // default: 80
    private static float SLOW_REGEN_HEAL_AMOUNT = 1.0F; // default: 1.0
    private static float SLOW_REGEN_EXHAUSTION = 6.0F; // default: 6.0

    // Other
    private static float EXHAUSTION_STEP = 4.0F; // default: 4.0
    private static float PASSIVE_EXHAUSTION_PER_TICK = 0.0F; // default: 0.0



    @Shadow public abstract void addExhaustion(float exhaustion);

    // Other
    @Inject(method = "tick", at = @At("HEAD"))
    private void scarcity$passiveExhaustion(Player player, CallbackInfo ci) {
        if (!player.level().isClientSide && PASSIVE_EXHAUSTION_PER_TICK != 0.0F) {
            this.addExhaustion(PASSIVE_EXHAUSTION_PER_TICK);
        }
    }

    @ModifyConstant(
            method = "tick",
            constant = @Constant(floatValue = 4.0F),
            slice = @Slice(
                    from = @At("HEAD"),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/GameRules;getBoolean(Lnet/minecraft/world/level/GameRules$Key;)Z")
            )
    )
    private float scarcity$exhaustionStep(float original) {
        return EXHAUSTION_STEP;
    }


    // Fast regen

    @ModifyConstant(
            method = "tick",
            constant = @Constant(intValue = 20),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isHurt()Z", ordinal = 0),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V", ordinal = 0)
            )
    )
    private int scarcity$fastRegenFoodThreshold(int original) {
        return FAST_REGEN_FOOD_THRESHOLD;
    }

    @ModifyConstant(
            method = "tick",
            constant = @Constant(intValue = 10),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isHurt()Z", ordinal = 0),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V", ordinal = 0)
            )
    )
    private int scarcity$fastRegenInterval(int original) {
        return FAST_REGEN_INTERVAL_TICKS;
    }

    @ModifyConstant(
            method = "tick",
            constant = @Constant(floatValue = 6.0F),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isHurt()Z", ordinal = 0),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V", ordinal = 0)
            )
    )
    private float scarcity$fastRegenSaturationCap(float original) {
        return FAST_REGEN_SATURATION_CAP;
    }

    @ModifyArg(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V", ordinal = 0),
            index = 0
    )
    private float scarcity$fastRegenExhaustionMult(float original) {
        return original * FAST_REGEN_EXHAUSTION_MULT;
    }


    // Slow regen

    @ModifyConstant(
            method = "tick",
            constant = @Constant(intValue = 18),
            require = 0
    )
    private int scarcity$slowRegenFoodThreshold(int original) {
        return SLOW_REGEN_FOOD_THRESHOLD;
    }

    @ModifyConstant(
            method = "tick",
            constant = @Constant(intValue = 80),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isHurt()Z", ordinal = 1),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V", ordinal = 1)
            )
    )
    private int scarcity$slowRegenInterval(int original) {
        return SLOW_REGEN_INTERVAL_TICKS;
    }

    @ModifyConstant(
            method = "tick",
            constant = @Constant(floatValue = 1.0F),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;isHurt()Z", ordinal = 1),
                    to = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V", ordinal = 1)
            )
    )
    private float scarcity$slowRegenHealAmount(float original) {
        return SLOW_REGEN_HEAL_AMOUNT;
    }

    @ModifyArg(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addExhaustion(F)V", ordinal = 1),
            index = 0
    )
    private float scarcity$slowRegenExhaustion(float original) {
        return SLOW_REGEN_EXHAUSTION;
    }
}
