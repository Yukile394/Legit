/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.util.hit.HitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.util.hit.EntityHitResult
 */
package com.legit.client.module.impl;

import com.legit.client.module.Category;
import com.legit.client.module.Module;
import com.legit.client.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.EntityHitResult;

public class WTapModule
extends Module {
    public final NumberSetting resetDuration = this.addSetting(new NumberSetting("Reset Hold", "Vuru\u015f sonras\u0131 sprint s\u0131f\u0131rlama s\u00fcresi", 55.0, 20.0, 160.0, 5.0, "ms"));
    public final NumberSetting cooldownWait = this.addSetting(new NumberSetting("Min Cooldown", "Tekrar W-Tap yapmadan \u00f6nce bekleme s\u00fcresi", 380.0, 150.0, 900.0, 20.0, "ms"));
    private long releaseStartTime = 0L;
    private long lastWTapTime = 0L;
    private boolean resettingSprint = false;

    public WTapModule() {
        super("WTap", "Vuru\u015f an\u0131nda sprinti milisaniyelik s\u0131f\u0131rlayarak rakibe maksimum knockback verir", Category.COMBAT, -1);
    }

    @Override
    public String getSuffix() {
        return this.resetDuration.getIntValue() + "ms";
    }

    @Override
    public void onDisable() {
        this.resettingSprint = false;
    }

    @Override
    public void onTick(MinecraftClient mc) {
        Entity e;
        HitResult hit;
        if (mc.player == null || mc.world == null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (this.resettingSprint) {
            if (now - this.releaseStartTime >= (long)this.resetDuration.getIntValue()) {
                mc.player.setSprinting(true);
                this.resettingSprint = false;
            } else {
                mc.player.setSprinting(false);
            }
            return;
        }
        if (mc.player.handSwinging && mc.player.handSwingTicks == 1 && mc.player.isSprinting() && (hit = mc.crosshairTarget) != null && hit.getType() == HitResult.Type.ENTITY && (e = ((EntityHitResult)hit).getEntity()) instanceof LivingEntity && now - this.lastWTapTime >= (long)this.cooldownWait.getIntValue()) {
            this.resettingSprint = true;
            this.releaseStartTime = now;
            this.lastWTapTime = now;
            mc.player.setSprinting(false);
        }
    }
}

