package org.main.culturesolutioncalculation;

import java.util.HashMap;
import java.util.Map;

public class SettingInfo {
    // SettingInfo 맵
    private static Map<String, String> totalSetting = new HashMap<>();

    public SettingInfo() {
    }

    public Map<String, String> getTotalSetting() {
        return totalSetting;
    }

    public static void setTotalSetting(Map<String, String> map) {
        totalSetting = map;
    }
}
