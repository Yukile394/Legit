/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraft.client.MinecraftClient
 */
package com.legit.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.legit.client.LegitClient;
import com.legit.client.module.Module;
import com.legit.client.setting.BooleanSetting;
import com.legit.client.setting.ModeSetting;
import com.legit.client.setting.NumberSetting;
import com.legit.client.setting.Setting;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import net.minecraft.client.MinecraftClient;

public class ConfigManager {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private String activeProfile = "Default";

    private File getConfigFile(String profileName) {
        String safe;
        String string = safe = profileName == null || profileName.isEmpty() ? "default" : profileName.toLowerCase().replaceAll("[^a-z0-9_-]", "");
        if ("default".equals(safe)) {
            return new File(MinecraftClient.getInstance().runDirectory, "legit-config.json");
        }
        return new File(MinecraftClient.getInstance().runDirectory, "legit-profile-" + safe + ".json");
    }

    public void save() {
        this.saveProfile(this.activeProfile);
    }

    public void saveProfile(String profileName) {
        try {
            this.activeProfile = profileName;
            JsonObject root = new JsonObject();
            root.addProperty("profile", profileName);
            JsonObject modulesJson = new JsonObject();
            for (Module module : LegitClient.getInstance().getModuleManager().getModules()) {
                JsonObject modObj = new JsonObject();
                modObj.addProperty("enabled", Boolean.valueOf(module.isEnabled()));
                modObj.addProperty("keybind", (Number)module.getKeybind().getValue());
                modObj.addProperty("holdMode", Boolean.valueOf(module.getKeybind().isHoldMode()));
                JsonObject settingsObj = new JsonObject();
                for (Setting<?> setting : module.getSettings()) {
                    if (setting instanceof BooleanSetting) {
                        BooleanSetting bs = (BooleanSetting)setting;
                        settingsObj.addProperty(bs.getName(), Boolean.valueOf(bs.isEnabled()));
                        continue;
                    }
                    if (setting instanceof NumberSetting) {
                        NumberSetting ns = (NumberSetting)setting;
                        settingsObj.addProperty(ns.getName(), (Number)ns.getValue());
                        continue;
                    }
                    if (!(setting instanceof ModeSetting)) continue;
                    ModeSetting ms = (ModeSetting)setting;
                    settingsObj.addProperty(ms.getName(), (String)ms.getValue());
                }
                modObj.add("settings", (JsonElement)settingsObj);
                modulesJson.add(module.getName(), (JsonElement)modObj);
            }
            root.add("modules", (JsonElement)modulesJson);
            Files.writeString(this.getConfigFile(profileName).toPath(), (CharSequence)this.gson.toJson((JsonElement)root), StandardCharsets.UTF_8, new OpenOption[0]);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public void load() {
        this.loadProfile("Default");
    }

    public boolean loadProfile(String profileName) {
        try {
            this.activeProfile = profileName;
            File file = this.getConfigFile(profileName);
            if (!file.exists()) {
                return false;
            }
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseString((String)content).getAsJsonObject();
            if (!root.has("modules")) {
                return false;
            }
            JsonObject modulesJson = root.getAsJsonObject("modules");
            for (Module module : LegitClient.getInstance().getModuleManager().getModules()) {
                if (!modulesJson.has(module.getName())) continue;
                JsonObject modObj = modulesJson.getAsJsonObject(module.getName());
                if (modObj.has("enabled")) {
                    module.setEnabledSilent(modObj.get("enabled").getAsBoolean());
                }
                if (modObj.has("keybind")) {
                    module.getKeybind().setValue(modObj.get("keybind").getAsInt());
                }
                if (modObj.has("holdMode")) {
                    module.getKeybind().setHoldMode(modObj.get("holdMode").getAsBoolean());
                }
                if (!modObj.has("settings")) continue;
                JsonObject settingsObj = modObj.getAsJsonObject("settings");
                for (Setting<?> setting : module.getSettings()) {
                    if (!settingsObj.has(setting.getName())) continue;
                    JsonElement el = settingsObj.get(setting.getName());
                    if (setting instanceof BooleanSetting) {
                        BooleanSetting bs = (BooleanSetting)setting;
                        bs.setValue(el.getAsBoolean());
                        continue;
                    }
                    if (setting instanceof NumberSetting) {
                        NumberSetting ns = (NumberSetting)setting;
                        ns.setValue(el.getAsDouble());
                        continue;
                    }
                    if (!(setting instanceof ModeSetting)) continue;
                    ModeSetting ms = (ModeSetting)setting;
                    ms.setValue(el.getAsString());
                }
            }
            return true;
        }
        catch (Exception ignored) {
            return false;
        }
    }

    public String getActiveProfile() {
        return this.activeProfile;
    }
}

