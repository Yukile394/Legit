/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.LivingEntity
 *  net.minecraft.entity.mob.HostileEntity
 *  net.minecraft.entity.player.PlayerEntity
 *  net.minecraft.entity.projectile.thrown.EnderPearlEntity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.block.Block
 *  net.minecraft.block.ChestBlock
 *  net.minecraft.block.EnderChestBlock
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Box
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.block.ShulkerBoxBlock
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.font.TextRenderer
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.block.BarrelBlock
 *  net.minecraft.client.render.Camera
 *  net.minecraft.client.network.PlayerListEntry
 */
package com.legit.client.gui;

import com.legit.client.LegitClient;
import com.legit.client.gui.NotificationManager;
import com.legit.client.gui.ThemeManager;
import com.legit.client.module.Category;
import com.legit.client.module.Module;
import com.legit.client.module.impl.AimAssistModule;
import com.legit.client.module.impl.ArmorStatusModule;
import com.legit.client.module.impl.EspRadarModule;
import com.legit.client.module.impl.HudModule;
import com.legit.client.module.impl.KeystrokesCpsModule;
import com.legit.client.module.impl.PearlTrackerModule;
import com.legit.client.module.impl.PlayerEspModule;
import com.legit.client.module.impl.StorageEspModule;
import com.legit.client.module.impl.TargetHudModule;
import com.legit.client.module.impl.TriggerBotModule;
import com.legit.client.security.LicenseGuard;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.BarrelBlock;
import net.minecraft.client.render.Camera;
import net.minecraft.client.network.PlayerListEntry;

