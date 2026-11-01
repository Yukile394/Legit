/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.client.MinecraftClient
 */
package com.legit.client.module.impl;

import com.legit.client.module.Category;
import com.legit.client.module.Module;
import com.legit.client.setting.ModeSetting;
import com.legit.client.setting.NumberSetting;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.client.MinecraftClient;

public class ElytraBoostModule
extends Module {
    public final ModeSetting boostMode = this.addSetting(new ModeSetting("Boost Mode", "Tek tu\u015fla g\u00fcvenli (Multi-Tick) fi\u015fek veya h\u0131z d\u00fc\u015f\u00fcnce otomatik fi\u015fek", "Legit Macro", "Legit Macro", "Auto Maintain"));
    public final NumberSetting minSpeedBps = this.addSetting(new NumberSetting("Min Speed", "Auto Maintain modunda h\u0131z bu de\u011ferin alt\u0131na inince fi\u015fek basar", 22.0, 12.0, 35.0, 1.0, " BPS").visibleWhen(() -> this.boostMode.isMode("Auto Maintain")));
    public final NumberSetting rocketCooldown = this.addSetting(new NumberSetting("Rocket Delay", "\u0130ki fi\u015fek aras\u0131ndaki minimum bekleme s\u00fcresi", 1750.0, 600.0, 4000.0, 50.0, "ms"));
    private int stage = 0;
    private int waitTicks = 0;
    private int prevSlot = -1;
    private int rocketSlot = -1;
    private long lastRocketMs = 0L;
    private boolean requestedMacro = false;

    public ElytraBoostModule() {
        super("ElytraFly", "Anti-ElytraTarget korumal\u0131 ger\u00e7ek\u00e7i gecikmeli havai fi\u015fek makrosu", Category.MOVEMENT, 67);
    }

    @Override
    public String getSuffix() {
        return (String)this.boostMode.getValue();
    }

    @Override
    public void onEnable() {
        if (this.boostMode.isMode("Legit Macro")) {
            this.requestedMacro = true;
        }
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (this.stage != 0 && this.prevSlot >= 0 && this.prevSlot < 9 && mc != null && mc.player != null) {
            mc.player.getInventory().selectedSlot = this.prevSlot;
        }
        this.stage = 0;
        this.waitTicks = 0;
        this.prevSlot = -1;
        this.rocketSlot = -1;
        this.requestedMacro = false;
    }

    @Override
    public void onTick(MinecraftClient mc) {
        double dz;
        double dy;
        if (mc.player == null || mc.interactionManager == null || mc.currentScreen != null) {
            return;
        }
        if (this.stage == 1) {
            if (this.waitTicks > 0) {
                --this.waitTicks;
                return;
            }
            if (this.rocketSlot >= 0 && this.rocketSlot < 9) {
                mc.player.getInventory().selectedSlot = this.rocketSlot;
            }
            if (mc.player.getMainHandStack().isOf(Items.FIREWORK_ROCKET)) {
                mc.interactionManager.interactItem((PlayerEntity)mc.player, Hand.MAIN_HAND);
                mc.player.swingHand(Hand.MAIN_HAND);
                this.lastRocketMs = System.currentTimeMillis();
            }
            this.stage = 2;
            this.waitTicks = 2;
            return;
        }
        if (this.stage == 2) {
            if (this.waitTicks > 0) {
                --this.waitTicks;
                return;
            }
            if (this.prevSlot >= 0 && this.prevSlot < 9) {
                mc.player.getInventory().selectedSlot = this.prevSlot;
            }
            this.stage = 0;
            this.prevSlot = -1;
            this.rocketSlot = -1;
            if (this.boostMode.isMode("Legit Macro")) {
                this.setEnabledSilent(false);
            }
            return;
        }
        if (this.boostMode.isMode("Legit Macro")) {
            if (this.requestedMacro) {
                this.requestedMacro = false;
                if (mc.player.isFallFlying()) {
                    this.startLegitRocket(mc);
                    if (this.stage == 0) {
                        this.setEnabledSilent(false);
                    }
                } else {
                    this.setEnabledSilent(false);
                }
            }
            return;
        }
        if (!mc.player.isFallFlying()) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.lastRocketMs < (long)this.rocketCooldown.getIntValue()) {
            return;
        }
        double dx = mc.player.getX() - mc.player.prevX;
        double bps = Math.sqrt(dx * dx + (dy = mc.player.getY() - mc.player.prevY) * dy + (dz = mc.player.getZ() - mc.player.prevZ) * dz) * 20.0;
        if (bps < (Double)this.minSpeedBps.getValue()) {
            this.startLegitRocket(mc);
        }
    }

    private void startLegitRocket(MinecraftClient mc) {
        if (mc.player.getOffHandStack().isOf(Items.FIREWORK_ROCKET)) {
            mc.interactionManager.interactItem((PlayerEntity)mc.player, Hand.OFF_HAND);
            mc.player.swingHand(Hand.OFF_HAND);
            this.lastRocketMs = System.currentTimeMillis();
            return;
        }
        int found = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (s.isEmpty() || !s.isOf(Items.FIREWORK_ROCKET)) continue;
            found = i;
            break;
        }
        if (found == -1) {
            return;
        }
        this.prevSlot = mc.player.getInventory().selectedSlot;
        this.rocketSlot = found;
        mc.player.getInventory().selectedSlot = found;
        this.stage = 1;
        this.waitTicks = 2;
    }
}

