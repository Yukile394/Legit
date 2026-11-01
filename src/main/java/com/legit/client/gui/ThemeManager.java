/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.DrawContext
 */
package com.legit.client.gui;

import com.legit.client.LegitClient;
import com.legit.client.module.impl.HudModule;
import net.minecraft.client.gui.DrawContext;

public class ThemeManager {
    private static final Palette LEGIT_CRIMSON = new Palette("Legit Crimson", -56264, -1697490, -301333241, -265812208, -180221161, -1, -1526596);
    private static final Palette GHOST_MONOCHROME = new Palette("Ghost Monochrome", -1906448, -7035976, -200668402, -183430376, -183035359, -460036, -9340020);
    private static final Palette BLOOD_MOON = new Palette("Blood Moon", -770210, -298619, -200406516, -183037676, -182446565, -3598, -6390910);
    private static final Palette CYBER_CYAN = new Palette("Cyber Cyan", -16337196, -12877066, -200602092, -183363806, -182968530, -984577, -8614234);
    private static final Palette EMERALD_GHOST = new Palette("Emerald Ghost", -15681151, -13315175, -200732659, -183494122, -183163874, -1245707, -8873077);
    private static final Palette MIDNIGHT_GOLD = new Palette("Midnight Gold", -680437, -278748, -200274422, -182971120, -182313450, -1045, -6516614);
    private static final Palette SAKURA_MIST = new Palette("Sakura Mist", -1292135, -5745161, -200209648, -182840807, -182183902, -134408, -6389098);

    public static Palette getActivePalette() {
        HudModule hud;
        if (LegitClient.getInstance() != null && LegitClient.getInstance().getModuleManager() != null && (hud = LegitClient.getInstance().getModuleManager().getModule(HudModule.class)) != null) {
            return switch ((String)hud.theme.getValue()) {
                case "Ghost Monochrome" -> GHOST_MONOCHROME;
                case "Blood Moon" -> BLOOD_MOON;
                case "Cyber Cyan" -> CYBER_CYAN;
                case "Emerald Ghost" -> EMERALD_GHOST;
                case "Midnight Gold" -> MIDNIGHT_GOLD;
                case "Sakura Mist" -> SAKURA_MIST;
                default -> LEGIT_CRIMSON;
            };
        }
        return LEGIT_CRIMSON;
    }

    public static int interpolateColor(int color1, int color2, float factor) {
        factor = Math.max(0.0f, Math.min(1.0f, factor));
        int a1 = color1 >> 24 & 0xFF;
        int r1 = color1 >> 16 & 0xFF;
        int g1 = color1 >> 8 & 0xFF;
        int b1 = color1 & 0xFF;
        int a2 = color2 >> 24 & 0xFF;
        int r2 = color2 >> 16 & 0xFF;
        int g2 = color2 >> 8 & 0xFF;
        int b2 = color2 & 0xFF;
        int a = (int)((float)a1 + (float)(a2 - a1) * factor);
        int r = (int)((float)r1 + (float)(r2 - r1) * factor);
        int g = (int)((float)g1 + (float)(g2 - g1) * factor);
        int b = (int)((float)b1 + (float)(b2 - b1) * factor);
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static int withAlpha(int color, int alpha) {
        alpha = Math.max(0, Math.min(255, alpha));
        return color & 0xFFFFFF | alpha << 24;
    }

    public static int getWaveAccent(int indexOffset) {
        Palette p = ThemeManager.getActivePalette();
        double time = (double)((System.currentTimeMillis() + (long)indexOffset * 160L) % 2800L) / 2800.0;
        float wave = (float)(0.5 + 0.5 * Math.sin(time * Math.PI * 2.0));
        return ThemeManager.interpolateColor(p.primary(), p.secondary(), wave);
    }

    public static void drawSoftShadow(DrawContext ctx, int x, int y, int width, int height) {
        ctx.fill(x - 2, y - 2, x + width + 2, y + height + 2, 0x22000000);
        ctx.fill(x - 1, y - 1, x + width + 1, y + height + 1, 0x44000000);
    }

    public static void drawRoundedCard(DrawContext ctx, int x, int y, int width, int height, int bgColor, int borderColor) {
        int bottomGrad = ThemeManager.interpolateColor(bgColor, ThemeManager.withAlpha(borderColor, 225), 0.32f);
        ctx.fillGradient(x, y, x + width, y + height, bgColor, bottomGrad);
        ctx.fill(x, y, x + width, y + 1, borderColor);
        ctx.fill(x, y + height - 1, x + width, y + height, borderColor);
        ctx.fill(x, y, x + 1, y + height, borderColor);
        ctx.fill(x + width - 1, y, x + width, y + height, borderColor);
    }

    public static void drawHorizontalGradientBar(DrawContext ctx, int x, int y, int width, int height, int leftColor, int rightColor) {
        int steps = Math.max(1, width / 2);
        for (int i = 0; i < steps; ++i) {
            float ratio = (float)i / (float)steps;
            int col = ThemeManager.interpolateColor(leftColor, rightColor, ratio);
            int sx = x + i * width / steps;
            int ex = x + (i + 1) * width / steps;
            ctx.fill(sx, y, Math.max(sx + 1, ex), y + height, col);
        }
    }

    public static void drawToggleSwitch(DrawContext ctx, int x, int y, boolean active, int accentColor) {
        int w = 22;
        int h = 11;
        int bg = active ? ThemeManager.withAlpha(accentColor, 180) : -15065818;
        int border = active ? accentColor : -13946816;
        ThemeManager.drawRoundedCard(ctx, x, y, w, h, bg, border);
        int knobX = active ? x + w - 9 : x + 2;
        int knobColor = active ? -1 : -9537396;
        ctx.fill(knobX, y + 2, knobX + 7, y + h - 2, knobColor);
    }

    public static void drawDashedCircleRing(DrawContext ctx, int centerX, int centerY, int radius, int color) {
        int points = Math.max(24, radius * 2);
        for (int i = 0; i < points; ++i) {
            double angle = Math.PI * 2 * (double)i / (double)points;
            int px = centerX + (int)Math.round(Math.cos(angle) * (double)radius);
            int py = centerY + (int)Math.round(Math.sin(angle) * (double)radius);
            ctx.fill(px, py, px + 1, py + 1, color);
        }
    }

    public record Palette(String name, int primary, int secondary, int bgDark, int bgCard, int bgCardHover, int textMain, int textMuted) {
    }
}

