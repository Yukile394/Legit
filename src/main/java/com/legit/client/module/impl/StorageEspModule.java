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

public class StorageEspModule
extends Module {
    public final BooleanSetting chests = this.addSetting(new BooleanSetting("Chests & Barrels", "Sand\u0131k ve varilleri duvar arkas\u0131ndan g\u00f6sterir", true));
    public final BooleanSetting enderChests = this.addSetting(new BooleanSetting("Ender Chests", "Ender sand\u0131klar\u0131n\u0131 mor/k\u0131rm\u0131z\u0131 kutu ile g\u00f6sterir", true));
    public final BooleanSetting shulkers = this.addSetting(new BooleanSetting("Shulker Boxes", "Shulker kutular\u0131n\u0131 parlak k\u0131rm\u0131z\u0131 kutu ile g\u00f6sterir", true));
    public final NumberSetting range = this.addSetting(new NumberSetting("Range", "Maksimum sand\u0131k tarama mesafesi", 48.0, 16.0, 96.0, 4.0, "m"));

    public StorageEspModule() {
        super("StorageESP", "Sand\u0131k, Ender Sand\u0131\u011f\u0131 ve Shulker kutular\u0131n\u0131 duvar arkas\u0131ndan g\u00f6sterir", Category.RENDER, -1);
    }

    @Override
    public String getSuffix() {
        return this.range.getIntValue() + "m";
    }

    @Override
    public void onTick(MinecraftClient mc) {
    }
}

