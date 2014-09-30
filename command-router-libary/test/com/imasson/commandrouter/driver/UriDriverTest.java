package com.imasson.commandrouter.driver;

import com.imasson.commandrouter.CommandRouter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class UriDriverTest {

    private UriDriver mDriver;

    @Before
    public void setUp() throws Exception {
        mDriver = new UriDriver();
    }

    @Test
    public void testParseNoParamCommand() throws Exception {
        CommandRouter.Op op = mDriver.parseCommand(this, "demo://trial/sayHello");

        assertNotNull(op);
        assertEquals(op.getHandlerName(), "trial");
        assertEquals(op.getCommandName(), "sayHello");
    }

    @Test
    public void testParseSingleParamCommand() throws Exception {
        CommandRouter.Op op = mDriver.parseCommand(this, "demo://trial/show?msg=hello");

        assertNotNull(op);
        assertEquals(op.getHandlerName(), "trial");
        assertEquals(op.getCommandName(), "show");
        assertEquals(op.getArgument("msg"), "hello");
    }

    @Test
    public void testParseMultiParamCommand() throws Exception {
        CommandRouter.Op op = mDriver.parseCommand(this,
                "demo://trial/show?msg=hello&repeat=true&time=3");

        assertNotNull(op);
        assertEquals(op.getHandlerName(), "trial");
        assertEquals(op.getCommandName(), "show");
        assertEquals(op.getArgument("msg"), "hello");
        assertEquals(op.getArgument("time"), "3");
        assertEquals(op.getArgument("repeat"), "true");
    }
}