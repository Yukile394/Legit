/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.item.ItemStack
 *  net.minecraft.client.MinecraftClient
 */
package com.legit.client.module.impl;

import com.legit.client.module.Category;
import com.legit.client.module.Module;
import com.legit.client.setting.NumberSetting;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.item.ItemStack;
import net.minecraft.client.MinecraftClient;

public class AutoRefillModule
extends Module {
    public final NumberSetting minStackCount = this.addSetting(new NumberSetting("Min Stack %", "Hotbar e\u015fyas\u0131 bu y\u00fczdeli\u011fin alt\u0131na inince envanterden tamamlar", 30.0, 10.0, 80.0, 5.0, "%"));
    public final NumberSetting refillDelay = this.addSetting(new NumberSetting("Refill Delay", "Yenileme i\u015flemleri aras\u0131 bekleme s\u00fcresi", 120.0, 40.0, 400.0, 20.0, "ms"));
    private long lastRefillMs = 0L;

    public AutoRefillModule() {
        super("AutoRefill", "Azalan blok, alt\u0131n elma, ender incisi ve iksirleri envanterden hotbara otomatik doldurur", Category.PLAYER, -1);
    }

    @Override
    public String getSuffix() {
        return this.minStackCount.getIntValue() + "%";
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.player == null || mc.interactionManager == null || mc.currentScreen != null) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.lastRefillMs < (long)this.refillDelay.getIntValue()) {
            return;
        }
        for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
            int invMatch;
            double ratio;
            int maxCount;
            ItemStack hotbarStack = mc.player.getInventory().getStack(hotbarSlot);
            if (hotbarStack.isEmpty() || !hotbarStack.isStackable() || (maxCount = hotbarStack.getMaxCount()) <= 1 || (ratio = (double)hotbarStack.getCount() / (double)maxCount * 100.0) > (Double)this.minStackCount.getValue() || (invMatch = this.findMatchingInventorySlot(mc, hotbarStack)) == -1) continue;
            mc.interactionManager.clickSlot(mc.player.playerScreenHandler.syncId, invMatch, 0, SlotActionType.QUICK_MOVE, (PlayerEntity)mc.player);
            this.lastRefillMs = now;
            return;
        }
    }

    private int findMatchingInventorySlot(MinecraftClient mc, ItemStack target) {
        for (int i = 9; i < 36; ++i) {
            ItemStack candidate = mc.player.getInventory().getStack(i);
            if (candidate.isEmpty() || !ItemStack.areItemsAndComponentsEqual(target, candidate)) continue;
            return i;
        }
        return -1;
    }
}

