package net.badbird5907.jdacommand.provider;

import net.badbird5907.jdacommand.context.CommandContext;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.lang.annotation.Annotation;
import java.util.List;

public interface Provider<T> {
    T provide(CommandContext context, List<? extends Annotation> annotations);

    OptionType getOptionType();
}
