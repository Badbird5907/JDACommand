package dev.badbird.jdacommand.provider.impl;

import dev.badbird.jdacommand.annotation.DefaultBoolean;
import dev.badbird.jdacommand.object.ExecutionContext;
import dev.badbird.jdacommand.inject.parameter.impl.CommandParameterWrapper;
import dev.badbird.jdacommand.object.ParameterInfo;
import dev.badbird.jdacommand.object.command.impl.CommandInfo;
import dev.badbird.jdacommand.provider.Provider;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class BooleanProvider implements Provider<Boolean> {
    @Override
    public Boolean provide(ExecutionContext context, CommandParameterWrapper commandParameterContext, CommandInfo commandInfo, ParameterInfo parameterInfo) {
        OptionMapping option = context.getOption(commandParameterContext.getArgName());
        if (option == null) {
            if (commandParameterContext.hasAnnotation(DefaultBoolean.class)) {
                return commandParameterContext.getAnnotation(DefaultBoolean.class).value();
            } else {
                return false;
            }
        }
        return option.getAsBoolean();
    }

    @Override
    public OptionType getOptionType(ParameterInfo parameter) {
        return OptionType.BOOLEAN;
    }
}
