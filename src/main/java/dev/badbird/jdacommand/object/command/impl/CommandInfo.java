package dev.badbird.jdacommand.object.command.impl;

import dev.badbird.jdacommand.annotation.DeferReply;
import dev.badbird.jdacommand.annotation.SlashCommand;
import dev.badbird.jdacommand.object.ExecutionContext;
import dev.badbird.jdacommand.object.ParameterInfo;
import dev.badbird.jdacommand.object.command.BaseCommandInfo;
import dev.badbird.jdacommand.object.command.ExecutableCommand;
import dev.badbird.jdacommand.util.ParameterUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CommandInfo extends BaseCommandInfo implements ExecutableCommand {
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
                list.add(parameter.generateOption());
            }
        }
        return list.toArray(new OptionData[0]);
    }

    public SlashCommandData generateCommand() {
        SlashCommandData slashCommandData = Commands.slash(annotation.name(), annotation.description());
        if (hasSubCommands()) {
            for (BaseCommandInfo value : this.subCommands.values()) {
                if (value instanceof SubGroupInfo) {
                    SubGroupInfo subGroupInfo = (SubGroupInfo) value;
                    slashCommandData = slashCommandData.addSubcommandGroups(subGroupInfo.generateSubGroupData());
                } else if (value instanceof CommandInfo) {
                    CommandInfo commandInfo = (CommandInfo) value;
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

    public Class<?> getCommandClass() {
        return method.getDeclaringClass();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        ExecutionContext context = new ExecutionContext(event, this);
        if (isAnnotationPresent(DeferReply.class)) {
            if (getAnnotation(DeferReply.class).value()) {
                System.out.println("Deferring reply");
                event.deferReply().queue();
            }
        }
        Object[] args = ParameterUtil.resolveParameters(event, parameters.toArray(new ParameterInfo[0]), context);
        try {
            method.invoke(instance, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
