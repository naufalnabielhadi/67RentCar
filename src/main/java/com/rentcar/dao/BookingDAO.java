package com.rentcar.dao;

import com.rentcar.config.DatabaseConnection;
import com.rentcar.model.BookingMobil;
import com.rentcar.model.DetailBooking;
import com.rentcar.model.Mobil;
import com.rentcar.util.ValidationUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingDAO {
    public static final String MOBIL_TIDAK_TERSEDIA = "Mobil ini sedang tidak tersedia. Silakan pilih kendaraan lain.";
    public static final String STATUS_MENUNGGU_KONFIRMASI = "MENUNGGU_KONFIRMASI";
    public static final String STATUS_DIKONFIRMASI = "DIKONFIRMASI";
    public static final String STATUS_MENUNGGU_PEMBAYARAN = "MENUNGGU_PEMBAYARAN";
    public static final String STATUS_DIBAYAR = "DIBAYAR";
    public static final String STATUS_DITOLAK = "DITOLAK";
    public static final String STATUS_DIBATALKAN = "DIBATALKAN";
    public static final String STATUS_SELESAI = "SELESAI";
    public static final String PAYMENT_LUNAS = "LUNAS";
    public static final String PAYMENT_DIKEMBALIKAN = "DIKEMBALIKAN";
    public static final String PAYMENT_TIDAK_DITERUSKAN = "TIDAK_DITERUSKAN";
    private final MobilDAO mobilDAO = new MobilDAO();

    public String createBooking(String idUser, String idMobil, LocalDate tanggalSewa, LocalDate tanggalKembali) throws SQLException {
        long durasi = Math.max(1, ChronoUnit.DAYS.between(tanggalSewa, tanggalKembali));
        String idBooking = ValidationUtil.generateId("BKG");
        String idDetail = ValidationUtil.generateId("DTL");

        String lockSql = "SELECT harga_sewa_per_hari, status_mobil FROM mobil WHERE id_mobil = ? FOR UPDATE";
        String bookingSql = "INSERT INTO booking (id_booking, id_user, status, total_biaya) VALUES (?, ?, ?, ?)";
        String detailSql = "INSERT INTO detail_booking (id_detail, id_booking, id_mobil, tanggal_sewa, tanggal_kembali, subtotal) VALUES (?, ?, ?, ?, ?, ?)";
        String updateMobilSql = "UPDATE mobil SET status = FALSE, status_mobil = ? WHERE id_mobil = ? AND status_mobil = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement lockStmt = conn.prepareStatement(lockSql);
                 PreparedStatement bookingStmt = conn.prepareStatement(bookingSql);
                 PreparedStatement updateMobilStmt = conn.prepareStatement(updateMobilSql);
                 PreparedStatement detailStmt = conn.prepareStatement(detailSql)) {
                lockStmt.setString(1, idMobil);
                double hargaSewaPerHari;
                try (ResultSet rs = lockStmt.executeQuery()) {
                    if (!rs.next() || !Mobil.STATUS_TERSEDIA.equals(rs.getString("status_mobil"))) {
                        conn.rollback();
                        throw new SQLException(MOBIL_TIDAK_TERSEDIA);
                    }
                    hargaSewaPerHari = rs.getDouble("harga_sewa_per_hari");
                }

                double total = hargaSewaPerHari * durasi;

                updateMobilStmt.setString(1, Mobil.STATUS_DISEWA);
                updateMobilStmt.setString(2, idMobil);
                updateMobilStmt.setString(3, Mobil.STATUS_TERSEDIA);
                if (updateMobilStmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new SQLException(MOBIL_TIDAK_TERSEDIA);
                }

                bookingStmt.setString(1, idBooking);
                bookingStmt.setString(2, idUser);
                bookingStmt.setString(3, STATUS_MENUNGGU_KONFIRMASI);
                bookingStmt.setDouble(4, total);
                bookingStmt.executeUpdate();

                detailStmt.setString(1, idDetail);
                detailStmt.setString(2, idBooking);
                detailStmt.setString(3, idMobil);
                detailStmt.setDate(4, Date.valueOf(tanggalSewa));
                detailStmt.setDate(5, Date.valueOf(tanggalKembali));
                detailStmt.setDouble(6, total);
                detailStmt.executeUpdate();

                conn.commit();
                return idBooking;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public boolean cancelBooking(String idBooking) throws SQLException {
        String selectSql = "SELECT b.status, d.id_mobil, p.status AS status_pembayaran " +
                "FROM booking b " +
                "JOIN detail_booking d ON b.id_booking = d.id_booking " +
                "LEFT JOIN pembayaran p ON b.id_booking = p.id_booking " +
                "WHERE b.id_booking = ?";
        String updateSql = "UPDATE booking SET status = ? WHERE id_booking = ? AND status = ?";
        String updatePaymentSql = "UPDATE pembayaran SET status = ? WHERE id_booking = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                 PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                 PreparedStatement updatePaymentStmt = conn.prepareStatement(updatePaymentSql)) {
                selectStmt.setString(1, idBooking);
                String idMobil;
                String status;
                String statusPembayaran;
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    status = rs.getString("status");
                    idMobil = rs.getString("id_mobil");
                    statusPembayaran = rs.getString("status_pembayaran");
                }

                if (!STATUS_MENUNGGU_KONFIRMASI.equals(status)) {
                    conn.rollback();
                    return false;
                }

                updateStmt.setString(1, STATUS_DIBATALKAN);
                updateStmt.setString(2, idBooking);
                updateStmt.setString(3, STATUS_MENUNGGU_KONFIRMASI);
                boolean updated = updateStmt.executeUpdate() > 0;
                if (updated) {
                    updatePaymentStmt.setString(1, refundedPaymentStatus(statusPembayaran));
                    updatePaymentStmt.setString(2, idBooking);
                    updatePaymentStmt.executeUpdate();
                    mobilDAO.releaseFromBooking(idMobil, conn);
                }
                conn.commit();
                return updated;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public boolean markAsPaid(String idBooking) throws SQLException {
        String sql = "UPDATE booking SET status = CASE WHEN status = ? THEN ? ELSE status END WHERE id_booking = ? AND status IN (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, STATUS_MENUNGGU_PEMBAYARAN);
            stmt.setString(2, STATUS_DIKONFIRMASI);
            stmt.setString(3, idBooking);
            stmt.setString(4, STATUS_DIKONFIRMASI);
            stmt.setString(5, STATUS_MENUNGGU_PEMBAYARAN);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean confirmBooking(String idBooking) throws SQLException {
        String sql = "UPDATE booking SET status = ? WHERE id_booking = ? AND status = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, STATUS_DIKONFIRMASI);
            stmt.setString(2, idBooking);
            stmt.setString(3, STATUS_MENUNGGU_KONFIRMASI);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean rejectBooking(String idBooking) throws SQLException {
        String selectSql = "SELECT d.id_mobil, p.status AS status_pembayaran " +
                "FROM booking b " +
                "JOIN detail_booking d ON b.id_booking = d.id_booking " +
                "LEFT JOIN pembayaran p ON b.id_booking = p.id_booking " +
                "WHERE b.id_booking = ? AND b.status = ?";
        String updateSql = "UPDATE booking SET status = ? WHERE id_booking = ? AND status = ?";
        String updatePaymentSql = "UPDATE pembayaran SET status = ? WHERE id_booking = ?";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                 PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                 PreparedStatement updatePaymentStmt = conn.prepareStatement(updatePaymentSql)) {
                selectStmt.setString(1, idBooking);
                selectStmt.setString(2, STATUS_MENUNGGU_KONFIRMASI);
                String idMobil;
                String statusPembayaran;
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    idMobil = rs.getString("id_mobil");
                    statusPembayaran = rs.getString("status_pembayaran");
                }

                updateStmt.setString(1, STATUS_DITOLAK);
                updateStmt.setString(2, idBooking);
                updateStmt.setString(3, STATUS_MENUNGGU_KONFIRMASI);
                boolean updated = updateStmt.executeUpdate() > 0;
                if (updated) {
                    updatePaymentStmt.setString(1, refundedPaymentStatus(statusPembayaran));
                    updatePaymentStmt.setString(2, idBooking);
                    updatePaymentStmt.executeUpdate();
                    mobilDAO.releaseFromBooking(idMobil, conn);
                }
                conn.commit();
                return updated;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public boolean completeBooking(String idBooking) throws SQLException {
        String selectSql = "SELECT b.status, d.id_mobil, d.tanggal_kembali " +
                "FROM booking b JOIN detail_booking d ON b.id_booking = d.id_booking " +
                "WHERE b.id_booking = ? FOR UPDATE";
        String updateBookingSql = "UPDATE booking SET status = ? WHERE id_booking = ? AND status IN (?, ?)";
        String activeBookingSql = "SELECT COUNT(*) FROM booking b " +
                "JOIN detail_booking d ON b.id_booking = d.id_booking " +
                "WHERE d.id_mobil = ? AND b.id_booking <> ? " +
                "AND b.status IN (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql);
                 PreparedStatement updateBookingStmt = conn.prepareStatement(updateBookingSql);
                 PreparedStatement activeBookingStmt = conn.prepareStatement(activeBookingSql)) {

                selectStmt.setString(1, idBooking);
                String statusBooking;
                String idMobil;
                LocalDate tanggalKembali;
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (!rs.next()) {
                        conn.rollback();
                        throw new SQLException("Booking tidak ditemukan.");
                    }
                    statusBooking = rs.getString("status");
                    idMobil = rs.getString("id_mobil");
                    tanggalKembali = rs.getDate("tanggal_kembali").toLocalDate();
                }

                if (!STATUS_DIKONFIRMASI.equals(statusBooking) && !STATUS_DIBAYAR.equals(statusBooking)) {
                    conn.rollback();
                    throw new SQLException("Booking sudah tidak aktif atau sudah selesai.");
                }

                if (tanggalKembali.isAfter(currentDate())) {
                    conn.rollback();
                    throw new SQLException("Booking belum dapat diselesaikan karena tanggal kembali belum tercapai.");
                }

                updateBookingStmt.setString(1, STATUS_SELESAI);
                updateBookingStmt.setString(2, idBooking);
                updateBookingStmt.setString(3, STATUS_DIKONFIRMASI);
                updateBookingStmt.setString(4, STATUS_DIBAYAR);
                if (updateBookingStmt.executeUpdate() == 0) {
                    conn.rollback();
                    return false;
                }

                activeBookingStmt.setString(1, idMobil);
                activeBookingStmt.setString(2, idBooking);
                activeBookingStmt.setString(3, STATUS_MENUNGGU_KONFIRMASI);
                activeBookingStmt.setString(4, STATUS_DIKONFIRMASI);
                activeBookingStmt.setString(5, STATUS_MENUNGGU_PEMBAYARAN);
                activeBookingStmt.setString(6, STATUS_DIBAYAR);
                boolean hasActiveBooking;
                try (ResultSet rs = activeBookingStmt.executeQuery()) {
                    hasActiveBooking = rs.next() && rs.getLong(1) > 0;
                }

                if (!hasActiveBooking) {
                    mobilDAO.markAsReturned(idMobil, conn);
                }

                conn.commit();
                return true;
            } catch (SQLException ex) {
                conn.rollback();
                throw ex;
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }

    public boolean updateStatus(String idBooking, String status) throws SQLException {
        String sql = "UPDATE booking SET status = ? WHERE id_booking = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setString(2, idBooking);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean hasActiveBookingByUser(String idUser) throws SQLException {
        String sql = "SELECT COUNT(*) FROM booking WHERE id_user = ? AND status IN (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idUser);
            stmt.setString(2, STATUS_MENUNGGU_KONFIRMASI);
            stmt.setString(3, STATUS_DIKONFIRMASI);
            stmt.setString(4, STATUS_MENUNGGU_PEMBAYARAN);
            stmt.setString(5, STATUS_DIBAYAR);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getLong(1) > 0;
            }
        }
    }

    public List<Map<String, Object>> findByUser(String idUser) throws SQLException {
        String sql = baseHistoryQuery() + " WHERE b.id_user = ? ORDER BY b.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idUser);
            try (ResultSet rs = stmt.executeQuery()) {
                return mapHistoryList(rs);
            }
        }
    }

    public List<Map<String, Object>> findByUserFiltered(String idUser, String query) throws SQLException {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder(baseHistoryQuery()).append(" WHERE b.id_user = ?");
        params.add(idUser);

        if (query != null && !query.trim().isEmpty()) {
            String keyword = "%" + query.trim() + "%";
            sql.append(" AND (b.id_booking LIKE ? OR m.merk LIKE ? OR m.model LIKE ? OR m.plat_nomor LIKE ? OR b.status LIKE ? OR p.status LIKE ?)");
            params.add(keyword);
            params.add(keyword);
            params.add(keyword);
            params.add(keyword);
            params.add(keyword);
            params.add(keyword);
        }

        sql.append(" ORDER BY b.created_at DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return mapHistoryList(rs);
            }
        }
    }

    public List<Map<String, Object>> findAll() throws SQLException {
        String sql = baseHistoryQuery() + " ORDER BY b.created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return mapHistoryList(rs);
        }
    }

    public List<Map<String, Object>> findAllFiltered(String query, LocalDate tanggalSewa) throws SQLException {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder(baseHistoryQuery()).append(" WHERE 1 = 1");

        if (tanggalSewa != null) {
            sql.append(" AND d.tanggal_sewa = ?");
            params.add(Date.valueOf(tanggalSewa));
        }

        if (query != null && !query.trim().isEmpty()) {
            String keyword = "%" + query.trim() + "%";
            sql.append(" AND (b.id_booking LIKE ? OR u.username LIKE ? OR m.merk LIKE ? OR m.model LIKE ?)");
            params.add(keyword);
            params.add(keyword);
            params.add(keyword);
            params.add(keyword);
        }

        sql.append(" ORDER BY d.tanggal_sewa DESC, b.created_at DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return mapHistoryList(rs);
            }
        }
    }

    public long countBookingBulanIni() throws SQLException {
        String sql = "SELECT COUNT(*) FROM booking b JOIN detail_booking d ON b.id_booking = d.id_booking " +
                "WHERE MONTH(d.tanggal_sewa) = MONTH(CURRENT_DATE()) AND YEAR(d.tanggal_sewa) = YEAR(CURRENT_DATE())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        }
    }

    public double sumPendapatanBulanIni() throws SQLException {
        String sql = "SELECT COALESCE(SUM(b.total_biaya), 0) FROM booking b " +
                "JOIN detail_booking d ON b.id_booking = d.id_booking " +
                "JOIN pembayaran p ON b.id_booking = p.id_booking " +
                "WHERE p.status = 'LUNAS' " +
                "AND b.status NOT IN ('DITOLAK', 'DIBATALKAN') " +
                "AND MONTH(d.tanggal_sewa) = MONTH(CURRENT_DATE()) " +
                "AND YEAR(d.tanggal_sewa) = YEAR(CURRENT_DATE())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getDouble(1) : 0;
        }
    }

    public long countMobilDibooking() throws SQLException {
        String sql = "SELECT COUNT(DISTINCT d.id_mobil) FROM booking b " +
                "JOIN detail_booking d ON b.id_booking = d.id_booking " +
                "JOIN pembayaran p ON b.id_booking = p.id_booking " +
                "WHERE p.status = 'LUNAS' AND b.status IN ('MENUNGGU_KONFIRMASI', 'DIKONFIRMASI', 'MENUNGGU_PEMBAYARAN', 'DIBAYAR')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        }
    }

    public Map<String, Object> findMobilFavorit() throws SQLException {
        String sql = "SELECT m.id_mobil, m.merk, m.model, COUNT(*) AS jumlah_pesanan, MAX(d.tanggal_sewa) AS tanggal_terbaru " +
                "FROM booking b " +
                "JOIN detail_booking d ON b.id_booking = d.id_booking " +
                "JOIN mobil m ON d.id_mobil = m.id_mobil " +
                "JOIN pembayaran p ON b.id_booking = p.id_booking " +
                "WHERE p.status = 'LUNAS' AND b.status NOT IN ('DITOLAK', 'DIBATALKAN') " +
                "GROUP BY m.id_mobil, m.merk, m.model " +
                "ORDER BY jumlah_pesanan DESC, tanggal_terbaru DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (!rs.next()) {
                return null;
            }
            Map<String, Object> row = new HashMap<>();
            row.put("idMobil", rs.getString("id_mobil"));
            row.put("merk", rs.getString("merk"));
            row.put("model", rs.getString("model"));
            row.put("jumlahPesanan", rs.getLong("jumlah_pesanan"));
            row.put("tanggalTerbaru", rs.getDate("tanggal_terbaru"));
            return row;
        }
    }

    public List<Map<String, Object>> findTransaksiTerbaru(int limit) throws SQLException {
        String sql = baseHistoryQuery() + " ORDER BY b.created_at DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, Math.max(1, limit));
            try (ResultSet rs = stmt.executeQuery()) {
                return mapHistoryList(rs);
            }
        }
    }

    public BookingMobil findById(String idBooking) throws SQLException {
        String sql = "SELECT * FROM booking WHERE id_booking = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idBooking);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new BookingMobil(
                            rs.getString("id_booking"),
                            rs.getString("id_user"),
                            rs.getString("status"),
                            rs.getDouble("total_biaya")
                    );
                }
            }
        }
        return null;
    }

    public Map<String, Object> findDetailById(String idBooking) throws SQLException {
        String sql = baseHistoryQuery() + " WHERE b.id_booking = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idBooking);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapHistory(rs);
                }
            }
        }
        return null;
    }

    public DetailBooking findDetailBooking(String idBooking) throws SQLException {
        String sql = "SELECT * FROM detail_booking WHERE id_booking = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, idBooking);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new DetailBooking(
                            rs.getString("id_detail"),
                            rs.getString("id_booking"),
                            rs.getString("id_mobil"),
                            rs.getDate("tanggal_sewa").toLocalDate(),
                            rs.getDate("tanggal_kembali").toLocalDate(),
                            rs.getDouble("subtotal")
                    );
                }
            }
        }
        return null;
    }

    private String baseHistoryQuery() {
        return "SELECT b.id_booking, b.id_user, u.username, b.status AS status_booking, b.total_biaya, " +
                "d.id_detail, d.id_mobil, d.tanggal_sewa, d.tanggal_kembali, d.subtotal, " +
                "m.merk, m.model, m.plat_nomor, m.harga_sewa_per_hari, m.tahun, m.transmisi, " +
                "m.bahan_bakar, m.kapasitas, m.gambar, " +
                "p.id_pembayaran, p.status AS status_pembayaran, p.metode_pembayaran, p.tanggal_pembayaran " +
                "FROM booking b " +
                "JOIN users u ON b.id_user = u.id_user " +
                "JOIN detail_booking d ON b.id_booking = d.id_booking " +
                "JOIN mobil m ON d.id_mobil = m.id_mobil " +
                "LEFT JOIN pembayaran p ON b.id_booking = p.id_booking";
    }

    private List<Map<String, Object>> mapHistoryList(ResultSet rs) throws SQLException {
        List<Map<String, Object>> list = new ArrayList<>();
        while (rs.next()) {
            list.add(mapHistory(rs));
        }
        return list;
    }

    private Map<String, Object> mapHistory(ResultSet rs) throws SQLException {
        Map<String, Object> row = new HashMap<>();
        String statusBooking = rs.getString("status_booking");
        String statusPembayaran = rs.getString("status_pembayaran");
        row.put("idBooking", rs.getString("id_booking"));
        row.put("idUser", rs.getString("id_user"));
        row.put("username", rs.getString("username"));
        row.put("statusBooking", statusBooking);
        row.put("statusLabel", statusLabel(statusBooking));
        row.put("statusBadgeClass", statusBadgeClass(statusBooking));
        row.put("statusIconClass", statusIconClass(statusBooking));
        row.put("paymentLabel", paymentLabel(statusBooking, statusPembayaran));
        row.put("paymentBadgeClass", paymentBadgeClass(statusBooking, statusPembayaran));
        row.put("paymentIconClass", paymentIconClass(statusBooking, statusPembayaran));
        row.put("totalBiaya", rs.getDouble("total_biaya"));
        row.put("idDetail", rs.getString("id_detail"));
        row.put("idMobil", rs.getString("id_mobil"));
        row.put("tanggalSewa", rs.getDate("tanggal_sewa"));
        row.put("tanggalKembali", rs.getDate("tanggal_kembali"));
        row.put("durasiHari", Math.max(1, ChronoUnit.DAYS.between(
                rs.getDate("tanggal_sewa").toLocalDate(),
                rs.getDate("tanggal_kembali").toLocalDate()
        )));
        row.put("subtotal", rs.getDouble("subtotal"));
        row.put("merk", rs.getString("merk"));
        row.put("model", rs.getString("model"));
        row.put("platNomor", rs.getString("plat_nomor"));
        row.put("hargaSewaPerHari", rs.getDouble("harga_sewa_per_hari"));
        row.put("tahun", rs.getInt("tahun"));
        row.put("transmisi", rs.getString("transmisi"));
        row.put("bahanBakar", rs.getString("bahan_bakar"));
        row.put("kapasitas", rs.getString("kapasitas"));
        String gambar = MobilDAO.normalizeImageName(
                rs.getString("id_mobil"),
                rs.getString("gambar"),
                rs.getString("merk"),
                rs.getString("model")
        );
        row.put("gambar", gambar);
        row.put("gambarPath", MobilDAO.resolveAssetPath(gambar));
        row.put("idPembayaran", rs.getString("id_pembayaran"));
        row.put("statusPembayaran", statusPembayaran);
        row.put("metodePembayaran", rs.getString("metode_pembayaran"));
        row.put("tanggalPembayaran", rs.getDate("tanggal_pembayaran"));
        return row;
    }

    private String statusLabel(String statusBooking) {
        if (STATUS_SELESAI.equals(statusBooking)) {
            return "SELESAI";
        }
        if (STATUS_DITOLAK.equals(statusBooking)) {
            return "DITOLAK";
        }
        if (STATUS_DIBATALKAN.equals(statusBooking)) {
            return "DIBATALKAN";
        }
        if (STATUS_DIKONFIRMASI.equals(statusBooking)
                || STATUS_MENUNGGU_PEMBAYARAN.equals(statusBooking)
                || STATUS_DIBAYAR.equals(statusBooking)) {
            return "DIKONFIRMASI";
        }
        return "MENUNGGU KONFIRMASI";
    }

    private String paymentLabel(String statusBooking, String statusPembayaran) {
        if (PAYMENT_DIKEMBALIKAN.equalsIgnoreCase(statusPembayaran)) {
            return "DIKEMBALIKAN";
        }
        if (isCancelledOrRejected(statusBooking)) {
            return PAYMENT_LUNAS.equalsIgnoreCase(statusPembayaran) ? "DIKEMBALIKAN" : "TIDAK DITERUSKAN";
        }
        if (PAYMENT_LUNAS.equalsIgnoreCase(statusPembayaran)) {
            return "LUNAS";
        }
        if (PAYMENT_TIDAK_DITERUSKAN.equalsIgnoreCase(statusPembayaran)) {
            return "TIDAK DITERUSKAN";
        }
        return "TIDAK LUNAS";
    }

    private String statusBadgeClass(String statusBooking) {
        if (STATUS_SELESAI.equals(statusBooking)) {
            return "status-finished";
        }
        if (STATUS_DITOLAK.equals(statusBooking)) {
            return "status-rejected";
        }
        if (STATUS_DIBATALKAN.equals(statusBooking)) {
            return "status-cancelled";
        }
        if (STATUS_DIKONFIRMASI.equals(statusBooking)
                || STATUS_MENUNGGU_PEMBAYARAN.equals(statusBooking)
                || STATUS_DIBAYAR.equals(statusBooking)) {
            return "status-active";
        }
        return "status-pending";
    }

    private String paymentBadgeClass(String statusBooking, String statusPembayaran) {
        if (PAYMENT_DIKEMBALIKAN.equalsIgnoreCase(statusPembayaran)
                || (isCancelledOrRejected(statusBooking) && PAYMENT_LUNAS.equalsIgnoreCase(statusPembayaran))) {
            return "payment-returned";
        }
        if (PAYMENT_TIDAK_DITERUSKAN.equalsIgnoreCase(statusPembayaran) || isCancelledOrRejected(statusBooking)) {
            return "payment-inactive";
        }
        if (PAYMENT_LUNAS.equalsIgnoreCase(statusPembayaran)) {
            return "payment-paid";
        }
        return "payment-unpaid";
    }

    private String statusIconClass(String statusBooking) {
        if (STATUS_SELESAI.equals(statusBooking)) {
            return "status-icon-finished";
        }
        if (STATUS_DITOLAK.equals(statusBooking)) {
            return "status-icon-rejected";
        }
        if (STATUS_DIBATALKAN.equals(statusBooking)) {
            return "status-icon-cancelled";
        }
        if (STATUS_DIKONFIRMASI.equals(statusBooking)
                || STATUS_MENUNGGU_PEMBAYARAN.equals(statusBooking)
                || STATUS_DIBAYAR.equals(statusBooking)) {
            return "status-icon-confirmed";
        }
        return "status-icon-waiting";
    }

    private String paymentIconClass(String statusBooking, String statusPembayaran) {
        if (PAYMENT_DIKEMBALIKAN.equalsIgnoreCase(statusPembayaran)
                || (isCancelledOrRejected(statusBooking) && PAYMENT_LUNAS.equalsIgnoreCase(statusPembayaran))) {
            return "status-icon-returned";
        }
        if (PAYMENT_TIDAK_DITERUSKAN.equalsIgnoreCase(statusPembayaran) || isCancelledOrRejected(statusBooking)) {
            return "status-icon-inactive";
        }
        if (PAYMENT_LUNAS.equalsIgnoreCase(statusPembayaran)) {
            return "status-icon-paid";
        }
        return "status-icon-unpaid";
    }

    private String refundedPaymentStatus(String statusPembayaran) {
        return PAYMENT_LUNAS.equalsIgnoreCase(statusPembayaran) ? PAYMENT_DIKEMBALIKAN : PAYMENT_TIDAK_DITERUSKAN;
    }

    private boolean isCancelledOrRejected(String statusBooking) {
        return STATUS_DIBATALKAN.equals(statusBooking) || STATUS_DITOLAK.equals(statusBooking);
    }

    private LocalDate currentDate() {
        return LocalDate.now(ZoneId.systemDefault());
    }
}
