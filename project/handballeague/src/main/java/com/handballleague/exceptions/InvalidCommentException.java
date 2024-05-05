package com.handballleague.exceptions;

public class InvalidCommentException extends RuntimeException{
    public InvalidCommentException(String errorMessage) {
        super(errorMessage);
    }
}
