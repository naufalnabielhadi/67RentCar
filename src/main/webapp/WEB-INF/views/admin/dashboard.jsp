<%@ include file="/WEB-INF/views/layout/header.jsp" %>
<jsp:useBean id="now" class="java.util.Date" />

<main class="admin-layout">
    <%@ include file="/WEB-INF/views/layout/sidebar-admin.jsp" %>
    <section class="admin-content">
        <div class="page-title">
            <div>
                <h1>Ringkasan Operasional</h1>
                <p>Pantau performa armada dan transaksi hari ini secara real-time.</p>
            </div>
            <span class="btn-secondary"><fmt:formatDate value="${now}" pattern="dd MMMM yyyy" /></span>
        </div>

        <c:if test="${not empty error}">
            <div class="alert error">${error}</div>
        </c:if>

        <section class="dashboard-grid">
            <div class="stat-card">
                <span>Total Mobil</span>
                <strong>${fn:length(mobilList)}</strong>
            </div>
            <div class="stat-card">
                <span>Tersedia</span>
                <strong>${mobilTersedia}</strong>
            </div>
            <div class="stat-card">
                <span>Booking Aktif</span>
                <strong>${fn:length(riwayatList)}</strong>
            </div>
            <div class="stat-card">
                <span>Pendapatan</span>
                <strong><fmt:formatNumber value="${pendapatanBulanIni}" type="currency" currencySymbol="Rp " maxFractionDigits="0"/></strong>
            </div>
        </section>

        <section class="admin-dashboard-orders mt-4">
            <div class="section-title mb-3">
                <h2>Pesanan Terbaru</h2>
            </div>
            <div class="table-wrap dashboard-orders-card">
            <div class="table-responsive">
                <table class="table admin-table">
                    <thead>
                        <tr>
                            <th>ID Booking</th>
                            <th>Pelanggan</th>
                            <th>Kendaraan</th>
                            <th>Tanggal Sewa</th>
                            <th>Status</th>
                            <th>Aksi</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${riwayatList}" end="4">
                            <tr>
                                <td>${item.idBooking}</td>
                                <td>${item.username}</td>
                                <td>${item.merk} ${item.model}</td>
                                <td>${item.tanggalSewa} - ${item.tanggalKembali}</td>
                                <td>
                                    <c:set var="adminStatusLabel" value="${item.statusLabel}" />
                                    <c:set var="adminStatusIconClass" value="${item.statusIconClass}" />
                                    <c:if test="${item.statusBooking == 'DIKONFIRMASI'}">
                                        <c:set var="adminStatusLabel" value="Selesai" />
                                        <c:set var="adminStatusIconClass" value="status-icon-check" />
                                    </c:if>
                                    <span class="status-badge ${item.statusBadgeClass}">
                                        <span class="status-badge-icon ${adminStatusIconClass}"></span>
                                        ${adminStatusLabel}
                                    </span>
                                </td>
                                <td><a class="btn small secondary" href="${pageContext.request.contextPath}/admin/pesanan">Lihat</a></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty riwayatList}">
                            <tr>
                                <td colspan="6">Belum ada pesanan.</td>
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
