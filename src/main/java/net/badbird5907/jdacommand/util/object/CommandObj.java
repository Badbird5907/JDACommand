package net.badbird5907.jdacommand.util.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.badbird5907.jdacommand.annotation.Arg;
import net.badbird5907.jdacommand.annotation.Command;

@Getter
@Setter
@RequiredArgsConstructor
public class CommandObj {
    private final String name;
    private final Command commandAnnotation;
    private final String[] aliases;
    private final Object instance;

}
