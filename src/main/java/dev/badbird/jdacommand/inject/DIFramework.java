package dev.badbird.jdacommand.inject;

public interface DIFramework {
    ParameterInjector getParameterInjector();
    ClassInjectionHandler getClassInjectionHandler();
}
