package net.badbird5907.jdacommand.provider;

import net.badbird5907.jdacommand.context.CommandContext;
import net.badbird5907.jdacommand.context.ParameterContext;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public interface Provider<T> {
    T provide(CommandContext context, ParameterContext pContext) throws Exception;

    /**
     * @param paramContext
     * @return The {@link OptionType} of this provider.
     * @return nullable {@link OptionType}. If it is null, this isn't considered a command arg.
     */
    OptionData getOptionData(ParameterContext paramContext);

    Class<?> getType();

    default Class<?>[] getExtraTypes() {
        return new Class[]{};
    }

    default boolean failOnException() {
        return true;
    }
}
