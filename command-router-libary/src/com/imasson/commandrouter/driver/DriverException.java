package com.imasson.commandrouter.driver;

import com.imasson.commandrouter.CommandRouterException;

import java.util.Arrays;

/**
 * Exception throws when the driver fail parsing the command.
 *
 * @author Masson
 */
public class DriverException extends CommandRouterException {

    public DriverException(String message, Object[] args) {
        this(message, null, args);
    }

    public DriverException(String message, Throwable cause, Object[] args) {
        super(message + (args != null ? Arrays.toString(args) : ""), cause);
    }
}
