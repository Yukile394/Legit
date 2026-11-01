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

public class PearlTrackerModule
extends Module {
    public final BooleanSetting showTracer = this.addSetting(new BooleanSetting("Pearl Tracer", "Havada u\u00e7an Ender \u0130ncilerine k\u0131rm\u0131z\u0131 \u00e7izgi \u00e7izer", true));

    public PearlTrackerModule() {
        super("PearlESP", "At\u0131lan Ender \u0130ncilerini (Pearl) havada k\u0131rm\u0131z\u0131 kutu ve mesafe ile takip eder", Category.RENDER, -1);
        this.setEnabledSilent(true);
    }

    @Override
    public void onTick(MinecraftClient mc) {
    }
}

