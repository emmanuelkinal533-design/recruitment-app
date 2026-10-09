package com.kinal.recruitment.util;

import com.kinal.recruitment.exception.AppException;
import java.io.IOException;
import java.net.URL;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

public class SceneManager {

    private static final String STYLESHEET = "/css/styles.css";

    private final Stage stage;
    private final Callback<Class<?>, Object> controllerFactory;

    public SceneManager(Stage stage, Callback<Class<?>, Object> controllerFactory) {
        this.stage = stage;
        this.controllerFactory = controllerFactory;
    }

    public void show(View view) {
        URL fxml = SceneManager.class.getResource(view.getFxmlPath());
        if (fxml == null) {
            throw new AppException("No se encontró la pantalla " + view.getFxmlPath());
        }
        try {
            FXMLLoader loader = new FXMLLoader(fxml);
            loader.setControllerFactory(controllerFactory);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            URL css = SceneManager.class.getResource(STYLESHEET);
            if (css != null) {
                scene.getStylesheets().add(css.toExternalForm());
            }
            stage.setScene(scene);
            stage.setTitle(view.getTitle());
            stage.setResizable(view.isResizable());
            stage.sizeToScene();
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            throw new AppException("No se pudo cargar la pantalla " + view.getFxmlPath(), e);
        }
    }
}
