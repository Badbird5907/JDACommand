package net.badbird5907.jdacommand.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.badbird5907.jdacommand.context.CommandContext;
import net.badbird5907.lightning.event.Cancellable;
import net.badbird5907.lightning.event.Event;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@Getter
@RequiredArgsConstructor
public class CommandPreProcessEvent implements Event, Cancellable {
    private final String command;
    private final SlashCommandInteractionEvent event;
    private final CommandContext context;
    @Setter
    @Getter
    private boolean cancelled = false;
}
