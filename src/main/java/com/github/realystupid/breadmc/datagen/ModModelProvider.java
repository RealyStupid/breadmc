package com.github.realystupid.breadmc.datagen;

import com.github.realystupid.breadmc.BreadMC;
import com.github.realystupid.breadmc.items.ModConsumableItems;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;

public class ModModelProvider extends ModelProvider{
    // constructer used when neoforge runs the data gen
    // It tells Minecraft where to write the generated JSON files and which mod ID to use.
    public ModModelProvider(PackOutput output){
        super(output, BreadMC.MODID);
    } 

    // This method is the data-generation hook that writes all block/item model JSON files.
    // If a block is registered but never added here, runData fails with a missing blockstate definition.
    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        // These item entries create the inventory sprites for the ruby-related items.
        itemModels.generateFlatItem(ModConsumableItems.BREAD_SLICE.get(), ModelTemplates.FLAT_ITEM);
    }
}
