package com.kinal.recruitment.service;

import com.kinal.recruitment.dto.LoginRequest;
import com.kinal.recruitment.dto.RegisterRequest;
import com.kinal.recruitment.dto.UserResponse;

public interface AuthService {

    void register(RegisterRequest request);

    UserResponse login(LoginRequest request);
}
