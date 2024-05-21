package dev.badbird.jdacommand.annotation;

import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({
        java.lang.annotation.ElementType.METHOD
})
public @interface CommandButton {
    String id();

    String label();
    String emoji() default "";

    ButtonStyle style() default ButtonStyle.PRIMARY;
}
