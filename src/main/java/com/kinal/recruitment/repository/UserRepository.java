package com.kinal.recruitment.repository;

import com.kinal.recruitment.model.User;
import java.util.Optional;

public interface UserRepository {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    void save(User user);
}
