package com.handballleague.exceptions;

import io.jsonwebtoken.io.IOException;

public class InitializerException extends IOException {
    public InitializerException(String message) {
        super(message);
    }

}
