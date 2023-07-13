package dev.badbird.jdacommand.object;

import dev.badbird.jdacommand.annotation.SlashCommand;
import lombok.Data;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Data
public class CommandInfo {
    private SlashCommand annotation;
    private Map<Class<? extends Annotation>, Annotation> annotations;
    private Map<String, CommandInfo> subCommands = new HashMap<>();
    private CommandInfo parent;

    private Object instance;
    private Method method;

    public CommandInfo(SlashCommand annotation, Object object) {
        this.annotation = annotation;
        this.instance = object;
    }

    public SlashCommandData addOptions(SlashCommandData data) {
        return data;
    }
    public SlashCommandData generateCommand() {
        return addOptions(Commands.slash(annotation.name(), annotation.description()));
    }

    public String getName() {
        return annotation.name();
    }
    public boolean isRootCommand() {
        return parent == null;
    }
}
