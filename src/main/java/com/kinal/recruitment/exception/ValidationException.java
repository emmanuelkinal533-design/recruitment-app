package com.kinal.recruitment.exception;

public class ValidationException extends AppException {

    public ValidationException(String message) {
        super(message);
    }

    @Override
    public String getTitle() {
        return "Datos inválidos";
    }
}
