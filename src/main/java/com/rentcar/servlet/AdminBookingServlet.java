package com.rentcar.servlet;

import com.rentcar.dao.BookingDAO;
import com.rentcar.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet({"/admin/booking/konfirmasi", "/admin/booking/tolak", "/admin/booking/selesai"})
public class AdminBookingServlet extends HttpServlet {
    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!requireRole(request, response, "ADMIN")) {
            return;
        }

        String idBooking = request.getParameter("idBooking");
        HttpSession session = request.getSession();
        try {
            String servletPath = request.getServletPath();
            boolean updated;
            if ("/admin/booking/konfirmasi".equals(servletPath)) {
                updated = bookingDAO.confirmBooking(idBooking);
                setFlash(session, updated, "Booking berhasil dikonfirmasi.", "Booking tidak ditemukan atau sudah tidak dapat dikonfirmasi.");
            } else if ("/admin/booking/tolak".equals(servletPath)) {
                updated = bookingDAO.rejectBooking(idBooking);
                setFlash(session, updated, "Booking berhasil ditolak. Status transaksi diperbarui sesuai pembayaran.", "Booking tidak ditemukan atau sudah tidak dapat ditolak.");
            } else if ("/admin/booking/selesai".equals(servletPath)) {
                updated = bookingDAO.completeBooking(idBooking);
                setFlash(session, updated, "Booking berhasil diselesaikan. Status mobil menjadi Sudah Dikembalikan.", "Booking tidak ditemukan atau gagal diselesaikan.");
            } else {
                session.setAttribute("error", "Aksi booking tidak dikenali.");
            }
            response.sendRedirect(request.getContextPath() + "/admin/pesanan");
        } catch (SQLException ex) {
            session.setAttribute("error", friendlyMessage(ex));
            response.sendRedirect(request.getContextPath() + "/admin/pesanan");
        }
    }

    private void setFlash(HttpSession session, boolean success, String successMessage, String errorMessage) {
        if (success) {
            session.setAttribute("success", successMessage);
        } else {
            session.setAttribute("error", errorMessage);
        }
    }

    private String friendlyMessage(SQLException ex) {
        String message = ex.getMessage();
        if ("Booking belum dapat diselesaikan karena tanggal kembali belum tercapai.".equals(message)
                || "Booking tidak ditemukan.".equals(message)
                || "Booking sudah tidak aktif atau sudah selesai.".equals(message)
                || "Mobil masih memiliki booking aktif lain.".equals(message)) {
            return message;
        }
        return "Gagal memproses booking. Silakan coba lagi.";
    }

    private boolean requireRole(HttpServletRequest request, HttpServletResponse response, String requiredRole) throws IOException {
        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        if (!requiredRole.equals(user.getRole())) {
            response.sendRedirect(request.getContextPath() + user.laman());
            return false;
        }
        return true;
    }
}
