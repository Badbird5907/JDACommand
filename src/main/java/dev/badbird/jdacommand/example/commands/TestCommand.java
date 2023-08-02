package dev.badbird.jdacommand.example.commands;

import dev.badbird.jdacommand.annotation.Arg;
import dev.badbird.jdacommand.annotation.DeferReply;
import dev.badbird.jdacommand.annotation.SlashCommand;
import dev.badbird.jdacommand.annotation.SubGroup;
import dev.badbird.jdacommand.object.ExecutionContext;

@SlashCommand(name = "hello", description = "test command")
@DeferReply
public class TestCommand {

    @SlashCommand(name = "subcommand", description = "Test subcommand")
    public void subCommand(ExecutionContext context) {
        context.reply("Hello, World! (subcommand)");
    }

    @SlashCommand(name = "sub2", description = "Another test sub command, with args")
    public void sub2(ExecutionContext context, @Arg(value = "arg", description = "Some arg") String arg) {
        context.reply("Hello, World! (subcommand 2) " + arg);
    }

    @SubGroup(name = "subgroup", description = "Test sub group")
    public static class TestSubGroup {
        @SlashCommand(name = "ss", description = "Test subcommand")
        public void subCommand(ExecutionContext context) {
            context.reply("Hello, World! (subcommand)");
        }
    }
}
