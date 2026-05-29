<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<main class="app-shell">
    <%@ include file="/WEB-INF/views/layout/sidebar-user.jsp" %>
    <section class="main-content">
        <div class="page-title">
            <div>
                <h1>Selesaikan Pemesanan Anda</h1>
                <p>Tinjau detail kendaraan dan tentukan jadwal sewa Anda.</p>
            </div>
        </div>

        <c:if test="${not empty error}">
            <div class="alert error">${error}</div>
        </c:if>

        <c:choose>
            <c:when test="${empty mobil}">
                <div class="detail-card">
                    <p class="muted">Mobil tidak ditemukan atau belum dipilih.</p>
                    <a class="btn-secondary" href="${pageContext.request.contextPath}/mobil">Kembali ke Katalog</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="booking-layout">
                    <article class="card booking-car-card">
                        <img class="detail-image" src="${pageContext.request.contextPath}/assets/${mobil.gambarPath}" alt="${mobil.merk} ${mobil.model}" onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/assets/img/booking-car.png';">
                        <div class="content">
                            <h2>${mobil.merk} ${mobil.model}</h2>
                            <p class="muted">${mobil.transmisi} - ${mobil.bahanBakar} - ${fn:replace(mobil.kapasitas, 'Penumpang', 'Kursi')}</p>
                            <div class="actions">
                                <span class="badge-warning">${fn:replace(mobil.kapasitas, 'Penumpang', 'Kursi')}</span>
                                <span class="badge-warning">${mobil.transmisi}</span>
                                <span class="badge-warning">${mobil.bahanBakar}</span>
                            </div>
                            <div class="summary-line" style="margin-top:24px;border-top:1px solid var(--line-soft);padding-top:22px;">
                                <span class="muted">Tarif Dasar</span>
                                <strong><fmt:formatNumber value="${mobil.hargaSewaPerHari}" type="currency" currencySymbol="Rp " maxFractionDigits="0"/> <small>/hari</small></strong>
                            </div>
                        </div>
                    </article>

                    <form method="post" action="${pageContext.request.contextPath}/booking" class="form-card js-booking-form" data-price="${mobil.hargaSewaPerHari}" novalidate>
                        <input type="hidden" name="idMobil" value="${mobil.idMobil}">
                        <h2>Informasi Penyewaan</h2>
                        <div class="alert error js-booking-error d-none" role="alert"></div>

                        <div class="form-row">
                            <div>
                                <label>Tanggal Sewa</label>
                                <input class="form-control js-date-start" type="date" name="tanggalSewa" value="${param.tanggalSewa}">
                            </div>
                            <div>
                                <label>Tanggal Kembali</label>
                                <input class="form-control js-date-end" type="date" name="tanggalKembali" value="${param.tanggalKembali}">
                            </div>
                        </div>

                        <div class="estimate-box">
                            <p style="font-weight:900;text-transform:uppercase;">Estimasi Biaya</p>
                            <div class="estimate-line">
                                <span>Harga per Hari</span>
                                <strong><fmt:formatNumber value="${mobil.hargaSewaPerHari}" type="currency" currencySymbol="Rp " maxFractionDigits="0"/></strong>
                            </div>
                            <div class="estimate-line">
                                <span>Durasi Sewa</span>
                                <strong><span class="js-duration">0</span> Hari</strong>
                            </div>
                            <div class="estimate-total">
                                <span>Total Biaya</span>
                                <strong class="js-total">Rp 0</strong>
                            </div>
                        </div>

                        <div class="actions booking-form-actions">
                            <a class="btn-secondary" href="${pageContext.request.contextPath}/mobil/detail?idMobil=${mobil.idMobil}">Kembali</a>
                            <button class="btn-primary" type="submit">Konfirmasi Booking</button>
                        </div>
                    </form>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</main>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
