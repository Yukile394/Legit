/*
 * Decompiled with CFR 0.152.
 */
package com.legit.client.setting;

import com.legit.client.setting.Setting;
import java.util.Arrays;
import java.util.List;
import java.util.function.BooleanSupplier;

public class ModeSetting
extends Setting<String> {
    private final List<String> modes;
    private int index;

    public ModeSetting(String name, String description, String defaultMode, String ... modes) {
        super(name, description, defaultMode);
        this.modes = Arrays.asList(modes);
        this.index = Math.max(0, this.modes.indexOf(defaultMode));
    }

    public ModeSetting visibleWhen(BooleanSupplier condition) {
        super.visibleWhen(condition);
        return this;
    }

    public List<String> getModes() {
        return this.modes;
    }

    public void cycleForward() {
        this.index = (this.index + 1) % this.modes.size();
        this.value = this.modes.get(this.index);
    }

    public void cycleBackward() {
        this.index = (this.index - 1 + this.modes.size()) % this.modes.size();
        this.value = this.modes.get(this.index);
    }

    @Override
    public void setValue(String mode) {
        int idx = this.modes.indexOf(mode);
        if (idx != -1) {
            this.index = idx;
            this.value = mode;
        }
    }

    public boolean isMode(String mode) {
        return this.value != null && ((String)this.value).equalsIgnoreCase(mode);
    }
}

