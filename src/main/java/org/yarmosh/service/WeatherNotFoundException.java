package org.yarmosh.service;

public class WeatherNotFoundException extends Exception {
    public WeatherNotFoundException(String message) {
        super(message);
    }
}

