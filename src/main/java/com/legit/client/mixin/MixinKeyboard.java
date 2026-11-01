/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Keyboard
 *  net.minecraft.client.MinecraftClient
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.legit.client.mixin;

import com.legit.client.LegitClient;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Keyboard.class})
public class MixinKeyboard {
    @Inject(method={"onKey"}, at={@At(value="HEAD")})
    private void onKeyEvent(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        LegitClient client;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc != null && window == mc.getWindow().getHandle() && (client = LegitClient.getInstance()) != null && client.getModuleManager() != null) {
            client.getModuleManager().onKeyInput(key, action);
        }
    }
}

