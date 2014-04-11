package com.imasson.commandrouter;

import com.imasson.commandrouter.converter.*;
import com.imasson.commandrouter.driver.AbstractDriver;

/**
 * The builder that provide easy way to build a general and simple {@link CommandRouter}
 *
 * @author Masson
 */
public final class CommandRouterBuilder {

    private CommandRouter router;

    public CommandRouterBuilder() {
        router = new CommandRouter();
    }

    public CommandRouterBuilder setDriver(AbstractDriver driver) {
        router.setDriver(driver);
        return this;
    }

    public CommandRouterBuilder addCommandHandler(CommandHandler handler) {
        router.addCommandHandler(handler);
        return this;
    }

    public CommandRouterBuilder addCommandHandler(Class<? extends CommandHandler> handlerClass) {
        if (handlerClass != null) {
            CommandHandler handler = null;
            try {
                handler = handlerClass.newInstance();
            } catch (Exception e) {
            }
            if (handler != null) {
                return addCommandHandler(handler);
            }
        }
        return this;
    }

    public CommandRouterBuilder addCommandHandler(String handlerClassName) {
        if (handlerClassName != null) {
            CommandHandler handler = null;
            try {
                final Class<?> handlerClass = Class.forName(handlerClassName);
                handler = (CommandHandler) handlerClass.newInstance();
            } catch (Exception e) {
            }
            if (handler != null) {
                return addCommandHandler(handler);
            }
        }
        return this;
    }

    public CommandRouterBuilder addValueConverter(Class<?> type, ValueConverter converter) {
        router.addValueConverter(type, converter);
        return this;
    }

    public CommandRouterBuilder addValueConverter(Class<?> type, Class<?> converterClass) {
        router.addValueConverter(type, converterClass);
        return this;
    }

    public CommandRouterBuilder addValueConverter(String typeClassName, String converterClassName) {
        Class<?> type = null;
        Class<?> converterClass = null;
        try {
            type = Class.forName(typeClassName);
            converterClass = Class.forName(converterClassName);
        } catch (Exception e) {
        }

        if (type != null && converterClass != null) {
            return addValueConverter(type, converterClass);
        }
        return this;
    }

    public CommandRouterBuilder addGeneralValueConverters() {
        addValueConverter(boolean.class, new BooleanConverter());
        addValueConverter(float.class, new FloatConverter());
        addValueConverter(int.class, new IntegerConverter());
        addValueConverter(long.class, new LongConverter());
        addValueConverter(String.class, new StringConverter());
        return this;
    }

    public CommandRouter build() {
        if (router.getDriver() == null) {
            throw new RuntimeException("CommandRouter has no driver!");
        }
        return router;
    }
}
