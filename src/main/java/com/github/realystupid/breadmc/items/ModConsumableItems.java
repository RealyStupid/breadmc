package com.github.realystupid.breadmc.items;

import com.github.realystupid.breadmc.BreadMC;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModConsumableItems {

    // create a list of consumable items
    public static final DeferredRegister.Items CONSUMABLE_ITEMS = DeferredRegister.createItems(BreadMC.MODID);

    // add an item to the list
    public static final DeferredItem<Item> BREAD_SLICE = CONSUMABLE_ITEMS.registerSimpleItem(
        "bread_slice",
        props -> props.food(
            new FoodProperties.Builder()
                .nutrition(2) // 1 full drumstick
                .saturationModifier(0.3f)
                .alwaysEdible()
                .build()
        )
    );

    // event to add ALL the items inside the CONSUMABLE_ITEMS list
    public static void register(IEventBus eventBus){
        CONSUMABLE_ITEMS.register(eventBus);
        BreadMC.LOGGER.info("Registered consumable items \n");
    }
}
