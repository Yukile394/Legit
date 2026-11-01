/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 */
package com.legit.client.security;

import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;

public class LicenseGuard {
    public void initialize() {
    }

    public boolean verifyLocalLicenseFile() {
        return true;
    }

    public void activateWithKeyAsync(String inputKey, Consumer<Boolean> callback) {
        if (callback != null) {
            callback.accept(true);
        }
    }

    public String getRemainingTimeFormatted() {
        return "SINIRSIZ";
    }

    public String getHwid() {
        return "LEGIT-FREE";
    }

    public boolean isAuthorized() {
        return true;
    }

    public String getStatusMessage() {
        return "Aktif";
    }

    public String getLicensedUser() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc != null && mc.getSession() != null) {
            return mc.getSession().getUsername();
        }
        return "LegitUser";
    }

    public String getLicenseKey() {
        return "UNLIMITED";
    }

    public String getLicensePlan() {
        return "UNLIMITED";
    }

    public String getLatestAnnouncement() {
        return "";
    }

    public String getLatestAnnouncementAuthor() {
        return "";
    }

    public long getLatestAnnouncementTimestamp() {
        return 0L;
    }

    public String getApiBaseUrl() {
        return "";
    }
}

