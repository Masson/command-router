package com.imasson.commandrouter.converter;

import com.imasson.commandrouter.util.ThreadSafeSimpleDateFormat;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Masson
 */
public class DateConverter implements ValueConverter {

    private static final int TIMESTAMP_MAX_LENGTH = 13;

    private static final String DEFAULT_PATTERN;
    private static final String[] DEFAULT_ACCEPTABLE_FORMATS;
    static {
        final String defaultPattern = "yyyy-MM-dd HH:mm:ss.S";
        final List<String> acceptablePatterns = new ArrayList<String>();
        acceptablePatterns.add("yyyy-MM-dd HH:mm:ss");
        acceptablePatterns.add("yyyy-MM-dd");
        acceptablePatterns.add("HH:mm:ss");
        acceptablePatterns.add("yyyy-MM-dd HH:mm:ss.S z");
        acceptablePatterns.add("yyyy-MM-dd HH:mm:ss.S a");
        acceptablePatterns.add("yyyy-MM-dd HH:mm:ss z");
        acceptablePatterns.add("yy-M-d H:m:s");
        acceptablePatterns.add("yy-M-d");

        DEFAULT_PATTERN = defaultPattern;
        DEFAULT_ACCEPTABLE_FORMATS = acceptablePatterns.toArray(new String[acceptablePatterns.size()]);
    }

    private ThreadSafeSimpleDateFormat defaultFormat;
    private ThreadSafeSimpleDateFormat[] acceptableFormats;

    private void setupIfNeed() {
        if (this.defaultFormat == null || this.acceptableFormats == null) {
            synchronized (this) {
                if (this.defaultFormat == null || this.acceptableFormats == null) {
                    this.defaultFormat = new ThreadSafeSimpleDateFormat(DEFAULT_PATTERN);
                    this.acceptableFormats = new ThreadSafeSimpleDateFormat[DEFAULT_ACCEPTABLE_FORMATS.length];
                    for (int i = 0; i < DEFAULT_ACCEPTABLE_FORMATS.length; i++) {
                        this.acceptableFormats[i] = new ThreadSafeSimpleDateFormat(DEFAULT_ACCEPTABLE_FORMATS[i]);
                    }
                }
            }
        }
    }

    @Override
    public String marshal(Object source) throws ValueConverterException {
        setupIfNeed();
        if (source instanceof Date) {
            Date date = (Date) source;
            return defaultFormat.format(date);
        }
        return "";
    }

    @Override
    public Object unmarshal(String source, Class<?> type) throws ValueConverterException {
        setupIfNeed();
        if (source == null) {
            return new Date(0L);
        }

        // date in timestamp format
        if (source.length() <= TIMESTAMP_MAX_LENGTH && isNumeric(source)) {
            try {
                final long timestamp = Long.parseLong(source);
                return new Date(timestamp);
            } catch (NumberFormatException ex) {
                throw new ValueConverterException(source, ex);
            }
        }

        // date in one of the readable date format
        for (final ThreadSafeSimpleDateFormat acceptableFormat : acceptableFormats) {
            try {
                return acceptableFormat.parse(source);
            } catch (final ParseException ex) {
                // no worries, let's try the next format.
            }
        }
        // no dateFormats left to try
        throw new ValueConverterException(source);
    }

    public static boolean isNumeric(String str){
        for (int i = str.length(); --i >= 0;) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
