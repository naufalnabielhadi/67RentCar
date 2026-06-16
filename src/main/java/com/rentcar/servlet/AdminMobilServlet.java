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
import java.util.ArrayList;
import java.util.List;
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
    private static final Set<String> ALLOWED_TRANSMISSIONS = Set.of("Manual", "Otomatis");
    private static final Set<String> ALLOWED_FUEL_TYPES = Set.of("Bensin", "Diesel", "Hybrid", "Listrik");
    private static final Set<String> ALLOWED_ADMIN_STATUSES = Set.of(Mobil.STATUS_TERSEDIA, Mobil.STATUS_DALAM_PERBAIKAN);
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
                handleDelete(request, response);
                return;
            }

            List<String> errors = new ArrayList<>();
            Mobil mobil = buildMobilFromRequest(request, errors);
            Mobil existing = ValidationUtil.isBlank(mobil.getIdMobil()) ? null : mobilDAO.findById(mobil.getIdMobil());

            if (!ValidationUtil.isBlank(mobil.getIdMobil()) && existing == null) {
                errors.add("Mobil tidak ditemukan atau gagal diperbarui.");
            }
            if (!ValidationUtil.isBlank(mobil.getPlatNomor())
                    && mobilDAO.existsByPlatNomor(mobil.getPlatNomor(), mobil.getIdMobil())) {
                errors.add("Plat nomor sudah digunakan mobil lain.");
            }

            mobil.setStatusMobil(resolveAdminStatus(mobil.getIdMobil(), request.getParameter("statusMobil"), existing, errors));
            validateImagePart(request, errors);
            if (!errors.isEmpty()) {
                forwardFormError(request, response, mobil, String.join(" ", errors));
                return;
            }

            mobil.setGambar(resolveCarImage(request, mobil));

            if (ValidationUtil.isBlank(mobil.getIdMobil())) {
                mobilDAO.insert(mobil);
            } else if (!mobilDAO.update(mobil)) {
                forwardFormError(request, response, mobil, "Mobil tidak ditemukan atau gagal diperbarui.");
                return;
            }

            response.sendRedirect(request.getContextPath() + "/admin/mobil");
        } catch (IllegalStateException ex) {
            forwardFormError(request, response, buildMobilFromRequest(request, new ArrayList<>()), "Ukuran gambar maksimal 2MB.");
        } catch (IOException ex) {
            forwardFormError(request, response, buildMobilFromRequest(request, new ArrayList<>()), "Gagal menyimpan gambar mobil. Pastikan folder upload dapat ditulis.");
        } catch (SQLException | IllegalArgumentException ex) {
            forwardFormError(request, response, buildMobilFromRequest(request, new ArrayList<>()), friendlySaveMessage(ex));
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idMobil = trim(request.getParameter("idMobil"));
        if (ValidationUtil.isBlank(idMobil)) {
            forwardListError(request, response, "Mobil tidak ditemukan atau gagal dihapus.");
            return;
        }

        try {
            Mobil mobil = mobilDAO.findById(idMobil);
            if (mobil == null) {
                forwardListError(request, response, "Mobil tidak ditemukan atau gagal dihapus.");
                return;
            }
            if (Mobil.STATUS_DISEWA.equals(mobil.getStatusMobil()) || mobilDAO.hasBookingHistory(idMobil)) {
                forwardListError(request, response, "Mobil tidak dapat dihapus karena sedang disewa atau memiliki riwayat booking.");
                return;
            }
            if (!mobilDAO.delete(idMobil)) {
                forwardListError(request, response, "Mobil tidak ditemukan atau gagal dihapus.");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/admin/mobil");
        } catch (SQLException ex) {
            forwardListError(request, response, "Mobil tidak dapat dihapus karena sedang disewa atau memiliki riwayat booking.");
        }
    }

    private Mobil buildMobilFromRequest(HttpServletRequest request, List<String> errors) {
        Mobil mobil = new Mobil();
        mobil.setIdMobil(trim(request.getParameter("idMobil")));
        mobil.setMerk(trim(request.getParameter("merk")));
        mobil.setModel(trim(request.getParameter("model")));
        mobil.setPlatNomor(trim(request.getParameter("platNomor")));
        mobil.setTransmisi(normalizeTransmisi(request.getParameter("transmisi")));
        mobil.setBahanBakar(normalizeBahanBakar(request.getParameter("bahanBakar")));
        mobil.setKapasitas(normalizeKapasitas(request.getParameter("kapasitas"), errors));
        mobil.setGambar(trim(request.getParameter("gambarLama")));

        if (ValidationUtil.isBlank(mobil.getMerk())) {
            errors.add("Merk wajib diisi.");
        }
        if (ValidationUtil.isBlank(mobil.getModel())) {
            errors.add("Model wajib diisi.");
        }
        if (ValidationUtil.isBlank(mobil.getPlatNomor())) {
            errors.add("Plat nomor wajib diisi.");
        }
        if (ValidationUtil.isBlank(mobil.getTransmisi()) || !ALLOWED_TRANSMISSIONS.contains(mobil.getTransmisi())) {
            errors.add("Transmisi tidak valid.");
        }
        if (ValidationUtil.isBlank(mobil.getBahanBakar()) || !ALLOWED_FUEL_TYPES.contains(mobil.getBahanBakar())) {
            errors.add("Bahan bakar tidak valid.");
        }

        Double harga = parseDouble(request.getParameter("hargaSewaPerHari"), "Harga sewa harus berupa angka.", errors);
        if (harga != null) {
            if (harga <= 0) {
                errors.add("Harga sewa harus lebih dari 0.");
            }
            mobil.setHargaSewaPerHari(harga);
        }

        Integer tahun = parseInt(request.getParameter("tahun"), "Tahun harus berupa angka.", errors);
        if (tahun != null) {
            if (tahun < 2000 || tahun > 2035) {
                errors.add("Tahun harus berada di antara 2000 sampai 2035.");
            }
            mobil.setTahun(tahun);
        }

        return mobil;
    }

    private String resolveAdminStatus(String idMobil, String requestedStatus, Mobil existing, List<String> errors) {
        String normalizedStatus = normalizeStatus(requestedStatus);
        if (existing != null && Mobil.STATUS_DISEWA.equals(existing.getStatusMobil())) {
            return Mobil.STATUS_DISEWA;
        }
        if (Mobil.STATUS_DISEWA.equals(normalizedStatus)) {
            errors.add("Status Disewa hanya diatur otomatis saat user booking.");
            return Mobil.STATUS_TERSEDIA;
        }
        if (!ALLOWED_ADMIN_STATUSES.contains(normalizedStatus)) {
            errors.add("Status mobil tidak valid.");
            return Mobil.STATUS_TERSEDIA;
        }
        if (ValidationUtil.isBlank(idMobil) && Mobil.STATUS_DISEWA.equals(normalizedStatus)) {
            errors.add("Admin tidak dapat membuat mobil baru langsung berstatus Disewa.");
            return Mobil.STATUS_TERSEDIA;
        }
        return normalizedStatus;
    }

    private void validateImagePart(HttpServletRequest request, List<String> errors) throws IOException, ServletException {
        Part imagePart = request.getPart("gambar");
        if (imagePart == null || imagePart.getSize() == 0 || ValidationUtil.isBlank(imagePart.getSubmittedFileName())) {
            return;
        }
        if (imagePart.getSize() > MAX_IMAGE_SIZE) {
            errors.add("Ukuran gambar maksimal 2MB.");
            return;
        }
        String submittedName = Paths.get(imagePart.getSubmittedFileName()).getFileName().toString();
        String extension = getExtension(submittedName);
        String contentType = imagePart.getContentType() == null ? "" : imagePart.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(extension) || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            errors.add("Format gambar harus JPG, JPEG, PNG, atau GIF.");
        }
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
        if (deployedPath == null) {
            throw new IOException("Folder upload tidak tersedia.");
        }
        Path deployedUploadDir = Paths.get(deployedPath);

        Files.createDirectories(deployedUploadDir);

        Path deployedTarget = deployedUploadDir.resolve(fileName).normalize();
        if (!deployedTarget.startsWith(deployedUploadDir.normalize())) {
            throw new IOException("Nama file gambar tidak valid.");
        }

        try (InputStream input = imagePart.getInputStream()) {
            Files.copy(input, deployedTarget, StandardCopyOption.REPLACE_EXISTING);
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

    private void forwardFormError(HttpServletRequest request, HttpServletResponse response, Mobil mobil, String message)
            throws ServletException, IOException {
        keepSubmittedValues(request);
        request.setAttribute("mobil", mobil);
        request.setAttribute("error", message);
        request.getRequestDispatcher("/WEB-INF/views/admin/form-mobil.jsp").forward(request, response);
    }

    private void forwardListError(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        try {
            request.setAttribute("mobilList", mobilDAO.findAll(request.getParameter("q")));
            request.setAttribute("query", request.getParameter("q"));
        } catch (SQLException ex) {
            request.setAttribute("mobilList", List.of());
        }
        request.setAttribute("error", message);
        request.getRequestDispatcher("/WEB-INF/views/admin/kelola-mobil.jsp").forward(request, response);
    }

    private void keepSubmittedValues(HttpServletRequest request) {
        request.setAttribute("merkValue", request.getParameter("merk"));
        request.setAttribute("modelValue", request.getParameter("model"));
        request.setAttribute("platNomorValue", request.getParameter("platNomor"));
        request.setAttribute("hargaSewaPerHariValue", request.getParameter("hargaSewaPerHari"));
        request.setAttribute("tahunValue", request.getParameter("tahun"));
        request.setAttribute("transmisiValue", normalizeTransmisi(request.getParameter("transmisi")));
        request.setAttribute("bahanBakarValue", normalizeBahanBakar(request.getParameter("bahanBakar")));
        request.setAttribute("kapasitasValue", request.getParameter("kapasitas"));
        request.setAttribute("statusMobilValue", normalizeStatus(request.getParameter("statusMobil")));
    }

    private String friendlySaveMessage(Exception ex) {
        String message = ex.getMessage() == null ? "" : ex.getMessage();
        if (message.toLowerCase(Locale.ROOT).contains("duplicate") || message.toLowerCase(Locale.ROOT).contains("plat_nomor")) {
            return "Plat nomor sudah digunakan mobil lain.";
        }
        return "Gagal menyimpan data mobil. Periksa kembali input yang dimasukkan.";
    }

    private Double parseDouble(String value, String message, List<String> errors) {
        try {
            return Double.parseDouble(trim(value));
        } catch (NumberFormatException ex) {
            errors.add(message);
            return null;
        }
    }

    private Integer parseInt(String value, String message, List<String> errors) {
        try {
            return Integer.parseInt(trim(value));
        } catch (NumberFormatException ex) {
            errors.add(message);
            return null;
        }
    }

    private String normalizeKapasitas(String value, List<String> errors) {
        String cleanValue = trim(value);
        String numericValue = cleanValue.replaceAll("(?i)\\s*(kursi|penumpang)\\s*$", "").trim();
        if (ValidationUtil.isBlank(numericValue)) {
            errors.add("Kapasitas harus berupa angka dan lebih dari 0.");
            return cleanValue;
        }
        try {
            int kapasitas = Integer.parseInt(numericValue);
            if (kapasitas <= 0) {
                errors.add("Kapasitas harus lebih dari 0.");
            }
            return kapasitas + " Kursi";
        } catch (NumberFormatException ex) {
            errors.add("Kapasitas harus berupa angka dan lebih dari 0.");
            return cleanValue;
        }
    }

    private String normalizeTransmisi(String value) {
        String cleanValue = trim(value);
        if ("automatic".equalsIgnoreCase(cleanValue) || "otomatis".equalsIgnoreCase(cleanValue)) {
            return "Otomatis";
        }
        if ("manual".equalsIgnoreCase(cleanValue)) {
            return "Manual";
        }
        return cleanValue;
    }

    private String normalizeBahanBakar(String value) {
        String cleanValue = trim(value);
        for (String allowedValue : ALLOWED_FUEL_TYPES) {
            if (allowedValue.equalsIgnoreCase(cleanValue)) {
                return allowedValue;
            }
        }
        return cleanValue;
    }

    private String normalizeStatus(String value) {
        String cleanValue = trim(value).toUpperCase(Locale.ROOT);
        if (ValidationUtil.isBlank(cleanValue)) {
            return Mobil.STATUS_TERSEDIA;
        }
        return cleanValue;
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
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
