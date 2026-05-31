package net.pawjwp.scarcity.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.level.Level;
import net.pawjwp.scarcity.BurnRule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Set fireImmune() to false and isSunSensitive() to true when zombified piglin burn rule matches.
// Server determines the outcome based on configured dimension, biome, and structure conditions.
// This value is synced to the client to ensure both sides have the same value.

@Mixin(ZombifiedPiglin.class)
public abstract class ZombifiedPiglinMixin extends Zombie {

    protected ZombifiedPiglinMixin(EntityType<? extends Zombie> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(BurnRule.OUTCOME_DATA, (byte) BurnRule.Outcome.NO_RULE.ordinal());
    }

    @Inject(method = "customServerAiStep", at = @At("HEAD"))
    private void scarcity$updateBurnOutcome(CallbackInfo ci) {
        this.getEntityData().set(BurnRule.OUTCOME_DATA, (byte) BurnRule.evaluate(this).ordinal());
    }

    @Override
    public boolean fireImmune() {
        return switch (BurnRule.currentOutcome(this)) {
            case BURN -> false;
            case NO_BURN -> true;
            case NO_RULE -> super.fireImmune();
        };
    }

    @Override
    protected boolean isSunSensitive() {
        return switch (BurnRule.currentOutcome(this)) {
            case BURN -> true;
            case NO_BURN -> false;
            case NO_RULE -> super.isSunSensitive();
        };
    }
}
