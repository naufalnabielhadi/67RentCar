<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<main class="app-shell">
    <%@ include file="/WEB-INF/views/layout/sidebar-user.jsp" %>
    <section class="main-content">
        <div class="page-title">
            <div>
                <h1>Selesaikan Pembayaran</h1>
                <p>Pastikan detail pesanan Anda sudah benar sebelum melanjutkan.</p>
            </div>
            <a class="btn-secondary" href="${pageContext.request.contextPath}/pelanggan/transaksi">Kembali</a>
        </div>

        <c:if test="${not empty error}">
            <div class="alert error">${error}</div>
        </c:if>

        <c:choose>
            <c:when test="${empty booking}">
                <div class="detail-card">
                    <p class="muted">Booking tidak ditemukan.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="payment-layout">
                    <article class="card payment-summary">
                        <div class="content">
                            <h2>Ringkasan Pesanan</h2>
                        </div>
                        <img class="detail-image" src="${pageContext.request.contextPath}/assets/${booking.gambarPath}" alt="${booking.merk} ${booking.model}" onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/assets/img/payment-car.png';">
                        <div class="content">
                            <div class="summary-line">
                                <span class="muted">Kendaraan</span>
                                <strong>${booking.merk} ${booking.model}<br><span class="muted">Tahun ${booking.tahun}</span></strong>
                            </div>
                            <div class="summary-line">
                                <span class="muted">Durasi Sewa</span>
                                <strong>${booking.durasiHari} Hari<br><span class="muted">${booking.tanggalSewa} - ${booking.tanggalKembali}</span></strong>
                            </div>
                            <div class="summary-line">
                                <span class="muted">Status</span>
                                <strong>
                                    <span class="status-badge ${booking.statusBadgeClass}">
                                        <span class="status-badge-icon ${booking.statusIconClass}"></span>
                                        ${booking.statusLabel}
                                    </span>
                                </strong>
                            </div>
                            <div class="total-box">
                                <span>Total Biaya</span>
                                <strong><fmt:formatNumber value="${booking.totalBiaya}" type="currency" currencySymbol="Rp " maxFractionDigits="0"/></strong>
                            </div>
                        </div>
                    </article>

                    <c:choose>
                        <c:when test="${booking.paymentLabel != 'LUNAS' && (booking.statusBooking == 'MENUNGGU_KONFIRMASI' || booking.statusBooking == 'DIKONFIRMASI' || booking.statusBooking == 'MENUNGGU_PEMBAYARAN')}">
                            <form method="post" action="${pageContext.request.contextPath}/pembayaran" enctype="multipart/form-data" class="form-card payment-form js-payment-form" novalidate>
                                <input type="hidden" name="idBooking" value="${booking.idBooking}">
                                <h2>Pilih Metode Pembayaran</h2>
                                <div class="alert error js-payment-error d-none" role="alert"></div>

                                <p class="form-label">METODE PEMBAYARAN</p>
                                <div class="payment-method payment-method-three">
                                    <label><input type="radio" name="metodePembayaran" value="Debit" required> Debit</label>
                                    <label><input type="radio" name="metodePembayaran" value="Qris"> Qris</label>
                                    <label><input type="radio" name="metodePembayaran" value="Tunai"> Tunai</label>
                                </div>

                                <label for="buktiPembayaran">Bukti Pembayaran</label>
                                <input class="form-control" id="buktiPembayaran" type="file" name="buktiPembayaran" accept=".pdf,.png,.svg,.jpg,.jpeg,application/pdf,image/png,image/svg+xml,image/jpeg" required>
                                <p class="muted payment-proof-help">PDF, PNG, SVG, JPG, atau JPEG. Maksimal 5MB.</p>

                                <button class="btn-primary full" type="submit">Bayar <fmt:formatNumber value="${booking.totalBiaya}" type="currency" currencySymbol="Rp " maxFractionDigits="0"/></button>
                                <p class="muted" style="margin-top:16px;font-size:13px;">Dengan menekan tombol Bayar, Anda menyetujui Syarat & Ketentuan yang berlaku.</p>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <div class="form-card">
                                <c:choose>
                                    <c:when test="${booking.statusBooking == 'DIBAYAR' || booking.paymentLabel == 'LUNAS'}">
                                        <div class="alert success">Pembayaran sudah diproses.</div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="alert error">Pembayaran tidak tersedia untuk booking yang sudah dibatalkan atau ditolak.</div>
                                    </c:otherwise>
                                </c:choose>
                                <p>Metode: ${empty booking.metodePembayaran ? '-' : booking.metodePembayaran}</p>
                                <a class="btn-secondary" href="${pageContext.request.contextPath}/pelanggan/riwayat">Lihat Riwayat</a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</main>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
