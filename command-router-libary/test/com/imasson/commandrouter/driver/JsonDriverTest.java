package com.imasson.commandrouter.driver;

import com.imasson.commandrouter.CommandRouter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class JsonDriverTest {

    private JsonDriver mDriver;

    @Before
    public void setUp() throws Exception {
        mDriver = new JsonDriver();
    }

    @Test
    public void testParseNoParamCommand() throws Exception {
        CommandRouter.Op op = mDriver.parseCommand(this,
                "{\"handler\":\"trial\", \"command\":\"sayHello\"}");

        assertNotNull(op);
        assertEquals(op.getHandlerName(), "trial");
        assertEquals(op.getCommandName(), "sayHello");
    }

    @Test
    public void testParseSingleParamCommand() throws Exception {
        CommandRouter.Op op = mDriver.parseCommand(this,
                "{\"handler\":\"trial\", \"command\":\"show\", \"param\":{\"msg\":\"hello\"}}");

        assertNotNull(op);
        assertEquals(op.getHandlerName(), "trial");
        assertEquals(op.getCommandName(), "show");
        assertEquals(op.getArgument("msg"), "hello");
    }

    @Test
    public void testParseMultiParamCommand() throws Exception {
        CommandRouter.Op op = mDriver.parseCommand(this,
                "{\n" +
                "    \"handler\": \"trial\", \n" +
                "    \"command\": \"show\", \n" +
                "    \"param\": {\n" +
                "        \"msg\": \"hello\", \n" +
                "        \"time\": 3, \n" +
                "        \"repeat\": true\n" +
                "    }\n" +
                "}");

        assertNotNull(op);
        assertEquals(op.getHandlerName(), "trial");
        assertEquals(op.getCommandName(), "show");
        assertEquals(op.getArgument("msg"), "hello");
        assertEquals(op.getArgument("time"), "3");
        assertEquals(op.getArgument("repeat"), "true");
    }
}