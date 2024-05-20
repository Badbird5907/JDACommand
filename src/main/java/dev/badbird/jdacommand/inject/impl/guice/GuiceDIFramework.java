package dev.badbird.jdacommand.inject.impl.guice;

import dev.badbird.jdacommand.inject.ClassInjectionHandler;
import dev.badbird.jdacommand.inject.DIFramework;
import dev.badbird.jdacommand.inject.ParameterInjector;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GuiceDIFramework implements DIFramework {
    private com.google.inject.Injector injector;

    @Override
    public ParameterInjector getParameterInjector() {
        return new GuiceParameterInjector(this);
    }

    @Override
    public ClassInjectionHandler getClassInjectionHandler() {
        return new GuiceParameterInjector(this);
    }
}
