package net.badbird5907.jdacommand.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class CommandPreProcessEvent {
    private final String command;
    private final String[] args;
    @Setter
    @Getter
    private boolean cancelled = false;
}
