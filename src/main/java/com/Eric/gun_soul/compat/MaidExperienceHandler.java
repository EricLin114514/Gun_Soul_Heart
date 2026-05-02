package com.Eric.gun_soul.compat;

import com.Eric.gun_soul.api.IExperienceHandler;
import com.Eric.gun_soul.util.LevelCalculator;
import com.github.tartaricacid.touhoulittlemaid.api.entity.IMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import net.minecraft.world.entity.LivingEntity;

public class MaidExperienceHandler implements IExperienceHandler {
    @Override
    public boolean canHandle(LivingEntity entity) {
        return entity instanceof IMaid;
    }

    @Override
    public int getCurrentLevel(LivingEntity entity) {
        int totalXP =((EntityMaid) entity).getExperience();
        return LevelCalculator.getLevelFromXP(totalXP);
    }

    @Override
    public void consumeExperience(LivingEntity entity, int amount) {
        EntityMaid maid =(EntityMaid) entity;
        maid.setExperience(maid.getExperience()-amount);
    }
}
