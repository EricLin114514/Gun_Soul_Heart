package com.Eric.gun_soul.compat;

import com.Eric.gun_soul.api.ExperienceHandlerRegistry;
import net.minecraftforge.fml.ModList;

public class TLMCompat {
    public static void init() {
        if (ModList.get().isLoaded("touhou_little_maid")) {
            // 註冊女僕經驗處理器
            ExperienceHandlerRegistry.register(new MaidExperienceHandler());
        }
    }
}