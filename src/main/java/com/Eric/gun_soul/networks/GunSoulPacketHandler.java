package com.Eric.gun_soul.networks;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class GunSoulPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    // 保持不變
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("gun_soul", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;
    private static int id() { return packetId++; }

    public static void register() {
        // 這裡註冊封包
        INSTANCE.registerMessage(id(),
                FrenzyEnergySyncPacket.class,
                FrenzyEnergySyncPacket::encode,
                FrenzyEnergySyncPacket::decode,
                FrenzyEnergySyncPacket::handle
        );
    }
}