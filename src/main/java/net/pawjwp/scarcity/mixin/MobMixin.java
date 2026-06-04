package net.pawjwp.scarcity.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.pawjwp.scarcity.SunlightSensitivity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// Adds an optional ScarcitySunlightSensitivity NBT tag to any LivingEntity, overriding fire immunity and making the mob burn in the daytime.
@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {

    @Unique
    private static final EntityDataAccessor<Byte> SCARCITY_SUNLIGHT_SENSITIVITY =
            SynchedEntityData.defineId(Mob.class, EntityDataSerializers.BYTE);

    protected MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    private SunlightSensitivity.State scarcity$sunlightState() {
        return SunlightSensitivity.State.byId(this.getEntityData().get(SCARCITY_SUNLIGHT_SENSITIVITY));
    }

    // Define sunlight sensitivity state
    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void scarcity$defineSunlight(CallbackInfo ci) {
        this.getEntityData().define(SCARCITY_SUNLIGHT_SENSITIVITY, (byte) SunlightSensitivity.State.DEFAULT.ordinal());
    }

    // Read the NBT tag as part of readAdditionalSaveData
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void scarcity$readSunlight(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains(SunlightSensitivity.TAG)) {
            SunlightSensitivity.State state = tag.getBoolean(SunlightSensitivity.TAG)
                    ? SunlightSensitivity.State.SENSITIVE
                    : SunlightSensitivity.State.INSENSITIVE;
            this.getEntityData().set(SCARCITY_SUNLIGHT_SENSITIVITY, (byte) state.ordinal());
        }
    }

    // Make the data persistent in addAdditionalSaveData
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void scarcity$writeSunlight(CompoundTag tag, CallbackInfo ci) {
        SunlightSensitivity.State state = scarcity$sunlightState();
        if (state != SunlightSensitivity.State.DEFAULT) {
            tag.putBoolean(SunlightSensitivity.TAG, state == SunlightSensitivity.State.SENSITIVE);
        }
    }

    // Remove fire immunity for applicable mobs
    @Override
    public boolean fireImmune() {
        if (scarcity$sunlightState() == SunlightSensitivity.State.SENSITIVE) {
            return false;
        }
        return super.fireImmune();
    }

    // Force fire to be displayed even on mobs that override isOnFire()
    @Override
    public boolean displayFireAnimation() {
        if (!this.isSpectator()
                && scarcity$sunlightState() == SunlightSensitivity.State.SENSITIVE
                && this.getSharedFlag(0)) {
            return true;
        }
        return super.displayFireAnimation();
    }

    // Override vanilla sunlight sensitivity for applicable mobs
    @Inject(method = "isSunBurnTick", at = @At("HEAD"), cancellable = true)
    private void scarcity$suppressVanillaSunBurn(CallbackInfoReturnable<Boolean> cir) {
        if (scarcity$sunlightState() != SunlightSensitivity.State.DEFAULT) {
            cir.setReturnValue(false);
        }
    }

    // Apply sunlight sensitivity for applicable mobs
    @Inject(method = "aiStep", at = @At("TAIL"))
    private void scarcity$applySunBurn(CallbackInfo ci) {
        if (scarcity$sunlightState() != SunlightSensitivity.State.SENSITIVE) {
            return;
        }
        Mob mob = (Mob) (Object) this;
        if (SunlightSensitivity.isSunBurnTick(mob)) {
            SunlightSensitivity.applySunBurn(mob);
        }
    }
}
