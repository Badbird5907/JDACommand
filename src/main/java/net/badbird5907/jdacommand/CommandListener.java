package net.badbird5907.jdacommand;

import net.badbird5907.jdacommand.annotation.Command;
import net.badbird5907.jdacommand.util.object.CommandWrapper;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class CommandListener extends ListenerAdapter {
    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent e) {
        String command = e.getCommandId();
        JDACommand.getCommandMap().forEach((name, pair) -> {
            if (command.equalsIgnoreCase(name)) { //TODO custom error messages
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
                        if (!e.getMember().getPermissions().contains(permissions[0]))
                            return;
                    } else return;
                }
                CommandWrapper wrapper = JDACommand.getCommandMap().get(command.toLowerCase());
                CommandManager.process(pair.getMethod(), e, wrapper.getObject(), c);
            }
        });
    }

}
