package dev.badbird.jdacommand.provider;

import dev.badbird.jdacommand.object.CommandInfo;
import dev.badbird.jdacommand.object.ExecutionContext;
import dev.badbird.jdacommand.object.ParameterContext;
import dev.badbird.jdacommand.object.ParameterInfo;

public interface Provider<T> {
    T provide(ExecutionContext context, ParameterContext parameterContext, CommandInfo commandInfo, ParameterInfo parameterInfo);
}
