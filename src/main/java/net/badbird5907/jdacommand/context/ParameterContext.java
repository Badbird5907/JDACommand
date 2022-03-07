package net.badbird5907.jdacommand.context;

import lombok.Getter;
import net.badbird5907.jdacommand.annotation.Arg;
import net.badbird5907.jdacommand.annotation.Required;
import net.badbird5907.jdacommand.annotation.Sender;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;

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
    }

    public boolean hasAnnotation(Class<? extends Annotation> annotation) {
        return parameter.isAnnotationPresent(annotation);
    }

    public boolean isSender() {
        return (getParameterIndex() == 0 || hasAnnotation(Sender.class)) && !hasAnnotation(Arg.class);
    }
}
