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
import com.legit.client.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;

public class EspRadarModule
extends Module {
    public final BooleanSetting showMiniRadar = this.addSetting(new BooleanSetting("2D Radar Map", "Sol \u00fcstte yak\u0131ndaki oyuncular\u0131 ve varl\u0131klar\u0131 g\u00f6steren dairesel/kare radar \u00e7izer", true));
    public final NumberSetting radarRange = this.addSetting(new NumberSetting("Radar Range", "Radar\u0131n tarad\u0131\u011f\u0131 maksimum mesafe", 48.0, 20.0, 120.0, 4.0, "m"));
    public final BooleanSetting showNearbyList = this.addSetting(new BooleanSetting("Nearby Players", "Radar\u0131n alt\u0131nda yak\u0131ndaki oyuncular\u0131n isim, can ve mesafesini listeler", true));
    public final BooleanSetting showMobsOnRadar = this.addSetting(new BooleanSetting("Show Mobs", "Canavarlar\u0131 da radarda turuncu nokta olarak g\u00f6ster", true));

    public EspRadarModule() {
        super("Radar2D", "Sol \u00fcstte yak\u0131ndaki oyuncular\u0131 2B mini radar ve mesafe/can listesiyle g\u00f6sterir", Category.RENDER, -1);
        this.setEnabledSilent(true);
    }

    @Override
    public String getSuffix() {
        return this.radarRange.getIntValue() + "m";
    }

    @Override
    public void onTick(MinecraftClient mc) {
    }
}

