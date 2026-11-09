package com.github.realystupid.breadmc.datagen;

import com.github.realystupid.breadmc.BreadMC;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

// Connects this class to NeoForge's automatic event registration system.
// modid specifies which mod this handler belongs to.
@EventBusSubscriber(modid = BreadMC.MODID)
public class ModDataGen {
    // Marks this method as an event listener.
    // NeoForge will automatically call this method when the "GatherDataEvent.Client" event fires.
    @SubscribeEvent 
    public static void gatherClientData(GatherDataEvent.Client event){
        // event.getGenerator(): Grabs Minecraft's internal DataGenerator engine.
        // DataGenerator is the core manager that runs all your data-generating tasks.
        DataGenerator generator = event.getGenerator();

        // generator.getPackOutput(): Retrieves the directory path configuration.
        // This tells the data generator WHERE on your computer (inside your project's resources folder) to write the output JSON files.
        PackOutput packOutput = generator.getPackOutput();

        // generator.addProvider(...): Registers a new data provider into the engine.
        // true: Tells the generator to overwrite existing generated files if they already exist.
        // new ModModelProvider(packOutput): Creates an instance of your custom model generator class, passing along the file output location.
        generator.addProvider(true, new ModModelProvider(packOutput));
    }
}
