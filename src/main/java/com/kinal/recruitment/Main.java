package com.kinal.recruitment;

import com.kinal.recruitment.config.DataBaseConnection;
import com.kinal.recruitment.controller.ApplicantController;
import com.kinal.recruitment.controller.LoginController;
import com.kinal.recruitment.controller.RegisterController;
import com.kinal.recruitment.exception.AppException;
import com.kinal.recruitment.repository.ApplicantRepository;
import com.kinal.recruitment.repository.RoleRepository;
import com.kinal.recruitment.repository.UserRepository;
import com.kinal.recruitment.repository.jdbc.JdbcApplicantRepository;
import com.kinal.recruitment.repository.jdbc.JdbcRoleRepository;
import com.kinal.recruitment.repository.jdbc.JdbcUserRepository;
import com.kinal.recruitment.security.PasswordHasher;
import com.kinal.recruitment.service.ApplicantService;
import com.kinal.recruitment.service.AuthService;
import com.kinal.recruitment.service.impl.ApplicantServiceImpl;
import com.kinal.recruitment.service.impl.AuthServiceImpl;
import com.kinal.recruitment.util.Alerts;
import com.kinal.recruitment.util.SceneManager;
import com.kinal.recruitment.util.SessionManager;
import com.kinal.recruitment.util.View;
import java.util.Map;
import java.util.function.Supplier;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class Main extends Application {

    private Map<Class<?>, Supplier<Object>> controllers;

    @Override
    public void start(Stage stage) {
        Thread.setDefaultUncaughtExceptionHandler((thread, error) -> Platform.runLater(
                () -> Alerts.error("Error inesperado", "Ocurrió un problema: " + error.getMessage())));
        try {
            SceneManager sceneManager = wireDependencies(stage);
            sceneManager.show(View.LOGIN);
        } catch (AppException e) {
            Alerts.showException(e);
        }
    }

    private SceneManager wireDependencies(Stage stage) {
        DataBaseConnection dataBase = new DataBaseConnection();

        RoleRepository roleRepository = new JdbcRoleRepository(dataBase);
        UserRepository userRepository = new JdbcUserRepository(dataBase);
        ApplicantRepository applicantRepository = new JdbcApplicantRepository(dataBase);

        AuthService authService = new AuthServiceImpl(userRepository, roleRepository, new PasswordHasher());
        ApplicantService applicantService = new ApplicantServiceImpl(applicantRepository);
        SessionManager session = new SessionManager();

        SceneManager sceneManager = new SceneManager(stage, this::createController);
        controllers = Map.of(
                LoginController.class, () -> new LoginController(authService, session, sceneManager),
                RegisterController.class, () -> new RegisterController(authService, sceneManager),
                ApplicantController.class, () -> new ApplicantController(applicantService, session, sceneManager));
        return sceneManager;
    }

    private Object createController(Class<?> type) {
        Supplier<Object> factory = controllers.get(type);
        if (factory == null) {
            throw new AppException("No hay un controlador registrado para " + type.getSimpleName());
        }
        return factory.get();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
