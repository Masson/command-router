package com.imasson.commandrouter.driver;

import com.imasson.commandrouter.CommandRouter;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * The command driver that parse command from Json in String form.
 *
 * @author Masson
 */
public class JsonDriver extends AbstractDriver {

    private String mHandlerKey;
    private String mCommandKey;
    private String mParamKey;

    public JsonDriver() {
        this("handler", "command", "param");
    }

    public JsonDriver(String handlerKey, String commandKey, String paramKey) {
        if (handlerKey == null || handlerKey.length() == 0) {
            throw new IllegalArgumentException("Argument 'handlerKey' is null or empty!");
        }
        if (commandKey == null || commandKey.length() == 0) {
            throw new IllegalArgumentException("Argument 'commandKey' is null or empty!");
        }
        if (paramKey == null || paramKey.length() == 0) {
            throw new IllegalArgumentException("Argument 'paramKey' is null or empty!");
        }

        mHandlerKey = handlerKey;
        mCommandKey = commandKey;
        mParamKey = paramKey;
    }

    @Override
    public CommandRouter.Op parseCommand(Object context, Object... rawArgs) {
        JSONObject jsonObject;

        final Object rawArg = rawArgs[0];
        if (rawArg instanceof String) {
            try {
                jsonObject = new JSONObject((String) rawArg);
            } catch (JSONException ex) {
                throw new DriverException("Error parsing Json", ex, rawArgs);
            }
        } else if (rawArg instanceof JSONObject) {
            jsonObject = (JSONObject) rawArg;
        } else {
            throw new DriverException("Unexpected format of args", rawArgs);
        }

        CommandRouter.Op op;
        try {
            String handlerName = jsonObject.getString(mHandlerKey);
            String commandName = jsonObject.getString(mCommandKey);
            op = new CommandRouter.Op(context, handlerName, commandName);

        } catch (JSONException ex) {
            throw new DriverException("Error parsing handler or command in Json", ex, rawArgs);
        }

        JSONObject paramJsonObject = jsonObject.optJSONObject(mParamKey);
        if (paramJsonObject != null) {
            Iterator keysIterator = paramJsonObject.keys();
            while (keysIterator.hasNext()) {
                String key = (String) keysIterator.next();
                String value = paramJsonObject.optString(key);
                if (value != null) {
                    op.addArgument(key, value);
                }
            }
        }

        return op;
    }
}
