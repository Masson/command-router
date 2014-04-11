package com.imasson.commandrouter.converter;

/**
 * @author Masson
 */
public class BooleanConverter extends SingleValueConverter {
    @Override
    public Object unmarshal(String source, Class<?> type) throws ValueConverterException {
        return Boolean.parseBoolean(source);
    }
}
