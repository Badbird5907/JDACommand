package net.badbird5907.jdacommand;

import lombok.Getter;

@Getter
public enum CommandResult {
    ERROR,
    SUCCESS,
    NO_PERMS,
    OTHER;
}
