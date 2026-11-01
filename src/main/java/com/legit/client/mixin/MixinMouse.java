/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.Mouse
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package com.legit.client.mixin;

import com.legit.client.LegitClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={Mouse.class})
public class MixinMouse {
    @Inject(method={"onMouseButton"}, at={@At(value="HEAD")})
    private void onMouseButtonEvent(long window, int button, int action, int mods, CallbackInfo ci) {
        LegitClient client;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc != null && window == mc.getWindow().getHandle() && (client = LegitClient.getInstance()) != null && client.getModuleManager() != null) {
            client.getModuleManager().onMouseInput(button, action);
        }
    }
}

