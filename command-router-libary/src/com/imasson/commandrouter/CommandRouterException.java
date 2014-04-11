package com.imasson.commandrouter;

/**
 * Base exception for all thrown exceptions with CommandRouter.
 *
 * @author Masson
 */
public class CommandRouterException extends RuntimeException {

    protected CommandRouterException() {
        this("", null, null);
    }

    public CommandRouterException(String message) {
        this(message, null, null);
    }

    public CommandRouterException(Throwable cause) {
        this("", cause);
    }

    public CommandRouterException(String message, CommandRouter.Op op) {
        this(message, null, op);
    }

    public CommandRouterException(Throwable cause, CommandRouter.Op op) {
        this("", cause, op);
    }

    public CommandRouterException(String message, Throwable cause) {
        this(message, cause, null);
    }

    public CommandRouterException(String message, Throwable cause, CommandRouter.Op op) {
        super(message
                + (cause == null ? "" : " : " + cause.getMessage())
                + (op == null ? "" : " \n @ " + op.toString()),
              cause);
    }
}
