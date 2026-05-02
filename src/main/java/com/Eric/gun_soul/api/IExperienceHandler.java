package com.Eric.gun_soul.api;

import net.minecraft.world.entity.LivingEntity;

public interface IExperienceHandler {

    //用於處理不同實體的經驗值消耗邏輯
    boolean canHandle(LivingEntity entity);

    //獲取實體當前的等級 (用於計算觸發機率)
    int getCurrentLevel(LivingEntity entity);

    //消耗實體的經驗值
    void consumeExperience(LivingEntity entity, int amount);
}
