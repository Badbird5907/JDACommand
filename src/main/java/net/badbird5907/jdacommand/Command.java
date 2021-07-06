package net.badbird5907.jdacommand;

import net.dv8tion.jda.api.Permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String name();
    String description() default "";
    String[] aliases() default {};
    Permission[] permission() default {}; //can't do Permission permission() default null, because this is a annotation
    boolean botOwnerOnly() default false;
    boolean serverOwnerOnly() default false;
    boolean adminOnly() default false;
    boolean disable() default false;
    boolean dmsOnly() default false;
    boolean serverOnly() default false;
    CommandType commandType() default CommandType.UTIL;
}
