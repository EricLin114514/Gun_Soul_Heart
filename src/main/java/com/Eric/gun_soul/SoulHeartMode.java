package com.Eric.gun_soul;

import net.minecraft.ChatFormatting;

import net.minecraft.network.chat.Component;

public enum SoulHeartMode {
    FRENZY("mode.gun_soul.frenzy", ChatFormatting.GOLD),
    BLOOD_RAGE("mode.gun_soul.blood_rage",ChatFormatting.RED),
    BLESSING("mode.gun_soul.blessing",ChatFormatting.GREEN);

    private final String translationKey;
    private final ChatFormatting color;

    SoulHeartMode(String translationKey,ChatFormatting color){
        this.translationKey = translationKey;
        this.color =color;
    }

    public Component getDisplayName(){
        return Component.translatable(this.translationKey).withStyle(this.color);
    }

    public SoulHeartMode next(){
        return values()[(this.ordinal()+1) % values().length];
    }
}
