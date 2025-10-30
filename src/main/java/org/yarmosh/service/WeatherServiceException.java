package org.yarmosh.service;

public class WeatherServiceException extends Exception {
    public WeatherServiceException(String message) {
        super(message);
    }

    public WeatherServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public WeatherServiceException(Throwable e) {
        super(e);
    }
}
