package com.imasson.commandrouter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to define a
 * {@link com.imasson.commandrouter.CommandHandler CommandHandler} alias.
 *
 * @author Masson
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HandlerAlias {
    /**
     * The name of the handler that will be used in raw command.
     */
    public String value();
}
