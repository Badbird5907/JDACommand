package net.badbird5907.jdacommand.provider.impl;

import net.badbird5907.jdacommand.context.CommandContext;
import net.badbird5907.jdacommand.context.ParameterContext;
import net.badbird5907.jdacommand.provider.Provider;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class StringContextProvider implements Provider<String> {
    @Override
    public String provide(CommandContext context, ParameterContext pContext) {
        if (pContext.isRequired())
            return context.getOption(pContext.getName()).getAsString();
        else return context.hasOption(pContext.getName()) ? context.getOption(pContext.getName()).getAsString() : null;
    }

    @Override
    public OptionData getOptionData(ParameterContext paramContext) {
        return new OptionData(OptionType.STRING, paramContext.getName(), "String", paramContext.isRequired());
    }

    @Override
    public Class<?> getType() {
        return String.class;
    }
}
