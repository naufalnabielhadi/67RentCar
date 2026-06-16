<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<main class="app-shell">
    <%@ include file="/WEB-INF/views/layout/sidebar-user.jsp" %>
    <section class="main-content">
        <div class="page-title">
            <div>
                <c:set var="dashboardFirstName" value="${sessionScope.user.username}" />
                <c:if test="${fn:contains(sessionScope.user.username, ' ')}">
                    <c:set var="dashboardFirstName" value="${fn:substringBefore(sessionScope.user.username, ' ')}" />
                </c:if>
                <h1>Halo, ${dashboardFirstName}! <span aria-hidden="true">&#128075;</span></h1>
                <p>Selamat datang kembali di dashboard Anda. Berikut ringkasan aktivitas rental Anda hari ini.</p>
            </div>
        </div>

        <c:if test="${not empty error}">
            <div class="alert error">${error}</div>
        </c:if>

        <c:set var="latestBooking" value="${null}" />
        <c:forEach var="item" items="${riwayatList}" end="0">
            <c:set var="latestBooking" value="${item}" />
        </c:forEach>

        <div class="row g-4 dashboard-summary">
            <div class="col-12 col-lg-8">
            <article class="dashboard-card booking-summary h-100">
                <div class="booking-summary-head d-flex align-items-start justify-content-between gap-3">
                    <span class="badge-success">Booking Aktif</span>
                    <div class="count-box" aria-label="Jumlah booking aktif">${empty latestBooking ? 0 : 1}</div>
                </div>
                <c:choose>
                    <c:when test="${not empty latestBooking}">
                        <h2>${latestBooking.merk} ${latestBooking.model}</h2>
                        <p class="muted">Sewa - ${latestBooking.durasiHari} Hari</p>
                        <div class="date-box">
                            <div>
                                <span class="muted">Pengambilan</span><br>
                                <strong>${latestBooking.tanggalSewa}, 08:00</strong>
                            </div>
                            <div>
                                <span class="muted">Pengembalian</span><br>
                                <strong>${latestBooking.tanggalKembali}, 08:00</strong>
                            </div>
                        </div>
                        <div class="actions dashboard-actions">
                            <a class="btn-primary" href="${pageContext.request.contextPath}/pelanggan/riwayat">Lihat Detail <span>></span></a>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <h2>Belum Ada Booking</h2>
                        <p class="muted">Pilih mobil dari katalog untuk membuat pesanan pertama Anda.</p>
                        <a class="btn-primary" href="${pageContext.request.contextPath}/mobil">Lihat Mobil</a>
                    </c:otherwise>
                </c:choose>
            </article>
            </div>

            <div class="col-12 col-lg-4">
            <article class="dashboard-card center payment-card h-100">
                <h3>Status Pembayaran</h3>
                <c:choose>
                    <c:when test="${not empty latestBooking && latestBooking.paymentLabel == 'LUNAS'}">
                        <div class="payment-status-icon payment-safe-icon"></div>
                        <h2 class="payment-safe">LUNAS</h2>
                        <p class="muted">Pembayaran untuk booking ini sudah diterima.</p>
                        <a class="btn-primary full" href="${pageContext.request.contextPath}/pelanggan/riwayat">Lihat Riwayat</a>
                    </c:when>
                    <c:when test="${not empty latestBooking && latestBooking.statusBooking == 'MENUNGGU_KONFIRMASI'}">
                        <div class="payment-status-icon payment-warning-icon">!</div>
                        <h2 class="payment-waiting">MENUNGGU KONFIRMASI</h2>
                        <p class="muted">Anda tetap bisa menyelesaikan pembayaran sambil menunggu persetujuan admin.</p>
                        <a class="btn-secondary full" href="${pageContext.request.contextPath}/pembayaran?idBooking=${latestBooking.idBooking}">Bayar Sekarang</a>
                    </c:when>
                    <c:when test="${not empty latestBooking && (latestBooking.statusBooking == 'DIKONFIRMASI' || latestBooking.statusBooking == 'MENUNGGU_PEMBAYARAN')}">
                        <div class="payment-status-icon payment-warning-icon">!</div>
                        <h2 class="payment-waiting">TIDAK LUNAS</h2>
                        <p class="muted">Selesaikan pembayaran untuk mengamankan kendaraan Anda.</p>
                        <a class="btn-secondary full" href="${pageContext.request.contextPath}/pembayaran?idBooking=${latestBooking.idBooking}">Bayar Sekarang</a>
                    </c:when>
                    <c:otherwise>
                        <div class="payment-status-icon payment-safe-icon"></div>
                        <h2 class="payment-safe">Aman</h2>
                        <p class="muted">Tidak ada pembayaran aktif yang perlu diselesaikan.</p>
                        <a class="btn-primary full" href="${pageContext.request.contextPath}/mobil">Booking Sekarang</a>
                    </c:otherwise>
                </c:choose>
            </article>
            </div>
        </div>

        <div class="dashboard-shortcuts mt-4">
            <a class="quick-link" href="${pageContext.request.contextPath}/mobil">
                <span class="quick-icon icon-car"></span>
                <span><strong>Katalog Mobil</strong><br><small>Jelajahi armada kami</small></span>
                <strong>></strong>
            </a>
            <a class="quick-link light" href="${pageContext.request.contextPath}/pelanggan/riwayat">
                <span class="quick-icon icon-history"></span>
                <span><strong>Riwayat Pesanan</strong><br><small>Lihat riwayat sewa</small></span>
                <strong>></strong>
            </a>
        </div>
    </section>
</main>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
