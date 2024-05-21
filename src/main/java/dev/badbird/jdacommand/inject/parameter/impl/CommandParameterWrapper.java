package dev.badbird.jdacommand.inject.parameter.impl;

import dev.badbird.jdacommand.annotation.Arg;
import dev.badbird.jdacommand.annotation.Required;
import dev.badbird.jdacommand.inject.parameter.ParameterWrapper;
import dev.badbird.jdacommand.object.ParameterInfo;
import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class CommandParameterWrapper implements ParameterWrapper {
    private final Parameter[] allParameters;
    private final int parameterIndex;
    private final ParameterInfo parameterInfo;
    private final String name;
    private final List<? extends Annotation> annotations;
    private final boolean required;

    public CommandParameterWrapper(Parameter[] allParameters, int parameterIndex, ParameterInfo parameterInfo, Annotation[] annotations) {
        this.allParameters = allParameters;
        this.parameterIndex = parameterIndex;
        this.parameterInfo = parameterInfo;
        this.annotations = Arrays.asList(annotations);
        this.name = parameterInfo.getParameter().getName().toLowerCase();
        this.required = parameterInfo.getParameter().isAnnotationPresent(Required.class);
        if (required && parameterInfo.getParameter().isAnnotationPresent(Arg.class) && !parameterInfo.getParameter().getAnnotation(Arg.class).required()) {
            throw new IllegalArgumentException("Parameter cannot be both required and optional");
        }
    }

    public boolean hasAnnotation(Class<? extends Annotation> annotation) {
        return parameterInfo.getParameter().isAnnotationPresent(annotation);
    }

    public String getArgName() {
        return hasAnnotation(Arg.class) ? getAnnotation(Arg.class).value().toLowerCase() : getName().toLowerCase();
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotation) {
        return parameterInfo.getParameter().getAnnotation(annotation);
    }

    public Parameter getParameter() {
        return parameterInfo.getParameter();
    }
}
