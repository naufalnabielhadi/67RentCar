package com.rentcar.servlet;

import com.rentcar.dao.BookingDAO;
import com.rentcar.dao.PembayaranDAO;
import com.rentcar.model.BookingMobil;
import com.rentcar.model.Pembayaran;
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
import java.util.Map;
import java.util.Set;

@WebServlet({"/pembayaran", "/pembayaran/bayar"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 8 * 1024 * 1024
)
public class PembayaranServlet extends HttpServlet {
    private static final long MAX_PROOF_SIZE = 5 * 1024 * 1024;
    private static final String PROOF_UPLOAD_DIR = "uploads/payments";
    private static final Set<String> ALLOWED_METHODS = Set.of("Debit", "Qris", "Tunai");
    private static final Set<String> ALLOWED_PROOF_EXTENSIONS = Set.of("pdf", "png", "svg", "jpg", "jpeg");
    private static final Set<String> ALLOWED_PROOF_CONTENT_TYPES = Set.of(
            "application/pdf",
            "image/png",
            "image/svg+xml",
            "image/jpeg"
    );
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
                forwardPaymentError(request, response, idBooking, "Metode pembayaran wajib dipilih.");
                return;
            }
            if (!ALLOWED_METHODS.contains(metodePembayaran)) {
                forwardPaymentError(request, response, idBooking, "Metode pembayaran tidak valid.");
                return;
            }

            String buktiPembayaran = resolvePaymentProof(request, idBooking);
            if (ValidationUtil.isBlank(buktiPembayaran)) {
                forwardPaymentError(request, response, idBooking, "Bukti pembayaran wajib diunggah dalam format PDF, PNG, SVG, JPG, atau JPEG maksimal 5MB.");
                return;
            }

            Pembayaran pembayaran = pembayaranDAO.findByBooking(idBooking);
            if (pembayaran == null) {
                pembayaran = new Pembayaran();
                pembayaran.setIdBooking(idBooking);
                pembayaran.setJumlah(booking.getTotalBiaya());
                pembayaran.setMetodePembayaran(metodePembayaran);
                pembayaran.setBuktiPembayaran(buktiPembayaran);
                pembayaran.bayar();
                pembayaranDAO.createPayment(pembayaran);
            } else {
                pembayaranDAO.updateStatusPayment(pembayaran.getIdPembayaran(), "LUNAS", metodePembayaran, buktiPembayaran);
            }

            bookingDAO.markAsPaid(idBooking);
            response.sendRedirect(request.getContextPath() + "/pelanggan/transaksi");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            forwardPaymentError(request, response, request.getParameter("idBooking"), ex.getMessage());
        } catch (SQLException ex) {
            forwardPaymentError(request, response, request.getParameter("idBooking"), "Pembayaran gagal: " + ex.getMessage());
        }
    }

    private void forwardPaymentError(HttpServletRequest request, HttpServletResponse response, String idBooking, String message)
            throws ServletException, IOException {
        try {
            request.setAttribute("booking", bookingDAO.findDetailById(idBooking));
        } catch (SQLException ignored) {
            request.setAttribute("booking", null);
        }
        request.setAttribute("error", message);
        request.getRequestDispatcher("/WEB-INF/views/pelanggan/pembayaran.jsp").forward(request, response);
    }

    private String resolvePaymentProof(HttpServletRequest request, String idBooking) throws IOException, ServletException {
        Part proofPart = request.getPart("buktiPembayaran");
        if (proofPart == null || proofPart.getSize() == 0 || ValidationUtil.isBlank(proofPart.getSubmittedFileName())) {
            return null;
        }
        if (proofPart.getSize() > MAX_PROOF_SIZE) {
            throw new IllegalArgumentException("Ukuran bukti pembayaran maksimal 5MB.");
        }

        String submittedName = Paths.get(proofPart.getSubmittedFileName()).getFileName().toString();
        String extension = getExtension(submittedName);
        String contentType = proofPart.getContentType() == null ? "" : proofPart.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED_PROOF_EXTENSIONS.contains(extension) || !ALLOWED_PROOF_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Format bukti pembayaran harus PDF, PNG, SVG, JPG, atau JPEG.");
        }

        String fileName = "bukti-" + idBooking.toLowerCase(Locale.ROOT) + "-" + System.currentTimeMillis() + "." + extension;
        savePaymentProof(proofPart, fileName);
        return PROOF_UPLOAD_DIR + "/" + fileName;
    }

    private void savePaymentProof(Part proofPart, String fileName) throws IOException {
        String deployedPath = getServletContext().getRealPath("/assets/" + PROOF_UPLOAD_DIR);
        Path sourceUploadDir = Paths.get(System.getProperty("user.dir"), "src", "main", "webapp", "assets", PROOF_UPLOAD_DIR);
        Path deployedUploadDir = deployedPath == null ? sourceUploadDir : Paths.get(deployedPath);

        Files.createDirectories(sourceUploadDir);
        Files.createDirectories(deployedUploadDir);

        Path sourceTarget = sourceUploadDir.resolve(fileName).normalize();
        Path deployedTarget = deployedUploadDir.resolve(fileName).normalize();
        if (!sourceTarget.startsWith(sourceUploadDir.normalize()) || !deployedTarget.startsWith(deployedUploadDir.normalize())) {
            throw new IOException("Nama file bukti pembayaran tidak valid.");
        }

        try (InputStream input = proofPart.getInputStream()) {
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
