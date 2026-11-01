/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Blocks
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.client.MinecraftClient
 */
package com.legit.client.module.impl;

import com.legit.client.module.Category;
import com.legit.client.module.Module;
import com.legit.client.setting.ModeSetting;
import com.legit.client.setting.NumberSetting;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.MinecraftClient;

public class AntiCobwebModule
extends Module {
    public final ModeSetting mode = this.addSetting(new ModeSetting("Mode", "\u00d6r\u00fcmcek a\u011f\u0131ndan kurtulma y\u00f6ntemi", "FastWalk", "FastWalk", "FastFall"));
    public final NumberSetting speedBoost = this.addSetting(new NumberSetting("Web Speed", "A\u011f i\u00e7indeki hareket h\u0131z\u0131 \u00e7arpan\u0131", 0.24, 0.12, 0.45, 0.02, ""));

    public AntiCobwebModule() {
        super("AntiCobweb", "PvP'de \u00f6r\u00fcmcek a\u011flar\u0131n\u0131n (Cobweb) sizi yava\u015flatmas\u0131n\u0131 engeller", Category.MOVEMENT, -1);
    }

    @Override
    public String getSuffix() {
        return (String)this.mode.getValue();
    }

    @Override
    public void onTick(MinecraftClient mc) {
        boolean inWeb;
        if (mc.player == null || mc.world == null) {
            return;
        }
        BlockPos pos = mc.player.getBlockPos();
        boolean bl = inWeb = mc.world.getBlockState(pos).isOf(Blocks.COBWEB) || mc.world.getBlockState(pos.up()).isOf(Blocks.COBWEB);
        if (!inWeb) {
            return;
        }
        if (this.mode.isMode("FastFall") && !mc.player.isOnGround()) {
            Vec3d v = mc.player.getVelocity();
            mc.player.setVelocity(v.x, -0.42, v.z);
        } else {
            double spd = (Double)this.speedBoost.getValue();
            float yaw = (float)Math.toRadians(mc.player.getYaw());
            double forward = mc.player.input.movementForward;
            double strafe = mc.player.input.movementSideways;
            if (forward != 0.0 || strafe != 0.0) {
                double vx = (-Math.sin(yaw) * forward + Math.cos(yaw) * strafe) * spd;
                double vz = (Math.cos(yaw) * forward + Math.sin(yaw) * strafe) * spd;
                mc.player.setVelocity(vx, mc.player.getVelocity().y, vz);
            }
        }
    }
}

