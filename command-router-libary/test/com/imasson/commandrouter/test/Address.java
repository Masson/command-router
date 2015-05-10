package com.imasson.commandrouter.test;


public class Address {
    public String street;
    public String city;
    public String postcode;

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", postcode='" + postcode + '\'' +
                '}';
    }
}
