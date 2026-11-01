/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.text.Text
 *  net.minecraft.client.MinecraftClient
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 */
package com.legit.client.gui;

import com.legit.client.LegitClient;
import com.legit.client.gui.ClickGuiScreen;
import com.legit.client.gui.ThemeManager;
import com.legit.client.security.LicenseGuard;
import net.minecraft.text.Text;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

public class LicenseGateScreen
extends Screen {
    private String keyInput = "";
    private boolean inputFocused = true;
    private boolean verifying = false;
    private String feedbackMessage = "";
    private boolean feedbackSuccess = false;

    public LicenseGateScreen() {
        super((Text)Text.literal((String)"Legit Lisans Do\u011frulama"));
    }

    public boolean shouldPause() {
        return false;
    }

    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, this.width, this.height, -1442445299);
        ThemeManager.Palette palette = ThemeManager.getActivePalette();
        LicenseGuard guard = LegitClient.getInstance().getLicenseGuard();
        int panelW = 340;
        int panelH = 225;
        int px = (this.width - panelW) / 2;
        int py = (this.height - panelH) / 2;
        ThemeManager.drawRoundedCard(ctx, px, py, panelW, panelH, palette.bgDark(), palette.primary());
        ThemeManager.drawHorizontalGradientBar(ctx, px + 1, py, panelW - 2, 3, palette.primary(), palette.secondary());
        String title = "LEGIT \u2022 L\u0130SANS MERKEZ\u0130";
        ctx.drawTextWithShadow(this.textRenderer, title, px + (panelW - this.textRenderer.getWidth(title)) / 2, py + 14, palette.primary());
        String status = "Durum: " + guard.getStatusMessage();
        ctx.drawTextWithShadow(this.textRenderer, status, px + (panelW - this.textRenderer.getWidth(status)) / 2, py + 29, -38005);
        int hwidY = py + 50;
        ctx.drawTextWithShadow(this.textRenderer, "Cihaz HWID Kimli\u011finiz:", px + 18, hwidY, palette.textMuted());
        ThemeManager.drawRoundedCard(ctx, px + 18, hwidY + 12, 215, 22, palette.bgCard(), -14142649);
        ctx.drawTextWithShadow(this.textRenderer, guard.getHwid(), px + 26, hwidY + 19, palette.textMain());
        int copyBtnX = px + 239;
        int copyBtnY = hwidY + 12;
        int copyBtnW = 83;
        int copyBtnH = 22;
        boolean copyHover = this.isHovered(mouseX, mouseY, copyBtnX, copyBtnY, copyBtnW, copyBtnH);
        ThemeManager.drawRoundedCard(ctx, copyBtnX, copyBtnY, copyBtnW, copyBtnH, copyHover ? palette.bgCardHover() : palette.bgCard(), palette.primary());
        String copyLabel = "HWID Kopyala";
        ctx.drawTextWithShadow(this.textRenderer, copyLabel, copyBtnX + (copyBtnW - this.textRenderer.getWidth(copyLabel)) / 2, copyBtnY + 7, palette.primary());
        int keyY = py + 96;
        ctx.drawTextWithShadow(this.textRenderer, "Lisans Anahtar\u0131 ile Do\u011frudan Etkinle\u015ftir (veya Discord /redeem):", px + 18, keyY, palette.textMuted());
        int inputX = px + 18;
        int inputY = keyY + 12;
        int inputW = panelW - 36;
        int inputH = 24;
        ThemeManager.drawRoundedCard(ctx, inputX, inputY, inputW, inputH, palette.bgCard(), this.inputFocused ? palette.primary() : -13879218);
        String displayKey = this.keyInput.isEmpty() && !this.inputFocused ? "LEGIT-XXXX-XXXX-XXXX (Ctrl+V ile yap\u0131\u015ft\u0131r\u0131n)" : this.keyInput + (this.inputFocused && System.currentTimeMillis() / 500L % 2L == 0L ? "_" : "");
        int keyTextCol = this.keyInput.isEmpty() && !this.inputFocused ? palette.textMuted() : palette.textMain();
        ctx.drawTextWithShadow(this.textRenderer, displayKey, inputX + 8, inputY + 8, keyTextCol);
        int btnY = py + 148;
        int btnW = (panelW - 44) / 2;
        int btnH = 26;
        int actX = px + 18;
        boolean actHover = this.isHovered(mouseX, mouseY, actX, btnY, btnW, btnH);
        ThemeManager.drawRoundedCard(ctx, actX, btnY, btnW, btnH, actHover ? ThemeManager.withAlpha(palette.primary(), 60) : palette.bgCard(), palette.primary());
        String actText = this.verifying ? "Do\u011frulan\u0131yor..." : "Anahtar\u0131 Etkinle\u015ftir";
        ctx.drawTextWithShadow(this.textRenderer, actText, actX + (btnW - this.textRenderer.getWidth(actText)) / 2, btnY + 9, palette.textMain());
        int relX = actX + btnW + 8;
        boolean relHover = this.isHovered(mouseX, mouseY, relX, btnY, btnW, btnH);
        ThemeManager.drawRoundedCard(ctx, relX, btnY, btnW, btnH, relHover ? palette.bgCardHover() : palette.bgCard(), palette.secondary());
        String relText = ".lic Dosyas\u0131n\u0131 Tara";
        ctx.drawTextWithShadow(this.textRenderer, relText, relX + (btnW - this.textRenderer.getWidth(relText)) / 2, btnY + 9, palette.textMain());
        if (!this.feedbackMessage.isEmpty()) {
            int fbCol = this.feedbackSuccess ? -16718218 : -21760;
            ctx.drawTextWithShadow(this.textRenderer, this.feedbackMessage, px + (panelW - this.textRenderer.getWidth(this.feedbackMessage)) / 2, py + 186, fbCol);
        }
        String footer = "Sunucu: " + guard.getApiBaseUrl() + " \u2022 Discord Bot ile Senkronize";
        ctx.drawTextWithShadow(this.textRenderer, footer, px + (panelW - this.textRenderer.getWidth(footer)) / 2, py + 205, palette.textMuted());
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int panelW = 340;
        int panelH = 225;
        int px = (this.width - panelW) / 2;
        int py = (this.height - panelH) / 2;
        LicenseGuard guard = LegitClient.getInstance().getLicenseGuard();
        if (this.isHovered(mouseX, mouseY, px + 239, py + 62, 83, 22)) {
            MinecraftClient.getInstance().keyboard.setClipboard(guard.getHwid());
            this.feedbackMessage = "HWID panoya kopyaland\u0131! Discord'da /redeem kullanabilirsiniz.";
            this.feedbackSuccess = true;
            return true;
        }
        this.inputFocused = this.isHovered(mouseX, mouseY, px + 18, py + 108, panelW - 36, 24);
        int btnY = py + 148;
        int btnW = (panelW - 44) / 2;
        int btnH = 26;
        if (this.isHovered(mouseX, mouseY, px + 18, btnY, btnW, btnH) && !this.verifying) {
            if (this.keyInput.trim().isEmpty()) {
                this.feedbackMessage = "L\u00fctfen \u00f6nce bir lisans anahtar\u0131 girin!";
                this.feedbackSuccess = false;
                return true;
            }
            this.verifying = true;
            this.feedbackMessage = "Bot sunucusu ile do\u011frulan\u0131yor...";
            guard.activateWithKeyAsync(this.keyInput.trim(), success -> {
                this.verifying = false;
                if (success.booleanValue()) {
                    this.feedbackSuccess = true;
                    this.feedbackMessage = "Lisans do\u011fruland\u0131! Men\u00fc a\u00e7\u0131l\u0131yor...";
                    MinecraftClient.getInstance().execute(() -> MinecraftClient.getInstance().setScreen((Screen)new ClickGuiScreen()));
                } else {
                    this.feedbackSuccess = false;
                    this.feedbackMessage = guard.getStatusMessage();
                }
            });
            return true;
        }
        if (this.isHovered(mouseX, mouseY, px + 18 + btnW + 8, btnY, btnW, btnH)) {
            if (guard.verifyLocalLicenseFile()) {
                this.feedbackSuccess = true;
                this.feedbackMessage = "Lisans do\u011fruland\u0131!";
                MinecraftClient.getInstance().setScreen((Screen)new ClickGuiScreen());
            } else {
                this.feedbackSuccess = false;
                this.feedbackMessage = guard.getStatusMessage();
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.inputFocused) {
            if (keyCode == 259 && !this.keyInput.isEmpty()) {
                this.keyInput = this.keyInput.substring(0, this.keyInput.length() - 1);
                return true;
            }
            if (keyCode == 86 && (modifiers & 2) != 0) {
                String clip = MinecraftClient.getInstance().keyboard.getClipboard();
                if (clip != null) {
                    this.keyInput = clip.trim();
                    if (this.keyInput.length() > 48) {
                        this.keyInput = this.keyInput.substring(0, 48);
                    }
                }
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean charTyped(char chr, int modifiers) {
        if (this.inputFocused && chr >= ' ' && chr != '\u007f' && this.keyInput.length() < 44) {
            this.keyInput = this.keyInput + chr;
            return true;
        }
        return super.charTyped(chr, modifiers);
    }

    private boolean isHovered(double mx, double my, int x, int y, int w, int h) {
        return mx >= (double)x && mx <= (double)(x + w) && my >= (double)y && my <= (double)(y + h);
    }
}

