<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<main class="admin-layout">
    <%@ include file="/WEB-INF/views/layout/sidebar-admin.jsp" %>
    <section class="admin-content">
        <div class="page-title">
            <div>
                <h1>Data Mobil</h1>
                <p>Kelola inventaris kendaraan yang tersedia untuk disewa.</p>
            </div>
            <form class="admin-search-form" method="get" action="${pageContext.request.contextPath}/admin/mobil">
                <div class="input-group catalog-search">
                    <button class="input-group-text catalog-search-button" type="submit" aria-label="Cari mobil">
                        <span class="search-icon" aria-hidden="true"></span>
                    </button>
                    <input class="form-control search-control" type="search" name="q" value="${fn:escapeXml(query)}" placeholder="Cari mobil...">
                </div>
            </form>
        </div>

        <c:if test="${not empty error}">
            <div class="alert error">${error}</div>
        </c:if>

        <section class="stats-grid admin-car-stats">
            <div class="stat-card">
                <span>Total Mobil</span>
                <strong>${fn:length(mobilList)}</strong>
            </div>
            <div class="stat-card">
                <span>Tersedia</span>
                <c:set var="tersedia" value="0" />
                <c:forEach var="mobil" items="${mobilList}">
                    <c:if test="${mobil.statusMobil == 'TERSEDIA'}">
                        <c:set var="tersedia" value="${tersedia + 1}" />
                    </c:if>
                </c:forEach>
                <strong>${tersedia}</strong>
            </div>
        </section>

        <div class="table-wrap admin-car-table-card">
            <table class="table admin-car-table">
                <colgroup>
                    <col class="car-col-id">
                    <col class="car-col-vehicle">
                    <col class="car-col-plate">
                    <col class="car-col-price">
                    <col class="car-col-status">
                    <col class="car-col-actions">
                </colgroup>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Kendaraan</th>
                        <th>Plat Nomor</th>
                        <th>Harga / Hari</th>
                        <th>Status</th>
                        <th>Aksi</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty mobilList}">
                            <tr>
                                <td colspan="6" class="text-center">
                                    <c:choose>
                                        <c:when test="${not empty query}">
                                            Hasil pencarian tidak ditemukan.
                                        </c:when>
                                        <c:otherwise>
                                            Belum ada data mobil.
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="mobil" items="${mobilList}">
                                <tr>
                                    <td>${mobil.idMobil}</td>
                                    <td>
                                        <div class="actions">
                                            <img class="admin-thumb" src="${pageContext.request.contextPath}/assets/${mobil.gambarPath}" alt="${mobil.merk} ${mobil.model}" onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/assets/img/default-car.svg';">
                                            <span><strong>${mobil.merk}</strong><br><span class="meta">${mobil.model} ${mobil.tahun}</span></span>
                                        </div>
                                    </td>
                                    <td>${mobil.platNomor}</td>
                                    <td><fmt:formatNumber value="${mobil.hargaSewaPerHari}" type="currency" currencySymbol="Rp " maxFractionDigits="0"/></td>
                                    <td><span class="badge ${mobil.statusBadgeClass}">${mobil.statusLabel}</span></td>
                                    <td class="table-actions">
                                        <c:choose>
                                            <c:when test="${mobil.statusMobil == 'DISEWA'}">
                                                <button class="btn small secondary" type="button" disabled title="Mobil sedang disewa. Selesaikan booking setelah tanggal kembali tercapai sebelum mengedit.">Edit</button>
                                            </c:when>
                                            <c:otherwise>
                                                <a class="btn small secondary" href="${pageContext.request.contextPath}/admin/mobil/form?id=${mobil.idMobil}">Edit</a>
                                            </c:otherwise>
                                        </c:choose>
                                        <form method="post" action="${pageContext.request.contextPath}/admin/mobil/delete" class="inline-form js-confirm" data-message="Hapus data mobil ini?">
                                            <input type="hidden" name="idMobil" value="${mobil.idMobil}">
                                            <button class="btn small danger" type="submit">Hapus</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
            <div class="table-footer">
                <span>Menampilkan ${fn:length(mobilList)} mobil</span>
                <a class="btn-success sidebar-add-button btn-add-content" href="${pageContext.request.contextPath}/admin/mobil/form"><span class="icon-plus"></span> Tambah Mobil Baru</a>
            </div>
        </div>
    </section>
</main>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
