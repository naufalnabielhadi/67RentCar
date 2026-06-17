package com.rentcar.servlet;

import com.rentcar.dao.BookingDAO;
import com.rentcar.dao.MobilDAO;
import com.rentcar.model.Mobil;
import com.rentcar.model.User;

// Tomcat 11 memakai Jakarta Servlet API.
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

@WebServlet({"/booking", "/booking/create", "/booking/batal", "/booking/cancel"})
public class BookingServlet extends HttpServlet {
    private final BookingDAO bookingDAO = new BookingDAO();
    private final MobilDAO mobilDAO = new MobilDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!requireRole(request, response, "PELANGGAN")) {
            return;
        }

        try {
            String idMobil = request.getParameter("idMobil");
            if (idMobil == null) {
                idMobil = request.getParameter("mobilId");
            }
            Mobil mobil = mobilDAO.findById(idMobil);
            if (mobil == null || !mobil.cekKetersediaan()) {
                response.sendRedirect(request.getContextPath() + "/mobil?error=unavailable");
                return;
            }
            User user = (User) request.getSession().getAttribute("user");
            if (user == null || !user.hasKartuIdentitas()) {
                request.setAttribute("identityRequired", true);
            }
            request.setAttribute("mobil", mobil);
            request.getRequestDispatcher("/WEB-INF/views/pelanggan/booking.jsp").forward(request, response);
        } catch (SQLException ex) {
            request.setAttribute("error", "Gagal membuka form booking: " + ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/pelanggan/booking.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!requireRole(request, response, "PELANGGAN")) {
            return;
        }

        String path = request.getServletPath();
        User user = (User) request.getSession().getAttribute("user");

        try {
            if ("/booking/cancel".equals(path) || "/booking/batal".equals(path)) {
                bookingDAO.cancelBooking(request.getParameter("idBooking"));
                response.sendRedirect(request.getContextPath() + "/pelanggan/riwayat");
                return;
            }

            String idMobil = request.getParameter("idMobil");
            String tanggalSewaParam = request.getParameter("tanggalSewa");
            String tanggalKembaliParam = request.getParameter("tanggalKembali");

            if (!user.hasKartuIdentitas()) {
                forwardIdentityRequired(request, response, idMobil);
                return;
            }

            if (isBlank(tanggalSewaParam)) {
                forwardBookingError(request, response, idMobil, "Tanggal sewa wajib diisi.");
                return;
            }
            if (isBlank(tanggalKembaliParam)) {
                forwardBookingError(request, response, idMobil, "Tanggal kembali wajib diisi.");
                return;
            }

            LocalDate tanggalSewa = LocalDate.parse(tanggalSewaParam);
            LocalDate tanggalKembali = LocalDate.parse(tanggalKembaliParam);
            if (!tanggalKembali.isAfter(tanggalSewa)) {
                forwardBookingError(request, response, idMobil, "Tanggal kembali harus setelah tanggal sewa.");
                return;
            }

            String idBooking = bookingDAO.createBooking(
                    user.getIdUser(),
                    idMobil,
                    tanggalSewa,
                    tanggalKembali
            );
            response.sendRedirect(request.getContextPath() + "/pembayaran?idBooking=" + idBooking);
        } catch (DateTimeParseException ex) {
            forwardBookingError(request, response, request.getParameter("idMobil"), "Format tanggal tidak valid.");
        } catch (SQLException | RuntimeException ex) {
            if (isUnavailable(ex)) {
                response.sendRedirect(request.getContextPath() + "/mobil?error=unavailable");
                return;
            }
            request.setAttribute("mobil", safeFindMobil(request.getParameter("idMobil")));
            request.setAttribute("error", "Booking gagal: " + ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/pelanggan/booking.jsp").forward(request, response);
        }
    }

    private void forwardBookingError(HttpServletRequest request, HttpServletResponse response, String idMobil, String message)
            throws ServletException, IOException {
        request.setAttribute("mobil", safeFindMobil(idMobil));
        request.setAttribute("error", message);
        request.getRequestDispatcher("/WEB-INF/views/pelanggan/booking.jsp").forward(request, response);
    }

    private void forwardIdentityRequired(HttpServletRequest request, HttpServletResponse response, String idMobil)
            throws ServletException, IOException {
        request.setAttribute("mobil", safeFindMobil(idMobil));
        request.setAttribute("identityRequired", true);
        request.getRequestDispatcher("/WEB-INF/views/pelanggan/booking.jsp").forward(request, response);
    }

    private Mobil safeFindMobil(String idMobil) {
        try {
            return mobilDAO.findById(idMobil);
        } catch (SQLException ex) {
            return null;
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private boolean isUnavailable(Exception ex) {
        return ex.getMessage() != null && ex.getMessage().contains(BookingDAO.MOBIL_TIDAK_TERSEDIA);
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
