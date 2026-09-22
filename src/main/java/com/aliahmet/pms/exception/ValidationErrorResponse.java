package com.aliahmet.pms.exception;

import java.util.Map;

public class ValidationErrorResponse {

    private final Map<String, String> errors;

    public ValidationErrorResponse(Map<String, String> errors) {
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}