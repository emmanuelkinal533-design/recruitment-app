package com.kinal.recruitment.util;

import com.kinal.recruitment.dto.UserResponse;
import java.util.Optional;

public class SessionManager {

    private UserResponse currentUser;

    public void start(UserResponse user) {
        this.currentUser = user;
    }

    public Optional<UserResponse> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    public void end() {
        this.currentUser = null;
    }
}
