/*
 * Decompiled with CFR 0.152.
 */
package com.legit.client.setting;

import com.legit.client.setting.Setting;
import java.util.function.BooleanSupplier;

public class NumberSetting
extends Setting<Double> {
    private final double min;
    private final double max;
    private final double step;
    private final String suffix;

    public NumberSetting(String name, String description, double defaultValue, double min, double max, double step, String suffix) {
        super(name, description, defaultValue);
        this.min = min;
        this.max = max;
        this.step = step;
        this.suffix = suffix == null ? "" : suffix;
    }

    public NumberSetting visibleWhen(BooleanSupplier condition) {
        super.visibleWhen(condition);
        return this;
    }

    @Override
    public void setValue(Double newValue) {
        double clamped = Math.max(this.min, Math.min(this.max, newValue));
        if (this.step > 0.0) {
            clamped = (double)Math.round(clamped / this.step) * this.step;
            clamped = (double)Math.round(clamped * 100.0) / 100.0;
        }
        this.value = Math.max(this.min, Math.min(this.max, clamped));
    }

    public float getFloatValue() {
        return ((Double)this.value).floatValue();
    }

    public int getIntValue() {
        return ((Double)this.value).intValue();
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    public double getStep() {
        return this.step;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public String getFormattedValue() {
        if (this.step >= 1.0) {
            return String.valueOf(((Double)this.value).intValue()) + this.suffix;
        }
        return String.format("%.1f%s", this.value, this.suffix);
    }
}

