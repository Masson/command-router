package com.imasson.commandrouter;

import com.imasson.commandrouter.driver.UriDriver;
import com.imasson.commandrouter.test.Address;
import com.imasson.commandrouter.test.AddressConverter;
import com.imasson.commandrouter.test.TestHandler;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CommandRouterTest {

    private CommandRouter mCommandRouter;

    @Before
    public void setUp() throws Exception {
        mCommandRouter = new CommandRouterBuilder()
                .setDriver(new UriDriver())
                .addCommandHandler(TestHandler.class)
                .addGeneralValueConverters()
                .addValueConverter(Address.class, AddressConverter.class)
                .build();
        mCommandRouter.setDebug(true);
    }

    @Test
    public void testExecuteCommand() throws Exception {
        Address result = (Address) mCommandRouter.executeCommand(this, "demo://trial/getAddress?postcode=123456");

        assertNotNull(result);
        Address address = new Address();
        address.street = "Huangpu Ave.";
        address.city = "Guangzhou";
        address.postcode = "123456";
        assertEquals(result, address);
    }

    @Test
    public void testExecuteCommandMarshalled() throws Exception {
        String result = mCommandRouter.executeCommandMarshalled(this, "demo://trial/getAddress?postcode=123456");

        assertNotNull(result);
        assertEquals(result, "Huangpu Ave.,Guangzhou,123456");
    }
}
