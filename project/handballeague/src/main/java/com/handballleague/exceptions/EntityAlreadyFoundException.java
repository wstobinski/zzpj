package com.handballleague.exceptions;

public class EntityAlreadyFoundException extends RuntimeException {
    public EntityAlreadyFoundException(String message) {
        super(message);
    }
}