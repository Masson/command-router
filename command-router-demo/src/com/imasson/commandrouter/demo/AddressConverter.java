package com.imasson.commandrouter.demo;


import com.imasson.commandrouter.converter.ValueConverter;
import com.imasson.commandrouter.converter.ValueConverterException;

public class AddressConverter implements ValueConverter {

    @Override
    public String marshal(Object source) throws ValueConverterException {
        Address address = (Address) source;
        return address.street + "," + address.city + "," + address.postcode;
    }

    @Override
    public Object unmarshal(String source, Class<?> type) throws ValueConverterException {
        String[] addressDataArray = source.split(",");
        Address address = new Address();
        address.street = addressDataArray[0];
        address.city = addressDataArray[1];
        address.postcode = addressDataArray[2];
        return address;
    }
}
