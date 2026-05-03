package com.Eric.gun_soul;

import com.Eric.gun_soul.capability.FrenzyEnergyProvider;
import com.Eric.gun_soul.networks.FrenzyEnergySyncPacket;
import com.Eric.gun_soul.networks.GunSoulPacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import javax.annotation.Nullable;
import java.util.List;

public class GunSoulItem extends Item implements ICurioItem {
    public static final String MODE_TAG ="SoulHeartMode";

    public GunSoulItem() {
        super(new Item.Properties().stacksTo(1));
    }

    //Curios
    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        LivingEntity entity = slotContext.entity();
        Level level = entity.level();

        if (level.isClientSide) return;

        entity.getCapability(FrenzyEnergyProvider.FRENZY_ENERGY).ifPresent(energy -> {
            if (energy.isFeverMode()) {
                // 1. 倒數計時
                energy.setFeverTicks(energy.getFeverTicks() - 1);

                // 2. 結束判斷
                if (energy.getFeverTicks() <= 0) {
                    energy.setEnergy(0f);
                    if (entity instanceof Player playerEntity) {
                        playerEntity.displayClientMessage(Component.translatable("message.gun_soul.fever_end"), true);
                    }
                }

                // 3. 同步封包
                GunSoulPacketHandler.INSTANCE.send(
                        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                        new FrenzyEnergySyncPacket(entity.getId(), energy.getEnergy(), energy.getFeverTicks())
                );
            } else {
                // --- 非 Fever 模式下的衰減邏輯 ---
                CompoundTag nbt = stack.getOrCreateTag();
                int modeindex = nbt.getInt("SoulHeartMode");
                SoulHeartMode mode = SoulHeartMode.values()[modeindex % SoulHeartMode.values().length];

                if (mode != SoulHeartMode.FRENZY)  return;

                int timer = nbt.getInt("DecayTimer") + 1;
                if (timer >= GunSoulConfig.DECAY_INTERVAL_TICKS.get()) {
                    if (energy.getEnergy() > 0) {
                        energy.addEnergy(-GunSoulConfig.BASE_DECAY_AMOUNT.get().floatValue());
                        // 封包同步
                        GunSoulPacketHandler.INSTANCE.send(
                                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                                new FrenzyEnergySyncPacket(entity.getId(), energy.getEnergy(), energy.getFeverTicks())
                        );
                    }
                    timer = 0;
                }
                nbt.putInt("DecayTimer", timer);
            }
        });
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player,@NotNull InteractionHand hand){
        ItemStack itemStack = player.getItemInHand(hand);

        //偵測及切換模式
        if (!player.isShiftKeyDown()) return  InteractionResultHolder.pass(itemStack);

        if (!level.isClientSide()){
            CompoundTag nbt = itemStack.getOrCreateTag();
            int currentModeIndex = nbt.getInt(MODE_TAG);
            SoulHeartMode currentmode =SoulHeartMode.values()[currentModeIndex % SoulHeartMode.values().length];
            SoulHeartMode nextMode = currentmode.next();
            nbt.putInt(MODE_TAG,nextMode.ordinal());
            player.displayClientMessage(Component.translatable("message.gun_soul.mode_switch", nextMode.getDisplayName()), true);
        }
        return InteractionResultHolder.sidedSuccess(itemStack,level.isClientSide());

    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip,@NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable("item.gun_soul.gun_soul_heart.desc"));

        CompoundTag nbt = stack.getOrCreateTag();
        int modeIndex = nbt.getInt("SoulHeartMode");
        SoulHeartMode mode = SoulHeartMode.values()[modeIndex % SoulHeartMode.values().length];

        // 顯示當前模式
        tooltip.add(Component.translatable("tooltip.gun_soul.current_mode", mode.getDisplayName()));
        tooltip.add(mode.getTooltip());
    }
}
