/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.HostileEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.client.MinecraftClient
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.legit.client.mixin;

import com.legit.client.LegitClient;
import com.legit.client.module.impl.HitboxModule;
import com.legit.client.module.impl.PlayerEspModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Entity.class})
public class MixinEntity {
    @Inject(method={"getTeamColorValue"}, at={@At(value="HEAD")}, cancellable=true)
    private void onGetTeamColorValue(CallbackInfoReturnable<Integer> cir) {
        LegitClient client = LegitClient.getInstance();
        if (client == null || client.getModuleManager() == null || !client.getLicenseGuard().isAuthorized()) {
            return;
        }
        PlayerEspModule esp = client.getModuleManager().getModule(PlayerEspModule.class);
        if (esp == null || !esp.isEnabled() || !esp.glowOutline.isEnabled()) {
            return;
        }
        Entity self = (Entity)(Object)this;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.player == null || self == mc.player) {
            return;
        }
        if (self instanceof PlayerEntity || self instanceof HostileEntity && esp.includeMobs.isEnabled()) {
            int argb = esp.getRedColor(mc.player.distanceTo(self));
            cir.setReturnValue(argb & 0xFFFFFF);
        }
    }

    @Inject(method={"getTargetingMargin"}, at={@At(value="HEAD")}, cancellable=true)
    private void onGetTargetingMargin(CallbackInfoReturnable<Float> cir) {
        LegitClient client = LegitClient.getInstance();
        if (client == null || client.getModuleManager() == null || !client.getLicenseGuard().isAuthorized()) {
            return;
        }
        HitboxModule hb = client.getModuleManager().getModule(HitboxModule.class);
        if (hb == null || !hb.isEnabled()) {
            return;
        }
        Entity self = (Entity)(Object)this;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.player == null || self == mc.player) {
            return;
        }
        if (self instanceof PlayerEntity || !hb.playersOnly.isEnabled() && self instanceof LivingEntity) {
            cir.setReturnValue(Float.valueOf(((Double)hb.expandSize.getValue()).floatValue()));
        }
    }
}

