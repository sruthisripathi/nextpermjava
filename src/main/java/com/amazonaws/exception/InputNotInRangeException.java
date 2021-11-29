package com.amazonaws.exception;

public class InputNotInRangeException extends IllegalArgumentException {
    public InputNotInRangeException(String message) {
        super(message);
    }
}
