package dev.badbird.jdacommand.object;

import dev.badbird.jdacommand.JDACommand;
import dev.badbird.jdacommand.object.command.BaseCommandInfo;
import dev.badbird.jdacommand.object.command.ExecutableCommand;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class CommandListener extends ListenerAdapter {
    private final JDACommand jdaCommand;
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // event.deferReply().queue();
        boolean hasSubCommand = event.getSubcommandName() != null;
        boolean hasSubGroup = event.getSubcommandGroup() != null;
        List<String> searchList = new ArrayList<>();
        searchList.add(event.getName());
        if (hasSubGroup)
            searchList.add(event.getSubcommandGroup());
        if (hasSubCommand)
            searchList.add(event.getSubcommandName());
        BaseCommandInfo commandInfo = jdaCommand.resolveCommand(searchList.toArray(new String[0]));
        if (!(commandInfo instanceof ExecutableCommand)) {
            event.reply("Command resolved but not executable! This shouldn't happen!").setEphemeral(true).queue(); // TODO error log
            return;
        }
        ExecutableCommand command = (ExecutableCommand) commandInfo;
        command.execute(event, jdaCommand);
        /*
        event.reply("Command string: " +  event.getCommandString() + "\n"
                + "Subcommand name: " + event.getSubcommandName() + "\n"
                + "Subcommand group: " + event.getSubcommandGroup() + "\n"
                + "Command: " + event.getName() + "\n"
                + "Has subcommand: " + hasSubCommand + "\n"
                + "Has subcommand group: " + hasSubGroup + "\n"
                + "Options: " + event.getOptions().size() + "\n"
                + "Search list: " + searchList + "\n"
                + "Command info: " + (commandInfo == null ? "null" : commandInfo.getName()) + "\n"
                + "Command info parent: " + (commandInfo == null ? "null" : commandInfo.getParent()) + "\n"
        ).queue();
         */
    }

    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        super.onGuildJoin(event);
        if (jdaCommand.getSettings().isCommitOnJoin()) {
            jdaCommand.commitGuildCommands(event.getGuild());
        }
    }
}
