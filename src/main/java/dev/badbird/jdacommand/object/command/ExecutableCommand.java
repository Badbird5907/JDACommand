package dev.badbird.jdacommand.object.command;

import dev.badbird.jdacommand.JDACommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface ExecutableCommand {
    void execute(SlashCommandInteractionEvent event, JDACommand jdaCommand);
}
