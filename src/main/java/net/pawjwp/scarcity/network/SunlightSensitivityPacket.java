package net.pawjwp.scarcity.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.pawjwp.scarcity.SunlightSensitivity;

import java.util.function.Supplier;

// Makes sure the client knows if a mob is fire immune
public record SunlightSensitivityPacket(int entityId, SunlightSensitivity.State state) {

    public SunlightSensitivityPacket(FriendlyByteBuf buffer) {
        this(buffer.readVarInt(), buffer.readEnum(SunlightSensitivity.State.class));
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(entityId);
        buffer.writeEnum(state);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        LogicalSidedProvider.CLIENTWORLD.get(LogicalSide.CLIENT).ifPresent(level -> {
            if (level.getEntity(entityId) instanceof Mob mob) {
                SunlightSensitivity.setState(mob, state);
            }
        });
    }
}
