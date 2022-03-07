package net.badbird5907.jdacommand.provider.impl;

import net.badbird5907.jdacommand.context.CommandContext;
import net.badbird5907.jdacommand.context.ParameterContext;
import net.badbird5907.jdacommand.provider.Provider;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class LongContextProvider implements Provider<Long> {
    @Override
    public Long provide(CommandContext context, ParameterContext pContext) throws Exception {
        Object o = context.getOrDefault(pContext.getName(), false);
        return o instanceof Long ? (Long) o : (int) ((OptionMapping) o).getAsLong();
    }

    @Override
    public OptionData getOptionData(ParameterContext paramContext) {
        return new OptionData(OptionType.INTEGER, paramContext.getName(), "Long", paramContext.isRequired());
    }

    @Override
    public Class<?> getType() {
        return Long.class;
    }

    @Override
    public Class<?>[] getExtraTypes() {
        return new Class[]{long.class};
    }
}
