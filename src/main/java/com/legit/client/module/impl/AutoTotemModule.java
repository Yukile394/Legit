/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.decoration.EndCrystalEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.ingame.InventoryScreen
 */
package com.legit.client.module.impl;

import com.legit.client.module.Category;
import com.legit.client.module.Module;
import com.legit.client.setting.BooleanSetting;
import com.legit.client.setting.NumberSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

public class AutoTotemModule
extends Module {
    public final NumberSetting healthThreshold = this.addSetting(new NumberSetting("Health Trigger", "Can bu de\u011ferin alt\u0131na d\u00fc\u015ft\u00fc\u011f\u00fcnde sol ele Totem al\u0131r", 12.0, 4.0, 20.0, 1.0, " HP"));
    public final NumberSetting swapDelay = this.addSetting(new NumberSetting("Swap Delay", "Totem ta\u015f\u0131ma gecikmesi", 40.0, 0.0, 250.0, 10.0, "ms"));
    public final BooleanSetting crystalSafety = this.addSetting(new BooleanSetting("Crystal Danger", "Yak\u0131nda (6m) End Kristali varsa can tam olsa bile Totem tutar", true));
    public final BooleanSetting fallSafety = this.addSetting(new BooleanSetting("Fall Danger", "Y\u00fcksekten d\u00fc\u015ferken otomatik Totem ku\u015fan\u0131r", true));
    private long lastSwapMs = 0L;

    public AutoTotemModule() {
        super("AutoTotem", "Kritik can, kristal tehlikesi veya y\u00fcksek d\u00fc\u015f\u00fc\u015fte sol ele otomatik Totem koyar", Category.PLAYER, -1);
    }

    @Override
    public String getSuffix() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.player == null) {
            return "";
        }
        int count = this.countTotems(mc);
        return count + " Totem";
    }

    @Override
    public void onTick(MinecraftClient mc) {
        boolean needsTotem;
        if (mc.player == null || mc.world == null || mc.interactionManager == null) {
            return;
        }
        if (mc.currentScreen != null && !(mc.currentScreen instanceof InventoryScreen)) {
            return;
        }
        if (mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
            return;
        }
        boolean bl = needsTotem = mc.player.getHealth() + mc.player.getAbsorptionAmount() <= this.healthThreshold.getFloatValue();
        if (!needsTotem && this.fallSafety.isEnabled() && mc.player.fallDistance > 5.0f) {
            needsTotem = true;
        }
        if (!needsTotem && this.crystalSafety.isEnabled()) {
            for (Entity e : mc.world.getEntities()) {
                if (!(e instanceof EndCrystalEntity) || !(mc.player.squaredDistanceTo(e) <= 36.0)) continue;
                needsTotem = true;
                break;
            }
        }
        if (!needsTotem) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now - this.lastSwapMs < (long)this.swapDelay.getIntValue()) {
            return;
        }
        int totemSlot = this.findTotemSlot(mc);
        if (totemSlot == -1) {
            return;
        }
        int syncId = mc.player.playerScreenHandler.syncId;
        int containerSlot = totemSlot < 9 ? totemSlot + 36 : totemSlot;
        mc.interactionManager.clickSlot(syncId, containerSlot, 0, SlotActionType.PICKUP, (PlayerEntity)mc.player);
        mc.interactionManager.clickSlot(syncId, 45, 0, SlotActionType.PICKUP, (PlayerEntity)mc.player);
        if (!mc.player.playerScreenHandler.getCursorStack().isEmpty()) {
            mc.interactionManager.clickSlot(syncId, containerSlot, 0, SlotActionType.PICKUP, (PlayerEntity)mc.player);
        }
        this.lastSwapMs = now;
    }

    private int findTotemSlot(MinecraftClient mc) {
        for (int i = 0; i < 36; ++i) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (s.isEmpty() || !s.isOf(Items.TOTEM_OF_UNDYING)) continue;
            return i;
        }
        return -1;
    }

    private int countTotems(MinecraftClient mc) {
        int total = mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING) ? mc.player.getOffHandStack().getCount() : 0;
        for (int i = 0; i < 36; ++i) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (!s.isOf(Items.TOTEM_OF_UNDYING)) continue;
            total += s.getCount();
        }
        return total;
    }
}

