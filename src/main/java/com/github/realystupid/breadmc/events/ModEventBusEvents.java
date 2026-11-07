package com.github.realystupid.breadmc.events;

import com.github.realystupid.breadmc.BreadMC;
import com.github.realystupid.breadmc.attachment.ModAttachments;
import com.github.realystupid.breadmc.items.ModConsumableItems;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

@EventBusSubscriber(modid = BreadMC.MODID)
public class ModEventBusEvents {

    // create an event that watches if the player eats a slice of bread
    @SubscribeEvent 
    public static void onEatingFinished(LivingEntityUseItemEvent.Finish event){
        // make sure if the player is eating
        if(event.getEntity() instanceof Player player){
            ItemStack consumedItem = event.getItem();

            // make sure player is eating the bread_slice
            if(consumedItem.is(ModConsumableItems.BREAD_SLICE.get())){
                // make sure its on the server side so we actually spawn/drop the item
                if(! player.level().isClientSide()){

                    // get the current money and add 10 bucks
                    int currentMoney = player.getData(ModAttachments.PLAYER_MONEY);
                    int newMoney = currentMoney + 10;

                    // Save it back to the player
                    player.setData(ModAttachments.PLAYER_MONEY, newMoney);

                    BreadMC.LOGGER.info("Player " + player.getName().getString() + " ate bread! Total Money: $" + newMoney);
                }
            }
        }
    }
}
