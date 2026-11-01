/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Hand
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.Items
 *  net.minecraft.client.MinecraftClient
 *  org.lwjgl.glfw.GLFW
 */
package com.legit.client.module.impl;

import com.legit.client.module.Category;
import com.legit.client.module.Module;
import net.minecraft.util.Hand;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class MiddleClickPearlModule
extends Module {
    private boolean wasMiddleDown = false;

    public MiddleClickPearlModule() {
        super("MidClickPearl", "Farenin orta tu\u015funa (Mouse 3) bas\u0131nca hotbardaki Ender \u0130ncisini an\u0131nda atar", Category.MISC, -1);
    }

    @Override
    public void onTick(MinecraftClient mc) {
        boolean isDown;
        if (mc.player == null || mc.interactionManager == null || mc.currentScreen != null) {
            return;
        }
        boolean bl = isDown = GLFW.glfwGetMouseButton((long)mc.getWindow().getHandle(), (int)2) == 1;
        if (isDown && !this.wasMiddleDown) {
            this.throwPearlSilently(mc);
        }
        this.wasMiddleDown = isDown;
    }

    private void throwPearlSilently(MinecraftClient mc) {
        int pearlSlot = -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack s = mc.player.getInventory().getStack(i);
            if (s.isEmpty() || !s.isOf(Items.ENDER_PEARL)) continue;
            pearlSlot = i;
            break;
        }
        if (pearlSlot == -1) {
            return;
        }
        int prev = mc.player.getInventory().selectedSlot;
        mc.player.getInventory().selectedSlot = pearlSlot;
        mc.interactionManager.interactItem((PlayerEntity)mc.player, Hand.MAIN_HAND);
        mc.player.getInventory().selectedSlot = prev;
    }
}

