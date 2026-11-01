/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.FireworkRocketEntity
 *  net.minecraft.item.Items
 *  net.minecraft.client.MinecraftClient
 */
package com.legit.client.module.impl;

import com.legit.client.LegitClient;
import com.legit.client.module.Category;
import com.legit.client.module.Module;
import com.legit.client.module.impl.TriggerBotModule;
import com.legit.client.setting.BooleanSetting;
import com.legit.client.setting.ModeSetting;
import com.legit.client.setting.NumberSetting;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.util.Hand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.Items;
import net.minecraft.client.MinecraftClient;

public class AutoFireworkModule
extends Module {
    public final ModeSetting bypassMode = this.addSetting(new ModeSetting("Bypass Mode", "ElytraTarget anti-cheat korumas\u0131 (Legit Delay = 3-A\u015famal\u0131 Ger\u00e7ek Oyuncu Ge\u00e7i\u015fi)", "Legit Multi-Tick", "Legit Multi-Tick", "Offhand Only"));
    public final NumberSetting minSpeedBps = this.addSetting(new NumberSetting("Min Speed BPS", "U\u00e7u\u015f h\u0131z\u0131 bu de\u011ferin alt\u0131na d\u00fc\u015ft\u00fc\u011f\u00fcnde fi\u015fek haz\u0131rl\u0131\u011f\u0131na ba\u015flar", 21.0, 10.0, 36.0, 1.0, "bps"));
    public final NumberSetting cooldownDelay = this.addSetting(new NumberSetting("Firework Delay", "\u0130ki fi\u015fek aras\u0131 minimum bekleme s\u00fcresi (+Rastgele Jitter eklenir)", 1850.0, 800.0, 4500.0, 50.0, "ms"));
    public final NumberSetting swapTicks = this.addSetting(new NumberSetting("Swap Wait Ticks", "Slota ge\u00e7tikten sonra sa\u011f t\u0131k basmadan \u00f6nce beklenecek tick (ElytraTarget Bypass)", 2.0, 1.0, 5.0, 1.0, "t"));
    public final BooleanSetting avoidCombatCollision = this.addSetting(new BooleanSetting("Combat Safe", "Tam vuru\u015f an\u0131nda (TriggerBot vururken) ayn\u0131 tickte fi\u015fek basmay\u0131 engeller", true));
    public final BooleanSetting checkActiveRocket = this.addSetting(new BooleanSetting("Wait Active Rocket", "Halihaz\u0131rda yanmakta olan bir fi\u015fe\u011finiz varsa yeni fi\u015fek basmaz", true));
    public final BooleanSetting fireOnEnable = this.addSetting(new BooleanSetting("Boost On Press", "Mod\u00fcl a\u00e7\u0131ld\u0131\u011f\u0131nda g\u00fcvenli tick s\u0131ras\u0131yla 1 adet fi\u015fek ate\u015fler", true));
    private State state = State.IDLE;
    private int stateTicksRemaining = 0;
    private int savedSlot = -1;
    private int targetRocketSlot = -1;
    private long lastFireTime = 0L;
    private long nextAllowedTime = 0L;
    private double currentJitteredMinBps = 21.0;
    private boolean pendingManualBoost = false;

    public AutoFireworkModule() {
        super("AutoFirework", "Anti-ElytraTarget korumal\u0131, \u00e7ok-tickli ger\u00e7ek\u00e7i havai fi\u015fek ate\u015fleme sistemi", Category.MOVEMENT, -1);
    }

    @Override
    public String getSuffix() {
        return (String)this.bypassMode.getValue();
    }

    public boolean isBusySwapping() {
        return this.isEnabled() && this.state != State.IDLE;
    }

    @Override
    public void onEnable() {
        this.state = State.IDLE;
        this.stateTicksRemaining = 0;
        this.savedSlot = -1;
        this.targetRocketSlot = -1;
        if (this.fireOnEnable.isEnabled()) {
            this.pendingManualBoost = true;
        }
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (this.state != State.IDLE && this.savedSlot >= 0 && this.savedSlot < 9 && mc != null && mc.player != null) {
            mc.player.getInventory().selectedSlot = this.savedSlot;
        }
        this.state = State.IDLE;
        this.savedSlot = -1;
        this.targetRocketSlot = -1;
        this.pendingManualBoost = false;
    }

    @Override
    public void onTick(MinecraftClient mc) {
        TriggerBotModule tb;
        if (mc.player == null || mc.world == null || mc.interactionManager == null || mc.currentScreen != null) {
            this.resetState(mc);
            return;
        }
        if (!mc.player.isFallFlying()) {
            this.resetState(mc);
            return;
        }
        if (this.state == State.SWITCHED_TO_ROCKET) {
            if (this.stateTicksRemaining > 0) {
                --this.stateTicksRemaining;
                return;
            }
            if (this.targetRocketSlot >= 0 && this.targetRocketSlot < 9) {
                mc.player.getInventory().selectedSlot = this.targetRocketSlot;
            }
            if (mc.player.getMainHandStack().isOf(Items.FIREWORK_ROCKET)) {
                mc.interactionManager.interactItem((PlayerEntity)mc.player, Hand.MAIN_HAND);
                mc.player.swingHand(Hand.MAIN_HAND);
                this.scheduleNextCooldown();
            }
            this.state = State.USED_ROCKET;
            this.stateTicksRemaining = Math.max(1, this.swapTicks.getIntValue() + ThreadLocalRandom.current().nextInt(0, 2));
            return;
        }
        if (this.state == State.USED_ROCKET) {
            if (this.stateTicksRemaining > 0) {
                --this.stateTicksRemaining;
                return;
            }
            if (this.savedSlot >= 0 && this.savedSlot < 9) {
                mc.player.getInventory().selectedSlot = this.savedSlot;
            }
            this.state = State.IDLE;
            this.savedSlot = -1;
            this.targetRocketSlot = -1;
            return;
        }
        long now = System.currentTimeMillis();
        if (!this.pendingManualBoost && now < this.nextAllowedTime) {
            return;
        }
        if (mc.player.isUsingItem()) {
            return;
        }
        if (this.checkActiveRocket.isEnabled() && this.hasActiveFireworkBoost(mc)) {
            this.pendingManualBoost = false;
            return;
        }
        if (this.avoidCombatCollision.isEnabled() && LegitClient.getInstance() != null && (tb = LegitClient.getInstance().getModuleManager().getModule(TriggerBotModule.class)) != null && tb.isEnabled() && tb.getCurrentLockedTarget() != null && (double)mc.player.distanceTo((Entity)tb.getCurrentLockedTarget()) <= 3.6) {
            return;
        }
        double dx = mc.player.getX() - mc.player.prevX;
        double dy = mc.player.getY() - mc.player.prevY;
        double dz = mc.player.getZ() - mc.player.prevZ;
        double bps = Math.sqrt(dx * dx + dy * dy + dz * dz) * 20.0;
        if (this.pendingManualBoost || bps < this.currentJitteredMinBps) {
            this.pendingManualBoost = false;
            this.startFireworkSequence(mc);
        }
    }

    private void startFireworkSequence(MinecraftClient mc) {
        if (mc.player.getOffHandStack().isOf(Items.FIREWORK_ROCKET)) {
            mc.interactionManager.interactItem((PlayerEntity)mc.player, Hand.OFF_HAND);
            mc.player.swingHand(Hand.OFF_HAND);
            this.scheduleNextCooldown();
            return;
        }
        if (this.bypassMode.isMode("Offhand Only")) {
            return;
        }
        if (mc.player.getMainHandStack().isOf(Items.FIREWORK_ROCKET)) {
            mc.interactionManager.interactItem((PlayerEntity)mc.player, Hand.MAIN_HAND);
            mc.player.swingHand(Hand.MAIN_HAND);
            this.scheduleNextCooldown();
            return;
        }
        int fireworkSlot = -1;
        for (int i = 0; i < 9; ++i) {
            if (!mc.player.getInventory().getStack(i).isOf(Items.FIREWORK_ROCKET)) continue;
            fireworkSlot = i;
            break;
        }
        if (fireworkSlot != -1) {
            this.savedSlot = mc.player.getInventory().selectedSlot;
            this.targetRocketSlot = fireworkSlot;
            mc.player.getInventory().selectedSlot = fireworkSlot;
            this.state = State.SWITCHED_TO_ROCKET;
            this.stateTicksRemaining = Math.max(1, this.swapTicks.getIntValue());
        }
    }

    private boolean hasActiveFireworkBoost(MinecraftClient mc) {
        for (Entity entity : mc.world.getEntities()) {
            FireworkRocketEntity rocket;
            if (!(entity instanceof FireworkRocketEntity) || !(rocket = (FireworkRocketEntity)entity).isAlive() || !(rocket.distanceTo((Entity)mc.player) < 2.2f)) continue;
            return true;
        }
        return false;
    }

    private void scheduleNextCooldown() {
        this.lastFireTime = System.currentTimeMillis();
        long baseCd = this.cooldownDelay.getIntValue();
        long jitter = ThreadLocalRandom.current().nextLong(-140L, 260L);
        this.nextAllowedTime = this.lastFireTime + Math.max(650L, baseCd + jitter);
        double speedJitter = ThreadLocalRandom.current().nextDouble(-1.4, 1.4);
        this.currentJitteredMinBps = Math.max(8.0, (Double)this.minSpeedBps.getValue() + speedJitter);
    }

    private void resetState(MinecraftClient mc) {
        if (this.state != State.IDLE && this.savedSlot >= 0 && this.savedSlot < 9 && mc != null && mc.player != null) {
            mc.player.getInventory().selectedSlot = this.savedSlot;
        }
        this.state = State.IDLE;
        this.stateTicksRemaining = 0;
        this.savedSlot = -1;
        this.targetRocketSlot = -1;
        this.pendingManualBoost = false;
    }

    private static enum State {
        IDLE,
        SWITCHED_TO_ROCKET,
        USED_ROCKET;

    }
}

