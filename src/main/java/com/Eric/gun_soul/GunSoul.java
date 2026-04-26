package com.Eric.gun_soul;

import com.Eric.gun_soul.networks.GunSoulPacketHandler;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

@Mod("gun_soul")
public class GunSoul {
    public GunSoul(){
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        //註冊Config.toml
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,GunSoulConfig.SPEC);

        // 註冊 CommonSetup 事件
        modEventBus.addListener(this::commonSetup);

        //註冊物品
        ModItems.ITEMS.register(modEventBus);

        //塞進創造模式分頁
        modEventBus.addListener(this::addCreative);
        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::enqueueIMC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(GunSoulPacketHandler::register);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey()== CreativeModeTabs.COMBAT){
            event.accept(ModItems.GUN_SOUL_HEART.get());
        }
    }

    private void enqueueIMC(final InterModEnqueueEvent event){
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE,() -> SlotTypePreset.NECKLACE.getMessageBuilder().build());
    }
}
