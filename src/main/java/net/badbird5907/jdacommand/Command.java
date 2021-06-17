package net.badbird5907.jdacommand;

import net.dv8tion.jda.api.Permission;

public @interface Command {
    String name();
    String description() default "";
    String[] aliases() default {};
    Permission[] permission() default {}; //can't do Permission permission() default null, because this is a annotation
    boolean ownerOnly() default false;
    boolean adminOnly() default false;
    boolean disable() default false;
}