public class HudOverlayRenderer {
    public static void render(DrawContext ctx, float tickDelta) {
        ArmorStatusModule armorMod;
        KeystrokesCpsModule ksMod;
        EspRadarModule radarMod;
        PearlTrackerModule pearlMod;
        StorageEspModule storageEsp;
        AimAssistModule aimMod;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.player == null) {
            return;
        }
        LegitClient client = LegitClient.getInstance();
        if (client == null) {
            return;
        }
        LicenseGuard guard = client.getLicenseGuard();
        if (guard.isAuthorized() && (aimMod = client.getModuleManager().getModule(AimAssistModule.class)) != null && aimMod.isEnabled()) {
            aimMod.onRenderFrame(mc, tickDelta);
        }
        if (mc.getDebugHud().shouldShowDebugHud()) {
            return;
        }
        HudModule hudMod = client.getModuleManager().getModule(HudModule.class);
        ThemeManager.Palette palette = ThemeManager.getActivePalette();
        TextRenderer tr = mc.textRenderer;
        int screenW = mc.getWindow().getScaledWidth();
        int screenH = mc.getWindow().getScaledHeight();
        int leftNextY = 8;
        if (hudMod == null || hudMod.showWatermark.isEnabled()) {
            leftNextY = HudOverlayRenderer.renderWatermark(ctx, tr, mc, guard, hudMod, palette);
        }
        if (!guard.isAuthorized()) {
            String warn = "Lisans Aktif De\u011fil \u2022 [INSERT] ile Do\u011frula";
            int bw = tr.getWidth(warn) + 16;
            ThemeManager.drawRoundedCard(ctx, screenW - bw - 8, 8, bw, 20, -300282606, -54700);
            ctx.drawTextWithShadow(tr, warn, screenW - bw, 14, -38005);
            return;
        }
        PlayerEspModule playerEsp = client.getModuleManager().getModule(PlayerEspModule.class);
        if (playerEsp != null && playerEsp.isEnabled()) {
            HudOverlayRenderer.renderPlayerEsp(ctx, tr, mc, playerEsp, tickDelta, screenW, screenH);
        }
        if ((storageEsp = client.getModuleManager().getModule(StorageEspModule.class)) != null && storageEsp.isEnabled()) {
            HudOverlayRenderer.renderStorageEsp(ctx, mc, storageEsp, tickDelta, screenW, screenH);
        }
        if ((pearlMod = client.getModuleManager().getModule(PearlTrackerModule.class)) != null && pearlMod.isEnabled()) {
            HudOverlayRenderer.renderPearlEsp(ctx, tr, mc, pearlMod, tickDelta, screenW, screenH);
        }
        if ((radarMod = client.getModuleManager().getModule(EspRadarModule.class)) != null && radarMod.isEnabled()) {
            HudOverlayRenderer.renderRadarAndNearby(ctx, tr, mc, radarMod, palette, 8, leftNextY + 4);
        }
        if ((ksMod = client.getModuleManager().getModule(KeystrokesCpsModule.class)) != null && ksMod.isEnabled()) {
            HudOverlayRenderer.renderKeystrokes(ctx, tr, mc, ksMod, palette, 8, screenH - 78);
        }
        if ((armorMod = client.getModuleManager().getModule(ArmorStatusModule.class)) != null && armorMod.isEnabled()) {
            HudOverlayRenderer.renderArmorHud(ctx, tr, mc, armorMod, palette, screenW / 2 + 96, screenH - 56);
        }
        if (hudMod == null || hudMod.showArrayList.isEnabled()) {
            HudOverlayRenderer.renderArrayList(ctx, tr, client, hudMod, palette, screenW);
        }
        HudOverlayRenderer.renderCrosshairAndFov(ctx, client, hudMod, palette, screenW, screenH);
        TargetHudModule targetHud = client.getModuleManager().getModule(TargetHudModule.class);
        if (targetHud != null && targetHud.isEnabled()) {
            HudOverlayRenderer.renderTargetHud(ctx, tr, mc, targetHud, palette, screenW, screenH);
        }
        if (hudMod == null || hudMod.showAnnouncements.isEnabled()) {
            HudOverlayRenderer.renderAnnouncementBanner(ctx, tr, guard, palette, screenW);
        }
        if (hudMod == null || hudMod.showNotifications.isEnabled()) {
            HudOverlayRenderer.renderToasts(ctx, tr, client.getNotificationManager(), palette, screenW, screenH);
        }
    }

    private static int renderWatermark(DrawContext ctx, TextRenderer tr, MinecraftClient mc, LicenseGuard guard, HudModule hudMod, ThemeManager.Palette palette) {
        int fps = mc.getCurrentFps();
        int ping = HudOverlayRenderer.getPlayerPing(mc);
        String user = guard.isAuthorized() ? guard.getLicensedUser() : "Lisanss\u0131z";
        String brand = "Legit";
        String info = String.format(" \u2022 %d FPS \u2022 %dms \u2022 %s", fps, ping, user);
        int brandW = tr.getWidth(brand);
        int totalW = brandW + tr.getWidth(info) + 18;
        int cardH = 18;
        int borderCol = -56264;
        ctx.fillGradient(8, 8, 8 + totalW, 8 + cardH, -334887673, -262665704);
        ctx.fill(8, 8, 8 + totalW, 9, borderCol);
        ctx.fill(8, 8 + cardH - 1, 8 + totalW, 8 + cardH, borderCol);
        ctx.fill(8, 8, 9, 8 + cardH, borderCol);
        ctx.fill(8 + totalW - 1, 8, 8 + totalW, 8 + cardH, borderCol);
        ctx.fill(8, 8, 10, 8 + cardH, -1697490);
        ctx.drawTextWithShadow(tr, brand, 14, 13, -50354);
        ctx.drawTextWithShadow(tr, info, 14 + brandW, 13, -1);
        int bottomY = 8 + cardH;
        if (hudMod != null && hudMod.showCoordinates.isEnabled() && mc.player != null) {
            double dx = mc.player.getX() - mc.player.prevX;
            double dz = mc.player.getZ() - mc.player.prevZ;
            double bps = Math.sqrt(dx * dx + dz * dz) * 20.0;
            String coords = String.format("XYZ: %d, %d, %d  |  %.1f BPS", mc.player.getBlockX(), mc.player.getBlockY(), mc.player.getBlockZ(), bps);
            int cw = tr.getWidth(coords) + 14;
            int cy = bottomY + 2;
            ctx.fillGradient(8, cy, 8 + cw, cy + 15, -334887673, -262665704);
            ctx.fill(8, cy, 8 + cw, cy + 1, borderCol);
            ctx.fill(8, cy + 14, 8 + cw, cy + 15, borderCol);
            ctx.fill(8, cy, 9, cy + 15, borderCol);
            ctx.fill(8 + cw - 1, cy, 8 + cw, cy + 15, borderCol);
            ctx.drawTextWithShadow(tr, coords, 14, cy + 4, -8992);
            bottomY += 18;
        }
        return bottomY;
    }

    private static void renderRadarAndNearby(DrawContext ctx, TextRenderer tr, MinecraftClient mc, EspRadarModule radarMod, ThemeManager.Palette palette, int x, int y) {
        if (mc.world == null || mc.player == null) {
            return;
        }
        int nextY = y;
        double maxRange = (Double)radarMod.radarRange.getValue();
        if (radarMod.showMiniRadar.isEnabled()) {
            int size = 76;
            ThemeManager.drawRoundedCard(ctx, x, nextY, size, size, palette.bgDark(), ThemeManager.withAlpha(palette.primary(), 130));
            ThemeManager.drawHorizontalGradientBar(ctx, x + 1, nextY, size - 2, 2, palette.primary(), palette.secondary());
            int cx = x + size / 2;
            int cy = nextY + size / 2;
            ctx.fill(x + 4, cy, x + size - 4, cy + 1, 1144407395);
            ctx.fill(cx, nextY + 4, cx + 1, nextY + size - 4, 1144407395);
            float yawRad = (float)Math.toRadians(mc.player.getYaw());
            float cos = MathHelper.cos((float)yawRad);
            float sin = MathHelper.sin((float)yawRad);
            for (Entity e : mc.world.getEntities()) {
                double dz;
                double dx;
                double dist;
                if (e == mc.player || !e.isAlive()) continue;
                boolean isPlayer = e instanceof PlayerEntity;
                boolean isHostile = e instanceof HostileEntity;
                if (!isPlayer && (!isHostile || !radarMod.showMobsOnRadar.isEnabled()) || (dist = Math.sqrt((dx = e.getX() - mc.player.getX()) * dx + (dz = e.getZ() - mc.player.getZ()) * dz)) > maxRange) continue;
                double rx = -(dx * (double)cos + dz * (double)sin);
                double rz = -(dz * (double)cos - dx * (double)sin);
                double scale = ((double)size / 2.0 - 6.0) / maxRange;
                int px = cx + (int)MathHelper.clamp((double)(rx * scale), (double)(-(size / 2 - 5)), (double)(size / 2 - 5));
                int py = cy + (int)MathHelper.clamp((double)(rz * scale), (double)(-(size / 2 - 5)), (double)(size / 2 - 5));
                int dotColor = isPlayer ? -54700 : -21760;
                ctx.fill(px - 1, py - 1, px + 2, py + 2, dotColor);
            }
            ctx.fill(cx - 1, cy - 1, cx + 2, cy + 2, palette.primary());
            nextY += size + 4;
        }
        if (radarMod.showNearbyList.isEnabled()) {
            ArrayList<PlayerEntity> nearby = new ArrayList<PlayerEntity>();
            for (PlayerEntity p2 : mc.world.getPlayers()) {
                if (p2 == mc.player || p2.isSpectator() || !p2.isAlive() || !((double)mc.player.distanceTo((Entity)p2) <= maxRange)) continue;
                nearby.add(p2);
            }
            nearby.sort(Comparator.comparingDouble(p -> mc.player.distanceTo((Entity)p)));
            if (!nearby.isEmpty()) {
                int count = Math.min(4, nearby.size());
                int boxH = 14 + count * 12;
                int boxW = 122;
                ThemeManager.drawRoundedCard(ctx, x, nextY, boxW, boxH, palette.bgDark(), -14273974);
                ctx.drawTextWithShadow(tr, "Yak\u0131n Oyuncular (" + nearby.size() + ")", x + 6, nextY + 3, palette.primary());
                int py = nextY + 14;
                for (int i = 0; i < count; ++i) {
                    PlayerEntity p3 = (PlayerEntity)nearby.get(i);
                    Object pName = p3.getName().getString();
                    if (tr.getWidth((String)pName) > 64) {
                        pName = tr.trimToWidth((String)pName, 60) + "..";
                    }
                    String distStr = String.format("%dm \u2022 %d\u2665", (int)mc.player.distanceTo((Entity)p3), (int)p3.getHealth());
                    ctx.drawTextWithShadow(tr, (String)pName, x + 6, py, palette.textMain());
                    ctx.drawTextWithShadow(tr, distStr, x + boxW - tr.getWidth(distStr) - 5, py, -38005);
                    py += 12;
                }
            }
        }
    }

    private static void renderKeystrokes(DrawContext ctx, TextRenderer tr, MinecraftClient mc, KeystrokesCpsModule ksMod, ThemeManager.Palette palette, int x, int y) {
        int keySize = 20;
        int gap = 2;
        HudOverlayRenderer.drawKeyBox(ctx, tr, x + keySize + gap, y, keySize, keySize, "W", mc.options.forwardKey.isPressed(), palette);
        HudOverlayRenderer.drawKeyBox(ctx, tr, x, y + keySize + gap, keySize, keySize, "A", mc.options.leftKey.isPressed(), palette);
        HudOverlayRenderer.drawKeyBox(ctx, tr, x + keySize + gap, y + keySize + gap, keySize, keySize, "S", mc.options.backKey.isPressed(), palette);
        HudOverlayRenderer.drawKeyBox(ctx, tr, x + (keySize + gap) * 2, y + keySize + gap, keySize, keySize, "D", mc.options.rightKey.isPressed(), palette);
        if (ksMod.showCpsRow.isEnabled()) {
            int rowY = y + (keySize + gap) * 2;
            int totalW = keySize * 3 + gap * 2;
            int halfW = (totalW - gap) / 2;
            String lmb = ksMod.getLeftCps() + " CPS";
            String rmb = ksMod.getRightCps() + " CPS";
            HudOverlayRenderer.drawKeyBox(ctx, tr, x, rowY, halfW, 18, lmb, mc.options.attackKey.isPressed(), palette);
            HudOverlayRenderer.drawKeyBox(ctx, tr, x + halfW + gap, rowY, totalW - halfW - gap, 18, rmb, mc.options.useKey.isPressed(), palette);
        }
    }

    private static void drawKeyBox(DrawContext ctx, TextRenderer tr, int x, int y, int w, int h, String label, boolean pressed, ThemeManager.Palette palette) {
        int bg = pressed ? ThemeManager.withAlpha(palette.primary(), 95) : palette.bgDark();
        int border = pressed ? palette.primary() : -14340025;
        ThemeManager.drawRoundedCard(ctx, x, y, w, h, bg, border);
        ctx.drawTextWithShadow(tr, label, x + (w - tr.getWidth(label)) / 2, y + (h - 8) / 2, pressed ? palette.textMain() : palette.textMuted());
    }

    private static void renderArmorHud(DrawContext ctx, TextRenderer tr, MinecraftClient mc, ArmorStatusModule armorMod, ThemeManager.Palette palette, int x, int y) {
        if (mc.player == null) {
            return;
        }
        int curX = x;
        for (int i = 3; i >= 0; --i) {
            ItemStack stack = mc.player.getInventory().getArmorStack(i);
            if (stack.isEmpty()) continue;
            ctx.drawItem(stack, curX, y);
            ctx.drawItemInSlot(tr, stack, curX, y);
            if (armorMod.showPercent.isEnabled() && stack.isDamageable()) {
                int max = stack.getMaxDamage();
                int rem = max - stack.getDamage();
                int pct = Math.max(0, Math.min(100, rem * 100 / Math.max(1, max)));
                int col = pct > 50 ? -16718218 : (pct > 20 ? -21760 : -54700);
                String pStr = pct + "%";
                ctx.drawTextWithShadow(tr, pStr, curX + 8 - tr.getWidth(pStr) / 2, y - 9, col);
            }
            curX += 20;
        }
    }

    private static void renderArrayList(DrawContext ctx, TextRenderer tr, LegitClient client, HudModule hudMod, ThemeManager.Palette palette, int screenW) {
        String sfx;
        String name;
        Module m2;
        int i;
        for (Module m3 : client.getModuleManager().getModules()) {
            float target = m3.isEnabled() && m3.getCategory() != Category.CLIENT ? 1.0f : 0.0f;
            float current = m3.getAnimProgress();
            m3.setAnimProgress(current + (target - current) * 0.25f);
        }
        boolean showSuffix = hudMod == null || hudMod.arrayListSuffix.isEnabled();
        String style = hudMod != null ? (String)hudMod.arrayListStyle.getValue() : "Legit Boxed";
        List visible = client.getModuleManager().getModules().stream().filter(m -> m.getCategory() != Category.CLIENT && m.getAnimProgress() > 0.03f).sorted(Comparator.comparingInt(m -> {
            String sfxText = showSuffix ? m.getSuffix() : "";
            String full = sfxText.isEmpty() ? m.getName() : m.getName() + " " + sfxText;
            return -tr.getWidth(full);
        })).collect(Collectors.toList());
        if (visible.isEmpty()) {
            return;
        }
        int borderCol = -56264;
        int rightBarCol = -1697490;
        int topGrad = -334887673;
        int botGrad = -262665704;
        int rightMargin = 8;
        int yOffset = 8;
        int totalCount = visible.size();
        int[] rowX = new int[totalCount];
        int[] rowY = new int[totalCount];
        int[] rowW = new int[totalCount];
        int rowH = 15;
        boolean stepped = style.contains("Stepped");
        boolean boxed = style.contains("Boxed");
        int gap = stepped ? 0 : 2;
        for (i = 0; i < totalCount; ++i) {
            int slideX;
            m2 = (Module)visible.get(i);
            name = m2.getName();
            sfx = showSuffix ? m2.getSuffix() : "";
            String fullText = sfx.isEmpty() ? name : name + " " + sfx;
            int textW = tr.getWidth(fullText);
            int boxW = textW + 22;
            rowX[i] = slideX = (int)((float)(screenW - rightMargin) - (float)boxW * m2.getAnimProgress());
            rowY[i] = yOffset;
            rowW[i] = boxW;
            yOffset += Math.max(1, (int)((float)(rowH + gap) * m2.getAnimProgress()));
        }
        for (i = 0; i < totalCount; ++i) {
            m2 = (Module)visible.get(i);
            name = m2.getName();
            sfx = showSuffix ? m2.getSuffix() : "";
            int x = rowX[i];
            int y = rowY[i];
            int w = rowW[i];
            float ratioTop = (float)i / (float)Math.max(1, totalCount);
            float ratioBot = (float)(i + 1) / (float)Math.max(1, totalCount);
            int rowTopBg = ThemeManager.interpolateColor(topGrad, botGrad, ratioTop);
            int rowBotBg = ThemeManager.interpolateColor(topGrad, botGrad, ratioBot);
            ctx.fillGradient(x, y, x + w, y + rowH, rowTopBg, rowBotBg);
            double waveTime = (double)((System.currentTimeMillis() + (long)i * 140L) % 2400L) / 2400.0;
            float wave = (float)(0.5 + 0.5 * Math.sin(waveTime * Math.PI * 2.0));
            int nameColor = ThemeManager.interpolateColor(-56264, -34168, wave);
            if (boxed) {
                ctx.fill(x, y, x + w, y + 1, borderCol);
                ctx.fill(x, y + rowH - 1, x + w, y + rowH, borderCol);
                ctx.fill(x, y, x + 1, y + rowH, borderCol);
                ctx.fill(x + w - 2, y, x + w, y + rowH, rightBarCol);
            } else if (stepped) {
                if (i == 0) {
                    ctx.fill(x, y, x + w, y + 1, borderCol);
                }
                ctx.fill(x, y, x + 1, y + rowH, borderCol);
                ctx.fill(x + w - 2, y, x + w, y + rowH, rightBarCol);
                if (i > 0) {
                    ctx.fill(x + 1, y, x + w - 2, y + 1, 1157571640);
                }
                if (i < totalCount - 1) {
                    int nextX = rowX[i + 1];
                    if (nextX > x) {
                        ctx.fill(x, y + rowH - 1, nextX + 1, y + rowH, borderCol);
                    }
                } else {
                    ctx.fill(x, y + rowH - 1, x + w, y + rowH, borderCol);
                }
            } else {
                ctx.fill(x + w - 2, y, x + w, y + rowH, rightBarCol);
            }
            int textY = y + (rowH - 8) / 2;
            ctx.drawTextWithShadow(tr, name, x + 5, textY, nameColor);
            if (!sfx.isEmpty()) {
                ctx.drawTextWithShadow(tr, " " + sfx, x + 5 + tr.getWidth(name), textY, -1);
            }
            ctx.drawTextWithShadow(tr, "-", x + w - tr.getWidth("-") - 5, textY, -19266);
        }
    }

    private static void renderCrosshairAndFov(DrawContext ctx, LegitClient client, HudModule hudMod, ThemeManager.Palette palette, int screenW, int screenH) {
        TriggerBotModule tb;
        int cx = screenW / 2;
        int cy = screenH / 2;
        AimAssistModule aim = client.getModuleManager().getModule(AimAssistModule.class);
        if (aim != null && aim.isEnabled() && aim.drawFovCircle.isEnabled()) {
            int radius = Math.max(18, Math.min(140, (int)((Double)aim.fov.getValue() * 0.85)));
            int ringColor = aim.getActiveTarget() != null ? ThemeManager.withAlpha(palette.primary(), 150) : ThemeManager.withAlpha(palette.textMuted(), 70);
            ThemeManager.drawDashedCircleRing(ctx, cx, cy, radius, ringColor);
        }
        if ((hudMod == null || hudMod.showCrosshairRing.isEnabled()) && (tb = client.getModuleManager().getModule(TriggerBotModule.class)) != null && tb.isEnabled() && tb.getCurrentLockedTarget() != null) {
            float cd = MinecraftClient.getInstance().player.getAttackCooldownProgress(0.5f);
            int col = cd >= 0.9f ? -16718218 : palette.primary();
            int r = 9;
            ctx.fill(cx - r, cy - r, cx - r + 4, cy - r + 1, col);
            ctx.fill(cx - r, cy - r, cx - r + 1, cy - r + 4, col);
            ctx.fill(cx + r - 3, cy - r, cx + r + 1, cy - r + 1, col);
            ctx.fill(cx + r, cy - r, cx + r + 1, cy - r + 4, col);
            ctx.fill(cx - r, cy + r, cx - r + 4, cy + r + 1, col);
            ctx.fill(cx - r, cy + r - 3, cx - r + 1, cy + r + 1, col);
            ctx.fill(cx + r - 3, cy + r, cx + r + 1, cy + r + 1, col);
            ctx.fill(cx + r, cy + r - 3, cx + r + 1, cy + r + 1, col);
        }
    }

    private static void renderTargetHud(DrawContext ctx, TextRenderer tr, MinecraftClient mc, TargetHudModule mod, ThemeManager.Palette palette, int screenW, int screenH) {
        LivingEntity target = mod.getTrackedTarget();
        if (target == null) {
            return;
        }
        float maxHp = Math.max(1.0f, target.getMaxHealth());
        float hp = Math.max(0.0f, Math.min(maxHp, target.getHealth() + target.getAbsorptionAmount()));
        float targetRatio = Math.min(1.0f, hp / maxHp);
        float animRatio = mod.getAnimatedHealthRatio() + (targetRatio - mod.getAnimatedHealthRatio()) * 0.18f;
        mod.setAnimatedHealthRatio(animRatio);
        int cardW = 172;
        int cardH = 52;
        int x = screenW / 2 + 20;
        int y = screenH / 2 + 16;
        boolean hurt = target.hurtTime > 0;
        int borderCol = hurt ? -54700 : palette.primary();
        ThemeManager.drawRoundedCard(ctx, x, y, cardW, cardH, palette.bgDark(), borderCol);
        ThemeManager.drawHorizontalGradientBar(ctx, x + 1, y, cardW - 2, 2, borderCol, palette.secondary());
        int avatarBox = 32;
        int ax = x + 8;
        int ay = y + 11;
        int avatarFill = hurt ? 1727998548 : -15130061;
        ThemeManager.drawRoundedCard(ctx, ax, ay, avatarBox, avatarBox, avatarFill, borderCol);
        ItemStack held = target.getMainHandStack();
        if (!held.isEmpty()) {
            ctx.drawItem(held, ax + 8, ay + 8);
        } else {
            String initial = target.getName().getString().isEmpty() ? "?" : target.getName().getString().substring(0, 1).toUpperCase();
            ctx.drawTextWithShadow(tr, initial, ax + 13, ay + 12, hurt ? -38005 : palette.primary());
        }
        Object targetName = target.getName().getString();
        if (tr.getWidth((String)targetName) > 82) {
            targetName = tr.trimToWidth((String)targetName, 78) + "..";
        }
        ctx.drawTextWithShadow(tr, (String)targetName, ax + avatarBox + 8, ay + 1, palette.textMain());
        double dist = mc.player.distanceTo((Entity)target);
        double tdx = target.getX() - target.prevX;
        double tdy = target.getY() - target.prevY;
        double tdz = target.getZ() - target.prevZ;
        double targetBps = Math.sqrt(tdx * tdx + tdy * tdy + tdz * tdz) * 20.0;
        String flyTag = target.isFallFlying() ? String.format(" \u2022 \u2708%.0f", targetBps) : "";
        String hpText = String.format("%.1f HP \u2022 %.1fm%s", Float.valueOf(hp), dist, flyTag);
        ctx.drawTextWithShadow(tr, hpText, ax + avatarBox + 8, ay + 13, target.isFallFlying() ? palette.primary() : palette.textMuted());
        if (mod.showWinStatus.isEnabled()) {
            float myHp = mc.player.getHealth() + mc.player.getAbsorptionAmount();
            boolean winning = myHp >= hp;
            String badge = winning ? "WIN" : "RISK";
            int badgeCol = winning ? -16718218 : -21760;
            ctx.drawTextWithShadow(tr, badge, x + cardW - tr.getWidth(badge) - 7, ay + 1, badgeCol);
        }
        int barX = ax + avatarBox + 8;
        int barY = ay + 25;
        int barW = cardW - avatarBox - 24;
        int barH = 6;
        ctx.fill(barX, barY, barX + barW, barY + barH, -15065044);
        int fillW = Math.max(2, (int)((float)barW * animRatio));
        int hpColor = animRatio > 0.5f ? palette.primary() : (animRatio > 0.25f ? -21760 : -54700);
        ThemeManager.drawHorizontalGradientBar(ctx, barX, barY, fillW, barH, hpColor, palette.secondary());
    }

    private static void renderAnnouncementBanner(DrawContext ctx, TextRenderer tr, LicenseGuard guard, ThemeManager.Palette palette, int screenW) {
        long ageMs = System.currentTimeMillis() - guard.getLatestAnnouncementTimestamp() * 1000L;
        String msg = guard.getLatestAnnouncement();
        if (msg == null || msg.isEmpty() || ageMs > 18000L || ageMs < -5000L) {
            return;
        }
        String title = "CANLI DUYURU (" + guard.getLatestAnnouncementAuthor() + "): ";
        int w = Math.max(180, tr.getWidth(title + msg) + 24);
        int x = (screenW - w) / 2;
        int y = 10;
        ThemeManager.drawRoundedCard(ctx, x, y, w, 22, palette.bgDark(), palette.primary());
        ThemeManager.drawHorizontalGradientBar(ctx, x + 1, y, w - 2, 2, palette.primary(), palette.secondary());
        ctx.drawTextWithShadow(tr, title, x + 10, y + 7, palette.primary());
        ctx.drawTextWithShadow(tr, msg, x + 10 + tr.getWidth(title), y + 7, palette.textMain());
    }

    private static void renderToasts(DrawContext ctx, TextRenderer tr, NotificationManager nm, ThemeManager.Palette palette, int screenW, int screenH) {
        List<NotificationManager.Toast> toasts = nm.getActiveToasts();
        if (toasts.isEmpty()) {
            return;
        }
        long now = System.currentTimeMillis();
        int y = screenH - 36;
        for (int i = toasts.size() - 1; i >= 0; --i) {
            NotificationManager.Toast t = toasts.get(i);
            int w = Math.max(135, Math.max(tr.getWidth(t.title), tr.getWidth(t.message)) + 20);
            int h = 28;
            int x = screenW - w - 10;
            int accent = t.positive ? palette.primary() : -49023;
            ThemeManager.drawRoundedCard(ctx, x, y, w, h, palette.bgDark(), accent);
            ctx.fill(x, y + 1, x + 3, y + h - 1, accent);
            ctx.drawTextWithShadow(tr, t.title, x + 9, y + 5, accent);
            ctx.drawTextWithShadow(tr, t.message, x + 9, y + 15, palette.textMuted());
            float progress = 1.0f - t.getLifeProgress(now);
            ctx.fill(x + 3, y + h - 2, x + 3 + (int)((float)(w - 4) * progress), y + h - 1, accent);
            y -= h + 5;
        }
    }

    private static void renderPlayerEsp(DrawContext ctx, TextRenderer tr, MinecraftClient mc, PlayerEspModule esp, float tickDelta, int screenW, int screenH) {
        if (mc.world == null || mc.player == null || mc.gameRenderer == null) {
            return;
        }
        double maxDist = (Double)esp.maxDistance.getValue();
        double maxDistSq = maxDist * maxDist;
        String boxMode = (String)esp.boxMode.getValue();
        for (Entity entity : mc.world.getEntities()) {
            double distSq;
            boolean isHostile;
            PlayerEntity p;
            if (entity == mc.player || !entity.isAlive() || !(entity instanceof LivingEntity)) continue;
            LivingEntity living = (LivingEntity)entity;
            boolean isPlayer = entity instanceof PlayerEntity && !(p = (PlayerEntity)entity).isSpectator();
            boolean bl = isHostile = entity instanceof HostileEntity && esp.includeMobs.isEnabled();
            if (!isPlayer && !isHostile || (distSq = mc.player.squaredDistanceTo(entity)) > maxDistSq) continue;
            double dist = Math.sqrt(distSq);
            int redColor = esp.getRedColor(dist);
            int fillColor = 788471346;
            Vec3d lerped = entity.getLerpedPos(tickDelta);
            Vec3d offset = lerped.subtract(entity.getPos());
            Box box = entity.getBoundingBox().offset(offset).expand(0.05);
            int[] bounds = HudOverlayRenderer.projectBoxToScreen(mc, box, screenW, screenH);
            if (bounds == null) continue;
            int minX = bounds[0];
            int minY = bounds[1];
            int maxX = bounds[2];
            int maxY = bounds[3];
            int bw = Math.max(4, maxX - minX);
            int bh = Math.max(6, maxY - minY);
            if (!"Glow Only".equals(boxMode)) {
                if (esp.fillBox.isEnabled() || "2D Fill Box".equals(boxMode)) {
                    ctx.fillGradient(minX, minY, maxX, maxY, 571081991, fillColor);
                }
                if ("Corner Box".equals(boxMode)) {
                    int cW = Math.max(3, bw / 3);
                    int cH = Math.max(3, bh / 4);
                    HudOverlayRenderer.drawCornerBox(ctx, minX - 1, minY - 1, bw + 2, bh + 2, cW + 1, cH + 1, -872415232);
                    HudOverlayRenderer.drawCornerBox(ctx, minX, minY, bw, bh, cW, cH, redColor);
                } else {
                    ctx.fill(minX - 1, minY - 1, maxX + 1, minY, -872415232);
                    ctx.fill(minX - 1, maxY, maxX + 1, maxY + 1, -872415232);
                    ctx.fill(minX - 1, minY - 1, minX, maxY + 1, -872415232);
                    ctx.fill(maxX, minY - 1, maxX + 1, maxY + 1, -872415232);
                    ctx.fill(minX, minY, maxX, minY + 1, redColor);
                    ctx.fill(minX, maxY - 1, maxX, maxY, redColor);
                    ctx.fill(minX, minY, minX + 1, maxY, redColor);
                    ctx.fill(maxX - 1, minY, maxX, maxY, redColor);
                }
            }
            if (esp.healthBar.isEnabled()) {
                float maxHp = Math.max(1.0f, living.getMaxHealth());
                float hp = Math.max(0.0f, Math.min(maxHp, living.getHealth() + living.getAbsorptionAmount()));
                float ratio = Math.min(1.0f, hp / maxHp);
                int barH = Math.max(2, (int)((float)bh * ratio));
                int bx = minX - 4;
                ctx.fill(bx - 1, minY - 1, bx + 2, maxY + 1, -587202560);
                int hpCol = ratio > 0.5f ? -56264 : (ratio > 0.25f ? -21760 : -2354116);
                ctx.fill(bx, maxY - barH, bx + 1, maxY, hpCol);
            }
            if (esp.nametags.isEnabled()) {
                float hp = living.getHealth() + living.getAbsorptionAmount();
                String flyTag = living.isFallFlying() ? " \u2708" : "";
                String tag = String.format("%s \u2022 %.1f\u2665 \u2022 %dm%s", entity.getName().getString(), Float.valueOf(hp), (int)dist, flyTag);
                int tw = tr.getWidth(tag) + 6;
                int tx = minX + (bw - tw) / 2;
                int ty = minY - 13;
                ctx.fillGradient(tx, ty, tx + tw, ty + 11, -301333241, -296220136);
                ctx.fill(tx, ty, tx + tw, ty + 1, redColor);
                ctx.fill(tx, ty + 10, tx + tw, ty + 11, redColor);
                ctx.fill(tx, ty, tx + 1, ty + 11, redColor);
                ctx.fill(tx + tw - 1, ty, tx + tw, ty + 11, redColor);
                ctx.drawTextWithShadow(tr, tag, tx + 3, ty + 2, -1);
            }
            if (esp.showHeldItem.isEnabled() && !living.getMainHandStack().isEmpty()) {
                String itemName = living.getMainHandStack().getName().getString();
                int iw = tr.getWidth(itemName);
                ctx.drawTextWithShadow(tr, itemName, minX + (bw - iw) / 2, maxY + 2, -30058);
            }
            if (!esp.tracers.isEnabled()) continue;
            HudOverlayRenderer.drawScreenLine(ctx, screenW / 2, screenH / 2, minX + bw / 2, maxY, redColor);
        }
    }

    private static void renderPearlEsp(DrawContext ctx, TextRenderer tr, MinecraftClient mc, PearlTrackerModule mod, float tickDelta, int screenW, int screenH) {
        if (mc.world == null || mc.player == null) {
            return;
        }
        for (Entity entity : mc.world.getEntities()) {
            if (!(entity instanceof EnderPearlEntity)) continue;
            EnderPearlEntity pearl = (EnderPearlEntity)entity;
            Vec3d lerped = pearl.getLerpedPos(tickDelta);
            Box box = pearl.getBoundingBox().offset(lerped.subtract(pearl.getPos())).expand(0.15);
            int[] bounds = HudOverlayRenderer.projectBoxToScreen(mc, box, screenW, screenH);
            if (bounds == null) continue;
            int minX = bounds[0];
            int minY = bounds[1];
            int maxX = bounds[2];
            int maxY = bounds[3];
            int col = -56264;
            ctx.fill(minX, minY, maxX, maxY, 1157571640);
            ctx.fill(minX, minY, maxX, minY + 1, col);
            ctx.fill(minX, maxY - 1, maxX, maxY, col);
            ctx.fill(minX, minY, minX + 1, maxY, col);
            ctx.fill(maxX - 1, minY, maxX, maxY, col);
            String label = String.format("PEARL %dm", (int)mc.player.distanceTo((Entity)pearl));
            ctx.drawTextWithShadow(tr, label, minX + (maxX - minX - tr.getWidth(label)) / 2, minY - 10, -45726);
            if (!mod.showTracer.isEnabled()) continue;
            HudOverlayRenderer.drawScreenLine(ctx, screenW / 2, screenH / 2, (minX + maxX) / 2, (minY + maxY) / 2, col);
        }
    }

    private static void renderStorageEsp(DrawContext ctx, MinecraftClient mc, StorageEspModule mod, float tickDelta, int screenW, int screenH) {
        if (mc.world == null || mc.player == null) {
            return;
        }
        int radius = Math.min(48, mod.range.getIntValue());
        BlockPos center = mc.player.getBlockPos();
        for (int dx = -radius; dx <= radius; ++dx) {
            for (int dy = -radius / 2; dy <= radius / 2; ++dy) {
                for (int dz = -radius; dz <= radius; ++dz) {
                    Box box;
                    int[] bounds;
                    BlockPos pos = center.add(dx, dy, dz);
                    Block block = mc.world.getBlockState(pos).getBlock();
                    int color = 0;
                    if ((block instanceof ChestBlock || block instanceof BarrelBlock) && mod.chests.isEnabled()) {
                        color = -21760;
                    } else if (block instanceof EnderChestBlock && mod.enderChests.isEnabled()) {
                        color = -2817799;
                    } else if (block instanceof ShulkerBoxBlock && mod.shulkers.isEnabled()) {
                        color = -56264;
                    }
                    if (color == 0 || (bounds = HudOverlayRenderer.projectBoxToScreen(mc, box = new Box(pos).contract(0.06), screenW, screenH)) == null) continue;
                    int minX = bounds[0];
                    int minY = bounds[1];
                    int maxX = bounds[2];
                    int maxY = bounds[3];
                    ctx.fill(minX, minY, maxX, maxY, ThemeManager.withAlpha(color, 38));
                    ctx.fill(minX, minY, maxX, minY + 1, color);
                    ctx.fill(minX, maxY - 1, maxX, maxY, color);
                    ctx.fill(minX, minY, minX + 1, maxY, color);
                    ctx.fill(maxX - 1, minY, maxX, maxY, color);
                }
            }
        }
    }

    private static void drawCornerBox(DrawContext ctx, int x, int y, int w, int h, int cW, int cH, int color) {
        ctx.fill(x, y, x + cW, y + 1, color);
        ctx.fill(x, y, x + 1, y + cH, color);
        ctx.fill(x + w - cW, y, x + w, y + 1, color);
        ctx.fill(x + w - 1, y, x + w, y + cH, color);
        ctx.fill(x, y + h - 1, x + cW, y + h, color);
        ctx.fill(x, y + h - cH, x + 1, y + h, color);
        ctx.fill(x + w - cW, y + h - 1, x + w, y + h, color);
        ctx.fill(x + w - 1, y + h - cH, x + w, y + h, color);
    }

    private static void drawScreenLine(DrawContext ctx, int x0, int y0, int x1, int y1, int color) {
        int dx = x1 - x0;
        int dy = y1 - y0;
        int steps = Math.min(180, Math.max(Math.abs(dx), Math.abs(dy)) / 2);
        if (steps <= 0) {
            return;
        }
        for (int i = 0; i <= steps; ++i) {
            int px = x0 + dx * i / steps;
            int py = y0 + dy * i / steps;
            ctx.fill(px, py, px + 1, py + 1, color);
        }
    }

    private static int[] projectBoxToScreen(MinecraftClient mc, Box box, int screenW, int screenH) {
        Camera camera = mc.gameRenderer.getCamera();
        Vec3d camPos = camera.getPos();
        double yawRad = Math.toRadians(camera.getYaw());
        double pitchRad = Math.toRadians(camera.getPitch());
        double sinYaw = Math.sin(yawRad);
        double cosYaw = Math.cos(yawRad);
        double sinPitch = Math.sin(pitchRad);
        double cosPitch = Math.cos(pitchRad);
        double fx = -sinYaw * cosPitch;
        double fy = -sinPitch;
        double fz = cosYaw * cosPitch;
        double rx = -cosYaw;
        double ry = 0.0;
        double rz = -sinYaw;
        double ux = -sinYaw * sinPitch;
        double uy = cosPitch;
        double uz = cosYaw * sinPitch;
        double fovDeg = ((Integer)mc.options.getFov().getValue()).intValue();
        if (mc.player != null) {
            fovDeg *= (double)MathHelper.lerp(mc.getRenderTickCounter().getTickDelta(false), (float)1.0f, (float)mc.player.getFovMultiplier());
        }
        double focal = (double)screenH / 2.0 / Math.tan(Math.toRadians(MathHelper.clamp((double)fovDeg, (double)30.0, (double)150.0)) / 2.0);
        double halfW = (double)screenW / 2.0;
        double halfH = (double)screenH / 2.0;
        double[] xs = new double[]{box.minX, box.maxX};
        double[] ys = new double[]{box.minY, box.maxY};
        double[] zs = new double[]{box.minZ, box.maxZ};
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int validCorners = 0;
        for (double wx : xs) {
            for (double wy : ys) {
                double[] dArray = zs;
                int n = dArray.length;
                for (int i = 0; i < n; ++i) {
                    double dx = wx - camPos.x;
                    double dy = wy - camPos.y;
                    double wz = dArray[i];
                    double dz = wz - camPos.z;
                    double zCam = dx * fx + dy * fy + dz * fz;
                    if (zCam <= 0.12) continue;
                    double xCam = dx * rx + dy * ry + dz * rz;
                    double yCam = dx * ux + dy * uy + dz * uz;
                    int sx = (int)(halfW + xCam / zCam * focal);
                    int sy = (int)(halfH - yCam / zCam * focal);
                    minX = Math.min(minX, sx);
                    minY = Math.min(minY, sy);
                    maxX = Math.max(maxX, sx);
                    maxY = Math.max(maxY, sy);
                    ++validCorners;
                }
            }
        }
        if (validCorners < 2) {
            return null;
        }
        if (maxX < 0 || minX > screenW || maxY < 0 || minY > screenH) {
            return null;
        }
        minX = MathHelper.clamp((int)minX, (int)-20, (int)(screenW + 20));
        maxX = MathHelper.clamp((int)maxX, (int)-20, (int)(screenW + 20));
        minY = MathHelper.clamp((int)minY, (int)-20, (int)(screenH + 20));
        maxY = MathHelper.clamp((int)maxY, (int)-20, (int)(screenH + 20));
        return new int[]{minX, minY, maxX, maxY};
    }

    private static int getPlayerPing(MinecraftClient mc) {
        if (mc.getNetworkHandler() == null || mc.player == null) {
            return 0;
        }
        PlayerListEntry entry = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
        return entry != null ? entry.getLatency() : 0;
    }
}

