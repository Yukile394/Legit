/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 */
package com.legit.client.module.impl;

import com.legit.client.module.Category;
import com.legit.client.module.Module;
import com.legit.client.setting.BooleanSetting;
import net.minecraft.client.MinecraftClient;

public class ArmorStatusModule
extends Module {
    public final BooleanSetting showPercent = this.addSetting(new BooleanSetting("Durability %", "Z\u0131rh dayan\u0131kl\u0131l\u0131\u011f\u0131n\u0131 y\u00fczde olarak g\u00f6ster", true));
    public final BooleanSetting lowDurabilityAlert = this.addSetting(new BooleanSetting("Break Warning", "Z\u0131rh %20 alt\u0131na d\u00fc\u015ft\u00fc\u011f\u00fcnde ekranda k\u0131r\u0131lma uyar\u0131s\u0131 g\u00f6ster", true));

    public ArmorStatusModule() {
        super("ArmorHUD", "Ku\u015fan\u0131lan z\u0131rhlar\u0131, eldeki e\u015fyay\u0131 ve kalan dayan\u0131kl\u0131l\u0131k y\u00fczdelerini hotbar yan\u0131nda g\u00f6sterir", Category.RENDER, -1);
        this.setEnabledSilent(true);
    }

    @Override
    public void onTick(MinecraftClient mc) {
    }
}

