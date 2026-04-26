package com.Eric.gun_soul;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = "gun_soul",bus = Mod.EventBusSubscriber.Bus.MOD)
public class GunSoulConfig {
    private static final ForgeConfigSpec.Builder BUILDER =new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue DECAY_INTERVAL_TICKS;
    public static final ForgeConfigSpec.DoubleValue BASE_DECAY_AMOUNT;
    public static final ForgeConfigSpec.DoubleValue BASE_KILL_CHARGE;
    public static final ForgeConfigSpec.ConfigValue<List<?extends String>> SPECIAL_ENTITY_CHARGES;
    public static final ForgeConfigSpec.IntValue FEVER_DURATION_TICKS;

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
    }

    public static final ForgeConfigSpec SPEC =BUILDER.build();
}
