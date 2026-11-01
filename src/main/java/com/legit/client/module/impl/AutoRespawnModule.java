/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.DeathScreen
 */
package com.legit.client.module.impl;

import com.legit.client.module.Category;
import com.legit.client.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DeathScreen;

public class AutoRespawnModule
extends Module {
    public AutoRespawnModule() {
        super("AutoRespawn", "\u00d6ld\u00fc\u011f\u00fcn\u00fczde \u00f6l\u00fcm ekran\u0131n\u0131 beklemeden an\u0131nda yeniden do\u011far", Category.MISC, -1);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.player != null && mc.currentScreen instanceof DeathScreen) {
            mc.player.requestRespawn();
            mc.setScreen(null);
        }
    }
}

