package dev.badbird.jdacommand.inject.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.List;

public interface ParameterWrapper {
    Parameter[] getAllParameters();

    Parameter getParameter();

    int getParameterIndex();

    String getName();

    List<? extends Annotation> getAnnotations();
}
