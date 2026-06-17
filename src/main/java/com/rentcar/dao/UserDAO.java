package com.rentcar.dao;

import com.rentcar.config.DatabaseConnection;
import com.rentcar.model.Admin;
import com.rentcar.model.Pelanggan;
import com.rentcar.model.User;
import com.rentcar.util.ValidationUtil;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {
    public boolean register(Pelanggan pelanggan) throws SQLException {
        ensureUserColumns();
        String sql = "INSERT INTO users (id_user, username, email, password, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ValidationUtil.generateId("USR"));
            stmt.setString(2, pelanggan.getUsername());
            stmt.setString(3, pelanggan.getEmail());
            stmt.setString(4, pelanggan.getPassword());
            stmt.setString(5, "PELANGGAN");
            return stmt.executeUpdate() > 0;
        }
    }

    public User login(String email, String password) throws SQLException {
        ensureUserColumns();
        String sql = "SELECT * FROM users WHERE email = ? AND password = ? AND status_akun = 'AKTIF'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    public User findByEmail(String email) throws SQLException {
        ensureUserColumns();
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    public User findById(String idUser) throws SQLException {
        ensureUserColumns();
        String sql = "SELECT * FROM users WHERE id_user = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idUser);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    public boolean updateProfile(String idUser, String username, String email, String telepon, String fotoProfil) throws SQLException {
        ensureUserColumns();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(profileUpdateSql(conn, fotoProfil))) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, telepon);
            if (fotoProfil == null || fotoProfil.isBlank()) {
                stmt.setString(4, idUser);
            } else {
                stmt.setString(4, fotoProfil);
                stmt.setString(5, idUser);
            }
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updatePassword(String idUser, String currentPassword, String newPassword) throws SQLException {
        ensureUserColumns();
        String sql = "UPDATE users SET password = ? WHERE id_user = ? AND password = ? AND status_akun = 'AKTIF'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newPassword);
            stmt.setString(2, idUser);
            stmt.setString(3, currentPassword);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateIdentityCard(String idUser, String kartuIdentitas) throws SQLException {
        ensureUserColumns();
        String sql = "UPDATE users SET kartu_identitas = ? WHERE id_user = ? AND status_akun = 'AKTIF'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, kartuIdentitas);
            stmt.setString(2, idUser);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deactivate(String idUser) throws SQLException {
        ensureUserColumns();
        String sql = "UPDATE users SET status_akun = 'NONAKTIF' WHERE id_user = ? AND role = 'PELANGGAN'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idUser);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteAccountAndHistory(String idUser) throws SQLException {
        ensureUserColumns();
        String deletePembayaranSql = "DELETE FROM pembayaran WHERE id_booking IN (SELECT id_booking FROM booking WHERE id_user = ?)";
        String deleteDetailSql = "DELETE FROM detail_booking WHERE id_booking IN (SELECT id_booking FROM booking WHERE id_user = ?)";
        String deleteBookingSql = "DELETE FROM booking WHERE id_user = ?";
        String deleteUserSql = "DELETE FROM users WHERE id_user = ? AND role = 'PELANGGAN'";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement activeBookingStmt = conn.prepareStatement(activeBookingSql());
                 PreparedStatement deletePembayaranStmt = conn.prepareStatement(deletePembayaranSql);
                 PreparedStatement deleteDetailStmt = conn.prepareStatement(deleteDetailSql);
                 PreparedStatement deleteBookingStmt = conn.prepareStatement(deleteBookingSql);
                 PreparedStatement deleteUserStmt = conn.prepareStatement(deleteUserSql)) {
                activeBookingStmt.setString(1, idUser);
                try (ResultSet rs = activeBookingStmt.executeQuery()) {
                    if (rs.next() && rs.getLong(1) > 0) {
                        conn.rollback();
                        throw new SQLException("Akun tidak dapat dihapus saat masih memiliki booking aktif.");
                    }
                }

                deletePembayaranStmt.setString(1, idUser);
                deletePembayaranStmt.executeUpdate();

                deleteDetailStmt.setString(1, idUser);
                deleteDetailStmt.executeUpdate();

                deleteBookingStmt.setString(1, idUser);
                deleteBookingStmt.executeUpdate();

                deleteUserStmt.setString(1, idUser);
                boolean deleted = deleteUserStmt.executeUpdate() > 0;
                conn.commit();
                return deleted;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    private String activeBookingSql() {
        return "SELECT COUNT(*) FROM booking WHERE id_user = ? " +
                "AND status IN ('MENUNGGU_KONFIRMASI', 'DIKONFIRMASI', 'MENUNGGU_PEMBAYARAN', 'DIBAYAR')";
    }

    private User mapUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        User user;
        if ("ADMIN".equals(role)) {
            user = new Admin(
                    rs.getString("id_user"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password"),
                    role
            );
        } else {
            user = new Pelanggan(
                    rs.getString("id_user"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("password"),
                    role
            );
        }

        user.setTelepon(getOptionalString(rs, "telepon"));
        user.setFotoProfil(getOptionalString(rs, "foto_profil"));
        user.setKartuIdentitas(getOptionalString(rs, "kartu_identitas"));
        String statusAkun = getOptionalString(rs, "status_akun");
        user.setStatusAkun(statusAkun == null ? "AKTIF" : statusAkun);
        return user;
    }

    private String profileUpdateSql(Connection conn, String fotoProfil) throws SQLException {
        if (fotoProfil == null || fotoProfil.isBlank()) {
            return "UPDATE users SET username = ?, email = ?, telepon = ? WHERE id_user = ? AND status_akun = 'AKTIF'";
        }
        if (!hasColumn(conn, "users", "foto_profil")) {
            throw new SQLException("Kolom foto_profil belum ada. Jalankan SQL ALTER TABLE users ADD COLUMN foto_profil VARCHAR(255) NULL;");
        }
        return "UPDATE users SET username = ?, email = ?, telepon = ?, foto_profil = ? WHERE id_user = ? AND status_akun = 'AKTIF'";
    }

    private String getOptionalString(ResultSet rs, String columnName) throws SQLException {
        try {
            return rs.getString(columnName);
        } catch (SQLException ex) {
            return null;
        }
    }

    private void ensureUserColumns() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (!hasColumn(conn, "users", "telepon")) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("ALTER TABLE users ADD COLUMN telepon VARCHAR(30) NULL AFTER email");
                }
            }
            if (!hasColumn(conn, "users", "status_akun")) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("ALTER TABLE users ADD COLUMN status_akun ENUM('AKTIF', 'NONAKTIF') NOT NULL DEFAULT 'AKTIF' AFTER role");
                }
            }
            if (!hasColumn(conn, "users", "foto_profil")) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("ALTER TABLE users ADD COLUMN foto_profil VARCHAR(255) NULL AFTER status_akun");
                }
            }
            if (!hasColumn(conn, "users", "kartu_identitas")) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate("ALTER TABLE users ADD COLUMN kartu_identitas VARCHAR(255) NULL AFTER foto_profil");
                }
            }
        }
    }

    private boolean hasColumn(Connection conn, String tableName, String columnName) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        try (ResultSet rs = metaData.getColumns(conn.getCatalog(), null, tableName, columnName)) {
            return rs.next();
        }
    }
}
