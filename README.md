# ZeroPoint Server Bugfix Mod
GTNH version 2.6.1

Used to fix some bugs in already released versions such as GTNH2.6.1

Auto-update is supported in versions since 0.6.2! The Mod will be automatically downloaded into the Mods folder when there is an update and will be enabled on the next restart

## Function integration
1. [InputFix](https://github.com/zlainsama/InputFix) - Unable to enter Chinese bug
2. [Raw Mouse Input](https://github.com/seanld03/RawInputMod-1.12.2-1.7.10/tree/1.7.10) - Higher polling rate prevents mouse drift due to FPS changes

## Bug list:

These are versions from the release of 261. Other versions of the Mod will not apply!

1. TwilightForest (2.5.25) - Naga NPE bug (Crash) [PR](https://github.com/GTNewHorizons/twilightforest/pull/73)
2. AE2 (rv3-beta-357-GTNH) - CraftingCPUCluster NPE bug (Crash) [PR](https://github.com/GTNewHorizons/Applied-Energistics-2-Unofficial/pull/518)
3. bartworks (0.9.26) - Glass Bug (Crash) [PR](https://github.com/GTNewHorizons/GT5-Unofficial/pull/2640)
4. FindIt (1.2.5) - NPE bug [Code](https://github.com/GTNewHorizons/FindIt/blob/fc56416560b11689ce910308ca9832b5aaeb751b/src/main/java/com/gtnh/findit/service/itemfinder/ItemFindService.java#L74)
5. GT5u (5.09.45.168) - Tool Wrench NPE Bug [Code](https://github.com/GTNewHorizons/GT5-Unofficial/blob/0f3c69bf9cd31b0e006c85198deaa2056fb9a954/src/main/java/gregtech/common/tools/GT_Tool_Wrench.java#L214)
