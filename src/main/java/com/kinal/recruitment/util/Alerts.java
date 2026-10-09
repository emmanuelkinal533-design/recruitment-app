package com.kinal.recruitment.util;

import com.kinal.recruitment.exception.AppException;
import com.kinal.recruitment.exception.DatabaseException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

public final class Alerts {

    private Alerts() {
    }

    public static void info(String title, String header, String message) {
        show(AlertType.INFORMATION, title, header, message);
    }

    public static void warning(String title, String message) {
        show(AlertType.WARNING, title, null, message);
    }

    public static void error(String title, String message) {
        show(AlertType.ERROR, title, null, message);
    }

    public static void showException(AppException exception) {
        if (exception instanceof DatabaseException) {
            error(exception.getTitle(), exception.getMessage());
        } else {
            warning(exception.getTitle(), exception.getMessage());
        }
    }

    public static boolean confirm(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle(title);
        alert.setHeaderText(null);
        return alert.showAndWait().filter(button -> button == ButtonType.OK).isPresent();
    }

    private static void show(AlertType type, String title, String header, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
