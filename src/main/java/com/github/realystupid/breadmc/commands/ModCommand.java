package com.github.realystupid.breadmc.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.github.realystupid.breadmc.BreadMC;
import com.github.realystupid.breadmc.attachment.ModAttachments;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = BreadMC.MODID)
public class ModCommand {
    // Simple data holder for our backup cache
    private record PlayerBackup(Long money, Long breadCount) {}
    
    // Stores the last cleared stats per player UUID so admins can undo it
    private static final Map<UUID, PlayerBackup> BACKUP_CACHE = new HashMap<>();

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // Register commands
        dispatcher.register(
            // default /stats command to check your own stats
            Commands.literal("stats")
                // actually run code!
                .executes(context -> {
                    CommandSourceStack source = context.getSource();

                    if(source.getEntity() instanceof Player player){
                        sendStatsMessage(source, player);
                    } else {
                        source.sendSuccess(() -> Component.literal("Only players can use this command!"), false);
                    }

                    return 1; // to tell minecraft the command succeeded
                })

                // adding subcommands like this, /stats check <target>
                .then(Commands.literal("check")
                    // the .then allows us to add more stuff to our command
                    // such as /stats check before and the <target> here
                    .then(Commands.argument("target", EntityArgument.player())
                        .executes(context -> {
                            // extract the targeted player arguments safely
                            Player targetPlayer = EntityArgument.getPlayer(context, "target");
                            sendStatsMessage(context.getSource(), targetPlayer);
                            return 1;
                        })
                    )
                )

                // another subcommand! adding /stats give with elivated perms (OP level 2)
                .then(Commands.literal("give")
                    .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                    .then(Commands.argument("target", EntityArgument.player())
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                            .executes(context -> {
                                // we need to get the argument stuff and store them in variables
                                Player targetPlayer = EntityArgument.getPlayer(context, "target");
                                int amount = IntegerArgumentType.getInteger(context, "amount");

                                // get the players current money
                                Long currentMoney = targetPlayer.getData(ModAttachments.PLAYER_MONEY);

                                // set the money
                                targetPlayer.setData(ModAttachments.PLAYER_MONEY, currentMoney + amount);

                                // TO EVERYONE
                                context.getSource().sendSuccess(
                                    () -> Component.literal("§aGave $" + amount + " to " + targetPlayer.getName().getString()),
                                    true
                                );
                                
                                // to the target player
                                targetPlayer.sendSystemMessage(
                                    Component.literal("§aYou received $" + amount + "!")
                                );

                                return 1;
                            })
                        )
                    )
                )

                // another subcommand, for clearing all data/reset them
                .then(Commands.literal("clear")
                    .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                    .then(Commands.argument("target", EntityArgument.player())
                        .executes(context -> {
                            // this is an EXTREEMLY destructive command that wipes out the players data and sets it to zero. 
                            // So we block broad selectors so we dont accidentally remove EVERYONES data
                            // (EntityArgument.player() usually restricts to 1, but this adds extra safety)
                            Player targetPlayer = EntityArgument.getPlayer(context, "target");

                            //Save backup data before wiping!
                            Long oldMoney = targetPlayer.getData(ModAttachments.PLAYER_MONEY);
                            Long oldBread = targetPlayer.getData(ModAttachments.BREAD_COUNT);
                            BACKUP_CACHE.put(targetPlayer.getUUID(), new PlayerBackup(oldMoney, oldBread));

                            targetPlayer.setData(ModAttachments.PLAYER_MONEY, 0L);
                            targetPlayer.setData(ModAttachments.BREAD_COUNT, 0L);

                            context.getSource().sendSuccess(
                                () -> Component.literal("§cCleared data for " + targetPlayer.getName().getString() + ". (Use '/stats undo " + targetPlayer.getName().getString() + "' to revert)"),
                                false
                            );

                            targetPlayer.sendSystemMessage(
                                Component.literal("§cYour data has been cleared by a moderator of this server.")
                            );

                            return 1;
                        })
                    )
                )

                // undo command even if the mod had made a mistake
                .then(Commands.literal("undo")
                    .requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                    .then(Commands.argument("target", EntityArgument.player())
                        .executes(context -> {
                            Player targetPlayer = EntityArgument.getPlayer(context, "target");
                            UUID targetUuid = targetPlayer.getUUID();

                            // Check if a backup exists for this player
                            if (!BACKUP_CACHE.containsKey(targetUuid)) {
                                context.getSource().sendFailure(Component.literal("§cNo recent clear backup found for " + targetPlayer.getName().getString() + "!"));
                                return 0;
                            }

                            // Restore data from cache
                            PlayerBackup backup = BACKUP_CACHE.get(targetUuid);
                            targetPlayer.setData(ModAttachments.PLAYER_MONEY, backup.money());
                            targetPlayer.setData(ModAttachments.BREAD_COUNT, backup.breadCount());

                            // Clear them from cache so they can't multi-undo infinitely
                            BACKUP_CACHE.remove(targetUuid);

                            context.getSource().sendSuccess(
                                () -> Component.literal("§aSuccessfully restored stats for " + targetPlayer.getName().getString() + "! (Money: $" + backup.money() + ", Bread: " + backup.breadCount() + ")"),
                                false
                            );

                            targetPlayer.sendSystemMessage(
                                Component.literal("§aYour stats have been restored by a moderator!")
                            );

                            return 1;
                        })
                    )
                )
        );
    }

    private static void sendStatsMessage(CommandSourceStack source, Player target) {
        Long money = target.getData(ModAttachments.PLAYER_MONEY);
        Long count = target.getData(ModAttachments.BREAD_COUNT);
        
        source.sendSuccess(
            () -> Component.literal("§6[BreadMC] §e" + target.getName().getString() + " has $" + money + " and ate " + count + " bread!"),
            false
        );
    }
}