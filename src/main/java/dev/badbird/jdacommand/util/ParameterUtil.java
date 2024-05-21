package dev.badbird.jdacommand.util;

import dev.badbird.jdacommand.inject.InjectorManager;
import dev.badbird.jdacommand.object.ExecutionContext;
import dev.badbird.jdacommand.inject.parameter.impl.CommandParameterWrapper;
import dev.badbird.jdacommand.object.ParameterInfo;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.lang.reflect.Parameter;

public class ParameterUtil {
    public static Object[] resolveParameters(SlashCommandInteractionEvent event, ParameterInfo[] parameters, ExecutionContext context) {
        Parameter[] params = new Parameter[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            params[i] = parameters[i].getParameter();
        }
        Object[] objects = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            try {
                ParameterInfo parameter = parameters[i];
                CommandParameterWrapper commandParameterContext = new CommandParameterWrapper(params, i, parameter, parameter.getParameter().getDeclaredAnnotations());
                objects[i] = InjectorManager.getInstance().resolveParameter(event, commandParameterContext, context);
            } catch (Exception e) {
                throw new RuntimeException("Failed to resolve parameter " + parameters[i].getParameter().getName() + " in command " + context.getCommandName() + " (class " + context.getCommandInfo().getCommandClass().getName() + ")", e);
            }
        }
        return objects;
    }
}
