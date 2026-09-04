package com.aliahmet.pms.common.exception;

public class InvalidTestOrderStateException
        extends RuntimeException {

    public InvalidTestOrderStateException(String message) {
        super(message);
    }
}