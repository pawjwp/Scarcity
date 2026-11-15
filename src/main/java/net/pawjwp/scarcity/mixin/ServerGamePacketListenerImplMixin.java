package net.pawjwp.scarcity.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;
import net.pawjwp.scarcity.config.ScarcityConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {

    @Shadow
    public ServerPlayer player;

    @Shadow
    private double firstGoodX;

    @Shadow
    private double firstGoodY;

    @Shadow
    private double firstGoodZ;

    // The distance the server simulation moved the player this tick (which is how far water pushed them)
    @Unique
    private Vec3 scarcity$pushedDistance = Vec3.ZERO;

    // Records how far the simulated step moved the player before the server resets their position
    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;absMoveTo(DDDFF)V")
    )
    private void scarcity$recordPushedDistance(CallbackInfo ci) {
        this.scarcity$pushedDistance = this.player.position().subtract(this.firstGoodX, this.firstGoodY, this.firstGoodZ);
    }

    // Subtracts the distance pushed from the reported distance moved when in water
    @WrapOperation(
            method = "handleMovePlayer",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;checkMovementStatistics(DDD)V")
    )
    private void scarcity$subtractPushedDistance(ServerPlayer player, double distanceX, double distanceY, double distanceZ, Operation<Void> original) {
        Vec3 pushed = ScarcityConfig.ignorePushedMovement && player.isInWater() ? this.scarcity$pushedDistance : Vec3.ZERO;
        // Clear amount so it doesn't get counted again
        this.scarcity$pushedDistance = Vec3.ZERO;
        original.call(player, distanceX - pushed.x, distanceY - pushed.y, distanceZ - pushed.z);
    }
}
