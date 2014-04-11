package com.imasson.commandrouter.converter;

import com.imasson.commandrouter.CommandRouterException;

/**
 * Exception throws when fail to convert value.
 *
 * @author Masson
 */
public class ValueConverterException extends CommandRouterException {

    public ValueConverterException(Object source) {
        this(source, null);
    }

    public ValueConverterException(Object source, Throwable cause) {
        super("Convert value fail, source: " + source, cause);
    }
}
