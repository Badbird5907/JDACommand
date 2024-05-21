package dev.badbird.jdacommand.inject.parameter.impl;

import dev.badbird.jdacommand.inject.parameter.ParameterWrapper;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;

@Data
public class PlainParameterWrapper implements ParameterWrapper {
    private final Parameter[] allParameters;
    private final int parameterIndex;
    private final String name;
    private final List<? extends Annotation> annotations;

    @Override
    public Parameter getParameter() {
        return allParameters[parameterIndex];
    }
}
