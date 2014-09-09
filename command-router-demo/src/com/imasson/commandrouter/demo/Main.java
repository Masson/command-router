package com.imasson.commandrouter.demo;

import com.imasson.commandrouter.CommandRouter;
import com.imasson.commandrouter.CommandRouterBuilder;
import com.imasson.commandrouter.driver.UriDriver;

public class Main {

    public static void main(String[] args) {
        final CommandRouter router = new CommandRouterBuilder()
                .setDriver(new UriDriver())
                .addCommandHandler(TestHandler.class)
                .addGeneralValueConverters()
                .build();
        router.setDebug(true);

        System.out.println(router.dump());


        // Sample 1: hello world
        final String rawCommand1 = "kk://trial/show?msg=helloworld";
        router.executeCommand("I'm context", rawCommand1);

        // Sample 2: custom converter
        final String rawCommand2 = "kk://trial/locate?addr=N%20Western%20Avenue,Chicago,90027";
        router.executeCommand(null, rawCommand2);

        // Sample 3: use return value
        final String rawCommand3 = "kk://trial/sayHello";
        String ret = (String) router.executeCommand(null, rawCommand3);
        System.out.println("'kk://trial/sayHello' outputs '" + ret + "'");
    }
}
