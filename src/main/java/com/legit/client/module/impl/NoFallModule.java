/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.packet.Packet
 *  net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket$OnGroundOnly
 *  net.minecraft.client.MinecraftClient
 */
package com.legit.client.module.impl;

import com.legit.client.module.Category;
import com.legit.client.module.Module;
import com.legit.client.setting.BooleanSetting;
import com.legit.client.setting.ModeSetting;
import com.legit.client.setting.NumberSetting;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.client.MinecraftClient;

public class NoFallModule
extends Module {
    public final ModeSetting mode = this.addSetting(new ModeSetting("Mode", "D\u00fc\u015fme hasar\u0131n\u0131 engelleme y\u00f6ntemi", "Packet", "Packet", "Grim / GroundSpoof"));
    public final NumberSetting minFallDist = this.addSetting(new NumberSetting("Trigger Fall", "Paketin g\u00f6nderilece\u011fi minimum d\u00fc\u015fme mesafesi", 2.8, 2.0, 5.0, 0.2, "m"));
    public final BooleanSetting pauseWhileGliding = this.addSetting(new BooleanSetting("Pause On Elytra", "Elytra ile s\u00fcz\u00fcl\u00fcrken paketi bekletir (Yere yakla\u015f\u0131rken korur)", true));

    public NoFallModule() {
        super("NoFall", "Y\u00fcksekten d\u00fc\u015ferken paket g\u00f6ndererek d\u00fc\u015fme hasar\u0131n\u0131 s\u0131f\u0131rlar", Category.PLAYER, -1);
    }

    @Override
    public String getSuffix() {
        return (String)this.mode.getValue();
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.player == null || mc.getNetworkHandler() == null) {
            return;
        }
        if (this.pauseWhileGliding.isEnabled() && mc.player.isFallFlying() && mc.player.fallDistance < 6.0f) {
            return;
        }
        if ((double)mc.player.fallDistance >= (Double)this.minFallDist.getValue()) {
            mc.getNetworkHandler().sendPacket((Packet)new PlayerMoveC2SPacket.OnGroundOnly(true));
            if (this.mode.isMode("Grim / GroundSpoof")) {
                mc.player.fallDistance = 0.0f;
            }
        }
    }
}

