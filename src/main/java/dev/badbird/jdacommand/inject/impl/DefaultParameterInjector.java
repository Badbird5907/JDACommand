package dev.badbird.jdacommand.inject.impl;

import dev.badbird.jdacommand.inject.ParameterInjector;
import dev.badbird.jdacommand.object.ExecutionContext;
import dev.badbird.jdacommand.object.ParameterContext;
import dev.badbird.jdacommand.provider.Provider;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class DefaultParameterInjector implements ParameterInjector {
    public static final DefaultParameterInjector INSTANCE = new DefaultParameterInjector();

    @Override
    public Object resolve(SlashCommandInteractionEvent event, ParameterContext parameter, ExecutionContext context) {
        Provider<?> provider = parameter.getParameterInfo().getProvider();
        if (provider == null) {
            throw new IllegalArgumentException("No provider found for parameter " + parameter.getParameter().getName() + " in command " + context.getCommandName() + " (class " + context.getCommandInfo().getCommandClass().getName() + ")");
        }
        Object obj = provider.provide(context, parameter, context.getCommandInfo(), parameter.getParameterInfo());
        Class<?> expectedType = parameter.getParameter().getType();
        // check if the type is correct
        if (expectedType != obj.getClass() && provider.tryToCast()) {
            return obj.getClass().cast(obj);
        }
        return obj;
    }

    @Override
    public boolean supports(ParameterContext parameter) {
        return parameter.getParameterInfo().getProvider() != null;
    }
}
