/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.hit.HitResult$Type
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket$PositionAndOnGround
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
 *  net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket$Mode
 *  net.minecraft.client.MinecraftClient
 */
package com.legit.client.module.impl;

import com.legit.client.module.Category;
import com.legit.client.module.Module;
import com.legit.client.setting.BooleanSetting;
import com.legit.client.setting.ModeSetting;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.client.MinecraftClient;

public class CriticalsModule
extends Module {
    public final ModeSetting mode = this.addSetting(new ModeSetting("Crit Mode", "Kritik vuru\u015f olu\u015fturma y\u00f6ntemi", "Packet", "Packet", "LowHop", "StopSprint Only"));
    public final BooleanSetting stopSprint = this.addSetting(new BooleanSetting("Auto StopSprint", "Vuru\u015f an\u0131nda sunucuda ko\u015fmay\u0131 keserek %100 kritik \u00e7\u0131kmas\u0131n\u0131 sa\u011flar", true));
    private boolean wasAttackPressed = false;

    public CriticalsModule() {
        super("Criticals", "T\u00fcm e\u015fyalarla yap\u0131lan vuru\u015flarda paket veya mini-z\u0131plama ile kritik hasar verir", Category.COMBAT, -1);
    }

    @Override
    public String getSuffix() {
        return (String)this.mode.getValue();
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.player == null || mc.getNetworkHandler() == null || mc.currentScreen != null) {
            return;
        }
        boolean attackPressed = mc.options.attackKey.isPressed();
        if (attackPressed && !this.wasAttackPressed && mc.crosshairTarget != null && mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
            this.triggerCrit(mc);
        }
        this.wasAttackPressed = attackPressed;
    }

    public void triggerCrit(MinecraftClient mc) {
        if (mc.player == null || mc.getNetworkHandler() == null) {
            return;
        }
        if (mc.player.isTouchingWater() || mc.player.isInLava() || mc.player.isClimbing()) {
            return;
        }
        if (this.stopSprint.isEnabled() && mc.player.isSprinting()) {
            mc.player.setSprinting(false);
            mc.getNetworkHandler().sendPacket((Packet)new ClientCommandC2SPacket((Entity)mc.player, ClientCommandC2SPacket.Mode.STOP_SPRINTING));
        }
        if (!mc.player.isOnGround()) {
            return;
        }
        double x = mc.player.getX();
        double y = mc.player.getY();
        double z = mc.player.getZ();
        if (this.mode.isMode("Packet")) {
            mc.getNetworkHandler().sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 0.0625, z, false));
            mc.getNetworkHandler().sendPacket((Packet)new PlayerMoveC2SPacket.PositionAndOnGround(x, y + 1.0E-4, z, false));
        } else if (this.mode.isMode("LowHop")) {
            mc.player.addVelocity(0.0, 0.14, 0.0);
            mc.player.fallDistance = 0.14f;
            mc.player.setOnGround(false);
        }
    }
}

