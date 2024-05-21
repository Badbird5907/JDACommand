package dev.badbird.jdacommand.inject.impl.guice;

import com.google.inject.Binding;
import dev.badbird.jdacommand.inject.ClassInjectionHandler;
import dev.badbird.jdacommand.inject.ParameterInjector;
import dev.badbird.jdacommand.inject.parameter.ParameterWrapper;
import dev.badbird.jdacommand.object.ExecutionContext;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@RequiredArgsConstructor
public class GuiceParameterInjector implements ParameterInjector, ClassInjectionHandler {
    private final GuiceDIFramework framework;

    @Override
    public void inject(Object inst) {
        framework.getInjector().injectMembers(inst);
    }

    @Override
    public Object resolve(SlashCommandInteractionEvent event, ParameterWrapper parameter, ExecutionContext context) {
        return resolvePlain(parameter, context);
    }

    @Override
    public Object resolvePlain(ParameterWrapper parameter, ExecutionContext context) {
        return framework.getInjector().getInstance(parameter.getParameter().getType());
    }

    @Override
    public boolean supports(ParameterWrapper parameter) {
        try {
            Binding<?> binding = framework.getInjector().getBinding(parameter.getParameter().getType());
            return binding != null;
        } catch (Exception e) {
            return false;
        }
    }
}
