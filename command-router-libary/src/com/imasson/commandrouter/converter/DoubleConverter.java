package com.imasson.commandrouter.converter;

/**
 * @author Masson
 */
public class DoubleConverter extends SingleValueConverter {
    @Override
    public Object unmarshal(String source, Class<?> type) throws ValueConverterException {
        if (source == null) return 0f;

        try {
            return Double.parseDouble(source);
        } catch (NumberFormatException ex) {
            throw new ValueConverterException(source, ex);
        }
    }
}
