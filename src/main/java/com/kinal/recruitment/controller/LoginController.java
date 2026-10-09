package com.kinal.recruitment.controller;

import com.kinal.recruitment.dto.LoginRequest;
import com.kinal.recruitment.dto.UserResponse;
import com.kinal.recruitment.exception.AppException;
import com.kinal.recruitment.service.AuthService;
import com.kinal.recruitment.util.Alerts;
import com.kinal.recruitment.util.SceneManager;
import com.kinal.recruitment.util.SessionManager;
import com.kinal.recruitment.util.View;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    private final AuthService authService;
    private final SessionManager session;
    private final SceneManager sceneManager;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    public LoginController(AuthService authService, SessionManager session, SceneManager sceneManager) {
        this.authService = authService;
        this.session = session;
        this.sceneManager = sceneManager;
    }

    @FXML
    private void handleLogin() {
        try {
            UserResponse user = authService.login(new LoginRequest(txtEmail.getText(), txtPassword.getText()));
            session.start(user);
            Alerts.info("Inicio de sesión", "Bienvenido, " + user.username(), "Rol del usuario: " + user.role());
            sceneManager.show(View.APPLICANTS);
        } catch (AppException e) {
            Alerts.showException(e);
        }
    }

    @FXML
    private void handleGoToRegister() {
        sceneManager.show(View.REGISTER);
    }
}
