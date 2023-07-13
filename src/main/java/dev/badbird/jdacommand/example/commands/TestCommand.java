package dev.badbird.jdacommand.example.commands;

import dev.badbird.jdacommand.annotation.Arg;
import dev.badbird.jdacommand.annotation.SlashCommand;
import dev.badbird.jdacommand.object.ExecutionContext;

@SlashCommand(name = "test", description = "test command")
public class TestCommand {
    @SlashCommand(name = "")
    public void mainCommand(ExecutionContext context) {
        context.reply("Hello, World!");
    }

    @SlashCommand(name = "subcommand", description = "Test subcommand")
    public void subCommand(ExecutionContext context) {
        context.reply("Hello, World! (subcommand)");
    }

    @SlashCommand(name = "sub2", description = "Another test sub command, with args")
    public void sub2(ExecutionContext context, @Arg("arg") String arg) {
        context.reply("Hello, World! (subcommand 2) " + arg);
    }
}
