package com.rentcar.dao;

import com.rentcar.config.DatabaseConnection;
import com.rentcar.model.Pembayaran;
import com.rentcar.util.ValidationUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;

public class PembayaranDAO {
    public boolean createPayment(Pembayaran pembayaran) throws SQLException {
        String sql = "INSERT INTO pembayaran (id_pembayaran, id_booking, jumlah, status, metode_pembayaran, bukti_pembayaran, tanggal_pembayaran) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            ensureProofColumn(conn);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, ValidationUtil.generateId("PAY"));
                stmt.setString(2, pembayaran.getIdBooking());
                stmt.setDouble(3, pembayaran.getJumlah());
                stmt.setString(4, pembayaran.getStatus());
                stmt.setString(5, pembayaran.getMetodePembayaran());
                stmt.setString(6, pembayaran.getBuktiPembayaran());
                stmt.setDate(7, Date.valueOf(pembayaran.getTanggalPembayaran()));
                return stmt.executeUpdate() > 0;
            }
        }
    }

    public boolean updateStatusPayment(String idPembayaran, String status, String metodePembayaran, String buktiPembayaran) throws SQLException {
        String sql = "UPDATE pembayaran SET status = ?, metode_pembayaran = ?, bukti_pembayaran = ?, tanggal_pembayaran = ? WHERE id_pembayaran = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            ensureProofColumn(conn);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, status);
                stmt.setString(2, metodePembayaran);
                stmt.setString(3, buktiPembayaran);
                stmt.setDate(4, Date.valueOf(LocalDate.now(ZoneId.systemDefault())));
                stmt.setString(5, idPembayaran);
                return stmt.executeUpdate() > 0;
            }
        }
    }

    public boolean updateStatusPayment(String idPembayaran, String status, String metodePembayaran) throws SQLException {
        return updateStatusPayment(idPembayaran, status, metodePembayaran, null);
    }

    public Pembayaran findByBooking(String idBooking) throws SQLException {
        String sql = "SELECT * FROM pembayaran WHERE id_booking = ?";
        try (Connection conn = DatabaseConnection.getConnection()) {
            ensureProofColumn(conn);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, idBooking);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new Pembayaran(
                                rs.getString("id_pembayaran"),
                                rs.getString("id_booking"),
                                rs.getDouble("jumlah"),
                                rs.getString("status"),
                                rs.getString("metode_pembayaran"),
                                rs.getString("bukti_pembayaran"),
                                rs.getDate("tanggal_pembayaran").toLocalDate()
                        );
                    }
                }
            }
        }
        return null;
    }

    private void ensureProofColumn(Connection conn) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        try (ResultSet columns = metaData.getColumns(conn.getCatalog(), null, "pembayaran", "bukti_pembayaran")) {
            if (columns.next()) {
                return;
            }
        }
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("ALTER TABLE pembayaran ADD COLUMN bukti_pembayaran VARCHAR(255) NULL AFTER metode_pembayaran");
        }
    }
}
