package dev.badbird.jdacommand.object;

import dev.badbird.jdacommand.JDACommand;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

@RequiredArgsConstructor
public class CommandListener extends ListenerAdapter {
    private final JDACommand jdaCommand;
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        System.out.println("Received slash command interaction " + event.getCommandString());
        boolean hasSubCommand = event.getSubcommandName() != null;
        event.reply("hi").queue();
    }
}
