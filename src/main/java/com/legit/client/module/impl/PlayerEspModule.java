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
import com.legit.client.setting.NumberSetting;
import net.minecraft.client.MinecraftClient;

public class PlayerEspModule
extends Module {
    public final ModeSetting boxMode = this.addSetting(new ModeSetting("Box Style", "Oyuncu ESP kutu g\u00f6r\u00fcn\u00fcm\u00fc", "Corner Box", "Corner Box", "Full Box", "2D Fill Box", "Glow Only"));
    public final ModeSetting colorMode = this.addSetting(new ModeSetting("ESP Color", "PlayerESP ana renk tonu", "Crimson Red", "Crimson Red", "Blood Red", "Neon Red", "Distance Fade"));
    public final BooleanSetting glowOutline = this.addSetting(new BooleanSetting("Red Glow Shader", "Oyuncular\u0131n etraf\u0131na duvar arkas\u0131 k\u0131rm\u0131z\u0131 par\u0131lt\u0131 (Glow) \u00e7izer", true));
    public final BooleanSetting fillBox = this.addSetting(new BooleanSetting("Red Box Fill", "ESP kutusunun i\u00e7ini yar\u0131 saydam k\u0131rm\u0131z\u0131 ile doldurur", true));
    public final BooleanSetting healthBar = this.addSetting(new BooleanSetting("Health Bar", "ESP kutusunun solunda dikey can \u00e7ubu\u011fu g\u00f6sterir", true));
    public final BooleanSetting nametags = this.addSetting(new BooleanSetting("ESP Nametags", "Oyuncunun \u00fcst\u00fcnde isim, can ve mesafe kart\u0131 g\u00f6sterir", true));
    public final BooleanSetting showHeldItem = this.addSetting(new BooleanSetting("Show Held Item", "Oyuncunun elinde tuttu\u011fu e\u015fyay\u0131 ESP alt\u0131nda yazar", true));
    public final BooleanSetting tracers = this.addSetting(new BooleanSetting("Red Tracers", "Ekran merkezinden oyunculara k\u0131rm\u0131z\u0131 takip \u00e7izgisi (Tracer) \u00e7izer", false));
    public final NumberSetting maxDistance = this.addSetting(new NumberSetting("ESP Range", "PlayerESP maksimum g\u00f6sterim mesafesi", 120.0, 20.0, 250.0, 5.0, "m"));
    public final BooleanSetting includeMobs = this.addSetting(new BooleanSetting("Show Hostiles", "Canavarlar\u0131 da ESP i\u00e7inde g\u00f6ster", false));

    public PlayerEspModule() {
        super("PlayerESP", "Duvar arkas\u0131 k\u0131rm\u0131z\u0131 2B Kutu, K\u00f6\u015fe ESP, Glow Shader, Can Bar\u0131 ve Tracer", Category.RENDER, -1);
        this.setEnabledSilent(true);
    }

    public int getRedColor(double distance) {
        return switch ((String)this.colorMode.getValue()) {
            case "Blood Red" -> -2354116;
            case "Neon Red" -> -63686;
            case "Distance Fade" -> {
                float ratio = (float)Math.min(1.0, distance / Math.max(1.0, (Double)this.maxDistance.getValue()));
                int g = (int)(20.0f + ratio * 70.0f);
                yield 0xFFFF0000 | g << 8 | 0x30;
            }
            default -> -56264;
        };
    }

    @Override
    public String getSuffix() {
        return (String)this.boxMode.getValue();
    }

    @Override
    public void onTick(MinecraftClient mc) {
    }
}

