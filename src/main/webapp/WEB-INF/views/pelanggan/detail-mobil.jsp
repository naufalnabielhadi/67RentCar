<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<main class="app-shell">
    <%@ include file="/WEB-INF/views/layout/sidebar-user.jsp" %>
    <section class="main-content">
        <div class="page-title">
            <div>
                <h1>Detail Mobil</h1>
                <p>Tinjau spesifikasi mobil dan detail reservasi Anda sebelum melakukan pemesanan.</p>
            </div>
        </div>

        <c:choose>
            <c:when test="${empty mobil}">
                <div class="alert error">Data mobil tidak ditemukan.</div>
            </c:when>
            <c:otherwise>
                <div class="detail-layout">
                    <div>
                        <img class="detail-image" src="${pageContext.request.contextPath}/assets/${mobil.gambarPath}" alt="${mobil.merk} ${mobil.model}" onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/assets/img/default-car.svg';">
                        <div class="actions" style="margin-top:14px;">
                            <span class="meta">Inspeksi Terjamin</span>
                            <span class="meta">Dibersihkan Profesional</span>
                        </div>
                    </div>

                    <section class="detail-card">
                        <p class="meta" style="text-transform:uppercase;font-weight:800;">${mobil.merk}</p>
                        <h1>${mobil.model}</h1>
                        <div class="actions">
                            <span class="badge-warning">${mobil.platNomor}</span>
                            <span class="badge-warning">Tahun ${mobil.tahun}</span>
                        </div>

                        <div class="spec-grid">
                            <div class="spec-item"><span>Transmisi<br><strong>${mobil.transmisi}</strong></span></div>
                            <div class="spec-item"><span>Bahan Bakar<br><strong>${mobil.bahanBakar}</strong></span></div>
                            <div class="spec-item"><span>Kapasitas<br><strong>${fn:replace(mobil.kapasitas, 'Penumpang', 'Kursi')}</strong></span></div>
                            <div class="spec-item"><span>Status<br><strong>${mobil.statusLabel}</strong></span></div>
                        </div>

                        <p class="muted">Harga Sewa</p>
                        <p class="price"><fmt:formatNumber value="${mobil.hargaSewaPerHari}" type="currency" currencySymbol="Rp " maxFractionDigits="0"/> <small>/ hari</small></p>
                        <div class="form-actions">
                            <c:if test="${mobil.statusMobil == 'TERSEDIA'}">
                                <a class="btn-primary full" href="${pageContext.request.contextPath}/booking?idMobil=${mobil.idMobil}">Booking Sekarang</a>
                            </c:if>
                            <a class="btn-secondary full" href="${pageContext.request.contextPath}/mobil">Kembali</a>
                        </div>
                    </section>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</main>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
