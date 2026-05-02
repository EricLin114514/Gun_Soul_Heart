package com.Eric.gun_soul.api;

import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExperienceHandlerRegistry {
    private static final List<IExperienceHandler> HANDLERS = new ArrayList<>();

    //供其他 Mod 或本 Mod 內部註冊處理器
    public static void register(IExperienceHandler handler) {
        HANDLERS.add(handler);
    }

    //根據實體類型找到對應的處理器
    public static Optional<IExperienceHandler> getHandler(LivingEntity entity) {
        return HANDLERS.stream()
                .filter(handler -> handler.canHandle(entity))
                .findFirst();
    }
}
