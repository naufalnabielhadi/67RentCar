package com.rentcar.servlet;

import com.rentcar.dao.MobilDAO;
import com.rentcar.model.Mobil;
import com.rentcar.model.User;
import com.rentcar.util.ValidationUtil;

// Tomcat 11 memakai Jakarta Servlet API.
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

@WebServlet({"/admin/mobil", "/admin/mobil/form", "/admin/mobil/save", "/admin/mobil/delete"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 2 * 1024 * 1024,
        maxRequestSize = 8 * 1024 * 1024
)
public class AdminMobilServlet extends HttpServlet {
    private static final long MAX_IMAGE_SIZE = 2 * 1024 * 1024;
    private static final String UPLOAD_ASSET_DIR = "uploads/cars";
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/gif");
    private final MobilDAO mobilDAO = new MobilDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!requireRole(request, response, "ADMIN")) {
            return;
        }

        try {
            if ("/admin/mobil/form".equals(request.getServletPath())) {
                String idMobil = request.getParameter("id");
                if (!ValidationUtil.isBlank(idMobil)) {
                    request.setAttribute("mobil", mobilDAO.findById(idMobil));
                }
                request.getRequestDispatcher("/WEB-INF/views/admin/form-mobil.jsp").forward(request, response);
                return;
            }

            String query = request.getParameter("q");
            request.setAttribute("mobilList", mobilDAO.findAll(query));
            request.setAttribute("query", query);
            request.getRequestDispatcher("/WEB-INF/views/admin/kelola-mobil.jsp").forward(request, response);
        } catch (SQLException ex) {
            request.setAttribute("error", "Gagal memuat data mobil: " + ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/kelola-mobil.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!requireRole(request, response, "ADMIN")) {
            return;
        }

        try {
            if ("/admin/mobil/delete".equals(request.getServletPath())) {
                mobilDAO.delete(request.getParameter("idMobil"));
                response.sendRedirect(request.getContextPath() + "/admin/mobil");
                return;
            }

            Mobil mobil = new Mobil();
            mobil.setIdMobil(request.getParameter("idMobil"));
            mobil.setMerk(request.getParameter("merk"));
            mobil.setModel(request.getParameter("model"));
            mobil.setPlatNomor(request.getParameter("platNomor"));
            mobil.setHargaSewaPerHari(Double.parseDouble(request.getParameter("hargaSewaPerHari")));
            mobil.setStatusMobil(resolveAdminStatus(request.getParameter("idMobil"), request.getParameter("statusMobil")));
            mobil.setTahun(Integer.parseInt(request.getParameter("tahun")));
            mobil.setTransmisi(request.getParameter("transmisi"));
            mobil.setBahanBakar(request.getParameter("bahanBakar"));
            mobil.setKapasitas(request.getParameter("kapasitas"));
            mobil.setGambar(resolveCarImage(request, mobil));

            if (ValidationUtil.isBlank(mobil.getIdMobil())) {
                mobilDAO.insert(mobil);
            } else {
                mobilDAO.update(mobil);
            }

            response.sendRedirect(request.getContextPath() + "/admin/mobil");
        } catch (IllegalStateException ex) {
            request.setAttribute("error", "Gagal menyimpan data mobil: Ukuran gambar maksimal 2MB.");
            request.getRequestDispatcher("/WEB-INF/views/admin/form-mobil.jsp").forward(request, response);
        } catch (SQLException | IllegalArgumentException ex) {
            request.setAttribute("error", "Gagal menyimpan data mobil: " + ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/admin/form-mobil.jsp").forward(request, response);
        }
    }

    private String resolveAdminStatus(String idMobil, String requestedStatus) throws SQLException {
        if (!Mobil.STATUS_DISEWA.equals(requestedStatus)) {
            return requestedStatus;
        }
        if (ValidationUtil.isBlank(idMobil)) {
            throw new IllegalArgumentException("Status Disewa hanya diatur otomatis saat user booking.");
        }
        Mobil existing = mobilDAO.findById(idMobil);
        if (existing != null && Mobil.STATUS_DISEWA.equals(existing.getStatusMobil())) {
            return Mobil.STATUS_DISEWA;
        }
        throw new IllegalArgumentException("Status Disewa hanya diatur otomatis saat user booking.");
    }

    private String resolveCarImage(HttpServletRequest request, Mobil mobil) throws IOException, ServletException {
        Part imagePart = request.getPart("gambar");
        String oldImage = request.getParameter("gambarLama");
        if (imagePart == null || imagePart.getSize() == 0 || ValidationUtil.isBlank(imagePart.getSubmittedFileName())) {
            return ValidationUtil.isBlank(oldImage) ? "img/default-car.svg" : oldImage;
        }

        if (imagePart.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("Ukuran gambar maksimal 2MB.");
        }

        String submittedName = Paths.get(imagePart.getSubmittedFileName()).getFileName().toString();
        String extension = getExtension(submittedName);
        String contentType = imagePart.getContentType() == null ? "" : imagePart.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(extension) || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Format gambar harus JPG, JPEG, PNG, atau GIF.");
        }

        String baseName = slugify(mobil.getMerk() + "-" + mobil.getModel());
        String fileName = baseName + "-" + System.currentTimeMillis() + "." + extension;
        saveImage(imagePart, fileName);
        return UPLOAD_ASSET_DIR + "/" + fileName;
    }

    private void saveImage(Part imagePart, String fileName) throws IOException {
        String deployedPath = getServletContext().getRealPath("/assets/" + UPLOAD_ASSET_DIR);
        Path sourceUploadDir = Paths.get(System.getProperty("user.dir"), "src", "main", "webapp", "assets", UPLOAD_ASSET_DIR);
        Path deployedUploadDir = deployedPath == null ? sourceUploadDir : Paths.get(deployedPath);

        Files.createDirectories(sourceUploadDir);
        Files.createDirectories(deployedUploadDir);

        Path sourceTarget = sourceUploadDir.resolve(fileName).normalize();
        Path deployedTarget = deployedUploadDir.resolve(fileName).normalize();
        if (!sourceTarget.startsWith(sourceUploadDir.normalize()) || !deployedTarget.startsWith(deployedUploadDir.normalize())) {
            throw new IOException("Nama file gambar tidak valid.");
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

    private String slugify(String value) {
        String slug = value == null ? "" : value.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return slug.isBlank() ? "mobil" : slug;
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
