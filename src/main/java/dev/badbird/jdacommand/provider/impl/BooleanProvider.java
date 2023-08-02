package dev.badbird.jdacommand.provider.impl;

import dev.badbird.jdacommand.annotation.DefaultBoolean;
import dev.badbird.jdacommand.object.ExecutionContext;
import dev.badbird.jdacommand.object.ParameterContext;
import dev.badbird.jdacommand.object.ParameterInfo;
import dev.badbird.jdacommand.object.command.impl.CommandInfo;
import dev.badbird.jdacommand.provider.Provider;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class BooleanProvider implements Provider<Boolean> {
    @Override
    public Boolean provide(ExecutionContext context, ParameterContext parameterContext, CommandInfo commandInfo, ParameterInfo parameterInfo) {
        OptionMapping option = context.getOption(parameterContext.getArgName());
        if (option == null) {
            if (parameterContext.hasAnnotation(DefaultBoolean.class)) {
                return parameterContext.getAnnotation(DefaultBoolean.class).value();
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
