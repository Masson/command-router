package com.imasson.commandrouter.driver;

import com.imasson.commandrouter.CommandRouter;

/**
 * The command driver that parse command from shell-style command line String.
 *
 * @author Masson
 */
public class CommandLineDriver extends AbstractDriver {

    @Override
    public CommandRouter.Op parseCommand(Object context, Object... rawArgs) {
        final Object rawArg = rawArgs[0];
        if (!(rawArg instanceof String)) {
            throw new DriverException("RawArg of command must be String", rawArgs);
        }

        String raw = (String) rawArg;
        String[] segments = raw.split(" --");
        if (segments.length == 0) {
            throw new DriverException("Error parsing command line: cannot find main segment", rawArgs);
        }

        String mainSegment = segments[0];
        String[] mainSpins = mainSegment.split(" ");
        if (mainSpins.length <= 1) {
            throw new DriverException("Error parsing command line: cannot parse main segment", rawArgs);
        }

        String handlerName = mainSpins[0];
        String commandName = mainSpins[1];
        CommandRouter.Op op = new CommandRouter.Op(context, handlerName, commandName);

        for (int i = 1; i < segments.length; i++) {
            String segment = segments[i];
            String[] spins = segment.split(" ");
            if (spins.length > 1) {
                String paramName = spins[0];
                String paramValue = spins[1];
                if (paramName != null && paramName.length() > 0) {
                    op.addArgument(paramName, paramValue);
                }
            }
        }

        return op;
    }
}
