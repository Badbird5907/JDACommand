package dev.badbird.jdacommand.object.command;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public abstract class BaseCommandInfo {
    protected Map<String, BaseCommandInfo> subCommands = new HashMap<>();
    protected BaseCommandInfo parent;

    public abstract String getName();
}
