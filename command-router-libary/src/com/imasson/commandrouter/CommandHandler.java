package com.imasson.commandrouter;

import com.imasson.commandrouter.annotation.CommandAlias;
import com.imasson.commandrouter.annotation.ParamAlias;
import com.imasson.commandrouter.converter.StringConverter;
import com.imasson.commandrouter.converter.ValueConverter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The abstract class of all command handler.
 * A command handler provides a group of methods, and each method
 * can execute one command action.
 *
 * @author Masson
 */
public abstract class CommandHandler {

    private boolean mIsSetupped = false;
    private Map<String, CommandMeta> mCommandMap;

    void setup(CommandRouter router) throws CommandHandlerException {
        if (mIsSetupped) return;

        mCommandMap = new HashMap<String, CommandMeta>();

        Method[] methods = getClass().getMethods();
        for (Method method : methods) {
            CommandAlias alias = method.getAnnotation(CommandAlias.class);
            if (alias == null) continue;

            CommandMeta meta = generateCommandMeta(router, method);

            String[] commandNames = alias.value();
            for (String name : commandNames) {
                if (name != null) {
                    mCommandMap.put(name, meta);
                }
            }
        }

        onSetup();

        mIsSetupped = true;
    }

    private CommandMeta generateCommandMeta(CommandRouter router, Method method) {
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length == 0) {
            throw new CommandInvalidException("Invalid command method, " +
                    "no params found", this, method);
        }
        Class<?> contextType = paramTypes[0];
        final int argCount = paramTypes.length - 1;
        Class<?>[] keyParamTypes = new Class<?>[argCount];
        System.arraycopy(paramTypes, 1, keyParamTypes, 0, keyParamTypes.length);

        String[] keys = new String[argCount];
        String[] defaults = new String[argCount];
        ValueConverter[] converters = new ValueConverter[argCount];

        Annotation[][] paramAnnotations = method.getParameterAnnotations();
        for (int i = 1; i < paramAnnotations.length; i++) {
            Annotation[] annotations = paramAnnotations[i];
            ParamAlias paramAlias = null;
            for (Annotation a : annotations) {
                if (a instanceof ParamAlias) {
                    paramAlias = (ParamAlias) a;
                    break;
                }
            }
            if (paramAlias == null) {
                throw new CommandInvalidException("Invalid command argument," +
                        "no ParamAlias found", this, method);
            }

            String key = paramAlias.value();
            if (key == null || key.length() == 0) {
                throw new CommandInvalidException("Invalid command argument," +
                        "no argument key found", this, method);
            }
            keys[i - 1] = key;

            final String defaultValue = paramAlias.defaultRaw();
            defaults[i - 1] = defaultValue.length() > 0 ? defaultValue : null;

            ValueConverter converter = findConverter(router, method,
                    keyParamTypes[i - 1], paramAlias.converter());
            if (converter == null) {
                throw new CommandInvalidException("Invalid command argument," +
                        "no converter for type: " + keyParamTypes[i - 1], this, method);
            }
            converters[i - 1] = converter;
        }

        CommandMeta meta = new CommandMeta();
        meta.method = method.getName();
        meta.paramTypes = keyParamTypes;
        meta.paramKeys = keys;
        meta.defaultValues = defaults;
        meta.converters = converters;
        meta.contextType = contextType;
        return meta;
    }

    private ValueConverter findConverter(CommandRouter router, Method method, Class<?> paramType,
                                         Class<? extends ValueConverter> defaultClass) {
        ValueConverter converter;
        if (!StringConverter.class.equals(defaultClass)) {
            converter = router.getValueConverterInstance(defaultClass);
            if (converter == null) {
                try {
                    converter = defaultClass.newInstance();
                } catch (Exception e) {
                    throw new CommandInvalidException("Can not make converter: "
                            + defaultClass.getName(), e, this, method);
                }
            }
            return converter;
        }

        converter = router.getValueConverter(paramType);
        return converter;
    }

    void executeCommand(CommandRouter router, CommandRouter.Op op) throws CommandHandlerException {
        setup(router);

        final String commandName = op.getCommandName();
        CommandMeta meta = mCommandMap.get(commandName);

        if (meta == null) {
            onUnknownCommand(commandName);
            return;
        }

        Object[] params = generateParams(op, meta);

        Class<?>[] paramTypes = new Class<?>[meta.paramTypes.length + 1];
        paramTypes[0] = meta.contextType;
        System.arraycopy(meta.paramTypes, 0, paramTypes, 1, meta.paramTypes.length);

        try {
            Method method = getClass().getMethod(meta.method, paramTypes);
            method.invoke(this, params);
        } catch (Exception e) {
            throw new CommandHandlerException(e, op);
        }
    }

    private Object[] generateParams(CommandRouter.Op op, CommandMeta meta) throws CommandHandlerException {
        final int keyCount = meta.paramKeys.length;
        Object[] params = new Object[keyCount + 1];
        params[0] = op.getContext();
        for (int i = 0; i < keyCount; i++) {
            String key = meta.paramKeys[i];
            String rawArgument = op.getArgument(key);
            if (rawArgument == null) {
                rawArgument = meta.defaultValues[i];
            }

            ValueConverter converter = meta.converters[i];
            Class<?> paramType = meta.paramTypes[i];
            Object param = converter.unmarshal(rawArgument, paramType);
            params[i + 1] = param;
        }
        return params;
    }


    protected void onSetup() {}

    protected void onUnknownCommand(String command) {}


    String dump() {
        if (mCommandMap == null) return "";

        StringBuilder sb = new StringBuilder();
        final Set<String> keySet = mCommandMap.keySet();
        for (String key : keySet) {
            CommandMeta meta = mCommandMap.get(key);
            if (meta != null) {
                sb.append("    [Command] ").append(key).append(" : ")
                        .append(meta.dump()).append('\n');
            }
        }
        return sb.toString();
    }

    private static final class CommandMeta {
        private String method;
        private String[] paramKeys;
        private String[] defaultValues;
        private Class<?>[] paramTypes;
        private ValueConverter[] converters;
        private Class<?> contextType;

        String dump() {
            return "\'" + method + '\'' +
                    "\n      -- paramKeys=" + Arrays.toString(paramKeys) +
                    "\n      -- defaultValues=" + Arrays.toString(defaultValues) +
                    "\n      -- paramTypes=" + Arrays.toString(paramTypes) +
                    "\n      -- converters=" + dumpConverters(converters);
        }

        private String dumpConverters(ValueConverter[] converters) {
            if (converters.length == 0) return "";

            StringBuilder sb = new StringBuilder("[");
            for (ValueConverter vc : converters) {
                sb.append(vc.getClass().getSimpleName()).append(", ");
            }
            sb.delete(sb.length() - 2, sb.length());
            sb.append("]");
            return sb.toString();
        }
    }
}
