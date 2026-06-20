package com.rentcar.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    // Ganti localhost jika MySQL ada di komputer/server lain.
    // Ganti 3306 jika port MySQL Anda berbeda.
    // Ganti 67rentcar jika nama database yang di-import berbeda.
    private static final String DEFAULT_URL = "jdbc:mysql://127.0.0.1:3306/67rentcar?useSSL=false&serverTimezone=Asia/Jakarta&allowPublicKeyRetrieval=true";

    // Ganti USERNAME dan PASSWORD sesuai user MySQL Anda.
    // Default XAMPP biasanya username root dan password kosong.
    private static final String DEFAULT_USERNAME = "root";
    private static final String DEFAULT_PASSWORD = "";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException ex) {
            throw new SQLException("MySQL JDBC Driver tidak ditemukan. Pastikan mysql-connector-j ada di pom.xml dan WAR sudah dibuild ulang.", ex);
        }

        return DriverManager.getConnection(
                configValue("rentcar.db.url", "RENTCAR_DB_URL", DEFAULT_URL),
                configValue("rentcar.db.username", "RENTCAR_DB_USERNAME", DEFAULT_USERNAME),
                configValue("rentcar.db.password", "RENTCAR_DB_PASSWORD", DEFAULT_PASSWORD)
        );
    }

    private static String configValue(String propertyName, String envName, String fallback) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue;
        }
        String envValue = System.getenv(envName);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        return fallback;
    }
}
