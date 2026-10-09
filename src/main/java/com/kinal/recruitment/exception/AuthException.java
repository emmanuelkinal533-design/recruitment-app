package com.kinal.recruitment.exception;

public class AuthException extends AppException {

    public AuthException(String message) {
        super(message);
    }

    @Override
    public String getTitle() {
        return "Autenticación";
    }
}
