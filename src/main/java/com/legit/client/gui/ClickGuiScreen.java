/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.text.Text
 *  net.minecraft.client.gui.DrawContext
 *  net.minecraft.client.gui.screen.Screen
 */
package com.legit.client.gui;

import com.legit.client.LegitClient;
import com.legit.client.module.Category;
import com.legit.client.module.Module;
import com.legit.client.setting.BooleanSetting;
import com.legit.client.setting.KeybindSetting;
import com.legit.client.setting.ModeSetting;
import com.legit.client.setting.NumberSetting;
import com.legit.client.setting.Setting;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.text.Text;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

public class ClickGuiScreen
extends Screen {
    private static final Map<Category, PanelState> PANELS = new EnumMap<Category, PanelState>(Category.class);
    private static boolean initializedPositions = false;
    private Module bindingModule = null;
    private NumberSetting draggingSlider = null;
    private int sliderDragX = 0;
    private int sliderDragWidth = 1;
    private static final int PANEL_WIDTH = 98;
    private static final int HEADER_HEIGHT = 17;
    private static final int MODULE_ROW_HEIGHT = 15;
    private static final int SETTING_ROW_HEIGHT = 14;
    private static final int NOVA_BORDER = -56264;
    private static final int NOVA_FILL_ACTIVE = -1697490;
    private static final int NOVA_TEXT_ACTIVE = -50354;
    private static final int NOVA_BG_TOP = -334887673;
    private static final int NOVA_BG_BOTTOM = -262665704;

    public ClickGuiScreen() {
        super((Text)Text.literal((String)"Legit ClickGUI"));
    }

    private void ensurePanelPositions() {
        Category[] categories = Category.values();
        int totalNeeded = categories.length * 104;
        int startX = Math.max(8, (this.width - totalNeeded) / 2);
        if (!initializedPositions || PANELS.isEmpty()) {
            for (int i = 0; i < categories.length; ++i) {
                PanelState st = new PanelState();
                st.x = startX + i * 104;
                st.y = 18;
                PANELS.put(categories[i], st);
            }
            initializedPositions = true;
        }
    }

    protected void init() {
        super.init();
        this.ensurePanelPositions();
    }

    public boolean shouldPause() {
        return false;
    }

    public void close() {
        if (LegitClient.getInstance() != null && LegitClient.getInstance().getConfigManager() != null) {
            LegitClient.getInstance().getConfigManager().save();
        }
        super.close();
    }

    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        this.ensurePanelPositions();
        if (this.draggingSlider != null) {
            double ratio = (double)(mouseX - this.sliderDragX) / (double)Math.max(1, this.sliderDragWidth);
            ratio = Math.max(0.0, Math.min(1.0, ratio));
            double val = this.draggingSlider.getMin() + ratio * (this.draggingSlider.getMax() - this.draggingSlider.getMin());
            this.draggingSlider.setValue(val);
        }
        for (PanelState st : PANELS.values()) {
            if (!st.dragging) continue;
            st.x = mouseX - st.dragOffsetX;
            st.y = mouseY - st.dragOffsetY;
        }
        LegitClient client = LegitClient.getInstance();
        ctx.getMatrices().push();
        float scale = 2.4f;
        ctx.getMatrices().scale(scale, scale, 1.0f);
        String watermark = "Legit";
        int wmW = this.textRenderer.getWidth(watermark);
        int wmX = (int)((float)this.width / 2.0f / scale - (float)wmW / 2.0f);
        int wmY = (int)((float)(this.height - 34) / scale);
        ctx.drawTextWithShadow(this.textRenderer, watermark, wmX, wmY, -56264);
        ctx.getMatrices().pop();
        for (Category cat : Category.values()) {
            PanelState panel = PANELS.get((Object)cat);
            if (panel == null) continue;
            this.renderCategoryPanel(ctx, client, cat, panel, mouseX, mouseY);
        }
    }

    private void renderCategoryPanel(DrawContext ctx, LegitClient client, Category cat, PanelState panel, int mouseX, int mouseY) {
        int px = panel.x;
        int py = panel.y;
        int pw = 98;
        List<Module> modules = client.getModuleManager().getModulesByCategory(cat);
        int bodyHeight = 0;
        if (panel.open) {
            bodyHeight += 3;
            for (Module mod : modules) {
                bodyHeight += 15;
                if (!mod.isExpanded()) continue;
                List vis = mod.getSettings().stream().filter(Setting::isVisible).collect(Collectors.toList());
                bodyHeight += (vis.size() + 1) * 14 + 2;
            }
            bodyHeight += 3;
        }
        int totalH = 17 + bodyHeight;
        ctx.fillGradient(px, py, px + pw, py + totalH, -334887673, -262665704);
        this.drawNovaBorder(ctx, px, py, pw, totalH, -56264);
        ctx.drawTextWithShadow(this.textRenderer, cat.getDisplayName(), px + 5, py + 4, -1);
        ctx.fill(px + 2, py + 17 - 1, px + pw - 2, py + 17, -56264);
        if (!panel.open) {
            return;
        }
        int curY = py + 17 + 2;
        for (Module mod : modules) {
            boolean hovered = this.isHovered(mouseX, mouseY, px + 2, curY, pw - 4, 15);
            if (hovered) {
                ctx.fill(px + 2, curY, px + pw - 2, curY + 15, 872358968);
            }
            String modName = mod.getName();
            int nameW = this.textRenderer.getWidth(modName);
            int nameX = px + (pw - nameW) / 2 - 2;
            int nameColor = mod.isEnabled() ? -50354 : -986377;
            ctx.drawTextWithShadow(this.textRenderer, modName, nameX, curY + 3, nameColor);
            String sign = mod.isExpanded() ? "-" : "+";
            ctx.drawTextWithShadow(this.textRenderer, sign, px + pw - 9, curY + 3, -2565912);
            curY += 15;
            if (!mod.isExpanded()) continue;
            List<Setting<?>> visibleSettings = mod.getSettings().stream().filter(Setting::isVisible).collect(Collectors.toList());
            for (Setting setting : visibleSettings) {
                this.renderNovaSetting(ctx, setting, px + 2, curY, pw - 4);
                curY += 14;
            }
            KeybindSetting kb = mod.getKeybind();
            ctx.fill(px + 2, curY, px + pw - 2, curY + 14, 1711801870);
            String bindLabel = "Bind:";
            String bindVal = this.bindingModule == mod ? "..." : kb.getKeyName() + (kb.isHoldMode() ? "(H)" : "");
            ctx.drawTextWithShadow(this.textRenderer, bindLabel, px + 4, curY + 3, -3355444);
            ctx.drawTextWithShadow(this.textRenderer, bindVal, px + pw - 4 - this.textRenderer.getWidth(bindVal), curY + 3, this.bindingModule == mod ? -50354 : -1);
            curY += 16;
        }
    }

    private void renderNovaSetting(DrawContext ctx, Setting<?> setting, int x, int y, int w) {
        ctx.fill(x, y, x + w, y + 14, 1426588941);
        ctx.fill(x, y, x + w, y + 1, 0x44000000);
        if (setting instanceof BooleanSetting) {
            BooleanSetting bs = (BooleanSetting)setting;
            if (bs.isEnabled()) {
                ctx.fill(x, y + 1, x + w, y + 14, -1697490);
            }
            String label = this.trimText(setting.getName(), w - 6);
            ctx.drawTextWithShadow(this.textRenderer, label, x + 3, y + 3, -1);
        } else if (setting instanceof ModeSetting) {
            ModeSetting ms = (ModeSetting)setting;
            String left = setting.getName() + ":";
            String right = (String)ms.getValue();
            int maxRightW = w - this.textRenderer.getWidth(left) - 8;
            right = this.trimText(right, Math.max(24, maxRightW));
            ctx.drawTextWithShadow(this.textRenderer, left, x + 3, y + 3, -2236963);
            ctx.drawTextWithShadow(this.textRenderer, right, x + w - 3 - this.textRenderer.getWidth(right), y + 3, -1);
        } else if (setting instanceof NumberSetting) {
            NumberSetting ns = (NumberSetting)setting;
            double ratio = ((Double)ns.getValue() - ns.getMin()) / (ns.getMax() - ns.getMin());
            int fillW = (int)((double)w * (ratio = Math.max(0.0, Math.min(1.0, ratio))));
            if (fillW > 0) {
                ctx.fill(x, y + 1, x + fillW, y + 14, -1697490);
            }
            String valStr = this.formatCompactNumber(ns);
            int valW = this.textRenderer.getWidth(valStr);
            String nameStr = this.trimText(setting.getName(), w - valW - 8);
            ctx.drawTextWithShadow(this.textRenderer, nameStr, x + 3, y + 3, -1);
            ctx.drawTextWithShadow(this.textRenderer, valStr, x + w - 3 - valW, y + 3, -1);
        }
    }

    private String formatCompactNumber(NumberSetting ns) {
        if (ns.getStep() >= 1.0) {
            return String.valueOf(ns.getIntValue());
        }
        return String.format("%.1f", ns.getValue());
    }

    private String trimText(String text, int maxWidth) {
        if (this.textRenderer.getWidth(text) <= maxWidth) {
            return text;
        }
        return this.textRenderer.trimToWidth(text, Math.max(8, maxWidth));
    }

    private void drawNovaBorder(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x + 1, y, x + w - 1, y + 1, color);
        ctx.fill(x + 1, y + h - 1, x + w - 1, y + h, color);
        ctx.fill(x, y + 1, x + 1, y + h - 1, color);
        ctx.fill(x + w - 1, y + 1, x + w, y + h - 1, color);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.bindingModule != null && button >= 2) {
            this.bindingModule.getKeybind().setValue(KeybindSetting.fromMouseButton(button));
            this.bindingModule = null;
            return true;
        }
        LegitClient client = LegitClient.getInstance();
        for (Category cat : Category.values()) {
            PanelState panel = PANELS.get((Object)cat);
            if (panel == null) continue;
            int px = panel.x;
            int py = panel.y;
            int pw = 98;
            if (this.isHovered(mouseX, mouseY, px, py, pw, 17)) {
                if (button == 0) {
                    panel.dragging = true;
                    panel.dragOffsetX = (int)mouseX - px;
                    panel.dragOffsetY = (int)mouseY - py;
                } else if (button == 1) {
                    panel.open = !panel.open;
                }
                return true;
            }
            if (!panel.open) continue;
            int curY = py + 17 + 2;
            List<Module> modules = client.getModuleManager().getModulesByCategory(cat);
            for (Module mod : modules) {
                if (this.isHovered(mouseX, mouseY, px + 2, curY, pw - 4, 15)) {
                    if (button == 1 || mouseX >= (double)(px + pw - 14)) {
                        mod.setExpanded(!mod.isExpanded());
                    } else if (button == 0) {
                        mod.toggle();
                    }
                    return true;
                }
                curY += 15;
                if (!mod.isExpanded()) continue;
                List<Setting<?>> visibleSettings = mod.getSettings().stream().filter(Setting::isVisible).collect(Collectors.toList());
                int sx = px + 2;
                int sw = pw - 4;
                for (Setting setting : visibleSettings) {
                    if (this.isHovered(mouseX, mouseY, sx, curY, sw, 14)) {
                        if (setting instanceof BooleanSetting) {
                            BooleanSetting bs = (BooleanSetting)setting;
                            if (button == 0) {
                                bs.toggle();
                                return true;
                            }
                        }
                        if (setting instanceof ModeSetting) {
                            ModeSetting ms = (ModeSetting)setting;
                            if (button == 1) {
                                ms.cycleBackward();
                            } else {
                                ms.cycleForward();
                            }
                            return true;
                        }
                        if (setting instanceof NumberSetting) {
                            NumberSetting ns = (NumberSetting)setting;
                            if (button == 1) {
                                ns.reset();
                                return true;
                            }
                            if (button == 0) {
                                this.draggingSlider = ns;
                                this.sliderDragX = sx;
                                this.sliderDragWidth = sw;
                                double ratio = (mouseX - (double)sx) / (double)sw;
                                ratio = Math.max(0.0, Math.min(1.0, ratio));
                                ns.setValue(ns.getMin() + ratio * (ns.getMax() - ns.getMin()));
                                return true;
                            }
                        }
                    }
                    curY += 14;
                }
                if (this.isHovered(mouseX, mouseY, sx, curY, sw, 14)) {
                    if (button == 0) {
                        this.bindingModule = this.bindingModule == mod ? null : mod;
                    } else if (button == 1 || button == 2) {
                        mod.getKeybind().setHoldMode(!mod.getKeybind().isHoldMode());
                    }
                    return true;
                }
                curY += 16;
            }
        }
        this.bindingModule = null;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.draggingSlider = null;
            for (PanelState st : PANELS.values()) {
                st.dragging = false;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int shift = (int)(verticalAmount * 12.0);
        for (PanelState st : PANELS.values()) {
            st.y += shift;
        }
        return true;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.bindingModule != null) {
            if (keyCode == 256 || keyCode == 261 || keyCode == 259) {
                this.bindingModule.getKeybind().setValue(-1);
            } else {
                this.bindingModule.getKeybind().setValue(keyCode);
            }
            this.bindingModule = null;
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private boolean isHovered(double mx, double my, int x, int y, int w, int h) {
        return mx >= (double)x && mx <= (double)(x + w) && my >= (double)y && my <= (double)(y + h);
    }

    private static class PanelState {
        int x;
        int y;
        boolean open = true;
        boolean dragging = false;
        int dragOffsetX;
        int dragOffsetY;

        private PanelState() {
        }
    }
}

