package com.Eric.gun_soul;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeDeferredRegistriesSetup;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,"gun_soul");

    public static final RegistryObject<Item> GUN_SOUL_HEART = ITEMS.register("gun_soul_heart",() -> new GunSoulItem());
}
