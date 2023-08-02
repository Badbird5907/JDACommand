package dev.badbird.jdacommand.util;

import dev.badbird.jdacommand.object.ExecutionContext;
import dev.badbird.jdacommand.object.ParameterContext;
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
                ParameterContext parameterContext = new ParameterContext(params, i, parameter, parameter.getParameter().getDeclaredAnnotations());
                objects[i] = parameter.getResolver().resolve(event, parameterContext, context);
            } catch (Exception e) {
                throw new RuntimeException("Failed to resolve parameter " + parameters[i].getParameter().getName() + " in command " + context.getCommandName() + " (class " + context.getCommandInfo().getCommandClass().getName() + ")", e);
            }
        }
        return objects;
    }
}
