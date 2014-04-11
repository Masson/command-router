package com.imasson.commandrouter.converter;

/**
 * Converter implementations are responsible marshalling Java objects
 * to/from raw textual data.
 *
 * @author Masson
 */
public interface ValueConverter {

    /**
     * Convert an object to textual data.
     *
     * @param source the object to be marshalled
     * @return the resulting object to write into raw data
     */
    String marshal(Object source) throws ValueConverterException;

    /**
     * Convert textual data back into an object.
     *
     * @param source the object to be unmarshalled
     * @param type the target type that expect to convert to
     * @return the resulting object for command handler
     */
    Object unmarshal(String source, Class<?> type) throws ValueConverterException;
}
