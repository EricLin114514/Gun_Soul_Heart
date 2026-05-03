# 銃魂之心
## [中文](#中文說明) **/** [English](#english-description)
# 中文說明
## 簡介

> **⚠️ 注意：本模組仍處於開發階段，使用前請務必備份存檔。**

本模組新增一個Curios飾品"銃魂之心"，並提供三種模式來為TACZ模組增加帶奇幻要素的玩法

## 支援版本及依賴模組
- **Minecraft** : 1.20.1
- **Forge** : 47.4.10
- **Timeless&Classic Zero** : 1.14+
- **Curios API** : 5.9.1+

## 功能概要
手持飾品並潛行並右鍵點擊可以在以下三種模式切換
1. **狂喜** :使用槍械擊殺生物時可以累計能量條，當能量條填滿時便會自動觸發**狂歡時刻**期間手持所有槍械皆進入無限子彈狀態
2. **血怒** :手持槍械時受到致命傷害時，會根據傷害量給予子彈和藥水效果(可自行設定)，具有冷卻時間
3. **賜福** :手持槍械射擊時，將有一定機率消耗經驗值轉換成靈魂子彈

## 開發者 API 接入指南
本模組飾品的所有功能能對任何具備配戴Curios飾品及使用TACZ槍械的實體生效

### 1. 依賴配置 
請直接將本模組jar置於`/libs`下並在`build.gradle`加入
```gradle
dependencies {
    compileOnly fg.deobf(fileTree(dir: 'libs', include: ['gun_soul-*.jar']))
}
```

### 2. 註冊自定義經驗系統
其中賜福的功能需要藉由API註冊實體要消耗的經驗系統類型才能生效，請參考
[VanillaPlayerExpHandler.java](src/main/java/com/Eric/gun_soul/impl/VanillaPlayerExpHandler.java)
的實作及以下註冊範例:
```java
//在模組的FMLCommonSetupEvent或其他初始化事件加入此程式碼
if (ModList.get().isLoaded("gun_soul")) {
        //只在安裝gun_soul註冊ExpHandler
        ExperienceHandlerRegistry.register(new TheirCustomExpHandler());
        }
```

> 本模組已原生兼容 **Touhou Little Maid (TLM)** 的女僕實體

## 本模組開發環境配置
由於 TACZ 採用 GPL 3.0 協議且資產禁止改作，本倉庫不包含其二進制 .jar 文件。若要進行開發或編譯，請執行以下步驟：
1. 下載依賴：前往 TACZ CurseForge 下載對應版本的模組文件。
2. 放置文件：在專案根目錄下手動創建 libs 資料夾（如果不存在）。
3. 移動檔案：將下載好的 tacz-xxx.jar 放入 libs 資料夾中。
4. 重新同步：在 IntelliJ IDEA 中點擊 Reload Gradle Project。

### AI生成內容聲明
本專案之邏輯架構均由本人構思完成。開發過程中僅將 Gemini (AI) 作為輔助工具，用於 Java 程式碼實作與架構優化建議。
> 注意： 儘管所有程式碼均經過人工審閱與測試優化，但仍無法保證完全排除邏輯漏洞。使用本模組存在存檔毀損等潜在風險，請務必在使用前進行備份。

如對代碼有優化相關的想法，歡迎提出

### 開發狀態與維護聲明
- **更新頻率**：由於開發者目前仍為學生（高三），開發時間極其有限。本模組的更新頻率將不固定，敬請見諒。
- **Bug 處理**：目前將優先處理導致遊戲崩潰或存檔毀損的重大 Bug，其餘輕微顯示問題或功能建議將視情況處理。

### 已知問題 (Known Issues)
- **彈藥顯示錯誤**：數值顯示異常，僅發生於使用靈魂子彈或無限子彈狀態下。
    - *狀態*: **WONTFIX**. 這僅是客戶端顯示錯誤，不影響遊戲體驗。
- *歡迎透過提交Issue來回報Bug!*

