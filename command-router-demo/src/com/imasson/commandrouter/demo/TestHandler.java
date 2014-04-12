package com.imasson.commandrouter.demo;

import com.imasson.commandrouter.CommandHandler;
import com.imasson.commandrouter.annotation.CommandAlias;
import com.imasson.commandrouter.annotation.HandlerAlias;
import com.imasson.commandrouter.annotation.ParamAlias;

@HandlerAlias("trial")
public class TestHandler extends CommandHandler {

    @CommandAlias("show")
    public void showText(Object context,
                         @ParamAlias("msg") String text,
                         @ParamAlias(value = "num", defaultRaw = "5") int number) {
        System.out.println("SHOW! context=" + context + ", text=" + text + ", number=" + number);
    }

    @CommandAlias(value = {"locate", "where"})
    public void showAddress(Object context,
                            @ParamAlias(value = "addr", converter = AddressConverter.class) Address address) {
        System.out.println("show address: " + address.toString());
    }

    @Override
    protected void onUnknownCommand(String command) {
        super.onUnknownCommand(command);
        System.out.println("unknown command: " + command);
    }
}
