package com.Eric.gun_soul;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = "gun_soul",bus = Mod.EventBusSubscriber.Bus.MOD)
public class GunSoulConfig {
    private static final ForgeConfigSpec.Builder BUILDER =new ForgeConfigSpec.Builder();

    //Frenzy
    public static final ForgeConfigSpec.IntValue DECAY_INTERVAL_TICKS;
    public static final ForgeConfigSpec.DoubleValue BASE_DECAY_AMOUNT;
    public static final ForgeConfigSpec.DoubleValue BASE_KILL_CHARGE;
    public static final ForgeConfigSpec.ConfigValue<List<?extends String>> SPECIAL_ENTITY_CHARGES;
    public static final ForgeConfigSpec.IntValue FEVER_DURATION_TICKS;

    //Blood Rage
    public static final ForgeConfigSpec.DoubleValue BLOOD_RAGE_THRESHOLD;
    public static final ForgeConfigSpec.DoubleValue AMMO_PER_DAMAGE;
    public static final ForgeConfigSpec.IntValue BLOOD_RAGE_COOLDOWN;
    public static final ForgeConfigSpec.BooleanValue BLOOD_RAGE_GIVE_EFFECTS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BLOOD_RAGE_EFFECTS;

    static {
        BUILDER.push("狂喜模式設定(Frenzy Mode Settings)");

        BASE_DECAY_AMOUNT =BUILDER
                .comment("能量衰減值(0.0 - 100.0)")
                .defineInRange("baseDecayAmount",1,0.0,100.0);

        DECAY_INTERVAL_TICKS = BUILDER
                .comment("能量衰減頻率(tick,20 tick = 1 sec)")
                .defineInRange("decayIntervalTicks", 20,1,Integer.MAX_VALUE);

        FEVER_DURATION_TICKS = BUILDER
                .comment("Fever Mode 持續時間 (單位為 Tick, 200 Ticks = 10秒)")
                .defineInRange("feverDurationTicks", 200, 20, Integer.MAX_VALUE);

        BASE_KILL_CHARGE = BUILDER
                .comment("擊殺時獲取的基礎能量值")
                .defineInRange("baseKillCharge", 5.0,0.0,100.0);

        List<String> defaults = new ArrayList<>();

        defaults.add("minecraft:ender_dragon:100.0");
        defaults.add("minecraft:wither:50.0");

        SPECIAL_ENTITY_CHARGES = BUILDER
                .comment("enetyID:Charge")
                .defineList("specialEntityCharges", defaults, entry -> entry instanceof String);

        BUILDER.pop();

        BUILDER.push("血怒模式設定 (Blood Rage Settings)");
        BLOOD_RAGE_THRESHOLD = BUILDER
                .comment("觸發血怒的生命值百分比 (0.5 代表 50% 血量以下才觸發)")
                .defineInRange("bloodRageThreshold", 0.5, 0.0, 1.0);
        AMMO_PER_DAMAGE = BUILDER
                .comment("每 1 點傷害轉換的子彈數量")
                .defineInRange("ammoPerDamage", 10.0, 0.1, 64.0);
        BLOOD_RAGE_COOLDOWN = BUILDER
                .comment("觸發後的冷卻時間 (Tick)")
                .defineInRange("bloodRageCooldown", 100, 0, Integer.MAX_VALUE);
        BLOOD_RAGE_GIVE_EFFECTS = BUILDER
                .comment("觸發時是否給予藥水效果 (如力量、抗性)")
                .define("bloodRageGiveEffects", true);
        BLOOD_RAGE_EFFECTS = BUILDER
                .comment("觸發血怒時給予的藥水效果清單 (格式: \"效果ID;持續時間;等級\")",
                        "例如: \"minecraft:strength;60;0\" 代表 力量 I，持續 3 秒 (60 ticks)")
                .defineList("bloodRageEffects",
                        List.of("minecraft:strength;60;0", "minecraft:resistance;60;0"),
                        obj -> obj instanceof String);

        BUILDER.pop();
    }

    public static final ForgeConfigSpec SPEC =BUILDER.build();
}
