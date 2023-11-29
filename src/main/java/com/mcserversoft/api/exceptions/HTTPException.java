package com.mcserversoft.api.exceptions;

public class HTTPException extends Exception {

    private int code;

    public HTTPException(int code, String message) {
        super(message);
    }

    public int getCode() {
        return this.code;
    }
}
