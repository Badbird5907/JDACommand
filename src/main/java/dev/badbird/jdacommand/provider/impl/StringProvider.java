package dev.badbird.jdacommand.provider.impl;

import dev.badbird.jdacommand.object.CommandInfo;
import dev.badbird.jdacommand.object.ExecutionContext;
import dev.badbird.jdacommand.object.ParameterContext;
import dev.badbird.jdacommand.object.ParameterInfo;
import dev.badbird.jdacommand.provider.Provider;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class StringProvider implements Provider<String> {
    @Override
    public String provide(ExecutionContext context, ParameterContext parameterContext, CommandInfo commandInfo, ParameterInfo parameterInfo) {
        return null;
    }

    @Override
    public OptionType getOptionType() {
        return OptionType.STRING;
    }

    @Override
    public boolean matchWithInstanceOf() {
        return true;
    }
}
