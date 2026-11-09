package com.github.realystupid.breadmc.attachment;

import java.util.function.Supplier;

import com.github.realystupid.breadmc.BreadMC;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModAttachments {
    /*
        Neoforge has a `AttachmentType` that allows us to attach custom data to players easily!
        Here we create a attachment storing the money the user has
    */
    // kinda like the blocks/items, we need a list of attachments
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPE =
        DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, BreadMC.MODID);

    // now we create an integer attachment that defaults to 0 for every new player
    // this one tracks the money the player has
    public static final Supplier<AttachmentType<Long>> PLAYER_MONEY = ATTACHMENT_TYPE.register("player_money",
        () -> AttachmentType.builder(() -> 0L)
            .serialize(new IAttachmentSerializer<Long>() {
                @Override
                public Long read(IAttachmentHolder holder, ValueInput input) {
                    return input.getLongOr("value", 0L);
                }

                @Override
                public boolean write(Long attachment, ValueOutput output) {
                    output.putLong("value", attachment);
                    return true;
                }
            })
            // Copy the saved money value to the replacement player entity after death.
            .copyOnDeath()
            // Persistence saves the value on the server; sync sends changes to the client HUD.
            .sync(ByteBufCodecs.VAR_LONG)
            .build()
    );

    // this one tracks how many times the player ate bread (any kind)
    public static final Supplier<AttachmentType<Long>> BREAD_COUNT = ATTACHMENT_TYPE.register("bread_count",
        () -> AttachmentType.builder(() -> 0L)
        .serialize(new IAttachmentSerializer<Long>() {
            @Override 
            public Long read(IAttachmentHolder holder, ValueInput input) {
                return input.getLongOr("value", 0L);
            }

            @Override 
            public boolean write(Long attachment, ValueOutput output) {
                output.putLong("value", attachment);
                return true;
            }
        })
        .copyOnDeath()
        .sync(ByteBufCodecs.VAR_LONG)
        .build()
    );

    public static void register(IEventBus eventBus) {
        ATTACHMENT_TYPE.register(eventBus);
        BreadMC.LOGGER.info("Attachments registered\n");
    }
}