## 銘謝
- TACZ模組開發團隊，感謝他們設計如此優秀的槍械模組
- Curios API開發團隊，感謝他們提供API使我可以實現自定飾品

## 📜 授權
本專案採用 [MIT](LICENSE) 授權。

---------------
# English Description

## Description

> **⚠️ WARNING: This mod is still in the development stage. Please make sure to back up your saves before use.**

This mod adds a Curios accessory, the "Gun Soul Heart," providing three unique modes to integrate fantasy elements into the Timeless & Classic Zero (TACZ) mod.

## Supported Versions & Dependencies
- **Minecraft** : 1.20.1
- **Forge** : 47.4.10
- **Timeless&Classic Zero** : 1.14+
- **Curios API** : 5.9.1+

## Feature Overview
Hold the accessory and Sneak + Right-click to cycle through the following three modes:
1. **Frenzy** :Killing mobs with firearms accumulates energy. Once the bar is full, "Fever Time" triggers automatically, granting infinite ammo for all held firearms.
2. **Blood Rage** :When taking fatal damage while holding a firearm, you receive ammo and potion effects (configurable) based on the damage taken. This ability has a cooldown.
3. **Blessing** :While shooting, there is a chance to consume experience points to generate soul ammo.

## Developer API Integration Guide
All features of this accessory apply to any entity that can equip Curios and use TACZ firearms.
### 1. Dependency Configuration
Place the mod JAR in your /libs folder and add the following to your build.gradle:
```gradle
dependencies {
    compileOnly fg.deobf(fileTree(dir: 'libs', include: ['gun_soul-*.jar']))
}
```

### 2. Registering Custom Experience Systems
To use the "Blessing" feature, register your entity's experience system via the API. Refer to VanillaPlayerExpHandler.java for implementation:
[VanillaPlayerExpHandler.java](src/main/java/com/Eric/gun_soul/impl/VanillaPlayerExpHandler.java)
and use the registration example below:
```java
//Add this code to FMLCommonSetupEvent or other initialization events
if (ModList.get().isLoaded("gun_soul")) {
        //Register ExpHandler only if gun_soul is installed
        ExperienceHandlerRegistry.register(new TheirCustomExpHandler());
        }
```

> This mod natively supports entities from **Touhou Little Maid (TLM)** 

## Workspace Setup
Since TACZ is licensed under **GNU GPL v3.0** and prohibits the redistribution or modification of its assets, this repository does not include its binary `.jar` files. To develop or compile this project, please follow these steps:

1. **Download Dependencies**: Download the corresponding version from the TACZ CurseForge page.
2. **Setup Folder**: Create a `libs` folder in the project root.
3. **Add JAR**: Place the `tacz-xxx.jar` into the `libs` folder.
4. **Resync**: Click **Reload Gradle Project** in IntelliJ IDEA.

### AI-Generated Content Statement
The project's logical architecture was created by the developer. Gemini (AI) was used as a tool for Java implementation and structural suggestions.
> Note: All AI-generated code has been reviewed and optimized for readability. However, the developer cannot guarantee the absence of logical bugs. Using this mod may carry risks, such as save file corruption. Ensure backups are made before use.

Suggestions for code optimization are welcome.

### Development Status & Maintenance
- **Update Frequency**: As the developer is currently a high school senior, development time is limited. Updates will be irregular.
- **Bug Fixes**: Priority will be given to critical bugs (e.g., game crashes or save corruption). Minor visual issues or feature requests will be addressed as time permits.

### Known Issues
- **Ammo Display Glitch**: Visual numerical error only when using Soul Ammo or during Fever Mode.
    - *Status*: **WONTFIX**. This is a client-side visual only bug and does not affect gameplay.
- *Feel free to report bugs by opening an Issue!*


## Credits
- **TACZ Dev Team**: For designing such an outstanding firearm mod
- **Curios API Team**: For providing the API that enables custom accessory integration.

## 📜 License
This project is licensed under the MIT License.
