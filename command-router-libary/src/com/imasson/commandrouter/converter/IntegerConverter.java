package com.imasson.commandrouter.converter;

/**
 * @author Masson
 */
public class IntegerConverter extends SingleValueConverter {
    @Override
    public Object unmarshal(String source, Class<?> type) throws ValueConverterException {
        try {
            return Integer.parseInt(source);
        } catch (NumberFormatException ex) {
            throw new ValueConverterException(source, ex);
        }
    }
}
