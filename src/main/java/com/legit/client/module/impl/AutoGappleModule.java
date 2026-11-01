/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.client.MinecraftClient
 */
package com.legit.client.module.impl;

import com.legit.client.module.Category;
import com.legit.client.module.Module;
import com.legit.client.setting.BooleanSetting;
import com.legit.client.setting.NumberSetting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.client.MinecraftClient;

public class AutoGappleModule
extends Module {
    public final NumberSetting healthThreshold = this.addSetting(new NumberSetting("Min Health", "Can bu de\u011ferin alt\u0131na d\u00fc\u015ft\u00fc\u011f\u00fcnde otomatik Alt\u0131n Elma yer", 13.0, 4.0, 19.0, 0.5, "HP"));
    public final BooleanSetting eatAbsorption = this.addSetting(new BooleanSetting("Keep Absorption", "Sar\u0131 kalpler (Absorption) bitti\u011finde de otomatik elma yer", false));
    private boolean eating = false;
    private int previousSlot = -1;

    public AutoGappleModule() {
        super("AutoGapple", "Can\u0131n\u0131z d\u00fc\u015ft\u00fc\u011f\u00fcnde otomatik Alt\u0131n Elma yer ve tekrar k\u0131l\u0131ca d\u00f6ner", Category.PLAYER, -1);
    }

    @Override
    public String getSuffix() {
        return String.format("%.1f HP", this.healthThreshold.getValue());
    }

    @Override
    public void onDisable() {
        this.stopEating(MinecraftClient.getInstance());
    }

    @Override
    public void onTick(MinecraftClient mc) {
        boolean needsHeal;
        if (mc.player == null || mc.currentScreen != null) {
            if (this.eating) {
                this.stopEating(mc);
            }
            return;
        }
        float hp = mc.player.getHealth();
        boolean bl = needsHeal = (double)hp <= (Double)this.healthThreshold.getValue() || this.eatAbsorption.isEnabled() && mc.player.getAbsorptionAmount() <= 0.0f && hp < 19.5f;
        if (!needsHeal) {
            if (this.eating) {
                this.stopEating(mc);
            }
            return;
        }
        if (this.isGapple(mc.player.getOffHandStack())) {
            mc.options.useKey.setPressed(true);
            this.eating = true;
            return;
        }
        int gapSlot = this.findHotbarGapple(mc);
        if (gapSlot != -1) {
            if (!this.eating) {
                this.previousSlot = mc.player.getInventory().selectedSlot;
            }
            mc.player.getInventory().selectedSlot = gapSlot;
            mc.options.useKey.setPressed(true);
            this.eating = true;
        } else if (this.eating) {
            this.stopEating(mc);
        }
    }

    private void stopEating(MinecraftClient mc) {
        if (mc != null && mc.options != null) {
            mc.options.useKey.setPressed(false);
            if (this.previousSlot >= 0 && this.previousSlot < 9 && mc.player != null) {
                mc.player.getInventory().selectedSlot = this.previousSlot;
            }
        }
        this.eating = false;
        this.previousSlot = -1;
    }

    private int findHotbarGapple(MinecraftClient mc) {
        for (int i = 0; i < 9; ++i) {
            if (!this.isGapple(mc.player.getInventory().getStack(i))) continue;
            return i;
        }
        return -1;
    }

    private boolean isGapple(ItemStack stack) {
        return stack.isOf(Items.GOLDEN_APPLE) || stack.isOf(Items.ENCHANTED_GOLDEN_APPLE);
    }
}

