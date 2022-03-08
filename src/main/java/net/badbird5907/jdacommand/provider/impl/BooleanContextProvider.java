package net.badbird5907.jdacommand.provider.impl;

import net.badbird5907.jdacommand.annotation.Arg;
import net.badbird5907.jdacommand.context.CommandContext;
import net.badbird5907.jdacommand.context.ParameterContext;
import net.badbird5907.jdacommand.provider.Provider;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class BooleanContextProvider implements Provider<Boolean> {
    @Override
    public Boolean provide(CommandContext context, ParameterContext pContext) throws Exception {
        Object o = context.getOrDefault(pContext.getName(), false);
        return o instanceof Boolean ? (Boolean) o : ((OptionMapping) o).getAsBoolean();
    }

    @Override
    public OptionData getOptionData(ParameterContext paramContext) {
        return new OptionData(OptionType.BOOLEAN, paramContext.getArgName(), "Boolean", paramContext.isRequired());
    }

    @Override
    public Class<?> getType() {
        return Boolean.class;
    }

    @Override
    public Class<?>[] getExtraTypes() {
        return new Class[]{boolean.class};
    }
}
