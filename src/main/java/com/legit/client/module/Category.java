/*
 * Decompiled with CFR 0.152.
 */
package com.legit.client.module;

public enum Category {
    COMBAT("Combat", "\u2694", "Combat modules"),
    MOVEMENT("Movement", "\u00bb", "Movement & Elytra modules"),
    PLAYER("Player", "\u26a1", "Player & Inventory utilities"),
    RENDER("Render", "\u2726", "ESP, TargetHUD & Visuals"),
    MISC("Misc", "\u2756", "Keystrokes & Misc utilities"),
    CLIENT("Client", "\u2699", "Arraylist, Colors, GUI & Watermark");

    private final String displayName;
    private final String icon;
    private final String description;

    private Category(String displayName, String icon, String description) {
        this.displayName = displayName;
        this.icon = icon;
        this.description = description;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getIcon() {
        return this.icon;
    }

    public String getDescription() {
        return this.description;
    }
}

