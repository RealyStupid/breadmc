package com.github.realystupid.breadmc;

import org.slf4j.Logger;

import com.github.realystupid.breadmc.items.ModConsumableItems;
import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(BreadMC.MODID)
public class BreadMC {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "breadmc";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public BreadMC(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("_____________________________________________________________________");
        LOGGER.info("Starting Up the BreadMC mod!\n");

        ModConsumableItems.register(modEventBus);

        LOGGER.info("Mod has fully loaded!");
        LOGGER.info("_____________________________________________________________________");
    }
}
