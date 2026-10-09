package com.kinal.recruitment.controller;

import com.kinal.recruitment.dto.RegisterRequest;
import com.kinal.recruitment.exception.AppException;
import com.kinal.recruitment.service.AuthService;
import com.kinal.recruitment.util.Alerts;
import com.kinal.recruitment.util.SceneManager;
import com.kinal.recruitment.util.View;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    private final AuthService authService;
    private final SceneManager sceneManager;

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private PasswordField txtConfirmPassword;

    public RegisterController(AuthService authService, SceneManager sceneManager) {
        this.authService = authService;
        this.sceneManager = sceneManager;
    }

    @FXML
    private void handleRegister() {
        try {
            authService.register(new RegisterRequest(txtUsername.getText(), txtEmail.getText(),
                    txtPassword.getText(), txtConfirmPassword.getText()));
            Alerts.info("Registro", "Cuenta creada", "Ya puedes iniciar sesión.");
            sceneManager.show(View.LOGIN);
        } catch (AppException e) {
            Alerts.showException(e);
        }
    }

    @FXML
    private void handleGoToLogin() {
        sceneManager.show(View.LOGIN);
    }
}
