<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<main class="app-shell">
    <%@ include file="/WEB-INF/views/layout/sidebar-user.jsp" %>
    <section class="main-content history-page">
        <div class="page-title">
            <div>
                <h1>Riwayat Transaksi</h1>
                <p>Pantau status pembayaran dari setiap pesanan Anda.</p>
            </div>
        </div>

        <c:if test="${not empty error}">
            <div class="alert error">${error}</div>
        </c:if>

        <form class="history-toolbar mb-4" method="get" action="${pageContext.request.contextPath}/pelanggan/transaksi">
            <div class="input-group history-search">
                <button class="input-group-text catalog-search-button" type="submit" aria-label="Cari transaksi">
                    <span class="search-icon" aria-hidden="true"></span>
                </button>
                <input class="form-control" type="search" name="q" value="${fn:escapeXml(query)}" placeholder="Cari ID Booking atau nama mobil...">
            </div>
        </form>

        <section class="table-wrap table-card user-transaction-card">
            <div class="table-responsive">
            <table class="table admin-table report-table">
                <thead>
                    <tr>
                        <th>ID<br>Transaksi</th>
                        <th>Mobil</th>
                        <th>Metode</th>
                        <th>Tanggal</th>
                        <th>Total</th>
                        <th>Status</th>
                        <th>Aksi</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${riwayatList}">
                        <tr>
                            <td><strong>#${item.idPembayaran == null ? item.idBooking : item.idPembayaran}</strong></td>
                            <td>
                                <strong>${item.merk}<br>${item.model}</strong>
                                <span>${item.platNomor}</span>
                            </td>
                            <td>${empty item.metodePembayaran ? '-' : item.metodePembayaran}</td>
                            <td>${empty item.tanggalPembayaran ? '-' : item.tanggalPembayaran}</td>
                            <td><fmt:formatNumber value="${item.totalBiaya}" type="currency" currencySymbol="Rp " maxFractionDigits="0"/></td>
                            <td>
                                <span class="payment-badge ${item.paymentBadgeClass}">
                                    <span class="status-badge-icon ${item.paymentIconClass}"></span>
                                    ${item.paymentLabel}
                                </span>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${item.paymentLabel != 'LUNAS' && (item.statusBooking == 'MENUNGGU_KONFIRMASI' || item.statusBooking == 'DIKONFIRMASI' || item.statusBooking == 'MENUNGGU_PEMBAYARAN')}">
                                        <a class="btn-primary history-action" href="${pageContext.request.contextPath}/pembayaran?idBooking=${item.idBooking}">Bayar Sekarang</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a class="btn-secondary history-action" href="${pageContext.request.contextPath}/pelanggan/riwayat">Lihat Pesanan</a>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty riwayatList}">
                        <tr class="empty-row">
                            <td colspan="7">
                                <c:choose>
                                    <c:when test="${not empty query}">
                                        <strong>Hasil pencarian tidak ditemukan.</strong>
                                        <span>Coba gunakan ID booking, merk, model, plat nomor, atau status lain.</span>
                                    </c:when>
                                    <c:otherwise>
                                        <strong>Belum ada transaksi.</strong>
                                        <span>Transaksi dari pesanan Anda akan muncul di tabel ini.</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
            </div>
            <div class="history-footer">
                <span>Menampilkan ${empty riwayatList ? 0 : 1}-${fn:length(riwayatList)} dari ${fn:length(riwayatList)} transaksi</span>
                <div class="pagination">
                    <button type="button">&lt;</button>
                    <button class="active" type="button">1</button>
                    <button type="button">2</button>
                    <button type="button">3</button>
                    <button type="button">&gt;</button>
                </div>
            </div>
        </section>
    </section>
</main>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
