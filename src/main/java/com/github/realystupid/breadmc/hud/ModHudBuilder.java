package com.github.realystupid.breadmc.hud;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class ModHudBuilder {

    // internal class to each lines data
    private static class HudLine {
        String text;
        int color;
        boolean centered;

        public HudLine(String text, int color, boolean centered) {
            this.text = text;
            this.color = color;
            this.centered = centered;
        }
    }

    private final List<HudLine> lines = new ArrayList<>();
    private int padding = 6;
    private int lineHeight = 10;

    // Set custom padding if you want
    public ModHudBuilder padding(int padding) {
        this.padding = padding;
        return this;
    }

    // Set line height/spacing
    public ModHudBuilder lineHeight(int lineHeight) {
        this.lineHeight = lineHeight;
        return this;
    }

    // Add a left-aligned line
    public ModHudBuilder addLine(String text, int color) {
        lines.add(new HudLine(text, color, false));
        return this;
    }

    // Add a centered line
    public ModHudBuilder addCenteredLine(String text, int color) {
        lines.add(new HudLine(text, color, true));
        return this;
    }

    // Render the entire box automatically based on where you want it anchored
    public void renderRightAligned(GuiGraphicsExtractor guiGraphics, Minecraft minecraft, int marginY) {
        if (lines.isEmpty()) return;

        // 1. Find the widest string automatically
        int maxTextWidth = 0;
        for (HudLine line : lines) {
            maxTextWidth = Math.max(maxTextWidth, minecraft.font.width(line.text));
        }

        // 2. Calculate total box dimensions
        int boxWidth = maxTextWidth + (padding * 2);
        int boxHeight = (lineHeight * lines.size()) + (padding * 2);

        // 3. Screen coordinates (Anchored to top-right corner, vertically centered)
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        
        int marginX = 5;
        int x = screenWidth - boxWidth - marginX;
        int y = (screenHeight - boxHeight) / 2 + marginY; // Allows a small vertical offset

        // 4. Draw background box
        guiGraphics.fill(x, y, x + boxWidth, y + boxHeight, 0x99000000);

        // 5. Loop through and draw all lines dynamically (no hardcoded math!)
        int currentY = y + padding;
        for (HudLine line : lines) {
            int drawX;
            if (line.centered) {
                drawX = x + padding + (maxTextWidth - minecraft.font.width(line.text)) / 2;
            } else {
                drawX = x + padding;
            }

            guiGraphics.text(minecraft.font, line.text, drawX, currentY, line.color);
            currentY += lineHeight;
        }
    }
}
