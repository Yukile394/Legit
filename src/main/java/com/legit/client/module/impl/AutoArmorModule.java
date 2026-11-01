/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EquipmentSlot
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.screen.slot.SlotActionType
 *  net.minecraft.item.ArmorItem
 *  net.minecraft.item.ElytraItem
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.enchantment.Enchantments
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.ingame.InventoryScreen
 */
package com.legit.client.module.impl;

import com.legit.client.module.Category;
import com.legit.client.module.Module;
import com.legit.client.setting.BooleanSetting;
import com.legit.client.setting.NumberSetting;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

public class AutoArmorModule
extends Module {
    public final NumberSetting equipDelay = this.addSetting(new NumberSetting("Equip Delay", "Z\u0131rh par\u00e7alar\u0131 aras\u0131 giyme gecikmesi", 90.0, 20.0, 400.0, 10.0, "ms"));
    public final BooleanSetting preferElytra = this.addSetting(new BooleanSetting("Prefer Elytra", "G\u00f6\u011f\u00fcsl\u00fck yerine Elytra takmay\u0131 \u00f6nceliklendirir (ElytraPvP)", true));
    public final BooleanSetting openInventoryOnly = this.addSetting(new BooleanSetting("Inventory Only", "Sadece envanter a\u00e7\u0131kken z\u0131rh giyer", false));
    private long lastEquipTime = 0L;

    public AutoArmorModule() {
        super("AutoArmor", "Envanterdeki en g\u00fc\u00e7l\u00fc z\u0131rhlar\u0131 (veya Elytra'y\u0131) otomatik giyer", Category.PLAYER, -1);
    }

    @Override
    public String getSuffix() {
        return this.preferElytra.isEnabled() ? "Elytra" : "Armor";
    }

    @Override
    public void onTick(MinecraftClient mc) {
        if (mc.player == null || mc.interactionManager == null) {
            return;
        }
        if (this.openInventoryOnly.isEnabled() && !(mc.currentScreen instanceof InventoryScreen)) {
            return;
        }
        if (mc.currentScreen != null && !(mc.currentScreen instanceof InventoryScreen)) {
            return;
        }
        long now = System.currentTimeMillis();
        if ((double)(now - this.lastEquipTime) < (Double)this.equipDelay.getValue()) {
            return;
        }
        EquipmentSlot[] slots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        for (int armorIdx = 0; armorIdx < 4; ++armorIdx) {
            EquipmentSlot targetSlot = slots[3 - armorIdx];
            ItemStack current = mc.player.getInventory().getArmorStack(armorIdx);
            double bestScore = this.getScore(current, targetSlot);
            int bestInvSlot = -1;
            for (int invSlot = 0; invSlot < 36; ++invSlot) {
                double score;
                ItemStack candidate = mc.player.getInventory().getStack(invSlot);
                if (candidate.isEmpty() || !((score = this.getScore(candidate, targetSlot)) > bestScore)) continue;
                bestScore = score;
                bestInvSlot = invSlot;
            }
            if (bestInvSlot == -1) continue;
            int containerSlot = bestInvSlot < 9 ? bestInvSlot + 36 : bestInvSlot;
            int armorContainerSlot = 8 - armorIdx;
            int syncId = mc.player.currentScreenHandler.syncId;
            mc.interactionManager.clickSlot(syncId, containerSlot, 0, SlotActionType.PICKUP, (PlayerEntity)mc.player);
            mc.interactionManager.clickSlot(syncId, armorContainerSlot, 0, SlotActionType.PICKUP, (PlayerEntity)mc.player);
            if (!current.isEmpty()) {
                mc.interactionManager.clickSlot(syncId, containerSlot, 0, SlotActionType.PICKUP, (PlayerEntity)mc.player);
            }
            this.lastEquipTime = now;
            return;
        }
    }

    private double getScore(ItemStack stack, EquipmentSlot slot) {
        ArmorItem armor;
        if (stack.isEmpty()) {
            return -1.0;
        }
        if (slot == EquipmentSlot.CHEST && stack.getItem() instanceof ElytraItem) {
            if (!ElytraItem.isUsable((ItemStack)stack)) {
                return 0.0;
            }
            return this.preferElytra.isEnabled() ? 999.0 : 4.0;
        }
        Item item = stack.getItem();
        if (!(item instanceof ArmorItem) || (armor = (ArmorItem)item).getType().getEquipmentSlot() != slot) {
            return -1.0;
        }
        double prot = (double)armor.getProtection() * 4.0 + (double)armor.getToughness() * 2.0;
        prot += (double)this.level(Enchantments.PROTECTION, stack) * 2.5;
        prot += (double)this.level(Enchantments.UNBREAKING, stack) * 0.5;
        return prot += (double)this.level(Enchantments.MENDING, stack) * 0.8;
    }

    private int level(RegistryKey<Enchantment> key, ItemStack stack) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.world == null) {
            return 0;
        }
        return mc.world.getRegistryManager().get(RegistryKeys.ENCHANTMENT).getEntry(key).map(entry -> EnchantmentHelper.getLevel((RegistryEntry<Enchantment>)entry, stack)).orElse(0);
    }
}

