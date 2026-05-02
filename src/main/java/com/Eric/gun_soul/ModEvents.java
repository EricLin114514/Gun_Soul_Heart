package com.Eric.gun_soul;

import com.Eric.gun_soul.api.ExperienceHandlerRegistry;
import com.Eric.gun_soul.capability.FrenzyEnergyProvider;
import com.Eric.gun_soul.networks.FrenzyEnergySyncPacket;
import com.Eric.gun_soul.networks.GunSoulPacketHandler;
import com.mojang.logging.LogUtils;
import com.tacz.guns.api.event.common.GunFireEvent;
import com.tacz.guns.api.item.IGun;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.List;

@Mod.EventBusSubscriber(modid = "gun_soul")
public class ModEvents {

    private static final Logger LOGGER = LogUtils.getLogger();

    //Gun Soul專屬虛擬彈藥，不採用TACZ的Dummy Ammo
    private static final String RESERVE_AMMO_TAG = "GunSoulReserveAmmo";
    private static final Logger log = LoggerFactory.getLogger(ModEvents.class);

    @SubscribeEvent
    public static void  onAttachCapablilitiiesPlayer(AttachCapabilitiesEvent<Entity> event){
        if (event.getObject() instanceof LivingEntity){
            if (!event.getObject().getCapability(FrenzyEnergyProvider.FRENZY_ENERGY).isPresent()){
                event.addCapability(new ResourceLocation("gun_soul","frenzy_energy"),new FrenzyEnergyProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onGunFire(GunFireEvent event) {
        LivingEntity shooter = event.getShooter();
        if (shooter.level().isClientSide) return;

        ItemStack gunStack = event.getGunItemStack();
        IGun iGun = IGun.getIGunOrNull(gunStack);
        if (iGun == null) return;

        // 獲取儲備彈藥數值
        int reserve = shooter.getPersistentData().getInt(RESERVE_AMMO_TAG);

        // --- 賜福模式處理 (Blessing Mode) ---
        handleBlessingLogic(shooter, reserve);

        // 檢查 Capability 狀態
        shooter.getCapability(FrenzyEnergyProvider.FRENZY_ENERGY).ifPresent(energy -> {
            // 1. 優先處理 Fever 模式 (無限子彈，不消耗儲備)
            if (energy.isFeverMode()) {
                iGun.setCurrentAmmoCount(gunStack, iGun.getCurrentAmmoCount(gunStack) + 1);
                return;
            }

            // 2. 處理虛擬儲備彈藥 (如果有儲備，則消耗儲備來補彈)
            if (reserve > 0) {
                iGun.setCurrentAmmoCount(gunStack, iGun.getCurrentAmmoCount(gunStack) + 1);
                shooter.getPersistentData().putInt(RESERVE_AMMO_TAG, reserve - 1);
            }
        });
    }

    //狂喜機制，能量條填充部分
    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        DamageSource source = event.getSource();

        // 1. 守衛：判定是否為槍械傷害 (檢查消息 ID)
        if (!source.getMsgId().contains("bullet")) return;

        // 2. 守衛：判定擊殺者是否為 LivingEntity
        if (!(source.getEntity() instanceof LivingEntity killer)) return;

        // 3. 核心守衛：檢查是否佩戴了銃魂之心
        CuriosApi.getCuriosHelper().findFirstCurio(killer, ModItems.GUN_SOUL_HEART.get()).ifPresent(slotResult -> {
            ItemStack heartStack = slotResult.stack();

            // 4. 核心邏輯：獲取 Capability 並執行充能
            killer.getCapability(FrenzyEnergyProvider.FRENZY_ENERGY).ifPresent(energy -> {

                int modeIndex = heartStack.getOrCreateTag().getInt("SoulHeartMode");
                SoulHeartMode mode = SoulHeartMode.values()[modeIndex % SoulHeartMode.values().length];

                // 守衛：如果已經在 Fever Mode，不增加能量
                if (energy.isFeverMode()) return;

                // 守衛：如果不在狂喜，不增加能量
                if (mode != SoulHeartMode.FRENZY) return;

                // 先獲取基礎值
                double charge = GunSoulConfig.BASE_KILL_CHARGE.get();
                String victimId = ForgeRegistries.ENTITY_TYPES.getKey(event.getEntity().getType()).toString();

                // 處理特殊實體加成 (這段可以考慮抽成一個獨立方法，但目前先平舖)
                List<? extends String> specialList = GunSoulConfig.SPECIAL_ENTITY_CHARGES.get();
                for (String entry : specialList) {
                    String[] parts = entry.split(":");
                    // 處理 domain:path:value (長度 3) 或 path:value (長度 2)
                    if (parts.length >= 2) {
                        String id = (parts.length == 3) ? parts[0] + ":" + parts[1] : parts[0];
                        String valStr = (parts.length == 3) ? parts[2] : parts[1];

                        if (id.equals(victimId)) {
                            try {
                                charge = Double.parseDouble(valStr);
                            } catch (NumberFormatException e) {
                                // 防止 Config 填錯導致崩潰
                                charge = GunSoulConfig.BASE_KILL_CHARGE.get();
                            }
                            break;
                        }
                    }
                }

                // 執行充能
                energy.addEnergy((float) charge);

                // 判定觸發 Fever Mode
                if (energy.getEnergy() >= 100f) {
                    int duration = GunSoulConfig.FEVER_DURATION_TICKS.get();
                    energy.setFeverTicks(duration);
                    energy.setEnergy(0); // 觸發後清空能量，或者你想滿條保持 100 也行，依你的規則

                    // Action bar 提示
                    if (killer instanceof net.minecraft.world.entity.player.Player player) {
                        player.displayClientMessage(
                                Component.translatable("message.gun_soul.fever_start")
                                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                                true // true 表示顯示在 Action Bar，false 則顯示在聊天欄
                        );
                    }
                }

                // 同步封包到客戶端
                GunSoulPacketHandler.INSTANCE.send(
                        PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> killer),
                        new FrenzyEnergySyncPacket(killer.getId(), energy.getEnergy(), energy.getFeverTicks())
                );

                // 除錯訊息 (之後可移除)
                // killer.sendSystemMessage(Component.literal("擊殺充能: " + charge + " | 當前能量: " + energy.getEnergy()));
            });

        });
    }

    //血怒機制：受傷時增加虛擬儲備彈藥
    @SubscribeEvent
    public static void onLivingHurt(@NotNull LivingHurtEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.level().isClientSide) return;

        // 1. 守衛：檢查是否佩戴首飾 (使用 Curios API)
        var curioOpt = CuriosApi.getCuriosHelper()
                .findFirstCurio(entity, ModItems.GUN_SOUL_HEART.get());
        if (curioOpt.isEmpty()) return;

        // 2. 守衛：模式檢查
        ItemStack heartStack = curioOpt.get().stack();
        int modeIndex = heartStack.getOrCreateTag().getInt("SoulHeartMode");
        SoulHeartMode mode = SoulHeartMode.values()[modeIndex % SoulHeartMode.values().length];
        if (mode != SoulHeartMode.BLOOD_RAGE) return;

        // 3. 守衛：生命值門檻檢查
        float threshold = entity.getMaxHealth() * GunSoulConfig.BLOOD_RAGE_THRESHOLD.get().floatValue();
        if (entity.getHealth() > threshold) return;

        // 4. 守衛：冷卻檢查 (使用 PersistentData)
        long currentTime = entity.level().getGameTime();
        long lastTrigger = entity.getPersistentData().getLong("BloodRageLastTick");
        if (currentTime - lastTrigger < GunSoulConfig.BLOOD_RAGE_COOLDOWN.get()) return;

        // 5. 守衛：手持槍械檢查
        ItemStack mainHand = entity.getMainHandItem();
        if (!(mainHand.getItem() instanceof IGun)) return;

        // --- 核心邏輯 ---

        // A. 計算回填量
        int ammoToAdd = Math.round(event.getAmount() * GunSoulConfig.AMMO_PER_DAMAGE.get().floatValue());
        if (ammoToAdd <= 0) return;

        // B. 執行補彈
        int currentReserve = entity.getPersistentData().getInt(RESERVE_AMMO_TAG);
        entity.getPersistentData().putInt(RESERVE_AMMO_TAG, currentReserve + ammoToAdd);

        entity.getPersistentData().putLong("BloodRageLastTick", currentTime);

        // C. 施加藥水效果
        applyBloodRageEffects(entity);

        // D. 更新狀態
        entity.getPersistentData().putLong("BloodRageLastTick", currentTime);
        if (entity instanceof net.minecraft.world.entity.player.Player player) {
            player.displayClientMessage(Component.translatable("message.gun_soul.blood_rage_triggered")
                    .withStyle(ChatFormatting.RED), true);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // 1. 守衛：基本環境判定
        if (event.phase != TickEvent.Phase.END || event.side.isClient()) return;

        Player player = event.player;

        // 2. 守衛：獲取並檢查 Fever 模式 (Fever 模式下不顯示此 UI)
        player.getCapability(FrenzyEnergyProvider.FRENZY_ENERGY).ifPresent(energy -> {
            if (energy.isFeverMode()) return;

            // 3. 守衛：檢查儲備彈藥數值
            int reserve = player.getPersistentData().getInt("GunSoulReserveAmmo");
            if (reserve <= 0) return;

            // 4. 守衛：檢查是否手持槍械 (使用IGun 介面)
            ItemStack mainHand = player.getMainHandItem();
            if (!(mainHand.getItem() instanceof IGun)) return;

            // --- 通過所有守衛，開始渲染 Action Bar ---

            // 構建顯示內容，例如: §c[ 血怒儲備: 25 ]
            Component uiMessage = Component.translatable("tooltip.gun_soul.reserve_ammo")
                    .append(Component.literal("§6[ §e" + reserve + " §6] "))
                    .withStyle(ChatFormatting.BOLD);

            // 常駐顯示在 Action Bar (第二個參數為 true)
            player.displayClientMessage(uiMessage, true);
        });
    }

    public static void handleBlessingLogic(LivingEntity shooter, int currentReserve){
        LOGGER.debug("[GunSoul] blessing start");
        // 守衛：檢查是否佩戴首飾
        var curioOpt = CuriosApi.getCuriosHelper().findFirstCurio(shooter, ModItems.GUN_SOUL_HEART.get());
        if (curioOpt.isEmpty()) return;

        // 守衛：模式檢查 (賜福模式)
        ItemStack heartStack = curioOpt.get().stack();
        int modeIndex = heartStack.getOrCreateTag().getInt(GunSoulItem.MODE_TAG);
        SoulHeartMode mode = SoulHeartMode.values()[modeIndex % SoulHeartMode.values().length];
        if (mode != SoulHeartMode.BLESSING) return;

        // 透過 API 獲取對應的處理器 (解耦點)
        ExperienceHandlerRegistry.getHandler(shooter).ifPresent(handler -> {
            //1.守衛:檢查reserve是否大於0
            if (currentReserve > 0) return;

            // 2. 守衛：等級底線判定
            int level = handler.getCurrentLevel(shooter);
            if (level < GunSoulConfig.BLESSING_MIN_LEVEL.get()) return;

            // 3. 守衛：機率判定 (隨等級增加概率)
            double chance = Math.min(GunSoulConfig.BLESSING_BASE_CHANCE.get() + ((level-GunSoulConfig.BLESSING_MIN_LEVEL.get()) * GunSoulConfig.CHANCE_PER_LEVEL.get()),GunSoulConfig.BLESSING_MAX_CHANCE.get());
            double roll =shooter.getRandom().nextDouble();

            LOGGER.debug("[GunSoul] chance={}, roll={}", String.format("%.2f", chance), String.format("%.2f", roll));

            if (roll > chance) return;

            // 4. 執行：消耗經驗並轉換為儲備彈藥
            int expCost = GunSoulConfig.BLESSING_EXP_COST.get();
            handler.consumeExperience(shooter, expCost);

            // 增加虛擬儲備 (轉換比例由 Config 設定)
            int ammoToGive = GunSoulConfig.BLESSING_AMMO_PER_TRIGGER.get();
            shooter.getPersistentData().putInt(RESERVE_AMMO_TAG,ammoToGive);

            LOGGER.info("blessing successes");
        });
    }

    private static void applyBloodRageEffects(LivingEntity entity) {
        List<? extends String> effectStrings = GunSoulConfig.BLOOD_RAGE_EFFECTS.get();
        for (String s : effectStrings) {
            try {
                String[] parts = s.split(";");
                if (parts.length < 3) continue;

                ResourceLocation id = new ResourceLocation(parts[0]);
                MobEffect effect = ForgeRegistries.MOB_EFFECTS.getValue(id);
                if (effect == null) continue;

                int duration = Integer.parseInt(parts[1]);
                int amplifier = Integer.parseInt(parts[2]);
                entity.addEffect(new MobEffectInstance(effect, duration, amplifier, false, true));
            } catch (Exception ignored) {}
        }
    }
}