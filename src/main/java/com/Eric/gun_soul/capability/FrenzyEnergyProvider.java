package com.Eric.gun_soul.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FrenzyEnergyProvider  implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static Capability<IFrenzyEnergy> FRENZY_ENERGY = CapabilityManager.get(new CapabilityToken<IFrenzyEnergy>() {
    });

    private  IFrenzyEnergy backend = null;

    private final LazyOptional<IFrenzyEnergy> optional=LazyOptional.of(this::createFrenzyEnergy);

    private IFrenzyEnergy createFrenzyEnergy(){
        if (this.backend == null) {
            this.backend = new FrenzyEnergy();
        }
        return this.backend;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        // 這裡只負責回傳 Capability 本身，不處理 NBT
        if (cap == FRENZY_ENERGY) {
            return optional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        IFrenzyEnergy data = createFrenzyEnergy();

        // 儲存目前的能量值
        nbt.putFloat("energy", data.getEnergy());

        // 儲存 Fever Mode 的剩餘時間
        nbt.putInt("feverTicks", data.getFeverTicks());

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt){
        IFrenzyEnergy data = createFrenzyEnergy();

        // 讀取能量值
        data.setEnergy(nbt.getFloat("energy"));

        // 讀取 Fever Mode 剩餘時間
        data.setFeverTicks(nbt.getInt("feverTicks"));
    }

}
