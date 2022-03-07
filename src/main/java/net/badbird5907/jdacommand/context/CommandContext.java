package net.badbird5907.jdacommand.context;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.badbird5907.jdacommand.annotation.Command;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

@Getter
@RequiredArgsConstructor
public class CommandContext {
    private final Member member;
    private final SlashCommandEvent event;

    public OptionMapping getOption(String name) {
        return event.getOption(name);
    }
    public Object getOrDefault(String name, Object def) {
        return hasOption(name) ? getOption(name) : def;
    }
    public boolean hasOption(String name) {
        return event.getOption(name) != null;
    }
}
