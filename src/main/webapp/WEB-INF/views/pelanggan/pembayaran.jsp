<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<main class="app-shell">
    <%@ include file="/WEB-INF/views/layout/sidebar-user.jsp" %>
    <section class="main-content">
        <div class="page-title">
            <div>
                <h1>Selesaikan Pembayaran</h1>
                <p>Pastikan detail pesanan Anda sudah benar sebelum melanjutkan.</p>
            </div>
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
                                <strong>${booking.statusLabel}</strong>
                            </div>
                            <div class="total-box">
                                <span>Total Biaya</span>
                                <strong><fmt:formatNumber value="${booking.totalBiaya}" type="currency" currencySymbol="Rp " maxFractionDigits="0"/></strong>
                            </div>
                        </div>
                    </article>

                    <c:choose>
                        <c:when test="${booking.paymentLabel != 'Lunas' && (booking.statusBooking == 'MENUNGGU_KONFIRMASI' || booking.statusBooking == 'DIKONFIRMASI' || booking.statusBooking == 'MENUNGGU_PEMBAYARAN')}">
                            <form method="post" action="${pageContext.request.contextPath}/pembayaran" class="form-card">
                                <input type="hidden" name="idBooking" value="${booking.idBooking}">
                                <h2>Pilih Metode Pembayaran</h2>

                                <p class="form-label">TRANSFER BANK (VIRTUAL ACCOUNT)</p>
                                <div class="payment-method">
                                    <label><input type="radio" name="metodePembayaran" value="BCA Virtual Account" required> BCA Virtual Account</label>
                                    <label><input type="radio" name="metodePembayaran" value="Mandiri Virtual Account"> Mandiri Virtual Account</label>
                                </div>

                                <p class="form-label">E-WALLET</p>
                                <div class="payment-method">
                                    <label><input type="radio" name="metodePembayaran" value="GoPay"> GoPay</label>
                                    <label><input type="radio" name="metodePembayaran" value="OVO"> OVO</label>
                                </div>

                                <div style="height:160px;border-bottom:1px solid var(--line);"></div>
                                <button class="btn-primary full" type="submit">Bayar <fmt:formatNumber value="${booking.totalBiaya}" type="currency" currencySymbol="Rp " maxFractionDigits="0"/></button>
                                <p class="muted" style="margin-top:16px;font-size:13px;">Dengan menekan tombol Bayar, Anda menyetujui Syarat & Ketentuan yang berlaku.</p>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <div class="form-card">
                                <c:choose>
                                    <c:when test="${booking.statusBooking == 'DIBAYAR' || booking.paymentLabel == 'Lunas'}">
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
