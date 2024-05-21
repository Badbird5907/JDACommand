package dev.badbird.jdacommand.inject.impl;

import dev.badbird.jdacommand.inject.ParameterInjector;
import dev.badbird.jdacommand.inject.parameter.ParameterWrapper;
import dev.badbird.jdacommand.object.ExecutionContext;
import dev.badbird.jdacommand.inject.parameter.impl.CommandParameterWrapper;
import dev.badbird.jdacommand.provider.Provider;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class DefaultParameterInjector implements ParameterInjector {
    public static final DefaultParameterInjector INSTANCE = new DefaultParameterInjector();

    @Override
    public Object resolve(SlashCommandInteractionEvent event, ParameterWrapper param, ExecutionContext context) {
        if (!(param instanceof CommandParameterWrapper)) {
            throw new IllegalArgumentException("Parameter is not a CommandParameterWrapper");
        }
        CommandParameterWrapper parameter = (CommandParameterWrapper) param;
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
    public Object resolvePlain(ParameterWrapper parameter, ExecutionContext context) {
        return null; // providers don't support plain injection
    }

    @Override
    public boolean supports(ParameterWrapper parameter) {
        if (parameter instanceof CommandParameterWrapper param) {
            return param.getParameterInfo().getProvider() != null;
        }
        return false;
    }
}
