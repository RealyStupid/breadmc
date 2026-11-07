package com.github.realystupid.breadmc.events;

import com.github.realystupid.breadmc.BreadMC;
import com.github.realystupid.breadmc.attachment.ModAttachments;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

// This class is client-side only, so it is registered for the CLIENT distribution.
// NeoForge will call the methods here when the relevant client events fire.
@EventBusSubscriber(modid = BreadMC.MODID, value = Dist.CLIENT)
public class ModCLientEvents {

    // We register a custom HUD layer that renders above the vanilla chat layer.
    // The layer name must be a proper minecraft Identifier, not a plain String.
    // We also pick a fixed on-screen position so the HUD is always visible in the same spot.
    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiLayersEvent event) {
        event.registerAbove(
            VanillaGuiLayers.CHAT,
            Identifier.fromNamespaceAndPath(BreadMC.MODID, "breadmc_hud"),
            (guiGraphics, deltaTracker) -> {
                // Grab the current client instance and early-out if the player is not present.
                // This avoids trying to render HUD data when we are in a menu or not connected.
                Minecraft minecraft = Minecraft.getInstance();
                if (minecraft.player == null) {
                    return;
                }

                // Pull this player's money from the custom attachment we registered earlier.
                // The attachment stores data per-player, so each client sees its own saved value.
                int money = minecraft.player.getData(ModAttachments.PLAYER_MONEY);

                // This NeoForge version uses guiGraphics.text(...), and its color argument is ARGB.
                // The leading FF makes each color fully opaque; without it the text is invisible.
                int x = 265;
                int y = 70;

                String serverText = "~You Are Playing BreadMC~";
                String seperator =  "-------------------------";
                String moneyText = "Money: $" + money;

                // Find the widest string to size the box
                int textWidth = Math.max(
                    minecraft.font.width(serverText), 
                    Math.max(
                        minecraft.font.width(seperator), 
                        minecraft.font.width(moneyText)
                    )
                );

                // Draw background box (using a dynamic height based on 3 lines + padding)
                int padding = 6;
                int lineHeight = 7; // 9px font + spacing
                int boxHeight = (lineHeight * 3) + (padding * 2);
                
                guiGraphics.fill(x - padding, y - padding, x + textWidth + padding, y - padding + boxHeight, 0x99000000);

                // 1. Center serverText horizontally inside the box width
                int serverTextX = x + (textWidth - minecraft.font.width(serverText)) / 2;
                guiGraphics.text(minecraft.font, serverText, serverTextX, y, 0xCCCE9532);

                int seperatorX = x + (textWidth - minecraft.font.width(seperator)) / 2;
                guiGraphics.text(minecraft.font, seperator, seperatorX, y + lineHeight, 0xff0027c4);

                guiGraphics.text(minecraft.font, moneyText, x, y + (lineHeight * 2), 0xFF55FF55);
            }
        );
    }
}
