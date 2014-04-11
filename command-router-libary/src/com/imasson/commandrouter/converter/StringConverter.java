package com.imasson.commandrouter.converter;

/**
 * @author Masson
 */
public class StringConverter extends SingleValueConverter {
    @Override
    public Object unmarshal(String source, Class<?> type) throws ValueConverterException {
        return source;
    }
}
