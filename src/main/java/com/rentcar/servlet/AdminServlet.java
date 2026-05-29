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

@WebServlet({"/admin/dashboard"})
public class AdminServlet extends HttpServlet {
    private final MobilDAO mobilDAO = new MobilDAO();
    private final BookingDAO bookingDAO = new BookingDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!requireRole(request, response, "ADMIN")) {
            return;
        }

        try {
            request.setAttribute("mobilList", mobilDAO.findAll());
            request.setAttribute("riwayatList", bookingDAO.findAll());
            request.setAttribute("mobilTersedia", mobilDAO.countByStatus(Mobil.STATUS_TERSEDIA));
            request.setAttribute("mobilPerbaikan", mobilDAO.countByStatus(Mobil.STATUS_DALAM_PERBAIKAN));
            request.setAttribute("mobilDisewa", mobilDAO.countByStatus(Mobil.STATUS_DISEWA));
            request.setAttribute("pendapatanBulanIni", bookingDAO.sumPendapatanBulanIni());
            request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
        } catch (SQLException ex) {
            request.setAttribute("error", "Gagal memuat dashboard admin: " + ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(request, response);
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
