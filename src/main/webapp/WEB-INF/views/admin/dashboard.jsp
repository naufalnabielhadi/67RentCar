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

        <c:set var="bookingAktif" value="0" />
        <c:forEach var="item" items="${riwayatList}">
            <c:if test="${item.statusBooking == 'MENUNGGU_KONFIRMASI' || item.statusBooking == 'DIKONFIRMASI' || item.statusBooking == 'MENUNGGU_PEMBAYARAN' || item.statusBooking == 'DIBAYAR'}">
                <c:set var="bookingAktif" value="${bookingAktif + 1}" />
            </c:if>
        </c:forEach>

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
                <strong>${bookingAktif}</strong>
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
            <c:set var="dashboardOrdersCount" value="0" />
            <div class="table-wrap dashboard-orders-card">
            <div class="table-responsive">
                <table class="table admin-table admin-history-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Pelanggan</th>
                            <th>Mobil</th>
                            <th>Tanggal Sewa</th>
                            <th>Total Harga</th>
                            <th>Status</th>
                            <th>Aksi</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="item" items="${riwayatList}">
                            <c:if test="${item.statusBooking != 'SELESAI'}">
                                <c:set var="dashboardOrdersCount" value="${dashboardOrdersCount + 1}" />
                                <tr>
                                    <td><strong>${item.idBooking}</strong></td>
                                    <td>
                                        <div class="admin-customer-cell">
                                            <span class="table-avatar"><span>${fn:toUpperCase(fn:substring(item.username, 0, 1))}</span></span>
                                            <span><strong>${item.username}</strong><br><span class="meta">${item.idUser}</span></span>
                                        </div>
                                    </td>
                                    <td>
                                        <strong>${item.merk} ${item.model}</strong><br>
                                        <span class="badge-warning">${item.platNomor}</span>
                                    </td>
                                    <td>${item.tanggalSewa}<br><span class="meta">sampai ${item.tanggalKembali}</span></td>
                                    <td><fmt:formatNumber value="${item.totalBiaya}" type="currency" currencySymbol="Rp " maxFractionDigits="0"/></td>
                                    <td>
                                        <span class="status-badge ${item.statusBadgeClass}">
                                            <span class="status-badge-icon ${item.statusIconClass}"></span>
                                            ${item.statusLabel}
                                        </span>
                                    </td>
                                    <td>
                                        <button class="btn-icon btn-eye" type="button" data-bs-toggle="modal" data-bs-target="#dashboardBookingModal-${item.idBooking}" title="Lihat Detail" aria-label="Lihat detail booking ${item.idBooking}">
                                            <span class="icon-eye"></span>
                                        </button>
                                    </td>
                                </tr>
                            </c:if>
                        </c:forEach>
                        <c:if test="${dashboardOrdersCount == 0}">
                            <tr>
                                <td colspan="7">Belum ada pesanan aktif.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
            <div class="table-footer">
                <span>Menampilkan ${dashboardOrdersCount} pesanan</span>
                <span>1</span>
            </div>
            </div>
        </section>
        <c:forEach var="item" items="${riwayatList}">
            <c:if test="${item.statusBooking != 'SELESAI'}">
                <div class="modal fade" id="dashboardBookingModal-${item.idBooking}" tabindex="-1" aria-labelledby="dashboardBookingModalLabel-${item.idBooking}" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered modal-lg">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h2 class="modal-title fs-5" id="dashboardBookingModalLabel-${item.idBooking}">Detail Booking ${item.idBooking}</h2>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Tutup"></button>
                            </div>
                            <div class="modal-body">
                                <dl class="booking-detail-grid">
                                    <dt>ID Booking</dt><dd>${item.idBooking}</dd>
                                    <dt>Pelanggan</dt><dd>${item.username}</dd>
                                    <dt>Kartu Identitas</dt>
                                    <dd>
                                        <c:choose>
                                            <c:when test="${not empty item.kartuIdentitas}">
                                                <a class="file-attachment" href="${pageContext.request.contextPath}/assets/${item.kartuIdentitas}" target="_blank" rel="noopener">
                                                    <span class="file-attachment-icon" aria-hidden="true"></span>
                                                    <span>Lampiran identitas</span>
                                                </a>
                                            </c:when>
                                            <c:otherwise>Belum diunggah</c:otherwise>
                                        </c:choose>
                                    </dd>
                                    <dt>Mobil</dt><dd>${item.merk} ${item.model}</dd>
                                    <dt>Plat Nomor</dt><dd>${item.platNomor}</dd>
                                    <dt>Tanggal Sewa</dt><dd>${item.tanggalSewa}</dd>
                                    <dt>Tanggal Kembali</dt><dd>${item.tanggalKembali}</dd>
                                    <dt>Durasi</dt><dd>${item.durasiHari} Hari</dd>
                                    <dt>Total Harga</dt><dd><fmt:formatNumber value="${item.totalBiaya}" type="currency" currencySymbol="Rp " maxFractionDigits="0"/></dd>
                                    <dt>Status Booking</dt><dd>${item.statusLabel}</dd>
                                    <dt>Status Pembayaran</dt><dd>${item.paymentLabel}</dd>
                                </dl>
                            </div>
                            <div class="modal-footer booking-modal-actions">
                                <button type="button" class="btn-secondary" data-bs-dismiss="modal">Tutup</button>
                                <c:if test="${item.statusBooking == 'MENUNGGU_KONFIRMASI'}">
                                    <form method="post" action="${pageContext.request.contextPath}/admin/booking/tolak"
                                          class="js-confirm"
                                          data-message="Tolak booking ini? Jika transaksi sudah lunas, status transaksi akan menjadi Dikembalikan.">
                                        <input type="hidden" name="idBooking" value="${item.idBooking}">
                                        <button class="btn-danger" type="submit">Tolak Booking</button>
                                    </form>
                                    <form method="post" action="${pageContext.request.contextPath}/admin/booking/konfirmasi">
                                        <input type="hidden" name="idBooking" value="${item.idBooking}">
                                        <button class="btn-primary" type="submit">Konfirmasi Booking</button>
                                    </form>
                                </c:if>
                                <c:if test="${item.statusBooking == 'DIKONFIRMASI' || item.statusBooking == 'DIBAYAR'}">
                                    <form method="post" action="${pageContext.request.contextPath}/admin/booking/selesai"
                                          class="js-confirm"
                                          data-message="Selesaikan booking ini dan ubah status mobil menjadi Sudah Dikembalikan?">
                                        <input type="hidden" name="idBooking" value="${item.idBooking}">
                                        <button class="btn-primary" type="submit">Selesaikan Booking</button>
                                    </form>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>
            </c:if>
        </c:forEach>
    </section>
</main>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
