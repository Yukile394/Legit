/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.fabricmc.api.ClientModInitializer
 *  net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
 *  net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
 *  net.minecraft.client.option.KeyBinding
 *  net.minecraft.client.util.InputUtil$Type
 *  net.minecraft.client.gui.screen.Screen
 */
package com.legit.client;

import com.legit.client.config.ConfigManager;
import com.legit.client.gui.ClickGuiScreen;
import com.legit.client.gui.NotificationManager;
import com.legit.client.module.ModuleManager;
import com.legit.client.security.LicenseGuard;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.gui.screen.Screen;

public class LegitClient
implements ClientModInitializer {
    public static final String CLIENT_NAME = "Legit";
    public static final String CLIENT_VERSION = "2.0.0";
    private static LegitClient instance;
    private LicenseGuard licenseGuard;
    private ModuleManager moduleManager;
    private ConfigManager configManager;
    private NotificationManager notificationManager;
    private KeyBinding openGuiKeyBinding;

    public void onInitializeClient() {
        instance = this;
        this.notificationManager = new NotificationManager();
        this.licenseGuard = new LicenseGuard();
        this.moduleManager = new ModuleManager();
        this.configManager = new ConfigManager();
        this.configManager.load();
        this.licenseGuard.initialize();
        this.openGuiKeyBinding = KeyBindingHelper.registerKeyBinding((KeyBinding)new KeyBinding("key.legit.open_gui", InputUtil.Type.KEYSYM, 260, "category.legit.general"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (this.openGuiKeyBinding.wasPressed()) {
                client.setScreen((Screen)new ClickGuiScreen());
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (this.configManager != null) {
                this.configManager.save();
            }
        }));
    }

    public static LegitClient getInstance() {
        return instance;
    }

    public LicenseGuard getLicenseGuard() {
        return this.licenseGuard;
    }

    public ModuleManager getModuleManager() {
        return this.moduleManager;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public NotificationManager getNotificationManager() {
        return this.notificationManager;
    }
}

