package com.kinal.recruitment.service.impl;

import com.kinal.recruitment.dto.LoginRequest;
import com.kinal.recruitment.dto.RegisterRequest;
import com.kinal.recruitment.dto.UserResponse;
import com.kinal.recruitment.exception.AuthException;
import com.kinal.recruitment.exception.ValidationException;
import com.kinal.recruitment.model.Role;
import com.kinal.recruitment.model.User;
import com.kinal.recruitment.repository.RoleRepository;
import com.kinal.recruitment.repository.UserRepository;
import com.kinal.recruitment.security.PasswordHasher;
import com.kinal.recruitment.service.AuthService;
import java.util.regex.Pattern;

public class AuthServiceImpl implements AuthService {

    private static final String DEFAULT_ROLE = "USER";
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern USERNAME = Pattern.compile("^[\\w.-]{3,50}$");

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordHasher passwordHasher;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
            PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public void register(RegisterRequest request) {
        String username = clean(request.username());
        String email = clean(request.email()).toLowerCase();
        String password = request.password() == null ? "" : request.password();
        String confirmPassword = request.confirmPassword() == null ? "" : request.confirmPassword();

        validateRegistration(username, email, password, confirmPassword);

        if (userRepository.existsByUsername(username)) {
            throw new AuthException("Ya existe una cuenta con ese nombre de usuario.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new AuthException("Ya existe una cuenta con ese correo.");
        }

        Role role = roleRepository.findByName(DEFAULT_ROLE)
                .orElseGet(() -> roleRepository.save(new Role(0, DEFAULT_ROLE)));
        userRepository.save(new User(0, username, email, passwordHasher.hash(password), role));
    }

    @Override
    public UserResponse login(LoginRequest request) {
        String email = clean(request.email()).toLowerCase();
        String password = request.password() == null ? "" : request.password();

        if (email.isEmpty() || password.isEmpty()) {
            throw new ValidationException("Ingresa tu correo y tu contraseña.");
        }

        User user = userRepository.findByEmail(email)
                .filter(found -> passwordHasher.matches(password, found.getPasswordHash()))
                .orElseThrow(() -> new AuthException("Correo o contraseña incorrectos."));

        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole().getName());
    }

    private void validateRegistration(String username, String email, String password, String confirmPassword) {
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            throw new ValidationException("Todos los campos son obligatorios.");
        }
        if (!USERNAME.matcher(username).matches()) {
            throw new ValidationException(
                    "El usuario debe tener entre 3 y 50 caracteres (letras, números, punto, guion o guion bajo).");
        }
        if (email.length() > 100 || !EMAIL.matcher(email).matches()) {
            throw new ValidationException("Ingresa un correo válido.");
        }
        if (password.length() < MIN_PASSWORD_LENGTH) {
            throw new ValidationException("La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres.");
        }
        if (!password.equals(confirmPassword)) {
            throw new ValidationException("Las contraseñas no coinciden.");
        }
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }
}
