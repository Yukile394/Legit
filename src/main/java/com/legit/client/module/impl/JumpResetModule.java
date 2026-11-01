/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 */
package com.legit.client.module.impl;

import com.legit.client.module.Category;
import com.legit.client.module.Module;
import com.legit.client.setting.BooleanSetting;
import com.legit.client.setting.NumberSetting;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;

public class JumpResetModule
extends Module {
    public final NumberSetting chance = this.addSetting(new NumberSetting("Reset Chance", "Hasar al\u0131nca Jump-Reset uygulama olas\u0131l\u0131\u011f\u0131", 85.0, 20.0, 100.0, 5.0, "%"));
    public final NumberSetting hurtTick = this.addSetting(new NumberSetting("Trigger HurtTime", "Hangi hasar karesinde (HurtTime) z\u0131planaca\u011f\u0131", 9.0, 7.0, 10.0, 1.0, "t"));
    public final BooleanSetting onlyWhileSprinting = this.addSetting(new BooleanSetting("Only Sprinting", "Sadece ko\u015farken (Sprint) aktif olsun", true));

    public JumpResetModule() {
        super("Velocity", "Darbe ald\u0131\u011f\u0131n anda tick-perfect z\u0131playarak geri savrulmay\u0131 (Knockback) do\u011fal azalt\u0131r", Category.MOVEMENT, -1);
    }

    @Override
    public String getSuffix() {
        return this.chance.getIntValue() + "%";
    }

    @Override
    public void onTick(MinecraftClient mc) {
        double roll;
        if (mc.player == null || mc.currentScreen != null) {
            return;
        }
        if (!mc.player.isOnGround() || mc.player.isTouchingWater() || mc.player.isInLava()) {
            return;
        }
        if (this.onlyWhileSprinting.isEnabled() && !mc.player.isSprinting()) {
            return;
        }
        if (mc.player.hurtTime == this.hurtTick.getIntValue() && (roll = ThreadLocalRandom.current().nextDouble() * 100.0) <= (Double)this.chance.getValue()) {
            mc.player.jump();
        }
    }
}

