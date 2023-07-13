package dev.badbird.jdacommand.object;

import dev.badbird.jdacommand.annotation.Arg;
import dev.badbird.jdacommand.annotation.Optional;
import dev.badbird.jdacommand.annotation.Required;
import lombok.Getter;
import lombok.Setter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
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
        this.name = parameter.getName().toLowerCase();
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

}
