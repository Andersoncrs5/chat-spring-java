package com.chat.api.utils.exceptions;

public class InternalServerErrorException extends RuntimeException {
    public InternalServerErrorException(Exception message) {
        super(message);
    }
}
