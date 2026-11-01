/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.AxeItem
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.hit.HitResult
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.util.hit.EntityHitResult
 */
package com.legit.client.module.impl;

import com.legit.client.module.Category;
import com.legit.client.module.Module;
import com.legit.client.setting.BooleanSetting;
import com.legit.client.setting.NumberSetting;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.hit.EntityHitResult;

public class ShieldBreakerModule
extends Module {
    public final NumberSetting switchBackDelay = this.addSetting(new NumberSetting("Return Delay", "Kalkan\u0131 k\u0131rd\u0131ktan sonra eski slota d\u00f6n\u00fc\u015f gecikmesi", 85.0, 20.0, 300.0, 10.0, "ms"));
    public final NumberSetting cooldownBetween = this.addSetting(new NumberSetting("Break Cooldown", "\u0130ki kalkan k\u0131rma denemesi aras\u0131ndaki minimum s\u00fcre", 450.0, 150.0, 1200.0, 25.0, "ms"));
    public final BooleanSetting autoSwing = this.addSetting(new BooleanSetting("Auto Swing", "Baltaya ge\u00e7ince otomatik vuru\u015f animasyonu oynat", true));
    private int savedSlot = -1;
    private long swapTime = 0L;
    private long lastBreakTime = 0L;

    public ShieldBreakerModule() {
        super("ShieldBreaker", "Rakip kalkan a\u00e7t\u0131\u011f\u0131nda otomatik baltaya ge\u00e7ip kalkan\u0131 k\u0131rar ve k\u0131l\u0131ca geri d\u00f6ner", Category.COMBAT, -1);
    }

    @Override
    public String getSuffix() {
        return this.switchBackDelay.getIntValue() + "ms";
    }

    @Override
    public void onDisable() {
        this.savedSlot = -1;
    }

    @Override
    public void onTick(MinecraftClient mc) {
        PlayerEntity target;
        if (mc.player == null || mc.world == null || mc.interactionManager == null || mc.currentScreen != null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (this.savedSlot != -1) {
            if (now - this.swapTime >= (long)this.switchBackDelay.getIntValue()) {
                if (this.savedSlot >= 0 && this.savedSlot < 9) {
                    mc.player.getInventory().selectedSlot = this.savedSlot;
                }
                this.savedSlot = -1;
            }
            return;
        }
        if (now - this.lastBreakTime < (long)this.cooldownBetween.getIntValue()) {
            return;
        }
        HitResult hit = mc.crosshairTarget;
        if (hit == null || hit.getType() != HitResult.Type.ENTITY) {
            return;
        }
        Entity entity = ((EntityHitResult)hit).getEntity();
        if (!(entity instanceof PlayerEntity) || (target = (PlayerEntity)entity) == mc.player || !target.isAlive()) {
            return;
        }
        if (!target.isBlocking()) {
            return;
        }
        int axeSlot = this.findHotbarAxe(mc);
        if (axeSlot == -1) {
            return;
        }
        int currentSlot = mc.player.getInventory().selectedSlot;
        if (currentSlot != axeSlot) {
            this.savedSlot = currentSlot;
            mc.player.getInventory().selectedSlot = axeSlot;
            this.swapTime = now;
        }
        mc.interactionManager.attackEntity((PlayerEntity)mc.player, (Entity)target);
        if (this.autoSwing.isEnabled()) {
            mc.player.swingHand(Hand.MAIN_HAND);
        }
        this.lastBreakTime = now;
    }

    private int findHotbarAxe(MinecraftClient mc) {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof AxeItem)) continue;
            return i;
        }
        return -1;
    }
}

