package com.rentcar.servlet;

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

@WebServlet({"/mobil", "/mobil/detail"})
public class MobilServlet extends HttpServlet {
    private final MobilDAO mobilDAO = new MobilDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        try {
            if (!requireRole(request, response, "PELANGGAN")) {
                return;
            }
            if ("/mobil/detail".equals(path)) {
                String idMobil = request.getParameter("idMobil");
                if (idMobil == null) {
                    idMobil = request.getParameter("id");
                }
                Mobil mobil = mobilDAO.findById(idMobil);
                request.setAttribute("mobil", mobil);
                request.getRequestDispatcher("/WEB-INF/views/pelanggan/detail-mobil.jsp").forward(request, response);
                return;
            }

            if ("unavailable".equals(request.getParameter("error"))) {
                request.setAttribute("error", "Mobil ini sedang tidak tersedia. Silakan pilih kendaraan lain yang masih bisa disewa.");
            }
            String query = request.getParameter("q");
            request.setAttribute("mobilList", mobilDAO.findAvailable(query));
            request.setAttribute("query", query);
            request.getRequestDispatcher("/WEB-INF/views/pelanggan/daftar-mobil.jsp").forward(request, response);
        } catch (SQLException ex) {
            request.setAttribute("error", "Gagal memuat data mobil: " + ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/pelanggan/daftar-mobil.jsp").forward(request, response);
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
