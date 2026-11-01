/*
 * Decompiled with CFR 0.152.
 */
package com.legit.client.setting;

import java.util.function.BooleanSupplier;

public abstract class Setting<T> {
    private final String name;
    private final String description;
    protected T value;
    private final T defaultValue;
    private BooleanSupplier visibility = () -> true;

    public Setting(String name, String description, T defaultValue) {
        this.name = name;
        this.description = description;
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

    public Setting<T> visibleWhen(BooleanSupplier condition) {
        this.visibility = condition;
        return this;
    }

    public boolean isVisible() {
        return this.visibility == null || this.visibility.getAsBoolean();
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public void reset() {
        this.value = this.defaultValue;
    }
}

