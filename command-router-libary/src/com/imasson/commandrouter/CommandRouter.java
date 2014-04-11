package com.imasson.commandrouter;

import com.imasson.commandrouter.annotation.HandlerAlias;
import com.imasson.commandrouter.converter.ValueConverter;
import com.imasson.commandrouter.converter.ValueConverterException;
import com.imasson.commandrouter.driver.AbstractDriver;
import com.imasson.commandrouter.driver.DriverException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Simple facade to CommandRouter Library.
 *
 * @author Masson
 */
public final class CommandRouter {
    private boolean debug = false;

    private AbstractDriver mDriver;
    private Map<String, CommandHandler> mHandlerMap = new HashMap<String, CommandHandler>();
    private Map<String, ValueConverter> mConverterMap = new HashMap<String, ValueConverter>();

    CommandRouter() {
    }

    public CommandRouter(AbstractDriver driver) {
        mDriver = driver;
    }

    void setDriver(AbstractDriver driver) {
        mDriver = driver;
    }

    AbstractDriver getDriver() {
        return mDriver;
    }

    public void addCommandHandler(CommandHandler handler) {
        if (handler == null) {
            if (!debug) return;
            throw new CommandRouterException("Argument 'handler' is null");
        }

        HandlerAlias handlerAlias = handler.getClass().getAnnotation(HandlerAlias.class);
        if (handlerAlias == null) {
            if (!debug) return;
            throw new CommandRouterException("The CommandHandler does not define alias: "
                    + handler.getClass().getName());
        }

        mHandlerMap.put(handlerAlias.value(), handler);
    }

    public void addValueConverter(Class<?> type, ValueConverter converter) {
        if (type == null) {
            if (!debug) return;
            throw new CommandRouterException("Argument 'type' is null");
        }

        if (converter == null) {
            if (!debug) return;
            throw new CommandRouterException("Argument 'converter' is null");
        }

        mConverterMap.put(type.getName(), converter);
    }

    public void addValueConverter(Class<?> type, Class<?> converterClass) {
        if (converterClass == null) {
            if (!debug) return;
            throw new CommandRouterException("Argument 'converterClass' is null");
        }

        ValueConverter converter = null;
        try {
            converterClass.newInstance();
        } catch (Exception e) {
            if (!debug) return;
            throw new CommandRouterException(e);
        }

        if (converter != null) {
            addValueConverter(type, converter);
        }
    }

    public ValueConverter getValueConverter(Class<?> type) {
        if (type == null) {
            if (!debug) return null;
            throw new CommandRouterException("Argument 'type' is null");
        }

        final String typeName = type.getName();
        return mConverterMap.get(typeName);
    }

    ValueConverter getValueConverterInstance(Class<?> converterClass) {
        final Collection<ValueConverter> converters = mConverterMap.values();
        for (ValueConverter converter : converters) {
            if (converter != null && converter.getClass().equals(converterClass)) {
                return converter;
            }
        }
        return null;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void invokeCommand(Object context, Object... args) {
        Op op = null;
        try {
            op = mDriver.parseCommand(context, args);
        } catch (DriverException ex) {
            if (!debug) return;
            throw new CommandRouterException(ex);
        }

        CommandHandler handler = mHandlerMap.get(op.getHandlerName());
        if (handler == null) {
            if (!debug) return;
            throw new CommandRouterException("CommandHandler not found", op);
        }

        try {
            handler.invokeCommand(this, op);
        } catch (CommandHandlerException ex) {
            if (debug) throw ex;
        } catch (ValueConverterException ex) {
            if (debug) throw ex;
        }
    }

    public String dump() {
        StringBuilder sb = new StringBuilder("==== CommandRouter ===================\n");

        final Set<String> keySet = mHandlerMap.keySet();
        for (String key : keySet) {
            CommandHandler handler = mHandlerMap.get(key);
            handler.setup(this);
            sb.append("  [Handler] ").append(key).append(" : ")
                    .append(handler.getClass().getName())
                    .append('\n')
                    .append(handler.dump());
        }

        sb.append("======================================");
        return sb.toString();
    }

    /**
     * The simple data object holds context, handler name, command name
     * and all arguments of object form will storage in map.
     *
     * @author Masson
     */
    public static final class Op {
        private Object context;
        private String handlerName;
        private String commandName;
        private Map<String, String> arguments;

        public Object getContext() {
            return context;
        }

        public String getHandlerName() {
            return handlerName;
        }

        public String getCommandName() {
            return commandName;
        }

        public Op(Object context, String handlerName, String commandName) {
            this.context = context;
            this.handlerName = handlerName;
            this.commandName = commandName;
            this.arguments = new HashMap<String, String>();
        }

        public void addArgument(String key, String value) {
            arguments.put(key, value);
        }

        public String getArgument(String key) {
            return arguments.get(key);
        }

        @Override
        public String toString() {
            return "Op{" +
                    "commandName='" + commandName + '\'' +
                    ", handlerName='" + handlerName + '\'' +
                    ", context=" + context +
                    '}';
        }
    }
}
