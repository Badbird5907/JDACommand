package dev.badbird.jdacommand;

import dev.badbird.jdacommand.annotation.DistributeOnMethods;
import dev.badbird.jdacommand.annotation.SlashCommand;
import dev.badbird.jdacommand.object.CommandInfo;
import dev.badbird.jdacommand.object.CommandListener;
import dev.badbird.jdacommand.object.JDACommandSettings;
import lombok.Getter;
import net.dv8tion.jda.api.JDA;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JDACommand {
    @Getter
    private final JDA jda;
    private Map<String, CommandInfo> commandMap = new ConcurrentHashMap<>();
    public JDACommand(JDACommandSettings settings) {
        jda = settings.getJda();
        jda.addEventListener(new CommandListener(this));
        if (settings.isWaitForJda()) {
            try {
                jda.awaitReady();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void registerCmd(Object object) {
        if (object == null) return;
        List<Annotation> distributedAnnotations = new ArrayList<>();
        for (Annotation annotation : object.getClass().getDeclaredAnnotations()) {
            if (annotation.getClass().isAnnotationPresent(DistributeOnMethods.class)) {
                distributedAnnotations.add(annotation);
            }
        }
        boolean classHasMainCommand = object.getClass().isAnnotationPresent(SlashCommand.class);
        SlashCommand parent = null;
        CommandInfo parentInfo;
        if (classHasMainCommand) {
            parent = object.getClass().getAnnotation(SlashCommand.class);
            Map<Class<? extends Annotation>, Annotation> annotations = new HashMap<>();
            for (Annotation declaredAnnotation : object.getClass().getDeclaredAnnotations()) {
                annotations.put(declaredAnnotation.annotationType(), declaredAnnotation);
            }
            for (Annotation distributedAnnotation : distributedAnnotations) {
                annotations.put(distributedAnnotation.annotationType(), distributedAnnotation);
            }
            parentInfo = commandMap.get(parent.name().toLowerCase());
            if (parentInfo == null) {
                parentInfo = new CommandInfo(parent, object);
            }
            parentInfo.setAnnotations(annotations);
        }
    }
}
