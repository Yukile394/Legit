/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.glfw.GLFW
 */
package com.legit.client.setting;

import com.legit.client.setting.Setting;
import org.lwjgl.glfw.GLFW;

public class KeybindSetting
extends Setting<Integer> {
    private boolean holdMode = false;

    public KeybindSetting(String name, int defaultKey) {
        super(name, "Mod\u00fcl k\u0131sayol tu\u015fu (Klavye veya Mouse 3/4/5)", defaultKey);
    }

    public boolean isHoldMode() {
        return this.holdMode;
    }

    public void setHoldMode(boolean holdMode) {
        this.holdMode = holdMode;
    }

    public static int fromMouseButton(int button) {
        return -100 - button;
    }

    public static boolean isMouseBind(int code) {
        return code <= -100;
    }

    public static int toMouseButton(int code) {
        return -100 - code;
    }

    public String getKeyName() {
        int code = (Integer)this.getValue();
        if (code == -1 || code == 0) {
            return "NONE";
        }
        if (KeybindSetting.isMouseBind(code)) {
            int btn = KeybindSetting.toMouseButton(code);
            return switch (btn) {
                case 2 -> "MOUSE3";
                case 3 -> "MOUSE4";
                case 4 -> "MOUSE5";
                default -> "MOUSE" + (btn + 1);
            };
        }
        String glfwName = GLFW.glfwGetKeyName((int)code, (int)0);
        if (glfwName != null && !glfwName.isBlank()) {
            return glfwName.toUpperCase();
        }
        return switch (code) {
            case 344 -> "RSHIFT";
            case 340 -> "LSHIFT";
            case 341 -> "LCTRL";
            case 345 -> "RCTRL";
            case 342 -> "LALT";
            case 346 -> "RALT";
            case 280 -> "CAPS";
            case 258 -> "TAB";
            case 32 -> "SPACE";
            case 260 -> "INS";
            case 261 -> "DEL";
            case 268 -> "HOME";
            case 269 -> "END";
            default -> "KEY_" + code;
        };
    }
}

