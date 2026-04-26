package com.Eric.gun_soul.capability;


public interface IFrenzyEnergy {
    float getEnergy();
    void setEnergy(float energy);
    void addEnergy(float amount);
    int getFeverTicks();
    void setFeverTicks(int ticks);
    boolean isFeverMode();
}
