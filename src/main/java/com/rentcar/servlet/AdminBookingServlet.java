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
import java.util.Collections;

@WebServlet({"/admin/booking/konfirmasi", "/admin/booking/tolak"})
public class AdminBookingServlet extends HttpServlet {
    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!requireRole(request, response, "ADMIN")) {
            return;
        }

        String idBooking = request.getParameter("idBooking");
        try {
            if ("/admin/booking/konfirmasi".equals(request.getServletPath())) {
                bookingDAO.confirmBooking(idBooking);
            } else {
                bookingDAO.rejectBooking(idBooking);
            }
            response.sendRedirect(request.getContextPath() + "/admin/pesanan");
        } catch (SQLException ex) {
            request.setAttribute("error", "Gagal memproses booking: " + ex.getMessage());
            request.setAttribute("riwayatList", Collections.emptyList());
            request.getRequestDispatcher("/WEB-INF/views/admin/riwayat-pesanan.jsp").forward(request, response);
        }
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
