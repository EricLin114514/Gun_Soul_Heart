package com.Eric.gun_soul.compat;

import com.Eric.gun_soul.api.ExperienceHandlerRegistry;
import net.minecraftforge.fml.ModList;

import static com.mojang.text2speech.Narrator.LOGGER;

public class TLMCompat {
    public static void init() {
        if (ModList.get().isLoaded("touhou_little_maid")) {
            // 註冊女僕經驗處理器
            ExperienceHandlerRegistry.register(new MaidExperienceHandler());
            LOGGER.info("[GunSoul] Touhou Little Maid，registered maid compat");
        }
    }
}