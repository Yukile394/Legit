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
import com.legit.client.setting.ModeSetting;
import net.minecraft.client.MinecraftClient;

public class HudModule
extends Module {
    public final ModeSetting theme = this.addSetting(new ModeSetting("Color Theme", "Legit aray\u00fcz renk temas\u0131", "Legit Crimson", "Legit Crimson", "Ghost Monochrome", "Blood Moon", "Cyber Cyan", "Emerald Ghost", "Midnight Gold", "Sakura Mist"));
    public final BooleanSetting ghostModeHud = this.addSetting(new BooleanSetting("Minimal Ghost HUD", "Ekran aray\u00fcz\u00fcn\u00fc daha sade ve gizli (Ghost Client) g\u00f6r\u00fcn\u00fcmde \u00e7izer", false));
    public final BooleanSetting showWatermark = this.addSetting(new BooleanSetting("Watermark Pill", "Sol \u00fcstte Legit durum \u00e7ubu\u011funu g\u00f6sterir", true));
    public final BooleanSetting showCoordinates = this.addSetting(new BooleanSetting("Show XYZ & Speed", "Sol \u00fcst kart\u0131n alt\u0131nda anl\u0131k koordinat ve h\u0131z (BPS) bilgisini g\u00f6sterir", true));
    public final BooleanSetting showArrayList = this.addSetting(new BooleanSetting("Active ArrayList", "Sa\u011f \u00fcstte aktif mod\u00fclleri Legit tarz\u0131 listeler", true));
    public final ModeSetting arrayListStyle = this.addSetting(new ModeSetting("ArrayList Style", "Legit ArrayList \u00e7er\u00e7eve ve ge\u00e7i\u015f g\u00f6r\u00fcn\u00fcm\u00fc", "Legit Boxed", "Legit Boxed", "Legit Stepped", "Legit Minimal"));
    public final BooleanSetting arrayListSuffix = this.addSetting(new BooleanSetting("ArrayList Suffix", "ArrayList \u00fczerinde mod\u00fcl mod/de\u011fer bilgisini g\u00f6sterir", true));
    public final BooleanSetting showCrosshairRing = this.addSetting(new BooleanSetting("Crosshair Ring", "TriggerBot hedefe kilitlendi\u011finde ni\u015fangah etraf\u0131nda durum halkas\u0131 \u00e7izer", true));
    public final BooleanSetting showAnnouncements = this.addSetting(new BooleanSetting("Live Announcements", "Discord botundan g\u00f6nderilen canl\u0131 duyurular\u0131 ekranda g\u00f6sterir", true));
    public final BooleanSetting showNotifications = this.addSetting(new BooleanSetting("Toast Notifications", "Mod\u00fcl a\u00e7\u0131l\u0131p kapand\u0131\u011f\u0131nda sa\u011f altta bildirim g\u00f6sterir", true));

    public HudModule() {
        super("ClientHUD", "Legit tema, HUD g\u00f6r\u00fcn\u00fcm\u00fc ve bildirim ayarlar\u0131", Category.CLIENT, -1);
        this.setEnabledSilent(true);
    }

    @Override
    public String getSuffix() {
        return (String)this.theme.getValue();
    }

    @Override
    public void onTick(MinecraftClient mc) {
    }
}

