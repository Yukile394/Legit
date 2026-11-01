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
import java.util.ArrayDeque;
import java.util.Deque;
import net.minecraft.client.MinecraftClient;

public class KeystrokesCpsModule
extends Module {
    public final BooleanSetting showCpsRow = this.addSetting(new BooleanSetting("Show LMB/RMB CPS", "Tu\u015flar\u0131n alt\u0131nda anl\u0131k Sol ve Sa\u011f CPS sayac\u0131n\u0131 g\u00f6sterir", true));
    private final Deque<Long> leftClicks = new ArrayDeque<Long>();
    private final Deque<Long> rightClicks = new ArrayDeque<Long>();

    public KeystrokesCpsModule() {
        super("Keystrokes", "WASD hareket tu\u015flar\u0131n\u0131 ve ger\u00e7ek zamanl\u0131 Sol/Sa\u011f t\u0131k CPS sayac\u0131n\u0131 ekranda g\u00f6sterir", Category.MISC, -1);
        this.setEnabledSilent(true);
    }

    public void recordClick(int button) {
        long now = System.currentTimeMillis();
        if (button == 0) {
            this.leftClicks.addLast(now);
        } else if (button == 1) {
            this.rightClicks.addLast(now);
        }
    }

    public int getLeftCps() {
        long cutoff = System.currentTimeMillis() - 1000L;
        while (!this.leftClicks.isEmpty() && this.leftClicks.peekFirst() < cutoff) {
            this.leftClicks.pollFirst();
        }
        return this.leftClicks.size();
    }

    public int getRightCps() {
        long cutoff = System.currentTimeMillis() - 1000L;
        while (!this.rightClicks.isEmpty() && this.rightClicks.peekFirst() < cutoff) {
            this.rightClicks.pollFirst();
        }
        return this.rightClicks.size();
    }

    @Override
    public String getSuffix() {
        return this.getLeftCps() + " CPS";
    }

    @Override
    public void onTick(MinecraftClient mc) {
    }
}

