package com.kinal.recruitment.exception;

public class ResourceNotFoundException extends AppException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    @Override
    public String getTitle() {
        return "No encontrado";
    }
}
