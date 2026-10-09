package com.kinal.recruitment.dto;

public record ApplicantRequest(String firstName, String lastName, String email,
        String phone, String address, String age) {
}
