package net.pawjwp.scarcity.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.pawjwp.scarcity.Scarcity;

public final class ScarcityNetwork {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(Scarcity.MOD_ID, "main"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    private ScarcityNetwork() {}

    public static void register() {
        CHANNEL.messageBuilder(SunlightSensitivityPacket.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SunlightSensitivityPacket::encode)
                .decoder(SunlightSensitivityPacket::new)
                .consumerMainThread(SunlightSensitivityPacket::handle)
                .add();
    }
}
