package net.badbird5907.jdacommand.example;

import net.badbird5907.jdacommand.CommandResult;
import net.badbird5907.jdacommand.annotation.*;
import net.dv8tion.jda.api.entities.User;

public class TestCommand {
    @Command(name = "test", description = "Test command")
    public static CommandResult onCommand(@Sender User sender, @Arg("string") @Required String string) {
        return CommandResult.SUCCESS;
    }
}
