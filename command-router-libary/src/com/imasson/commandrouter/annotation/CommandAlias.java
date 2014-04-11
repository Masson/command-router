package com.imasson.commandrouter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to define a command alias.
 *
 * @author Masson
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandAlias {
    /**
     * all names of the command that will be used in raw command.
     */
    public String[] value();
}
