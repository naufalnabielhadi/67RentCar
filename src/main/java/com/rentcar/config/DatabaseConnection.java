package com.rentcar.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    // Ganti localhost jika MySQL ada di komputer/server lain.
    // Ganti 3306 jika port MySQL Anda berbeda.
    // Ganti 67rentcar jika nama database yang di-import berbeda.
    private static final String URL = "jdbc:mysql://localhost:3306/67rentcar?useSSL=false&serverTimezone=Asia/Jakarta";

    // Ganti USERNAME dan PASSWORD sesuai user MySQL Anda.
    // Default XAMPP biasanya username root dan password kosong.
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName(DRIVER_CLASS);
        } catch (ClassNotFoundException ex) {
            throw new SQLException("MySQL JDBC Driver tidak ditemukan. Pastikan mysql-connector-j ada di pom.xml dan WAR sudah dibuild ulang.", ex);
        }

        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
