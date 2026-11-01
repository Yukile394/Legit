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

public class HitboxModule
extends Module {
    public final NumberSetting expandSize = this.addSetting(new NumberSetting("Expand Size", "Rakiplerin vuru\u015f kutusunu (Hitbox) b\u00fcy\u00fctme miktar\u0131", 0.22, 0.05, 0.65, 0.02, "m"));
    public final BooleanSetting playersOnly = this.addSetting(new BooleanSetting("Players Only", "Sadece oyuncular\u0131n hitbox kutusunu b\u00fcy\u00fct\u00fcr", true));

    public HitboxModule() {
        super("Hitboxes", "Rakiplerin vuru\u015f kutusunu (Hitbox) geni\u015fleterek vuru\u015flar\u0131 kolayla\u015ft\u0131r\u0131r", Category.COMBAT, -1);
    }

    @Override
    public String getSuffix() {
        return String.format("+%.2fm", this.expandSize.getValue());
    }

    @Override
    public void onTick(MinecraftClient mc) {
    }
}

