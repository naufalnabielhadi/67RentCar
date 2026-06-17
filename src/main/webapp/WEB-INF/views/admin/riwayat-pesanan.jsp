<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<main class="admin-layout">
    <%@ include file="/WEB-INF/views/layout/sidebar-admin.jsp" %>
    <section class="admin-content">
        <div class="page-title">
            <div>
                <h1>Riwayat Pesanan</h1>
                <p>Pantau dan kelola semua transaksi penyewaan. Gunakan filter untuk mempermudah pencarian data historis.</p>
            </div>
            <button class="btn-secondary" type="button">Ekspor CSV</button>
        </div>

        <c:if test="${not empty requestScope.error}">
            <div class="alert error">${requestScope.error}</div>
        </c:if>
        <c:if test="${not empty sessionScope.error}">
            <div class="alert error">${sessionScope.error}</div>
            <c:remove var="error" scope="session" />
        </c:if>
        <c:if test="${not empty sessionScope.success}">
            <div class="alert success">${sessionScope.success}</div>
            <c:remove var="success" scope="session" />
        </c:if>

        <form method="get" action="${pageContext.request.contextPath}/admin/pesanan" class="table-toolbar admin-history-toolbar card p-3 mb-4">
            <div class="row g-3 align-items-center">
                <div class="col-12 col-lg-8">
                    <div class="input-group history-search">
                        <button class="input-group-text catalog-search-button" type="submit" aria-label="Cari pesanan">
                            <span class="search-icon" aria-hidden="true"></span>
                        </button>
                        <input class="form-control" type="search" name="q" value="${param.q}" placeholder="Cari ID Pesanan, Nama Pelanggan...">
                    </div>
                </div>
                <div class="col-12 col-md-6 col-lg-3">
                    <input class="form-control" type="date" name="tanggal" value="${param.tanggal}">
                </div>
                <div class="col-12 col-md-6 col-lg-1">
                    <button class="btn-primary full admin-filter-button" type="submit">Cari</button>
                </div>
            </div>
        </form>

        <div class="table-wrap admin-history-card">
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
                                <button class="btn-icon btn-eye" type="button" data-bs-toggle="modal" data-bs-target="#bookingModal-${item.idBooking}" title="Lihat Detail" aria-label="Lihat detail booking ${item.idBooking}">
                                    <span class="icon-eye"></span>
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty riwayatList}">
                        <tr>
                            <td colspan="7">
                                <c:choose>
                                    <c:when test="${not empty param.q || not empty param.tanggal}">
                                        Hasil pencarian tidak ditemukan.
                                    </c:when>
                                    <c:otherwise>
                                        Belum ada pesanan.
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
            </div>
            <div class="table-footer">
                <span>Menampilkan ${fn:length(riwayatList)} pesanan</span>
                <span>1</span>
            </div>
        </div>
        <c:forEach var="item" items="${riwayatList}">
            <div class="modal fade" id="bookingModal-${item.idBooking}" tabindex="-1" aria-labelledby="bookingModalLabel-${item.idBooking}" aria-hidden="true">
                <div class="modal-dialog modal-dialog-centered modal-lg">
                    <div class="modal-content">
                        <div class="modal-header">
                            <h2 class="modal-title fs-5" id="bookingModalLabel-${item.idBooking}">Detail Booking ${item.idBooking}</h2>
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
        </c:forEach>
    </section>
</main>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
