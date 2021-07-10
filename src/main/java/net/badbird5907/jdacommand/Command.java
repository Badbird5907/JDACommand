package net.badbird5907.jdacommand;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    /**
     * The name of the command to be executed.
     * <b>Required</b>
     */
    String name();

    /**
     * Brief description of the command
     * <b>Defaults to ""</b>
     */
    String description() default "";

    /**
     * Any aliases for the command
     * <b>Defaults to {}</b>
     */
    String[] aliases() default {};

    /**
     * Permission required for command
     * <p><font color="red">
     *     <b>
     *         Only the first permission index in the array will be checked, multiple permissions will be implemented later.
     *         Also, this marks the command as server-only See: {@link Command#serverOnly()}
     *     </b>
     * </font>
     * <b>Defaults to {}</b>
     */
    Permission[] permission() default {}; //can't do Permission permission() default null, because this is a annotation

    /**
     * Bot owner can execute only.
     * See {@link JDACommand#isOwner(User)} and {@link JDACommand#addOwner(long)}
     * <b>Defaults to false</b>
     */
    boolean botOwnerOnly() default false;

    /**
     * Can only be executed by server owners.
     * <b>Defaults to false</b>
     * <p><font color="red">
     *     <b>
     *         This marks the command as server-only. See: {@link Command#serverOnly()}
     *     </b>
     *     </font></p>
     */
    boolean serverOwnerOnly() default false;

    /**
     * Can only be executed by server admins and above.
     * <b>Defaults to false</b>
     * <p><font color="red">
     * <b>
     * This marks the command as server-only See: {@link Command#serverOnly()}
     * </b>
     * </font></p>
     */
    boolean adminOnly() default false;

    /**
     * Disable this command (will not be registered)
     * <b>Defaults to false</b>
     */
    boolean disable() default false;

    /**
     * Make this command only execute when called in a private-message
     */
    boolean dmsOnly() default false;

    /**
     * Make this command only execute when called in a server
     */
    boolean serverOnly() default false;

    /**
     * The type of command this is. Purely for documentation and for help commands, no actual affect on the bot.
     */
    CommandType commandType() default CommandType.UTIL;
}
