package com.imasson.commandrouter.converter;

/**
 * @author Masson
 */
public class LongConverter extends SingleValueConverter {
    @Override
    public Object unmarshal(String source, Class<?> type) throws ValueConverterException {
        if (source == null) return 0L;

        try {
            return Long.parseLong(source);
        } catch (NumberFormatException ex) {
            throw new ValueConverterException(source, ex);
        }
    }
}
