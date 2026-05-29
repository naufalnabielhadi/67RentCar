<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<main class="app-shell">
    <%@ include file="/WEB-INF/views/layout/sidebar-user.jsp" %>
    <section class="main-content history-page">
        <div class="page-title">
            <div>
                <h1>Riwayat Pesanan Saya</h1>
                <p>Pantau status booking dan riwayat perjalanan Anda secara real-time.</p>
            </div>
        </div>

        <c:if test="${not empty error}">
            <div class="alert error">${error}</div>
        </c:if>

        <form class="history-toolbar mb-4" method="get" action="${pageContext.request.contextPath}/pelanggan/riwayat">
            <div class="input-group history-search">
                <button class="input-group-text catalog-search-button" type="submit" aria-label="Cari riwayat">
                    <span class="search-icon" aria-hidden="true"></span>
                </button>
                <input class="form-control" type="search" name="q" value="${fn:escapeXml(query)}" placeholder="Cari ID Booking atau nama mobil...">
            </div>
        </form>

        <section class="history-table-card">
            <table class="history-table">
                <thead>
                    <tr>
                        <th>ID<br>Booking</th>
                        <th>Mobil</th>
                        <th>Durasi</th>
                        <th>Total</th>
                        <th>Status</th>
                        <th>Aksi</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${riwayatList}">
                        <tr>
                            <td><strong>#${item.idBooking}</strong></td>
                            <td>
                                <strong>${item.merk}<br>${item.model}</strong>
                                <span>${item.platNomor}</span>
                            </td>
                            <td>
                                ${item.tanggalSewa}<br>- ${item.tanggalKembali}
                                <span>${item.durasiHari} Hari</span>
                            </td>
                            <td><fmt:formatNumber value="${item.totalBiaya}" type="currency" currencySymbol="Rp " maxFractionDigits="0"/></td>
                            <td>
                                <c:set var="userStatusLabel" value="${item.statusLabel}" />
                                <c:if test="${item.statusBooking == 'DIBAYAR' || item.statusLabel == 'Selesai'}">
                                    <c:set var="userStatusLabel" value="Dikonfirmasi" />
                                </c:if>
                                <c:if test="${item.statusBooking == 'DITOLAK'}">
                                    <c:set var="userStatusLabel" value="Dibatalkan" />
                                </c:if>
                                <span class="status-badge user-history-status ${item.statusBadgeClass}">${userStatusLabel}</span>
                                <span class="payment-badge ${item.paymentBadgeClass}">${item.paymentLabel}</span>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${item.statusBooking == 'MENUNGGU_KONFIRMASI'}">
                                        <div class="history-action-stack">
                                            <c:if test="${item.paymentLabel != 'Lunas'}">
                                                <a class="btn-primary history-action" href="${pageContext.request.contextPath}/pembayaran?idBooking=${item.idBooking}">Bayar Sekarang</a>
                                            </c:if>
                                            <button class="btn-danger-outline history-action" type="button" data-bs-toggle="modal" data-bs-target="#cancelModal-${item.idBooking}">
                                                Batalkan Booking
                                            </button>
                                        </div>
                                    </c:when>
                                    <c:when test="${(item.statusBooking == 'DIKONFIRMASI' || item.statusBooking == 'MENUNGGU_PEMBAYARAN') && item.paymentLabel != 'Lunas'}">
                                        <a class="btn-primary history-action" href="${pageContext.request.contextPath}/pembayaran?idBooking=${item.idBooking}">Bayar Sekarang</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a class="btn-secondary history-action" href="${pageContext.request.contextPath}/mobil/detail?idMobil=${item.idMobil}">Lihat Detail</a>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty riwayatList}">
                        <tr class="empty-row">
                            <td colspan="6">
                                <c:choose>
                                    <c:when test="${not empty query}">
                                        <strong>Hasil pencarian tidak ditemukan.</strong>
                                        <span>Coba gunakan ID booking, merk, model, plat nomor, atau status lain.</span>
                                    </c:when>
                                    <c:otherwise>
                                        <strong>Belum ada pesanan.</strong>
                                        <span>Pesanan yang Anda buat akan muncul di tabel ini.</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
            <div class="history-footer">
                <span>Menampilkan ${empty riwayatList ? 0 : 1}-${fn:length(riwayatList)} dari ${fn:length(riwayatList)} pesanan</span>
                <div class="pagination">
                    <button type="button">&lt;</button>
                    <button class="active" type="button">1</button>
                    <button type="button">2</button>
                    <button type="button">3</button>
                    <button type="button">&gt;</button>
                </div>
            </div>
        </section>
        <c:forEach var="item" items="${riwayatList}">
            <c:if test="${item.statusBooking == 'MENUNGGU_KONFIRMASI'}">
                <div class="modal fade" id="cancelModal-${item.idBooking}" tabindex="-1" aria-labelledby="cancelModalLabel-${item.idBooking}" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h2 class="modal-title fs-5" id="cancelModalLabel-${item.idBooking}">Batalkan Booking</h2>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Tutup"></button>
                            </div>
                            <div class="modal-body">
                                Booking <strong>${item.idBooking}</strong> untuk ${item.merk} ${item.model} akan dibatalkan dan mobil dikembalikan tersedia.
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn-secondary" data-bs-dismiss="modal">Tutup</button>
                                <form method="post" action="${pageContext.request.contextPath}/booking/cancel">
                                    <input type="hidden" name="idBooking" value="${item.idBooking}">
                                    <button class="btn-danger" type="submit">Batalkan Booking</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>
        </c:forEach>
    </section>
</main>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
