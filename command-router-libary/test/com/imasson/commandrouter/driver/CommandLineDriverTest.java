package com.imasson.commandrouter.driver;

import com.imasson.commandrouter.CommandRouter;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CommandLineDriverTest {

    private CommandLineDriver mDriver;

    @Before
    public void setUp() throws Exception {
        mDriver = new CommandLineDriver();
    }

    @Test
    public void testParseNoParamCommand() throws Exception {
        CommandRouter.Op op = mDriver.parseCommand(this, "trial sayHello");

        assertNotNull(op);
        assertEquals(op.getHandlerName(), "trial");
        assertEquals(op.getCommandName(), "sayHello");
    }

    @Test
    public void testParseSingleParamCommand() throws Exception {
        CommandRouter.Op op = mDriver.parseCommand(this, "trial show --msg hello");

        assertNotNull(op);
        assertEquals(op.getHandlerName(), "trial");
        assertEquals(op.getCommandName(), "show");
        assertEquals(op.getArgument("msg"), "hello");
    }

    @Test
    public void testParseMultiParamCommand() throws Exception {
        CommandRouter.Op op = mDriver.parseCommand(this, "trial show --msg hello --time 3 --repeat true");

        assertNotNull(op);
        assertEquals(op.getHandlerName(), "trial");
        assertEquals(op.getCommandName(), "show");
        assertEquals(op.getArgument("msg"), "hello");
        assertEquals(op.getArgument("time"), "3");
        assertEquals(op.getArgument("repeat"), "true");
    }
}