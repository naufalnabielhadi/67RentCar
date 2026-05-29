package com.rentcar.servlet;

import com.rentcar.dao.BookingDAO;
import com.rentcar.dao.PembayaranDAO;
import com.rentcar.model.BookingMobil;
import com.rentcar.model.Pembayaran;
import com.rentcar.model.User;
import com.rentcar.util.ValidationUtil;

// Tomcat 11 memakai Jakarta Servlet API.
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

@WebServlet({"/pembayaran", "/pembayaran/bayar"})
public class PembayaranServlet extends HttpServlet {
    private final BookingDAO bookingDAO = new BookingDAO();
    private final PembayaranDAO pembayaranDAO = new PembayaranDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!requireRole(request, response, "PELANGGAN")) {
            return;
        }

        try {
            Map<String, Object> booking = bookingDAO.findDetailById(request.getParameter("idBooking"));
            if (!canAccessBooking(request, booking)) {
                response.sendRedirect(request.getContextPath() + "/pelanggan/riwayat");
                return;
            }
            request.setAttribute("booking", booking);
            request.setAttribute("pembayaran", pembayaranDAO.findByBooking(request.getParameter("idBooking")));
            request.getRequestDispatcher("/WEB-INF/views/pelanggan/pembayaran.jsp").forward(request, response);
        } catch (SQLException ex) {
            request.setAttribute("error", "Gagal memuat pembayaran: " + ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/pelanggan/pembayaran.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!requireRole(request, response, "PELANGGAN")) {
            return;
        }

        try {
            String idBooking = request.getParameter("idBooking");
            String metodePembayaran = request.getParameter("metodePembayaran");
            BookingMobil booking = bookingDAO.findById(idBooking);
            User user = (User) request.getSession().getAttribute("user");
            if (booking == null || !user.getIdUser().equals(booking.getIdUser())) {
                response.sendRedirect(request.getContextPath() + "/pelanggan/riwayat");
                return;
            }
            if (!isPayableStatus(booking.getStatus())) {
                response.sendRedirect(request.getContextPath() + "/pelanggan/riwayat");
                return;
            }
            if (ValidationUtil.isBlank(metodePembayaran)) {
                request.setAttribute("booking", bookingDAO.findDetailById(idBooking));
                request.setAttribute("error", "Metode pembayaran wajib dipilih.");
                request.getRequestDispatcher("/WEB-INF/views/pelanggan/pembayaran.jsp").forward(request, response);
                return;
            }

            Pembayaran pembayaran = pembayaranDAO.findByBooking(idBooking);
            if (pembayaran == null) {
                pembayaran = new Pembayaran();
                pembayaran.setIdBooking(idBooking);
                pembayaran.setJumlah(booking.getTotalBiaya());
                pembayaran.setMetodePembayaran(metodePembayaran);
                pembayaran.bayar();
                pembayaranDAO.createPayment(pembayaran);
            } else {
                pembayaranDAO.updateStatusPayment(pembayaran.getIdPembayaran(), "LUNAS", metodePembayaran);
            }

            bookingDAO.markAsPaid(idBooking);
            response.sendRedirect(request.getContextPath() + "/pelanggan/riwayat");
        } catch (SQLException ex) {
            request.setAttribute("error", "Pembayaran gagal: " + ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/pelanggan/pembayaran.jsp").forward(request, response);
        }
    }

    private boolean canAccessBooking(HttpServletRequest request, Map<String, Object> booking) {
        User user = (User) request.getSession().getAttribute("user");
        return booking != null && user != null && user.getIdUser().equals(booking.get("idUser"));
    }

    private boolean isPayableStatus(String status) {
        return BookingDAO.STATUS_MENUNGGU_KONFIRMASI.equals(status)
                || BookingDAO.STATUS_DIKONFIRMASI.equals(status)
                || BookingDAO.STATUS_MENUNGGU_PEMBAYARAN.equals(status);
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
