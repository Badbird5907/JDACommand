package dev.badbird.jdacommand.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@AutoInstantiate
public @interface SubGroup {
    String name();
    String description() default "";
    String parent() default "";
    boolean inheritAnnotations() default true;
}
