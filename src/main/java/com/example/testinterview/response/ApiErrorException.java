package com.example.testinterview.response;

public class ApiErrorException extends RuntimeException {
    private final String code;

    public ApiErrorException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
