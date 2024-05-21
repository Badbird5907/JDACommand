package dev.badbird.jdacommand.inject;

import dev.badbird.jdacommand.inject.parameter.ParameterWrapper;
import dev.badbird.jdacommand.object.ExecutionContext;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface ParameterInjector {
    Object resolve(SlashCommandInteractionEvent event, ParameterWrapper parameter, ExecutionContext context);

    Object resolvePlain(ParameterWrapper parameter, ExecutionContext context);

    boolean supports(ParameterWrapper parameter);
}
