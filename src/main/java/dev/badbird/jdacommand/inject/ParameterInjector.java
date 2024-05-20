package dev.badbird.jdacommand.inject;

import dev.badbird.jdacommand.object.ExecutionContext;
import dev.badbird.jdacommand.object.ParameterContext;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface ParameterInjector {
    Object resolve(SlashCommandInteractionEvent event, ParameterContext parameter, ExecutionContext context);

    boolean supports(ParameterContext parameter);
}
