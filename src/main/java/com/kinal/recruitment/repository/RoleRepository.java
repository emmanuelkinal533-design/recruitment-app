package com.kinal.recruitment.repository;

import com.kinal.recruitment.model.Role;
import java.util.Optional;

public interface RoleRepository {

    Optional<Role> findByName(String name);

    Role save(Role role);
}
