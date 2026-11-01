/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.screen.Screen
 */
package com.legit.client.module;

import com.legit.client.gui.ClickGuiScreen;
import com.legit.client.module.Category;
import com.legit.client.module.Module;
import com.legit.client.module.impl.AimAssistModule;
import com.legit.client.module.impl.AntiCobwebModule;
import com.legit.client.module.impl.ArmorStatusModule;
import com.legit.client.module.impl.AutoArmorModule;
import com.legit.client.module.impl.AutoFireworkModule;
import com.legit.client.module.impl.AutoGappleModule;
import com.legit.client.module.impl.AutoRefillModule;
import com.legit.client.module.impl.AutoRespawnModule;
import com.legit.client.module.impl.AutoSprintModule;
import com.legit.client.module.impl.AutoTotemModule;
import com.legit.client.module.impl.CriticalsModule;
import com.legit.client.module.impl.EagleBridgeModule;
import com.legit.client.module.impl.ElytraBoostModule;
import com.legit.client.module.impl.EspRadarModule;
import com.legit.client.module.impl.FakePlayerModule;
import com.legit.client.module.impl.FastPlaceModule;
import com.legit.client.module.impl.FullbrightModule;
import com.legit.client.module.impl.HitboxModule;
import com.legit.client.module.impl.HudModule;
import com.legit.client.module.impl.JumpResetModule;
import com.legit.client.module.impl.KeystrokesCpsModule;
import com.legit.client.module.impl.MiddleClickPearlModule;
import com.legit.client.module.impl.NoFallModule;
import com.legit.client.module.impl.PearlTrackerModule;
import com.legit.client.module.impl.PlayerEspModule;
import com.legit.client.module.impl.ShieldBreakerModule;
import com.legit.client.module.impl.StorageEspModule;
import com.legit.client.module.impl.TargetHudModule;
import com.legit.client.module.impl.TriggerBotModule;
import com.legit.client.module.impl.WTapModule;
import com.legit.client.setting.KeybindSetting;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

public class ModuleManager {
    private final List<Module> modules = new ArrayList<Module>();
    private int clickGuiKey = 260;

    public ModuleManager() {
        this.register(new AimAssistModule());
        this.register(new TriggerBotModule());
        this.register(new HitboxModule());
        this.register(new CriticalsModule());
        this.register(new ShieldBreakerModule());
        this.register(new WTapModule());
        this.register(new JumpResetModule());
        this.register(new ElytraBoostModule());
        this.register(new AutoFireworkModule());
        this.register(new AutoSprintModule());
        this.register(new AntiCobwebModule());
        this.register(new EagleBridgeModule());
        this.register(new AutoTotemModule());
        this.register(new AutoGappleModule());
        this.register(new AutoArmorModule());
        this.register(new AutoRefillModule());
        this.register(new FastPlaceModule());
        this.register(new NoFallModule());
        this.register(new PlayerEspModule());
        this.register(new TargetHudModule());
        this.register(new PearlTrackerModule());
        this.register(new EspRadarModule());
        this.register(new StorageEspModule());
        this.register(new ArmorStatusModule());
        this.register(new FullbrightModule());
        this.register(new MiddleClickPearlModule());
        this.register(new KeystrokesCpsModule());
        this.register(new AutoRespawnModule());
        this.register(new FakePlayerModule());
        this.register(new HudModule());
    }

    private void register(Module module) {
        this.modules.add(module);
    }

    public void onTick(MinecraftClient mc) {
        for (Module module : this.modules) {
            if (!module.isEnabled()) continue;
            module.onTick(mc);
        }
    }

    public void onKeyInput(int key, int action) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.currentScreen != null) {
            return;
        }
        if (action == 1 && key == this.clickGuiKey) {
            mc.setScreen((Screen)new ClickGuiScreen());
            return;
        }
        for (Module module : this.modules) {
            int bind = (Integer)module.getKeybind().getValue();
            if (bind == -1 || bind == 0 || KeybindSetting.isMouseBind(bind) || bind != key) continue;
            if (module.getKeybind().isHoldMode()) {
                if (action == 1) {
                    module.setEnabled(true);
                    continue;
                }
                if (action != 0) continue;
                module.setEnabled(false);
                continue;
            }
            if (action != 1) continue;
            module.toggle();
        }
    }

    public void onMouseInput(int button, int action) {
        KeystrokesCpsModule ks;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null) {
            return;
        }
        if (action == 1 && mc.currentScreen == null && (ks = this.getModule(KeystrokesCpsModule.class)) != null) {
            ks.recordClick(button);
        }
        if (mc.currentScreen != null) {
            return;
        }
        int encoded = KeybindSetting.fromMouseButton(button);
        for (Module module : this.modules) {
            int bind = (Integer)module.getKeybind().getValue();
            if (bind != encoded) continue;
            if (module.getKeybind().isHoldMode()) {
                if (action == 1) {
                    module.setEnabled(true);
                    continue;
                }
                if (action != 0) continue;
                module.setEnabled(false);
                continue;
            }
            if (action != 1) continue;
            module.toggle();
        }
    }

    public List<Module> getModules() {
        return this.modules;
    }

    public List<Module> getModulesByCategory(Category category) {
        return this.modules.stream().filter(m -> m.getCategory() == category).collect(Collectors.toList());
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        for (Module m : this.modules) {
            if (!clazz.isInstance(m)) continue;
            return (T)m;
        }
        return null;
    }

    public Module getModuleByName(String name) {
        for (Module m : this.modules) {
            if (!m.getName().equalsIgnoreCase(name)) continue;
            return m;
        }
        return null;
    }

    public int getClickGuiKey() {
        return this.clickGuiKey;
    }

    public void setClickGuiKey(int clickGuiKey) {
        this.clickGuiKey = clickGuiKey;
    }
}

