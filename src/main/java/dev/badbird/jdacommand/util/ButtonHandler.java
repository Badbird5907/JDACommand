package dev.badbird.jdacommand.util;

import dev.badbird.jdacommand.inject.InjectorManager;
import dev.badbird.jdacommand.inject.parameter.ParameterWrapper;
import dev.badbird.jdacommand.inject.parameter.impl.PlainParameterWrapper;
import dev.badbird.jdacommand.object.ButtonExecutionContext;
import dev.badbird.jdacommand.object.ExecutionContext;
import dev.badbird.jdacommand.session.ExecutionSession;
import dev.badbird.jdacommand.session.ExecutionSessionHandler;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ButtonHandler {
    public static ButtonHandler INSTANCE = new ButtonHandler();

    public void handleButton(ButtonInteractionEvent event) {
        String[] split = event.getButton().getId().split(":");
        if (split.length != 3) {
            return;
        }
        String executionId = split[1];
        String buttonId = split[2];
        ExecutionSession session = ExecutionSessionHandler.INSTANCE.getSession(executionId);
        if (session == null) { // session expired
            return;
        }
        session.renew();
        Map<String, Method> buttons = session.getContext().getButtons();
        Method method = buttons.get(buttonId);
        if (method == null) {
            return;
        }
        List<Object> args = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        ButtonExecutionContext newCtx = new ButtonExecutionContext(session.getContext(), event);
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.getType().equals(ButtonInteractionEvent.class)) {
                args.add(event);
            } else if (parameter.getType().equals(ExecutionContext.class) || parameter.getType().equals(ButtonExecutionContext.class)) {
                args.add(newCtx);
            } else {
                ParameterWrapper parameterWrapper = new PlainParameterWrapper(parameters, i, parameter.getName(), Arrays.asList(parameter.getAnnotations()));
                Object o = InjectorManager.getInstance().resolvePlainParameter(parameterWrapper, session.getContext());
                args.add(o);
            }
        }
        try {
            method.invoke(session.getInstance(), args.toArray(new Object[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
