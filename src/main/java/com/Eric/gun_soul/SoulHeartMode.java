package com.Eric.gun_soul;

import net.minecraft.ChatFormatting;

import net.minecraft.network.chat.Component;

public enum SoulHeartMode {
    FRENZY("mode.gun_soul.frenzy", ChatFormatting.GOLD,"mode.gun_soul.tooltip.frenzy"),
    BLOOD_RAGE("mode.gun_soul.blood_rage",ChatFormatting.RED,"mode.gun_soul.tooltip.blood_rage"),
    BLESSING("mode.gun_soul.blessing",ChatFormatting.GREEN,"mode.gun_soul.tooltip.blessing");

    private final String translationKey;
    private final ChatFormatting color;
    private final String tooltip;

    SoulHeartMode(String translationKey,ChatFormatting color,String tooltip){
        this.translationKey = translationKey;
        this.color =color;
        this.tooltip = tooltip;
    }

    public Component getDisplayName(){
        return Component.translatable(this.translationKey).withStyle(this.color);
    }

    public Component getTooltip(){
        return Component.translatable(this.tooltip);
    }

    public SoulHeartMode next(){
        return values()[(this.ordinal()+1) % values().length];
    }
}
