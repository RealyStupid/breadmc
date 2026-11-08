package com.github.realystupid.breadmc.hud;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class ModHudBuilder {

    private static class HudLine {
        String text;
        int color;
        boolean centered;

        // Fields for multi-part (Key-Value / Role) lines
        boolean isKeyValue;
        String keyText;
        String valueText;
        int keyColor;
        int valueColor;

        int offsetX;
        int offsetY;

        // Standard single-text constructor
        public HudLine(String text, int color, boolean centered, int offsetX, int offsetY) {
            this.text = text;
            this.color = color;
            this.centered = centered;
            this.isKeyValue = false;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        // Key-Value constructor (e.g., "Role: " + "Nooby")
        public HudLine(String keyText, String valueText, int keyColor, int valueColor, boolean centered, int offsetX, int offsetY) {
            this.keyText = keyText;
            this.valueText = valueText;
            this.keyColor = keyColor;
            this.valueColor = valueColor;
            this.centered = centered;
            this.isKeyValue = true;
            this.text = keyText + valueText; // For width calculations
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }

        public int getWidth(Font font) {
            if (isKeyValue) {
                return font.width(keyText) + font.width(valueText);
            }
            return font.width(text);
        }
    }

    private final List<HudLine> lines = new ArrayList<>();
    private int padding = 6;
    private int lineHeight = 10;

    private int currentGroupOffsetX = 0;
    private int currentGroupOffsetY = 0;

    public ModHudBuilder padding(int padding) {
        this.padding = padding;
        return this;
    }

    public ModHudBuilder lineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
        return this;
    }

    public ModHudBuilder startGroup(int offsetX, int offsetY) {
        this.currentGroupOffsetX = offsetX;
        this.currentGroupOffsetY = offsetY;
        return this;
    }

    public ModHudBuilder endGroup() {
        this.currentGroupOffsetX = 0;
        this.currentGroupOffsetY = 0;
        return this;
    }

    public ModHudBuilder addLine(String text, int color) {
        return addLine(text, color, 0, 0);
    }

    public ModHudBuilder addLine(String text, int color, int offsetX, int offsetY) {
        lines.add(new HudLine(text, color, false, offsetX + currentGroupOffsetX, offsetY + currentGroupOffsetY));
        return this;
    }

    public ModHudBuilder addCenteredLine(String text, int color) {
        return addCenteredLine(text, color, 0, 0);
    }

    public ModHudBuilder addCenteredLine(String text, int color, int offsetX, int offsetY) {
        lines.add(new HudLine(text, color, true, offsetX + currentGroupOffsetX, offsetY + currentGroupOffsetY));
        return this;
    }

    // New method for split-color lines (like Role: Nooby)
    public ModHudBuilder addKeyValueLine(String key, String value, int keyColor, int valueColor, boolean centered) {
        return addKeyValueLine(key, value, keyColor, valueColor, centered, 0, 0);
    }

    public ModHudBuilder addKeyValueLine(String key, String value, int keyColor, int valueColor, boolean centered, int offsetX, int offsetY) {
        lines.add(new HudLine(key, value, keyColor, valueColor, centered, offsetX + currentGroupOffsetX, offsetY + currentGroupOffsetY));
        return this;
    }

    public void renderRightAligned(GuiGraphicsExtractor guiGraphics, Minecraft minecraft, int marginY) {
        if (lines.isEmpty()) return;

        // 1. Find max total width across all lines for proper alignment
        int maxTextWidth = 0;
        for (HudLine line : lines) {
            maxTextWidth = Math.max(maxTextWidth, line.getWidth(minecraft.font));
        }

        // 2. Compute bounding box factoring in all offsets and widths
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        int fontHeight = 9;

        for (int i = 0; i < lines.size(); i++) {
            HudLine line = lines.get(i);
            int w = line.getWidth(minecraft.font);
            int baseX = line.centered ? (maxTextWidth - w) / 2 : 0;

            int lineX1 = baseX + line.offsetX;
            int lineX2 = lineX1 + w;
            int lineY1 = (i * lineHeight) + line.offsetY;
            int lineY2 = lineY1 + fontHeight;

            minX = Math.min(minX, lineX1);
            minY = Math.min(minY, lineY1);
            maxX = Math.max(maxX, lineX2);
            maxY = Math.max(maxY, lineY2);
        }

        int contentWidth = maxX - minX;
        int contentHeight = maxY - minY;
        int boxWidth = contentWidth + (padding * 2);
        int boxHeight = contentHeight + (padding * 2);

        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        
        int marginX = 5;
        int x = screenWidth - boxWidth - marginX;
        int y = (screenHeight - boxHeight) / 2 + marginY;

        // Draw background box
        guiGraphics.fill(x, y, x + boxWidth, y + boxHeight, 0x99000000);

        // 3. Draw lines (supporting both single text and split key-value rendering)
        for (int i = 0; i < lines.size(); i++) {
            HudLine line = lines.get(i);
            int w = line.getWidth(minecraft.font);
            int baseX = line.centered ? (maxTextWidth - w) / 2 : 0;

            int lineX1 = baseX + line.offsetX;
            int lineY1 = (i * lineHeight) + line.offsetY;

            int finalX = x + padding + (lineX1 - minX);
            int finalY = y + padding + (lineY1 - minY);

            if (line.isKeyValue) {
                // Draw key (e.g., "Role: ") in its color
                guiGraphics.text(minecraft.font, line.keyText, finalX, finalY, line.keyColor);
                // Draw value right after it in its own custom color
                int valueX = finalX + minecraft.font.width(line.keyText);
                guiGraphics.text(minecraft.font, line.valueText, valueX, finalY, line.valueColor);
            } else {
                guiGraphics.text(minecraft.font, line.text, finalX, finalY, line.color);
            }
        }
    }
}