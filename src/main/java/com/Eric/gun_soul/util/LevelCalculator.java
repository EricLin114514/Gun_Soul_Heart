package com.Eric.gun_soul.util;

public class LevelCalculator {

    //將總經驗值 (Total XP) 轉換為等級 (Level)
    public static int getLevelFromXP(long totalXP) {
        int level = 0;
        // 使用迴圈遞增檢查，確保精確度且符合分段函數邏輯
        while (getXPForLevel(level + 1) <= totalXP) {
            level++;
        }
        return level;
    }


    //計算到達指定等級所需的總經驗值 (Total XP)
    public static long getXPForLevel(int level) {
        if (level <= 16) {
            return (long) level * level + 6L * level;
        } else if (level <= 31) {
            return (long) (2.5 * level * level - 139.5 * level + 4420);
        } else {
            return (long) (4.5 * level * level - 162.5 * level + 2220);
        }
    }
}