package dev.badbird.jdacommand.command;

import dev.badbird.jdacommand.object.CommandType;
import net.dv8tion.jda.api.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String name();

    String[] aliases() default {};

    String description() default "";

    Permission[] permissions() default {};

    CommandType type() default CommandType.SERVER;
}
