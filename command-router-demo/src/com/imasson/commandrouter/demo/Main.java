package com.imasson.commandrouter.demo;

import com.imasson.commandrouter.CommandRouter;
import com.imasson.commandrouter.CommandRouterBuilder;
import com.imasson.commandrouter.driver.UriDriver;

public class Main {

    public static void main(String[] args) {
        final String rawCommand = "kk://trial/show?msg=helloworld&num=8";

        final CommandRouter router = new CommandRouterBuilder()
                .setDriver(new UriDriver())
                .addCommandHandler(TestHandler.class)
                .addGeneralValueConverters()
                .build();
        router.setDebug(true);

        System.out.println(router.dump());

        router.invokeCommand("I'm context", rawCommand);
    }
}
