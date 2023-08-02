package dev.badbird.jdacommand.object.command.impl;

import dev.badbird.jdacommand.annotation.SubGroup;
import dev.badbird.jdacommand.object.command.BaseCommandInfo;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.ArrayList;
import java.util.List;

public class SubGroupInfo extends BaseCommandInfo {
    private final SubGroup annotation;

    public SubGroupInfo(SubGroup annotation, CommandInfo parent) {
        this.annotation = annotation;
        this.parent = parent;
    }

    @Override
    public String getName() {
        return annotation.name();
    }

    public SubcommandGroupData generateSubGroupData() {
        return new SubcommandGroupData(annotation.name(), annotation.description())
                .addSubcommands(generateSubCommands());
    }

    public SubcommandData[] generateSubCommands() {
        List<SubcommandData> list = new ArrayList<>();
        for (BaseCommandInfo value : subCommands.values()) {
            CommandInfo commandInfo = (CommandInfo) value;
            list.add(commandInfo.generateSubcommandData());
        }
        return list.toArray(new SubcommandData[0]);
    }
}
