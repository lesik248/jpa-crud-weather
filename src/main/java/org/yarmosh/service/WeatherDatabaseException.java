package org.yarmosh.service;

public class WeatherDatabaseException extends WeatherServiceException {
    public WeatherDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public WeatherDatabaseException(String message) {
        super(message);
    }
}

