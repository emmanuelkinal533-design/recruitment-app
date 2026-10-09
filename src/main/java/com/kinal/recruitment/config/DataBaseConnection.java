package com.kinal.recruitment.config;

import com.kinal.recruitment.exception.DatabaseException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(Credentials.URL_DB, Credentials.USER_DB, Credentials.PASS_DB);
        } catch (SQLException e) {
            throw new DatabaseException(
                    "No se pudo conectar a la base de datos. Revisa Credentials.java y que MySQL esté encendido.", e);
        }
    }
}
