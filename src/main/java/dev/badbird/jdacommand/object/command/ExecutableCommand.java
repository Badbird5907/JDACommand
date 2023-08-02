package dev.badbird.jdacommand.object.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface ExecutableCommand {
    void execute(SlashCommandInteractionEvent event);
}
