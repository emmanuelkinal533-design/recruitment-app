package com.kinal.recruitment.dto;

public record ApplicantResponse(int id, String firstName, String lastName, String email,
        String phone, String address, int age) {

    public String fullName() {
        return firstName + " " + lastName;
    }
}
