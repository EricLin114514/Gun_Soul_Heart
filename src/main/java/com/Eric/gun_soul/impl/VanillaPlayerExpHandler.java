package com.Eric.gun_soul.impl;

import com.Eric.gun_soul.api.IExperienceHandler;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class VanillaPlayerExpHandler implements IExperienceHandler {
    @Override
    public boolean canHandle(LivingEntity entity) {
        return entity instanceof Player;
    }

    @Override
    public int getCurrentLevel(LivingEntity entity) {
        return ((Player) entity).experienceLevel;
    }

    @Override
    public void consumeExperience(LivingEntity entity, int amount) {
        Player player = (Player) entity;
        // 這裡可以簡單扣除總經驗點數
        player.giveExperiencePoints(-amount);
    }
}