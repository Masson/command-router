package com.imasson.commandrouter.driver;

import com.imasson.commandrouter.CommandRouter;

import java.net.URI;
import java.net.URLDecoder;

/**
 * The command driver that parse command from {@link URI} or URI in String form.
 *
 * @author Masson
 */
public class UriDriver extends AbstractDriver {

    private static final String ENCODING = "utf-8";

    @Override
    public CommandRouter.Op parseCommand(Object context, Object... rawArgs) {
        URI uri;

        final Object rawArg = rawArgs[0];
        if (rawArg instanceof String) {
            try {
                uri = URI.create((String) rawArg);
            } catch (IllegalArgumentException ex) {
                throw new DriverException("Error parsing URI", ex, rawArgs);
            }
        } else if (rawArg instanceof URI) {
            uri = (URI) rawArg;
        } else {
            throw new DriverException("Unexpected format of args", rawArgs);
        }

        final String host = uri.getHost();
        if (host == null) {
            throw new DriverException("No handler name found", rawArgs);
        }

        String path = uri.getPath();
        if (path == null || path.length() <= 1) {
            throw new DriverException("No command name found", rawArgs);
        }
        path = path.substring(1);

        CommandRouter.Op op = new CommandRouter.Op(context, host, path);

        final String query = uri.getRawQuery();
        if (query != null) {
            try {
                final String[] querySegments = query.split("&");
                for (int i = 0; i < querySegments.length; i++) {
                    String seg = querySegments[i];
                    String[] kvpair = seg.split("=", -2);

                    String key = kvpair[0];
                    String value = URLDecoder.decode(kvpair[1], ENCODING);
                    op.addArgument(key, value);
                }
            } catch (Exception ex) {
                throw new DriverException("Error parsing query in URI", ex, rawArgs);
            }
        }

        return op;
    }
}
