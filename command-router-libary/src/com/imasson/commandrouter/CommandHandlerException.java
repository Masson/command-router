package com.imasson.commandrouter;

/**
 * Exception that wrap any exceptions while invoking command.
 *
 * @author Masson
 */
public class CommandHandlerException extends CommandRouterException {

    public CommandHandlerException(String message, CommandRouter.Op op) {
        super(message, null, op);
    }

    public CommandHandlerException(Throwable cause, CommandRouter.Op op) {
        super("", cause, op);
    }

    public CommandHandlerException(String message, Throwable cause, CommandRouter.Op op) {
        super(message, cause, op);
    }
}
