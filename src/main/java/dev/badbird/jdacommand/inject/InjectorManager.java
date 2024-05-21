package dev.badbird.jdacommand.inject;

import dev.badbird.jdacommand.annotation.Arg;
import dev.badbird.jdacommand.inject.impl.DefaultParameterInjector;
import dev.badbird.jdacommand.inject.parameter.ParameterWrapper;
import dev.badbird.jdacommand.object.ExecutionContext;
import dev.badbird.jdacommand.inject.parameter.impl.CommandParameterWrapper;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class InjectorManager {
    @Getter
    private static final InjectorManager instance = new InjectorManager();

    private List<ParameterInjector> parameterInjectors = new ArrayList<>(Collections.singletonList(DefaultParameterInjector.INSTANCE));
    private List<ClassInjectionHandler> classInjectionHandlers = new ArrayList<>();

    public void registerParameterInjector(ParameterInjector parameterInjector) {
        parameterInjectors.add(0, parameterInjector); // highest priority
    }

    public void registerClassInjectionHandler(ClassInjectionHandler classInjectionHandler) {
        classInjectionHandlers.add(0, classInjectionHandler); // highest priority
    }

    public void handleDIFramework(DIFramework framework) {
        ParameterInjector parameterInjector = framework.getParameterInjector();
        if (parameterInjector != null)
            registerParameterInjector(parameterInjector);
        ClassInjectionHandler classInjectionHandler = framework.getClassInjectionHandler();
        if (classInjectionHandler != null)
            registerClassInjectionHandler(classInjectionHandler);
    }


    public Object resolveParameter(SlashCommandInteractionEvent event, CommandParameterWrapper parameter, ExecutionContext context) {
        if (parameter.hasAnnotation(Arg.class)) { // parameters annotated with @Arg are always resolved by the default parameter injector
            return DefaultParameterInjector.INSTANCE.resolve(event, parameter, context);
        }
        for (ParameterInjector parameterInjector : parameterInjectors) {
            if (parameterInjector.supports(parameter)) {
                Object o = parameterInjector.resolve(event, parameter, context);
                if (o != null) {
                    return o;
                }
            }
        }
        return null;
    }

    public Object resolvePlainParameter(ParameterWrapper parameter, ExecutionContext context) {
         for (ParameterInjector parameterInjector : parameterInjectors) {
            if (parameterInjector.supports(parameter)) {
                Object o = parameterInjector.resolvePlain(parameter, context);
                if (o != null) {
                    return o;
                }
            }
        }
        return null;
    }

    public void injectInstance(Object inst) {
        for (ClassInjectionHandler classInjectionHandler : classInjectionHandlers) {
            classInjectionHandler.inject(inst);
        }
    }
}
