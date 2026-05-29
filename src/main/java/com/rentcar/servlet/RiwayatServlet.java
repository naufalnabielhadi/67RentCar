package com.rentcar.servlet;

import com.rentcar.dao.BookingDAO;
import com.rentcar.dao.MobilDAO;
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

@WebServlet({"/pelanggan/dashboard", "/riwayat", "/pelanggan/riwayat", "/admin/pesanan"})
public class RiwayatServlet extends HttpServlet {
    private final BookingDAO bookingDAO = new BookingDAO();
    private final MobilDAO mobilDAO = new MobilDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            if ("/admin/pesanan".equals(request.getServletPath())) {
                if (!requireRole(request, response, "ADMIN")) {
                    return;
                }
                LocalDate tanggal = parseDate(request.getParameter("tanggal"));
                request.setAttribute("riwayatList", bookingDAO.findAllFiltered(request.getParameter("q"), tanggal));
                request.getRequestDispatcher("/WEB-INF/views/admin/riwayat-pesanan.jsp").forward(request, response);
                return;
            }

            if (!requireRole(request, response, "PELANGGAN")) {
                return;
            }

            User user = (User) request.getSession().getAttribute("user");
            if ("/riwayat".equals(request.getServletPath()) || "/pelanggan/riwayat".equals(request.getServletPath())) {
                String query = request.getParameter("q");
                request.setAttribute("riwayatList", bookingDAO.findByUserFiltered(user.getIdUser(), query));
                request.setAttribute("query", query);
                request.getRequestDispatcher("/WEB-INF/views/pelanggan/riwayat.jsp").forward(request, response);
                return;
            }

            request.setAttribute("mobilList", mobilDAO.findAll());
            request.setAttribute("riwayatList", bookingDAO.findByUser(user.getIdUser()));
            request.getRequestDispatcher("/WEB-INF/views/pelanggan/dashboard.jsp").forward(request, response);
        } catch (SQLException ex) {
            request.setAttribute("error", "Gagal memuat data riwayat: " + ex.getMessage());
            if ("/admin/pesanan".equals(request.getServletPath())) {
                request.getRequestDispatcher("/WEB-INF/views/admin/riwayat-pesanan.jsp").forward(request, response);
            } else {
                request.getRequestDispatcher("/WEB-INF/views/pelanggan/dashboard.jsp").forward(request, response);
            }
        }
    }

    private LocalDate parseDate(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            return null;
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
