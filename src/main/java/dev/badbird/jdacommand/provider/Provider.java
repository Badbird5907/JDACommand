package dev.badbird.jdacommand.provider;

import dev.badbird.jdacommand.object.ExecutionContext;
import dev.badbird.jdacommand.inject.parameter.impl.CommandParameterWrapper;
import dev.badbird.jdacommand.object.ParameterInfo;
import dev.badbird.jdacommand.object.command.impl.CommandInfo;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public interface Provider<T> {
    T provide(ExecutionContext context, CommandParameterWrapper commandParameterContext, CommandInfo commandInfo, ParameterInfo parameterInfo);

    OptionType getOptionType(ParameterInfo parameter);

    default boolean matchWithInstanceOf() {
        return false;
    }
    default boolean tryToCast() {
        return false;
    }

    default Class<?>[] getExtraTypes() {
        return null;
    }

    default void modifyOptionData(OptionData data) {

    }
}
