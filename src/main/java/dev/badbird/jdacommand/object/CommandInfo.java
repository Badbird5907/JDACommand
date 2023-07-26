package dev.badbird.jdacommand.object;

import dev.badbird.jdacommand.annotation.Arg;
import dev.badbird.jdacommand.annotation.SlashCommand;
import lombok.Data;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CommandInfo extends BaseCommandInfo {
    private SlashCommand annotation;
    private Map<Class<? extends Annotation>, Annotation> annotations;

    private Object instance;
    private Method method;
    private List<ParameterInfo> parameters;

    public CommandInfo(SlashCommand annotation, Object object) {
        this.annotation = annotation;
        this.instance = object;
    }

    public OptionData[] generateOptions() {
        List<OptionData> list = new ArrayList<>();
        for (ParameterInfo parameter : parameters) {
            if (parameter.isArgument()) {
                Arg arg = parameter.getArgumentData();
                OptionType type = parameter.resolveType();
                if (type == null)
                    throw new IllegalArgumentException("Could not resolve type for parameter " + arg.value() + " in command " + annotation.name() + " in class " + instance.getClass().getName());
                OptionData data = new OptionData(type, arg.value(), arg.description(), arg.required());
                list.add(data);
            }
        }
        return list.toArray(new OptionData[0]);
    }
    public SlashCommandData generateCommand() {
        SlashCommandData slashCommandData = Commands.slash(annotation.name(), annotation.description());
        if (hasSubCommands()) {
            for (BaseCommandInfo value : this.subCommands.values()) {
                if (value instanceof SubGroupInfo subGroupInfo) {
                    slashCommandData = slashCommandData.addSubcommandGroups(subGroupInfo.generateSubGroupData());
                } else if (value instanceof CommandInfo commandInfo){
                    slashCommandData = slashCommandData.addSubcommands(commandInfo.generateSubcommandData());
                }
            }
        } else {
            slashCommandData = slashCommandData.addOptions(generateOptions());
        }
        return slashCommandData;
    }

    public SubcommandData generateSubcommandData() {
        return new SubcommandData(annotation.name(), annotation.description())
                .addOptions(generateOptions());
    }

    @Override
    public String getName() {
        return annotation.name();
    }
    public boolean isRootCommand() {
        return parent == null;
    }
    public boolean hasSubCommands() {
        return !subCommands.isEmpty();
    }
    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return annotations.containsKey(annotation);
    }
    public <T extends Annotation> T getAnnotation(Class<T> annotation) {
        return (T) annotations.get(annotation);
    }
}
