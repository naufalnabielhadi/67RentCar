package com.rentcar.servlet;

import com.rentcar.dao.BookingDAO;
import com.rentcar.dao.MobilDAO;
import com.rentcar.dao.UserDAO;
import com.rentcar.model.Mobil;
import com.rentcar.model.User;
import com.rentcar.util.ValidationUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Set;

@WebServlet({"/pelanggan/kontak", "/pelanggan/pengaturan", "/admin/laporan", "/admin/pengaturan"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 2 * 1024 * 1024,
        maxRequestSize = 6 * 1024 * 1024
)
public class StaticPageServlet extends HttpServlet {
    private static final long MAX_PROFILE_IMAGE_SIZE = 2 * 1024 * 1024;
    private static final String PROFILE_UPLOAD_DIR = "uploads/profiles";
    private static final Set<String> PROFILE_EXTENSIONS = Set.of("jpg", "jpeg", "png");
    private static final Set<String> PROFILE_CONTENT_TYPES = Set.of("image/jpeg", "image/png");
    private final UserDAO userDAO = new UserDAO();
    private final BookingDAO bookingDAO = new BookingDAO();
    private final MobilDAO mobilDAO = new MobilDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if (path.startsWith("/admin")) {
            if (!requireRole(request, response, "ADMIN")) {
                return;
            }
            if ("/admin/laporan".equals(path)) {
                try {
                    loadReportData(request);
                } catch (SQLException ex) {
                    request.setAttribute("error", "Gagal memuat laporan: " + ex.getMessage());
                }
                request.getRequestDispatcher("/WEB-INF/views/admin/laporan.jsp").forward(request, response);
                return;
            }
            request.getRequestDispatcher("/WEB-INF/views/admin/pengaturan.jsp").forward(request, response);
            return;
        }

        if (!requireRole(request, response, "PELANGGAN")) {
            return;
        }
        if ("/pelanggan/kontak".equals(path)) {
            request.getRequestDispatcher("/WEB-INF/views/pelanggan/kontak.jsp").forward(request, response);
            return;
        }
        request.getRequestDispatcher("/WEB-INF/views/pelanggan/pengaturan.jsp").forward(request, response);
    }

    private void loadReportData(HttpServletRequest request) throws SQLException {
        request.setAttribute("bookingBulanIni", bookingDAO.countBookingBulanIni());
        request.setAttribute("pendapatanBulanIni", bookingDAO.sumPendapatanBulanIni());
        request.setAttribute("mobilDibooking", bookingDAO.countMobilDibooking());
        request.setAttribute("mobilFavorit", bookingDAO.findMobilFavorit());
        request.setAttribute("transaksiTerbaru", bookingDAO.findTransaksiTerbaru(5));
        request.setAttribute("totalMobil", mobilDAO.findAll().size());
        request.setAttribute("mobilTersedia", mobilDAO.countByStatus(Mobil.STATUS_TERSEDIA));
        request.setAttribute("mobilPerbaikan", mobilDAO.countByStatus(Mobil.STATUS_DALAM_PERBAIKAN));
        request.setAttribute("mobilDisewa", mobilDAO.countByStatus(Mobil.STATUS_DISEWA));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();
        boolean adminSettings = "/admin/pengaturan".equals(path);
        boolean customerSettings = "/pelanggan/pengaturan".equals(path);
        if ((!adminSettings && !customerSettings) || !requireRole(request, response, adminSettings ? "ADMIN" : "PELANGGAN")) {
            return;
        }

        User user = (User) request.getSession().getAttribute("user");
        String action = request.getParameter("action");
        try {
            if ("profile".equals(action)) {
                if (adminSettings) {
                    updateAdminProfileImage(request, user);
                } else {
                    updateProfile(request, user);
                }
            } else if ("password".equals(action)) {
                updatePassword(request, user);
            } else if (!adminSettings && "deactivate".equals(action)) {
                deactivateAccount(request, response, user);
                return;
            }
        } catch (SQLException | IllegalArgumentException | IllegalStateException ex) {
            request.setAttribute("error", "Gagal menyimpan pengaturan: " + ex.getMessage());
        }

        String view = adminSettings ? "/WEB-INF/views/admin/pengaturan.jsp" : "/WEB-INF/views/pelanggan/pengaturan.jsp";
        request.getRequestDispatcher(view).forward(request, response);
    }

    private void updateAdminProfileImage(HttpServletRequest request, User user) throws SQLException, IOException, ServletException {
        String fotoProfil = resolveProfileImage(request, user);
        if (fotoProfil == null || fotoProfil.isBlank()) {
            request.setAttribute("error", "Pilih foto profil admin terlebih dahulu.");
            return;
        }

        if (userDAO.updateProfile(user.getIdUser(), user.getUsername(), user.getEmail(), user.getTelepon(), fotoProfil)) {
            user.setFotoProfil(fotoProfil);
            request.setAttribute("success", "Foto profil admin berhasil diperbarui.");
        } else {
            request.setAttribute("error", "Foto profil admin tidak dapat diperbarui.");
        }
    }

    private void updateProfile(HttpServletRequest request, User user) throws SQLException, IOException, ServletException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String telepon = request.getParameter("telepon");

        if (ValidationUtil.isBlank(username) || !ValidationUtil.isValidEmail(email)) {
            request.setAttribute("error", "Nama lengkap dan email valid wajib diisi.");
            return;
        }

        String fotoProfil = resolveProfileImage(request, user);
        if (userDAO.updateProfile(user.getIdUser(), username, email, telepon, fotoProfil)) {
            user.setUsername(username);
            user.setEmail(email);
            user.setTelepon(telepon);
            if (fotoProfil != null && !fotoProfil.isBlank()) {
                user.setFotoProfil(fotoProfil);
            }
            request.setAttribute("success", "Profil berhasil diperbarui.");
        } else {
            request.setAttribute("error", "Profil tidak dapat diperbarui.");
        }
    }

    private String resolveProfileImage(HttpServletRequest request, User user) throws IOException, ServletException {
        Part imagePart = request.getPart("fotoProfil");
        if (imagePart == null || imagePart.getSize() == 0 || ValidationUtil.isBlank(imagePart.getSubmittedFileName())) {
            return null;
        }
        if (imagePart.getSize() > MAX_PROFILE_IMAGE_SIZE) {
            throw new IllegalArgumentException("Ukuran foto profil maksimal 2MB.");
        }

        String submittedName = Paths.get(imagePart.getSubmittedFileName()).getFileName().toString();
        String extension = getExtension(submittedName);
        String contentType = imagePart.getContentType() == null ? "" : imagePart.getContentType().toLowerCase(Locale.ROOT);
        if (!PROFILE_EXTENSIONS.contains(extension) || !PROFILE_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Format foto profil harus JPG atau PNG.");
        }

        String fileName = "profile-" + user.getIdUser().toLowerCase(Locale.ROOT) + "-" + System.currentTimeMillis() + "." + extension;
        saveProfileImage(imagePart, fileName);
        return PROFILE_UPLOAD_DIR + "/" + fileName;
    }

    private void saveProfileImage(Part imagePart, String fileName) throws IOException {
        String deployedPath = getServletContext().getRealPath("/assets/" + PROFILE_UPLOAD_DIR);
        Path sourceUploadDir = Paths.get(System.getProperty("user.dir"), "src", "main", "webapp", "assets", PROFILE_UPLOAD_DIR);
        Path deployedUploadDir = deployedPath == null ? sourceUploadDir : Paths.get(deployedPath);

        Files.createDirectories(sourceUploadDir);
        Files.createDirectories(deployedUploadDir);

        Path sourceTarget = sourceUploadDir.resolve(fileName).normalize();
        Path deployedTarget = deployedUploadDir.resolve(fileName).normalize();
        if (!sourceTarget.startsWith(sourceUploadDir.normalize()) || !deployedTarget.startsWith(deployedUploadDir.normalize())) {
            throw new IOException("Nama file foto profil tidak valid.");
        }

        try (InputStream input = imagePart.getInputStream()) {
            Files.copy(input, deployedTarget, StandardCopyOption.REPLACE_EXISTING);
        }
        if (!sourceTarget.equals(deployedTarget)) {
            Files.copy(deployedTarget, sourceTarget, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex + 1).toLowerCase(Locale.ROOT);
    }

    private void updatePassword(HttpServletRequest request, User user) throws SQLException {
        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (ValidationUtil.isBlank(currentPassword) || ValidationUtil.isBlank(newPassword) || !newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Kata sandi saat ini, kata sandi baru, dan konfirmasi harus benar.");
            return;
        }

        if (userDAO.updatePassword(user.getIdUser(), currentPassword, newPassword)) {
            user.setPassword(newPassword);
            request.setAttribute("success", "Kata sandi berhasil diubah.");
        } else {
            request.setAttribute("error", "Kata sandi saat ini tidak sesuai.");
        }
    }

    private void deactivateAccount(HttpServletRequest request, HttpServletResponse response, User user) throws SQLException, IOException {
        if (userDAO.deactivate(user.getIdUser())) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login?success=deactivated");
            return;
        }
        request.setAttribute("error", "Akun tidak dapat dinonaktifkan.");
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
