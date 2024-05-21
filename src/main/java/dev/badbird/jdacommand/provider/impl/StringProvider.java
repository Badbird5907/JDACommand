package dev.badbird.jdacommand.provider.impl;

import dev.badbird.jdacommand.object.command.impl.CommandInfo;
import dev.badbird.jdacommand.object.ExecutionContext;
import dev.badbird.jdacommand.inject.parameter.impl.CommandParameterWrapper;
import dev.badbird.jdacommand.object.ParameterInfo;
import dev.badbird.jdacommand.provider.Provider;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class StringProvider implements Provider<String> {
    @Override
    public String provide(ExecutionContext context, CommandParameterWrapper commandParameterContext, CommandInfo commandInfo, ParameterInfo parameterInfo) {
        OptionMapping option = context.getEvent().getOption(commandParameterContext.getArgName());
        if (option == null)
            return null;
        return option.getAsString();
    }

    @Override
    public OptionType getOptionType(ParameterInfo parameter) {
        return OptionType.STRING;
    }

    @Override
    public boolean matchWithInstanceOf() {
        return true;
    }
}
