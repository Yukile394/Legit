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

import com.legit.client.LegitClient;
import com.legit.client.module.Category;
import com.legit.client.module.Module;
import com.legit.client.module.impl.AimAssistModule;
import com.legit.client.setting.BooleanSetting;
import com.legit.client.setting.ModeSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.EntityHitResult;

public class TargetHudModule
extends Module {
    public final ModeSetting style = this.addSetting(new ModeSetting("Card Style", "Hedef bilgi kart\u0131 g\u00f6r\u00fcn\u00fcm\u00fc", "Modern Glass", "Modern Glass", "Compact Neon"));
    public final BooleanSetting showWinStatus = this.addSetting(new BooleanSetting("Win/Loss Badge", "Can kar\u015f\u0131la\u015ft\u0131rmas\u0131na g\u00f6re WINNING / LOSING etiketi g\u00f6sterir", true));
    private LivingEntity trackedTarget = null;
    private long lastTargetSeenMs = 0L;
    private float animatedHealthRatio = 1.0f;

    public TargetHudModule() {
        super("TargetHUD", "Sava\u015ft\u0131\u011f\u0131n veya ni\u015fan ald\u0131\u011f\u0131n hedefin can\u0131n\u0131 ve mesafesini ekranda g\u00f6sterir", Category.RENDER, -1);
        this.setEnabledSilent(true);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        AimAssistModule aim;
        LivingEntity living;
        Entity entity;
        if (mc.player == null || mc.world == null) {
            this.trackedTarget = null;
            return;
        }
        LivingEntity candidate = null;
        HitResult hit = mc.crosshairTarget;
        if (hit != null && hit.getType() == HitResult.Type.ENTITY && (entity = ((EntityHitResult)hit).getEntity()) instanceof LivingEntity && (living = (LivingEntity)entity) != mc.player && living.isAlive()) {
            candidate = living;
        }
        if (candidate == null && LegitClient.getInstance() != null && (aim = LegitClient.getInstance().getModuleManager().getModule(AimAssistModule.class)) != null && aim.isEnabled() && aim.getActiveTarget() != null) {
            candidate = aim.getActiveTarget();
        }
        if (candidate != null) {
            this.trackedTarget = candidate;
            this.lastTargetSeenMs = System.currentTimeMillis();
        } else if (this.trackedTarget != null && (!this.trackedTarget.isAlive() || this.trackedTarget.isRemoved() || mc.player.squaredDistanceTo((Entity)this.trackedTarget) > 196.0 || System.currentTimeMillis() - this.lastTargetSeenMs > 3000L)) {
            this.trackedTarget = null;
        }
    }

    public LivingEntity getTrackedTarget() {
        return this.trackedTarget;
    }

    public float getAnimatedHealthRatio() {
        return this.animatedHealthRatio;
    }

    public void setAnimatedHealthRatio(float animatedHealthRatio) {
        this.animatedHealthRatio = animatedHealthRatio;
    }
}

