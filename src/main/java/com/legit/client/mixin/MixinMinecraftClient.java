/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.mob.HostileEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.client.MinecraftClient
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.legit.client.mixin;

import com.legit.client.LegitClient;
import com.legit.client.module.impl.PlayerEspModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={MinecraftClient.class})
public class MixinMinecraftClient {
    @Inject(method={"tick"}, at={@At(value="HEAD")})
    private void onClientTick(CallbackInfo ci) {
        LegitClient client = LegitClient.getInstance();
        if (client != null && client.getModuleManager() != null) {
            client.getModuleManager().onTick((MinecraftClient)this);
        }
    }

    @Inject(method={"hasOutline"}, at={@At(value="HEAD")}, cancellable=true)
    private void onHasOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity p;
        LegitClient client = LegitClient.getInstance();
        if (client == null || client.getModuleManager() == null || !client.getLicenseGuard().isAuthorized()) {
            return;
        }
        PlayerEspModule esp = client.getModuleManager().getModule(PlayerEspModule.class);
        if (esp == null || !esp.isEnabled() || !esp.glowOutline.isEnabled()) {
            return;
        }
        MinecraftClient mc = (MinecraftClient)this;
        if (mc.player == null || entity == mc.player || !entity.isAlive()) {
            return;
        }
        double maxDist = (Double)esp.maxDistance.getValue();
        if (mc.player.squaredDistanceTo(entity) > maxDist * maxDist) {
            return;
        }
        if (entity instanceof PlayerEntity && !(p = (PlayerEntity)entity).isSpectator()) {
            cir.setReturnValue((Object)true);
        } else if (entity instanceof HostileEntity && esp.includeMobs.isEnabled()) {
            cir.setReturnValue((Object)true);
        }
    }
}

