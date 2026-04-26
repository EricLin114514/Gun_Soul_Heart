package com.Eric.gun_soul.networks;

import com.Eric.gun_soul.capability.FrenzyEnergyProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FrenzyEnergySyncPacket {
    private final int entityId;
    private final float energy;
    private final int feverTicks; // 新增

    public FrenzyEnergySyncPacket(int entityId, float energy, int feverTicks) {
        this.entityId = entityId;
        this.energy = energy;
        this.feverTicks = feverTicks;
    }

    public static void encode(FrenzyEnergySyncPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.entityId);
        buf.writeFloat(msg.energy);
        buf.writeInt(msg.feverTicks); // 寫入封包
    }

    public static FrenzyEnergySyncPacket decode(FriendlyByteBuf buf) {
        return new FrenzyEnergySyncPacket(buf.readInt(), buf.readFloat(), buf.readInt()); // 讀取封包
    }

    // 在 FrenzyEnergySyncPacket.java 的 handle 方法內
    public static void handle(FrenzyEnergySyncPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // 這段在客戶端執行
            Entity entity = Minecraft.getInstance().level.getEntity(msg.entityId);
            if (entity instanceof LivingEntity living) {
                living.getCapability(FrenzyEnergyProvider.FRENZY_ENERGY).ifPresent(cap -> {
                    cap.setEnergy(msg.energy);
                    cap.setFeverTicks(msg.feverTicks); // 確保這行有加上
                });
            }
        });
        ctx.get().setPacketHandled(true);
    }
}