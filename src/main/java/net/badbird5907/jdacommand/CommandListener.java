package net.badbird5907.jdacommand;

import net.badbird5907.jdacommand.annotation.Command;
import net.badbird5907.jdacommand.util.object.CommandWrapper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class CommandListener extends ListenerAdapter {
    public CommandListener() {
        super();
        System.out.println("CommandListener loaded");
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent e) {
        try {
            String command = e.getName();
            System.out.println("Command: " + e.getName());
            JDACommand.getCommandMap().forEach((name, pair) -> {
                if (e.getName().equalsIgnoreCase(name)) { //TODO custom error messages
                    Command c = pair.getCommand();
                    if (c.disable())
                        return;
                    if (c.botOwnerOnly()) {
                        if (!JDACommand.getInstance().isOwner(e.getUser())) {
                            return;
                        }
                    }
                    if (c.serverOwnerOnly())
                        if (e.getChannelType() == ChannelType.TEXT && !e.getMember().isOwner())
                            return;
                    if (c.dmsOnly())
                        if (e.getChannelType() != ChannelType.PRIVATE)
                            return;
                    if (c.serverOnly())
                        if (e.getChannelType() != ChannelType.TEXT)
                            return;
                    if (c.adminOnly())
                        if (e.getChannelType() == ChannelType.TEXT && !e.getMember().getPermissions().contains(Permission.ADMINISTRATOR))
                            return;
                    if (c.permission().length != 0) {
                        if (e.getChannelType() == ChannelType.TEXT) {
                            Permission[] permissions = c.permission();
                            EnumSet<Permission> set = e.getMember().getPermissions();
                            for (Permission permission : permissions) {
                                if (!set.contains(permission)) {
                                    return;
                                }
                            }
                        } else return;
                    }
                    CommandWrapper wrapper = JDACommand.getCommandMap().get(command.toLowerCase());
                    e.deferReply().queue();
                    CommandManager.process(pair.getMethod(), e, wrapper.getObject(), c);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
