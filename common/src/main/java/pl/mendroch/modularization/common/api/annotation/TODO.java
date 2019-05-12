package pl.mendroch.modularization.common.api.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;

@Target({TYPE, METHOD, MODULE})
@Retention(SOURCE)
public @interface TODO {
    String value() default "";
}