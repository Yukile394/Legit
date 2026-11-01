/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.BlockItem
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.client.MinecraftClient
 */
package com.legit.client.module.impl;

import com.legit.client.module.Category;
import com.legit.client.module.Module;
import com.legit.client.setting.BooleanSetting;
import net.minecraft.item.BlockItem;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.MinecraftClient;

public class EagleBridgeModule
extends Module {
    public final BooleanSetting blocksOnly = this.addSetting(new BooleanSetting("Blocks Only", "Sadece elde blok varken \u00e7al\u0131\u015f\u0131r", true));
    public final BooleanSetting pitchCheck = this.addSetting(new BooleanSetting("Look Down Check", "Sadece a\u015fa\u011f\u0131 do\u011fru (Pitch > 45\u00b0) bakarken e\u011filir", true));
    private boolean sneakingByEagle = false;

    public EagleBridgeModule() {
        super("EagleBridge", "Yol yaparken (FastBridge / SpeedBridge) blok kenar\u0131nda otomatik e\u011filir", Category.MOVEMENT, 71);
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (this.sneakingByEagle && mc != null && mc.options != null) {
            mc.options.sneakKey.setPressed(false);
        }
        this.sneakingByEagle = false;
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.player == null || mc.world == null || mc.currentScreen != null) {
            return;
        }
        if (!mc.player.isOnGround() || mc.player.getAbilities().flying) {
            this.releaseIfNeeded(mc);
            return;
        }
        if (this.blocksOnly.isEnabled() && !(mc.player.getMainHandStack().getItem() instanceof BlockItem)) {
            this.releaseIfNeeded(mc);
            return;
        }
        if (this.pitchCheck.isEnabled() && mc.player.getPitch() < 45.0f) {
            this.releaseIfNeeded(mc);
            return;
        }
        BlockPos below = BlockPos.ofFloored((double)mc.player.getX(), (double)(mc.player.getY() - 0.5), (double)mc.player.getZ());
        boolean overAir = mc.world.getBlockState(below).isAir();
        if (overAir) {
            mc.options.sneakKey.setPressed(true);
            this.sneakingByEagle = true;
        } else {
            this.releaseIfNeeded(mc);
        }
    }

    private void releaseIfNeeded(MinecraftClient mc) {
        if (this.sneakingByEagle) {
            mc.options.sneakKey.setPressed(false);
            this.sneakingByEagle = false;
        }
    }
}

