package com.github.realystupid.breadmc.hud;

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
public class ModHudHandler {

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

                // same for the count
                int count = minecraft.player.getData(ModAttachments.BREAD_COUNT);

                // This NeoForge version uses guiGraphics.text(...), and its color argument is ARGB.
                // The leading FF makes each color fully opaque; without it the text is invisible.
                
                // Build your HUD dynamically line-by-line!
                ModHudBuilder hud = new ModHudBuilder()
                    .padding(6)
                    .lineHeight(10);

                hud.addCenteredLine("Welcome to BreadMC!", 0xCCCE9532);
                hud.addCenteredLine("-------------------", 0xff0027c4);
                hud.addLine("Money: $" + money, 0xFF55FF55);
                
                // Want to add a new line later? Just add it here:
                hud.addLine("Bread Eaten: " + count, 0xffa1a1a1);

                // Render it out to the screen on the right side
                hud.renderRightAligned(guiGraphics, minecraft, 0);
            }
        );
    }
}
