package com.imasson.commandrouter.converter;

/**
 * @author Masson
 */
public class BooleanConverter extends SingleValueConverter {
    @Override
    public Object unmarshal(String source, Class<?> type) throws ValueConverterException {
        if (source == null) return false;

        return Boolean.parseBoolean(source);
    }
}
