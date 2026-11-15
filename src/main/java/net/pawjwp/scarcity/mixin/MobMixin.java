package net.pawjwp.scarcity.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.pawjwp.scarcity.ScarcitySunlightAccess;
import net.pawjwp.scarcity.SunlightSensitivity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Adds an optional ScarcitySunlightSensitivity NBT tag to any Mob, overriding fire immunity and making the mob burn in the daytime.
@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity implements ScarcitySunlightAccess {

    @Unique
    private SunlightSensitivity.State scarcity$sunlightState = SunlightSensitivity.State.DEFAULT;

    protected MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    public SunlightSensitivity.State scarcity$getSunlightState() {
        return this.scarcity$sunlightState;
    }

    @Override
    public void scarcity$setSunlightState(SunlightSensitivity.State state) {
        this.scarcity$sunlightState = state;
    }

    // Read the NBT tag as part of readAdditionalSaveData
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void scarcity$readSunlight(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains(SunlightSensitivity.TAG)) {
            SunlightSensitivity.State state = tag.getBoolean(SunlightSensitivity.TAG)
                    ? SunlightSensitivity.State.SENSITIVE
                    : SunlightSensitivity.State.INSENSITIVE;
            this.scarcity$sunlightSensitivity = (byte) state.ordinal();
        }
    }

    // Make the data persistent in addAdditionalSaveData
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void scarcity$writeSunlight(CompoundTag tag, CallbackInfo ci) {
        SunlightSensitivity.State state = scarcity$sunlightState();
            tag.putBoolean(SunlightSensitivity.TAG, state == SunlightSensitivity.State.SENSITIVE);
        if (this.scarcity$sunlightState != SunlightSensitivity.State.DEFAULT) {
        }
    }

    // Remove fire immunity for applicable mobs
    @Override
    public boolean fireImmune() {
        if (this.scarcity$sunlightState == SunlightSensitivity.State.SENSITIVE) {
            return false;
        }
        return super.fireImmune();
    }

    // Override vanilla sunlight sensitivity for applicable mobs
    @Inject(method = "isSunBurnTick", at = @At("HEAD"), cancellable = true)
    private void scarcity$suppressVanillaSunBurn(CallbackInfoReturnable<Boolean> cir) {
        if (this.scarcity$sunlightState != SunlightSensitivity.State.DEFAULT) {
            cir.setReturnValue(false);
        }
    }

    // Apply sunlight sensitivity for applicable mobs
    @Inject(method = "aiStep", at = @At("TAIL"))
    private void scarcity$applySunBurn(CallbackInfo ci) {
        if (this.scarcity$sunlightState != SunlightSensitivity.State.SENSITIVE || !this.isAlive()) {
            return;
        }
        Mob mob = (Mob) (Object) this;
        if (SunlightSensitivity.isSunBurnTick(mob)) {
            SunlightSensitivity.applySunBurn(mob);
        }
    }
}
