package com.imasson.commandrouter.annotation;

import com.imasson.commandrouter.converter.StringConverter;
import com.imasson.commandrouter.converter.ValueConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to define a parameter alias.
 *
 * @author Masson
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface ParamAlias {
    /**
     * The name (use as key) of the parameter that will be used in raw command.
     */
    public String value();

    /**
     * The class of converter that used to convert value.
     * If null, default converter for the target type will be used.
     */
    public Class<? extends ValueConverter> converter() default StringConverter.class;

    /**
     * The default value of the parameter in raw String.
     * @return
     */
    public String defaultValue() default "";
}
