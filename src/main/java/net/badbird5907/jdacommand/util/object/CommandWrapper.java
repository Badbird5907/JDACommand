package net.badbird5907.jdacommand.util.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.badbird5907.jdacommand.annotation.Command;
import net.badbird5907.jdacommand.context.ParameterContext;
import net.badbird5907.jdacommand.provider.Provider;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class CommandWrapper {
    private final Command command;
    private final String name;
    private final Method method;
    private final Object object;

    private final Parameter[] params;
    private final List<Pair<ParameterContext, Provider<?>>> parameters;
}
