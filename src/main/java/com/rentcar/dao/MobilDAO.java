package com.rentcar.dao;

import com.rentcar.config.DatabaseConnection;
import com.rentcar.model.Mobil;
import com.rentcar.util.ValidationUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MobilDAO {
    public List<Mobil> findAll() throws SQLException {
        List<Mobil> mobilList = new ArrayList<>();
        String sql = "SELECT * FROM mobil ORDER BY id_mobil";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                mobilList.add(mapMobil(rs));
            }
        }
        return mobilList;
    }

    public List<Mobil> findAll(String query) throws SQLException {
        if (ValidationUtil.isBlank(query)) {
            return findAll();
        }

        List<Mobil> mobilList = new ArrayList<>();
        String sql = "SELECT * FROM mobil " +
                "WHERE LOWER(id_mobil) LIKE ? " +
                "OR LOWER(merk) LIKE ? " +
                "OR LOWER(model) LIKE ? " +
                "OR LOWER(plat_nomor) LIKE ? " +
                "OR LOWER(status_mobil) LIKE ? " +
                "ORDER BY id_mobil";
        String keyword = likeKeyword(query);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 1; i <= 5; i++) {
                stmt.setString(i, keyword);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mobilList.add(mapMobil(rs));
                }
            }
        }
        return mobilList;
    }

    public Mobil findById(String idMobil) throws SQLException {
        String sql = "SELECT * FROM mobil WHERE id_mobil = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idMobil);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapMobil(rs);
                }
            }
        }
        return null;
    }

    public List<Mobil> findAvailable() throws SQLException {
        List<Mobil> mobilList = new ArrayList<>();
        String sql = "SELECT * FROM mobil " +
                "WHERE status_mobil = ? " +
                "OR ((status_mobil IS NULL OR status_mobil = '') AND status = TRUE) " +
                "ORDER BY id_mobil";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, Mobil.STATUS_TERSEDIA);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mobilList.add(mapMobil(rs));
                }
            }
        }
        return mobilList;
    }

    public List<Mobil> findAvailable(String query) throws SQLException {
        if (ValidationUtil.isBlank(query)) {
            return findAvailable();
        }

        List<Mobil> mobilList = new ArrayList<>();
        String sql = "SELECT * FROM mobil " +
                "WHERE (status_mobil = ? " +
                "OR ((status_mobil IS NULL OR status_mobil = '') AND status = TRUE)) " +
                "AND (LOWER(merk) LIKE ? " +
                "OR LOWER(model) LIKE ? " +
                "OR LOWER(plat_nomor) LIKE ? " +
                "OR LOWER(transmisi) LIKE ? " +
                "OR LOWER(bahan_bakar) LIKE ? " +
                "OR LOWER(kapasitas) LIKE ?) " +
                "ORDER BY id_mobil";
        String keyword = likeKeyword(query);
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, Mobil.STATUS_TERSEDIA);
            for (int i = 2; i <= 7; i++) {
                stmt.setString(i, keyword);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    mobilList.add(mapMobil(rs));
                }
            }
        }
        return mobilList;
    }

    public boolean insert(Mobil mobil) throws SQLException {
        String sql = "INSERT INTO mobil (id_mobil, merk, model, plat_nomor, harga_sewa_per_hari, status, status_mobil, tahun, transmisi, bahan_bakar, kapasitas, gambar) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, ValidationUtil.generateId("MBL"));
            stmt.setString(2, mobil.getMerk());
            stmt.setString(3, mobil.getModel());
            stmt.setString(4, mobil.getPlatNomor());
            stmt.setDouble(5, mobil.getHargaSewaPerHari());
            stmt.setBoolean(6, mobil.isStatus());
            stmt.setString(7, mobil.getStatusMobil());
            stmt.setInt(8, mobil.getTahun());
            stmt.setString(9, mobil.getTransmisi());
            stmt.setString(10, mobil.getBahanBakar());
            stmt.setString(11, mobil.getKapasitas());
            stmt.setString(12, mobil.getGambar());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean update(Mobil mobil) throws SQLException {
        String sql = "UPDATE mobil SET merk = ?, model = ?, plat_nomor = ?, harga_sewa_per_hari = ?, status = ?, status_mobil = ?, tahun = ?, transmisi = ?, bahan_bakar = ?, kapasitas = ?, gambar = ? WHERE id_mobil = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, mobil.getMerk());
            stmt.setString(2, mobil.getModel());
            stmt.setString(3, mobil.getPlatNomor());
            stmt.setDouble(4, mobil.getHargaSewaPerHari());
            stmt.setBoolean(5, mobil.isStatus());
            stmt.setString(6, mobil.getStatusMobil());
            stmt.setInt(7, mobil.getTahun());
            stmt.setString(8, mobil.getTransmisi());
            stmt.setString(9, mobil.getBahanBakar());
            stmt.setString(10, mobil.getKapasitas());
            stmt.setString(11, mobil.getGambar());
            stmt.setString(12, mobil.getIdMobil());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean delete(String idMobil) throws SQLException {
        String sql = "DELETE FROM mobil WHERE id_mobil = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idMobil);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(String idMobil, boolean status, Connection conn) throws SQLException {
        String statusMobil = status ? Mobil.STATUS_TERSEDIA : Mobil.STATUS_DISEWA;
        String sql = "UPDATE mobil SET status = ?, status_mobil = ? WHERE id_mobil = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, status);
            stmt.setString(2, statusMobil);
            stmt.setString(3, idMobil);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean updateStatus(String idMobil, boolean status) throws SQLException {
        String statusMobil = status ? Mobil.STATUS_TERSEDIA : Mobil.STATUS_DISEWA;
        String sql = "UPDATE mobil SET status = ?, status_mobil = ? WHERE id_mobil = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, status);
            stmt.setString(2, statusMobil);
            stmt.setString(3, idMobil);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean releaseFromBooking(String idMobil, Connection conn) throws SQLException {
        String sql = "UPDATE mobil SET status = TRUE, status_mobil = ? WHERE id_mobil = ? AND status_mobil = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, Mobil.STATUS_TERSEDIA);
            stmt.setString(2, idMobil);
            stmt.setString(3, Mobil.STATUS_DISEWA);
            return stmt.executeUpdate() > 0;
        }
    }

    public long countByStatus(String statusMobil) throws SQLException {
        String sql;
        if (Mobil.STATUS_TERSEDIA.equals(statusMobil)) {
            sql = "SELECT COUNT(*) FROM mobil " +
                    "WHERE status_mobil = ? " +
                    "OR ((status_mobil IS NULL OR status_mobil = '') AND status = TRUE)";
        } else if (Mobil.STATUS_DISEWA.equals(statusMobil)) {
            sql = "SELECT COUNT(*) FROM mobil " +
                    "WHERE status_mobil = ? " +
                    "OR ((status_mobil IS NULL OR status_mobil = '') AND status = FALSE)";
        } else {
            sql = "SELECT COUNT(*) FROM mobil WHERE status_mobil = ?";
        }
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, statusMobil);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }
        }
    }

    private Mobil mapMobil(ResultSet rs) throws SQLException {
        return new Mobil(
                rs.getString("id_mobil"),
                rs.getString("merk"),
                rs.getString("model"),
                rs.getString("plat_nomor"),
                rs.getDouble("harga_sewa_per_hari"),
                rs.getBoolean("status"),
                rs.getInt("tahun"),
                rs.getString("transmisi"),
                rs.getString("bahan_bakar"),
                rs.getString("kapasitas"),
                normalizeImageName(
                        rs.getString("id_mobil"),
                        rs.getString("gambar"),
                        rs.getString("merk"),
                        rs.getString("model")
                ),
                rs.getString("status_mobil")
        );
    }

    public static String normalizeImageName(String idMobil, String gambar, String merk, String model) {
        String cleanImage = cleanAssetPath(gambar);
        if (isUploadImagePath(cleanImage) || isCustomImageFile(cleanImage)) {
            return cleanImage;
        }

        if ("MBL001".equalsIgnoreCase(idMobil)) {
            return "car-1.png";
        }
        if ("MBL002".equalsIgnoreCase(idMobil)) {
            return "car-2.png";
        }
        if ("MBL003".equalsIgnoreCase(idMobil)) {
            return "car-3.png";
        }
        if ("MBL004".equalsIgnoreCase(idMobil)) {
            return "car-4.png";
        }

        String slug = ((merk == null ? "" : merk) + " " + (model == null ? "" : model))
                .trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");

        if ("toyota-avanza".equals(slug)) {
            return "car-1.png";
        }
        if ("honda-brio".equals(slug)) {
            return "car-2.png";
        }
        if ("mitsubishi-xpander".equals(slug)) {
            return "car-3.png";
        }
        if ("daihatsu-terios".equals(slug)) {
            return "car-4.png";
        }
        if (slug.contains("alphard") || slug.contains("avanza") || slug.contains("toyota")) {
            return "car-1.png";
        }
        if (slug.contains("brio") || slug.contains("cr-v") || slug.contains("honda")) {
            return "car-2.png";
        }
        if (slug.contains("mercedes") || slug.contains("e300") || slug.contains("xpander")) {
            return "car-3.png";
        }
        if (slug.contains("pajero") || slug.contains("terios") || slug.contains("daihatsu")) {
            return "car-4.png";
        }
        return "default-car.svg";
    }

    public static String resolveAssetPath(String gambar) {
        String cleanImage = cleanAssetPath(gambar);
        if (cleanImage == null || cleanImage.isBlank()) {
            return "img/default-car.svg";
        }
        if (cleanImage.contains("/")) {
            return cleanImage;
        }
        return "img/" + cleanImage;
    }

    private static boolean isAllowedImagePath(String gambar) {
        if (gambar == null || gambar.isBlank()) {
            return false;
        }
        String lower = gambar.toLowerCase();
        return lower.endsWith(".jpg")
                || lower.endsWith(".jpeg")
                || lower.endsWith(".png")
                || lower.endsWith(".gif")
                || lower.endsWith(".svg");
    }

    private static boolean isUploadImagePath(String gambar) {
        return isAllowedImagePath(gambar) && gambar.replace("\\", "/").toLowerCase().startsWith("uploads/cars/");
    }

    private static boolean isCustomImageFile(String gambar) {
        if (!isAllowedImagePath(gambar) || gambar.contains("/") || gambar.contains("\\")) {
            return false;
        }
        return !"default-car.svg".equalsIgnoreCase(gambar);
    }

    private static String cleanAssetPath(String gambar) {
        if (gambar == null) {
            return null;
        }
        String cleanImage = gambar.trim().replace("\\", "/");
        while (cleanImage.startsWith("/")) {
            cleanImage = cleanImage.substring(1);
        }
        if (cleanImage.startsWith("assets/")) {
            cleanImage = cleanImage.substring("assets/".length());
        }
        return cleanImage;
    }

    private String likeKeyword(String query) {
        return "%" + query.trim().toLowerCase().replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
    }
}
