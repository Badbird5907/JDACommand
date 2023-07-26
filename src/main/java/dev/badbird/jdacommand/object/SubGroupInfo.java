package dev.badbird.jdacommand.object;

import dev.badbird.jdacommand.annotation.SubGroup;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.ArrayList;
import java.util.List;

public class SubGroupInfo extends BaseCommandInfo {
    private final SubGroup annotation;
    private final Object instance;

    public SubGroupInfo(SubGroup annotation, Object instance, CommandInfo parent) {
        this.annotation = annotation;
        this.instance = instance;
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
