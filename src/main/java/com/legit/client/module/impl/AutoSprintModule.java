/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 */
package com.legit.client.module.impl;

import com.legit.client.LegitClient;
import com.legit.client.module.Category;
import com.legit.client.module.Module;
import com.legit.client.module.impl.TriggerBotModule;
import com.legit.client.setting.BooleanSetting;
import net.minecraft.client.MinecraftClient;

public class AutoSprintModule
extends Module {
    public final BooleanSetting critSafe = this.addSetting(new BooleanSetting("Crit Safe Sync", "Havada kritik d\u00fc\u015f\u00fc\u015f an\u0131nda sprinti duraklatarak %100 kritik vuru\u015fa izin verir", true));

    public AutoSprintModule() {
        super("Sprint", "\u0130leri hareket ederken otomatik ko\u015fmay\u0131 (Sprint) aktif tutar (Kritik vuru\u015f uyumlu)", Category.MOVEMENT, -1);
        this.setEnabledSilent(true);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        TriggerBotModule tb;
        if (mc.player == null || mc.currentScreen != null) {
            return;
        }
        if (this.critSafe.isEnabled() && LegitClient.getInstance() != null && (tb = LegitClient.getInstance().getModuleManager().getModule(TriggerBotModule.class)) != null && tb.isPreparingCrit(mc)) {
            return;
        }
        if (mc.player.forwardSpeed > 0.0f && !mc.player.isSneaking() && !mc.player.horizontalCollision && mc.player.getHungerManager().getFoodLevel() > 6) {
            mc.player.setSprinting(true);
        }
    }
}

