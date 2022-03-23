package net.badbird5907.jdacommand.context;

import lombok.Getter;
import net.badbird5907.jdacommand.annotation.Arg;
import net.badbird5907.jdacommand.annotation.Optional;
import net.badbird5907.jdacommand.annotation.Required;
import net.badbird5907.jdacommand.annotation.Sender;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Getter
public class ParameterContext {
    private final Parameter[] allParameters;
    private final int parameterIndex;
    private final Parameter parameter;
    private final String name;
    private final List<? extends Annotation> annotations;
    private final boolean required;

    public ParameterContext(Parameter[] allParameters, int parameterIndex, Parameter parameter, Annotation[] annotations) {
        this.allParameters = allParameters;
        this.parameterIndex = parameterIndex;
        this.parameter = parameter;
        this.annotations = Arrays.asList(annotations);
        this.name = parameter.getName();
        this.required = parameter.isAnnotationPresent(Required.class);
        if (required && parameter.isAnnotationPresent(Optional.class)) {
            throw new IllegalArgumentException("Parameter cannot be both required and optional");
        }
    }

    public boolean hasAnnotation(Class<? extends Annotation> annotation) {
        return parameter.isAnnotationPresent(annotation);
    }

    public String getArgName() {
        return hasAnnotation(Arg.class) ? getAnnotation(Arg.class).value().toLowerCase() : getName().toLowerCase();
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotation) {
        return parameter.getAnnotation(annotation);
    }

    public boolean isSender() {
        return (getParameterIndex() == 0 || hasAnnotation(Sender.class)) && !hasAnnotation(Arg.class);
    }
}
