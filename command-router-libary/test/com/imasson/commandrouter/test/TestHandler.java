package com.imasson.commandrouter.test;

import com.imasson.commandrouter.CommandHandler;
import com.imasson.commandrouter.annotation.CommandAlias;
import com.imasson.commandrouter.annotation.HandlerAlias;
import com.imasson.commandrouter.annotation.ParamAlias;

@HandlerAlias("trial")
public class TestHandler extends CommandHandler {

    @CommandAlias(value = {"locate", "where"})
    public void showAddress(Object context,
                            @ParamAlias(value = "addr", converter = AddressConverter.class) Address address) {
        System.out.println("show address: " + address.toString());
    }

    @CommandAlias()
    public Address getAddress(Object context,
                           @ParamAlias(value = "postcode") String postcode) {
        Address address = new Address();
        address.city = "Guangzhou";
        address.street = "Huangpu Ave.";
        address.postcode = postcode;
        return address;
    }


    @Override
    protected void onUnknownCommand(Object context, String command) {
        super.onUnknownCommand(context, command);
        System.out.println("unknown command: " + command);
    }
}
