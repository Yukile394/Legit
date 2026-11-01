/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.effect.StatusEffectInstance
 *  net.minecraft.entity.effect.StatusEffects
 *  net.minecraft.client.MinecraftClient
 */
package com.legit.client.module.impl;

import com.legit.client.module.Category;
import com.legit.client.module.Module;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.client.MinecraftClient;

public class FullbrightModule
extends Module {
    private boolean addedNightVision = false;

    public FullbrightModule() {
        super("FullBright", "Karanl\u0131k ma\u011faralar\u0131, madenleri ve gece ortam\u0131n\u0131 tamamen ayd\u0131nlat\u0131r", Category.RENDER, 66);
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (this.addedNightVision && mc != null && mc.player != null) {
            mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
        this.addedNightVision = false;
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.player == null) {
            return;
        }
        mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 400, 0, false, false, false));
        this.addedNightVision = true;
    }
}

