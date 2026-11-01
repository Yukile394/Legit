/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.Entity$RemovalReason
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.network.OtherClientPlayerEntity
 */
package com.legit.client.module.impl;

import com.legit.client.module.Category;
import com.legit.client.module.Module;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;

public class FakePlayerModule
extends Module {
    private OtherClientPlayerEntity fakePlayer = null;
    private static final int FAKE_ENTITY_ID = -68420;

    public FakePlayerModule() {
        super("FakePlayer", "AimAssist, TriggerBot ve k\u0131rm\u0131z\u0131 PlayerESP'yi test etmek i\u00e7in sahte oyuncu olu\u015fturur", Category.MISC, -1);
    }

    @Override
    public void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.player == null || mc.world == null) {
            this.setEnabledSilent(false);
            return;
        }
        this.fakePlayer = new OtherClientPlayerEntity(mc.world, new GameProfile(UUID.randomUUID(), "LegitTarget"));
        this.fakePlayer.copyPositionAndRotation((Entity)mc.player);
        this.fakePlayer.setYaw(mc.player.getYaw());
        this.fakePlayer.headYaw = mc.player.headYaw;
        this.fakePlayer.bodyYaw = mc.player.bodyYaw;
        this.fakePlayer.setHealth(20.0f);
        this.fakePlayer.getInventory().clone(mc.player.getInventory());
        this.fakePlayer.setId(-68420);
        mc.world.addEntity((Entity)this.fakePlayer);
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc != null && mc.world != null) {
            mc.world.removeEntity(-68420, Entity.RemovalReason.DISCARDED);
        }
        this.fakePlayer = null;
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.world == null || mc.player == null) {
            this.setEnabledSilent(false);
        }
    }
}

