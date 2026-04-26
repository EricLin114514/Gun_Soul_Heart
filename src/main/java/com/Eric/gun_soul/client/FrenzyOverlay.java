package com.Eric.gun_soul.client;

import com.Eric.gun_soul.GunSoulConfig;
import com.Eric.gun_soul.ModItems;
import com.Eric.gun_soul.SoulHeartMode;
import com.Eric.gun_soul.capability.FrenzyEnergyProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;
import top.theillusivec4.curios.api.CuriosApi;

@Mod.EventBusSubscriber(modid = "gun_soul", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class FrenzyOverlay {

    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Pre event) {
        // 只針對經驗條進行攔截
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.EXPERIENCE_BAR.id())) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // 條件 1：檢查手持物品是否為 TACZ 的槍械
        // TACZ 的物品 ID 通常以 "tacz" 開頭
        ItemStack mainHand = mc.player.getMainHandItem();
        boolean isHoldingGun = mainHand.getItem().getCreatorModId(mainHand).equals("tacz");

        if (!isHoldingGun) return;

        // 條件 2：檢查飾品欄中的「銃魂之心」是否為狂喜模式
        CuriosApi.getCuriosHelper().findFirstCurio(mc.player, ModItems.GUN_SOUL_HEART.get()).ifPresent(slotResult -> {
            ItemStack heartStack = slotResult.stack();
            int modeIndex = heartStack.getOrCreateTag().getInt("SoulHeartMode");
            SoulHeartMode mode = SoulHeartMode.values()[modeIndex % SoulHeartMode.values().length];

            // 只有在狂喜模式下才取代 UI
            if (mode == SoulHeartMode.FRENZY) {
                mc.player.getCapability(FrenzyEnergyProvider.FRENZY_ENERGY).ifPresent(energy -> {
                    // 取消原版經驗條
                    event.setCanceled(true);
                    // 繪製我們的能量條
                    renderFrenzyBar(event.getGuiGraphics(), energy.getEnergy(),energy.isFeverMode(),energy.getFeverTicks());
                });
            }
        });
    }

    private static void renderFrenzyBar(GuiGraphics guiGraphics, float energy,Boolean isFever, float FeverTicks) {
        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();

        // 經驗條的標準位置通常在螢幕底部中心上方
        // X 軸：中心點左移 91 像素（經驗條總寬 182）
        // Y 軸：底部往上約 32 像素
        int x = width / 2 - 91;
        int y = height - 32 + 3; // 稍微調整一下高度對齊

        int barWidth = 182;
        int barHeight = 5;

        if (isFever) {
            // Fever 模式：畫金黃色或閃爍的倒計時條
            float ratio = (float) FeverTicks / GunSoulConfig.FEVER_DURATION_TICKS.get();
            int fillWidth = (int) (182 * ratio);

            guiGraphics.fill(x, y, x + 182, y + 5, 0xAA000000);
            guiGraphics.fill(x, y, x + fillWidth, y + 5, 0xFFFFD700); // 金色
        } else {

            // 計算目前能量對應的寬度
            int fillWidth = (int) (barWidth * (energy / 100.0f));

            // 繪製背景 (深灰色/黑色)
            guiGraphics.fill(x, y, x + barWidth, y + barHeight, 0xFF000000);

            // 繪製能量進度 (紅色，代表狂喜)
            // 使用 0xFFFF0000 為純紅，也可以加點透明度
            guiGraphics.fill(x, y, x + fillWidth, y + barHeight, 0xFFFF0000);

            // 繪製一個外框感 (可選)
            guiGraphics.renderOutline(x - 1, y - 1, barWidth + 2, barHeight + 2, 0xFF444444);
        }
    }
}