/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 */
package com.legit.client.module;

import com.legit.client.LegitClient;
import com.legit.client.module.Category;
import com.legit.client.setting.KeybindSetting;
import com.legit.client.setting.Setting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;

public abstract class Module {
    private final String name;
    private final String description;
    private final Category category;
    private boolean enabled;
    private boolean expanded;
    private float animProgress = 0.0f;
    private final KeybindSetting keybind;
    private final List<Setting<?>> settings = new ArrayList();

    public Module(String name, String description, Category category, int defaultKey) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.keybind = new KeybindSetting("Keybind", defaultKey);
    }

    protected <T extends Setting<?>> T addSetting(T setting) {
        this.settings.add(setting);
        return setting;
    }

    public void toggle() {
        this.setEnabled(!this.enabled);
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled) {
            return;
        }
        if (enabled && LegitClient.getInstance() != null && !LegitClient.getInstance().getLicenseGuard().isAuthorized()) {
            this.enabled = false;
            return;
        }
        this.enabled = enabled;
        if (this.enabled) {
            this.onEnable();
            if (LegitClient.getInstance() != null) {
                LegitClient.getInstance().getNotificationManager().push(this.name, "Mod\u00fcl aktif edildi", true);
            }
        } else {
            this.onDisable();
            if (LegitClient.getInstance() != null) {
                LegitClient.getInstance().getNotificationManager().push(this.name, "Mod\u00fcl devre d\u0131\u015f\u0131", false);
            }
        }
    }

    public void setEnabledSilent(boolean enabled) {
        this.enabled = enabled;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public abstract void onTick(MinecraftClient var1);

    public String getSuffix() {
        return "";
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Category getCategory() {
        return this.category;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isExpanded() {
        return this.expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public float getAnimProgress() {
        return this.animProgress;
    }

    public void setAnimProgress(float animProgress) {
        this.animProgress = Math.max(0.0f, Math.min(1.0f, animProgress));
    }

    public KeybindSetting getKeybind() {
        return this.keybind;
    }

    public List<Setting<?>> getSettings() {
        return this.settings;
    }
}

