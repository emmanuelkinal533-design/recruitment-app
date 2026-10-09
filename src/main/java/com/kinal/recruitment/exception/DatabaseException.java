package com.kinal.recruitment.exception;

public class DatabaseException extends AppException {

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public String getTitle() {
        return "Error de base de datos";
    }
}
