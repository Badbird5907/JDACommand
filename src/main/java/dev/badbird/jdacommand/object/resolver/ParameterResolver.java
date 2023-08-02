package dev.badbird.jdacommand.object.resolver;

import dev.badbird.jdacommand.object.ExecutionContext;
import dev.badbird.jdacommand.object.ParameterContext;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface ParameterResolver {
    Object resolve(SlashCommandInteractionEvent event, ParameterContext parameter, ExecutionContext context);
}
