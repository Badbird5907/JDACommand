package net.badbird5907.jdacommand.provider;

import net.badbird5907.jdacommand.context.CommandContext;
import net.badbird5907.jdacommand.context.ParameterContext;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.lang.annotation.Annotation;
import java.util.List;

public interface Provider<T> {
    T provide(CommandContext context, ParameterContext pContext) throws Exception;

    /**
     * @return The {@link OptionType} of this provider.
     * @param paramContext
     * @return nullable {@link OptionType}. If it is null, this isn't considered a command arg.
     */
    OptionData getOptionData(ParameterContext paramContext);

    Class<?> getType();

    default boolean failOnException() {
        return true;
    }
}
