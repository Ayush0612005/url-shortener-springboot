package com.ayush.urlshortener.exception;

public class UnauthorizedUrlAccessException extends RuntimeException {

    public UnauthorizedUrlAccessException(String message) {
        super(message);
    }

}
