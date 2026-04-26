package com.Eric.gun_soul.capability;

public class FrenzyEnergy implements IFrenzyEnergy{
    private float energy = 0.0f;

    @Override
    public float getEnergy() {
        return this.energy;
    }

    @Override
    public void setEnergy(float energy) {
        this.energy = Math.max(0,Math.min(100,energy));
    }

    @Override
    public void addEnergy(float amount) {
        this.setEnergy(this.energy + amount);
    }

    private int feverTicks = 0;

    @Override
    public int getFeverTicks() { return this.feverTicks; }
    @Override
    public void setFeverTicks(int ticks) { this.feverTicks = Math.max(0, ticks); }
    @Override
    public boolean isFeverMode() { return this.feverTicks > 0; }
}
