/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.BlockItem
 *  net.minecraft.item.ExperienceBottleItem
 *  net.minecraft.item.Item
 *  net.minecraft.client.MinecraftClient
 */
package com.legit.client.module.impl;

import com.legit.client.mixin.AccessorMinecraftClient;
import com.legit.client.module.Category;
import com.legit.client.module.Module;
import com.legit.client.setting.BooleanSetting;
import com.legit.client.setting.NumberSetting;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ExperienceBottleItem;
import net.minecraft.item.Item;
import net.minecraft.client.MinecraftClient;

public class FastPlaceModule
extends Module {
    public final NumberSetting delayTicks = this.addSetting(new NumberSetting("Place Delay", "Sa\u011f t\u0131k blok koyma gecikmesi (Normal: 4 tick)", 1.0, 0.0, 3.0, 1.0, "t"));
    public final BooleanSetting blocksAndXpOnly = this.addSetting(new BooleanSetting("Blocks & XP Only", "Sadece blok koyarken veya XP \u015fi\u015fesi atarken h\u0131zland\u0131r\u0131r (Yemek/Yay etkilemez)", true));

    public FastPlaceModule() {
        super("FastPlace", "Blok koyma ve XP \u015fi\u015fesi atma aras\u0131ndaki sa\u011f t\u0131k bekleme s\u00fcresini azalt\u0131r", Category.PLAYER, -1);
    }

    @Override
    public String getSuffix() {
        return this.delayTicks.getIntValue() + " tick";
    }

    @Override
    public void onTick(MinecraftClient mc) {
        AccessorMinecraftClient accessor;
        if (mc.player == null || mc.currentScreen != null) {
            return;
        }
        if (this.blocksAndXpOnly.isEnabled()) {
            boolean valid;
            Item held = mc.player.getMainHandStack().getItem();
            Item off = mc.player.getOffHandStack().getItem();
            boolean bl = valid = held instanceof BlockItem || held instanceof ExperienceBottleItem || off instanceof BlockItem || off instanceof ExperienceBottleItem;
            if (!valid) {
                return;
            }
        }
        if ((accessor = (AccessorMinecraftClient)mc).getItemUseCooldown() > this.delayTicks.getIntValue()) {
            accessor.setItemUseCooldown(this.delayTicks.getIntValue());
        }
    }
}

