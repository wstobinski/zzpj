package com.handballleague.exceptions;

public class ObjectNotFoundInDataBaseException extends RuntimeException {
    public ObjectNotFoundInDataBaseException(String errorMessage) {
        super(errorMessage);
    }
}
