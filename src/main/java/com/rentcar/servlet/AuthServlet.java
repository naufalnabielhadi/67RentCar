package com.rentcar.servlet;

import com.rentcar.dao.UserDAO;
import com.rentcar.model.Pelanggan;
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

@WebServlet({"/login", "/register", "/logout"})
public class AuthServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/logout".equals(path)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if ("/register".equals(path)) {
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();
        if ("/register".equals(path)) {
            handleRegister(request, response);
            return;
        }
        handleLogin(request, response);
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (ValidationUtil.isBlank(email) || ValidationUtil.isBlank(password)) {
            request.setAttribute("error", "Email dan password wajib diisi.");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            return;
        }

        try {
            User user = userDAO.login(email, password);
            if (user == null) {
                User existingUser = userDAO.findByEmail(email);
                if (existingUser != null && "NONAKTIF".equals(existingUser.getStatusAkun())) {
                    request.setAttribute("error", "Login gagal. Akun ini masih berstatus nonaktif.");
                    request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
                    return;
                }
                request.setAttribute("error", "Login gagal. Periksa kembali email dan password.");
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
                return;
            }

            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("role", user.getRole());

            if ("ADMIN".equals(user.getRole())) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/pelanggan/dashboard");
            }
        } catch (SQLException ex) {
            request.setAttribute("error", "Terjadi kesalahan database: " + ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (ValidationUtil.isBlank(username) || !ValidationUtil.isValidEmail(email) || ValidationUtil.isBlank(password)) {
            request.setAttribute("error", "Username, email valid, dan password wajib diisi.");
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
            return;
        }

        try {
            if (userDAO.findByEmail(email) != null) {
                request.setAttribute("error", "Email sudah terdaftar.");
                request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
                return;
            }

            Pelanggan pelanggan = new Pelanggan();
            pelanggan.setUsername(username);
            pelanggan.setEmail(email);
            pelanggan.setPassword(password);
            userDAO.register(pelanggan);
            response.sendRedirect(request.getContextPath() + "/login?success=register");
        } catch (SQLException ex) {
            request.setAttribute("error", "Registrasi gagal: " + ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
        }
    }
}
