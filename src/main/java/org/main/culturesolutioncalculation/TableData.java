package org.main.culturesolutioncalculation;

import java.util.Map;

public class TableData {
    private Map<String, String> macroSettings;
    private Map<String, String> microSettings;

    public TableData() {
    }


    public Map<String, String> getMacroSettings() {
        return macroSettings;
    }

    public Map<String, String> getMicroSettings() {
        return microSettings;
    }

    public void setMacroSettings(Map<String, String> macroSettings) {
        this.macroSettings = macroSettings;
    }

    public void setMicroSettings(Map<String, String> microSettings) {
        this.microSettings = microSettings;
    }
}
