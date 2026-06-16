<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<main class="admin-layout">
    <%@ include file="/WEB-INF/views/layout/sidebar-admin.jsp" %>
    <section class="admin-content">
        <div class="page-title">
            <div>
                <h1>Laporan & Analitik</h1>
                <p>Pantau performa bisnis dan analisis data transaksi secara mendalam.</p>
            </div>
            <button class="btn-primary" type="button">Unduh Laporan</button>
        </div>

        <c:if test="${not empty error}">
            <div class="alert error">${error}</div>
        </c:if>

        <section class="dashboard-grid">
            <div class="stat-card">
                <span>Booking Bulan Ini</span>
                <strong>${empty bookingBulanIni ? 0 : bookingBulanIni}</strong>
            </div>
            <div class="stat-card">
                <span>Pendapatan</span>
                <strong><fmt:formatNumber value="${empty pendapatanBulanIni ? 0 : pendapatanBulanIni}" type="currency" currencySymbol="Rp " maxFractionDigits="0"/></strong>
            </div>
            <div class="stat-card">
                <span>Mobil Favorit</span>
                <strong>
                    <c:choose>
                        <c:when test="${empty mobilFavorit}">-</c:when>
                        <c:otherwise>${mobilFavorit.merk} ${mobilFavorit.model}</c:otherwise>
                    </c:choose>
                </strong>
            </div>
            <div class="stat-card">
                <span>Mobil Dibooking</span>
                <strong>${empty mobilDibooking ? 0 : mobilDibooking}</strong>
            </div>
        </section>

        <section class="report-transactions mt-4">
            <div class="section-title report-section-title mb-3">
                <h2>Transaksi Terbaru</h2>
            </div>
            <div class="card table-card">
            <div class="table-responsive">
                <table class="table admin-table report-table">
                    <thead>
                        <tr>
                            <th>ID Transaksi</th>
                            <th>Tanggal</th>
                            <th>Kendaraan</th>
                            <th>Pelanggan</th>
                            <th>Durasi</th>
                            <th>Total Bayar</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${transaksiTerbaru}">
                            <tr>
                                <td>${item.idBooking}</td>
                                <td>${item.tanggalSewa}</td>
                                <td>${item.merk} ${item.model}</td>
                                <td>${item.username}</td>
                                <td>${item.durasiHari} Hari</td>
                                <td><fmt:formatNumber value="${item.totalBiaya}" type="currency" currencySymbol="Rp " maxFractionDigits="0"/></td>
                                <td>
                                    <span class="payment-badge ${item.paymentBadgeClass}">
                                        <span class="status-badge-icon ${item.paymentIconClass}"></span>
                                        ${item.paymentLabel}
                                    </span>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty transaksiTerbaru}">
                            <tr>
                                <td colspan="7">Belum ada transaksi.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
            </div>
        </section>
    </section>
</main>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
