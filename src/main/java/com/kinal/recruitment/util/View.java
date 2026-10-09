package com.kinal.recruitment.util;

public enum View {

    LOGIN("/view/login-view.fxml", "Reclutamiento - Iniciar sesión", false),
    REGISTER("/view/register-view.fxml", "Reclutamiento - Crear cuenta", false),
    APPLICANTS("/view/applicant-view.fxml", "Reclutamiento - Inventario de aspirantes", true);

    private final String fxmlPath;
    private final String title;
    private final boolean resizable;

    View(String fxmlPath, String title, boolean resizable) {
        this.fxmlPath = fxmlPath;
        this.title = title;
        this.resizable = resizable;
    }

    public String getFxmlPath() { return fxmlPath; }
    public String getTitle() { return title; }
    public boolean isResizable() { return resizable; }
}
