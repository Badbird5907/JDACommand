package dev.badbird.jdacommand.annotation;

import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@AutoInstantiate
public @interface SlashCommand {
    String name();
    String description() default "No description provided";
    boolean guildOnly() default true;
}
