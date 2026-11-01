/*
 * Decompiled with CFR 0.152.
 */
package com.legit.client.setting;

import com.legit.client.setting.Setting;
import java.util.function.BooleanSupplier;

public class BooleanSetting
extends Setting<Boolean> {
    public BooleanSetting(String name, String description, boolean defaultValue) {
        super(name, description, defaultValue);
    }

    public BooleanSetting visibleWhen(BooleanSupplier condition) {
        super.visibleWhen(condition);
        return this;
    }

    public void toggle() {
        this.value = (Boolean)this.value == false;
    }

    public boolean isEnabled() {
        return Boolean.TRUE.equals(this.value);
    }
}

