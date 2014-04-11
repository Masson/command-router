package com.imasson.commandrouter.converter;

/**
 * @author Masson
 */
public class FloatConverter extends SingleValueConverter {
    @Override
    public Object unmarshal(String source, Class<?> type) throws ValueConverterException {
        try {
            return Float.parseFloat(source);
        } catch (NumberFormatException ex) {
            throw new ValueConverterException(source, ex);
        }
    }
}
