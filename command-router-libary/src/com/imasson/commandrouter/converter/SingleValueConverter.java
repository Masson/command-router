package com.imasson.commandrouter.converter;

/**
 * Abstract converter for base value like String, Integer...
 *
 * @author Masson
 */
public abstract class SingleValueConverter implements ValueConverter {

    @Override
    public String marshal(Object source) throws ValueConverterException {
        return source != null ? source.toString() : "";
    }
}
