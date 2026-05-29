<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<main class="admin-layout">
    <%@ include file="/WEB-INF/views/layout/sidebar-admin.jsp" %>
    <section class="admin-content">
        <section class="form-card">
            <span class="eyebrow">Manajemen Armada</span>
            <h1>${empty mobil ? 'Tambah Mobil' : 'Edit Mobil'}</h1>
            <p class="muted">Unggah gambar JPG, JPEG, PNG, atau GIF maksimal 2MB. Jika tidak ada gambar, sistem memakai gambar default.</p>

            <c:if test="${not empty error}">
                <div class="alert error">${error}</div>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/admin/mobil/save" enctype="multipart/form-data" class="js-car-form" novalidate>
                <input type="hidden" name="idMobil" value="${mobil.idMobil}">
                <input type="hidden" name="gambarLama" value="${mobil.gambar}">

                <label>Merk</label>
                <input class="form-control" type="text" name="merk" value="${mobil.merk}" required>

                <label>Model</label>
                <input class="form-control" type="text" name="model" value="${mobil.model}" required>

                <label>Plat Nomor</label>
                <input class="form-control" type="text" name="platNomor" value="${mobil.platNomor}" required>

                <label>Harga Sewa Per Hari</label>
                <input class="form-control" type="number" name="hargaSewaPerHari" value="${mobil.hargaSewaPerHari}" min="0" step="1000" required>

                <label>Tahun</label>
                <input class="form-control" type="number" name="tahun" value="${empty mobil ? 2024 : mobil.tahun}" min="2000" max="2035" required>

                <label>Transmisi</label>
                <select class="form-select" name="transmisi" required>
                    <option value="Manual" ${mobil.transmisi == 'Manual' ? 'selected' : ''}>Manual</option>
                    <option value="Automatic" ${mobil.transmisi == 'Automatic' ? 'selected' : ''}>Automatic</option>
                </select>

                <label>Bahan Bakar</label>
                <input class="form-control" type="text" name="bahanBakar" value="${empty mobil ? 'Bensin' : mobil.bahanBakar}" required>

                <label>Kapasitas</label>
                <input class="form-control" type="text" name="kapasitas" value="${empty mobil ? '5 Kursi' : fn:replace(mobil.kapasitas, 'Penumpang', 'Kursi')}" required>

                <label>Gambar Mobil</label>
                <div class="upload-dropzone car-upload-dropzone js-car-dropzone">
                    <input class="visually-hidden js-car-image-input" id="gambarMobil" type="file" name="gambar" accept=".jpg,.jpeg,.png,.gif,image/jpeg,image/png,image/gif">
                    <label class="upload-dropzone-label" for="gambarMobil">
                        <span class="upload-icon"></span>
                        <strong>Tarik gambar ke sini atau klik untuk pilih file</strong>
                        <small>JPG, JPEG, PNG, GIF. Maksimal 2MB.</small>
                    </label>
                    <div class="car-upload-preview js-car-preview-wrap ${empty mobil.gambar ? 'd-none' : ''}">
                        <img class="js-car-preview" src="${pageContext.request.contextPath}/assets/${mobil.gambarPath}" alt="Preview gambar mobil" onerror="this.closest('.js-car-preview-wrap').classList.add('d-none');">
                    </div>
                    <div class="car-upload-actions ${empty mobil.gambar ? 'd-none' : ''} js-car-upload-actions">
                        <label class="btn-secondary btn-sm" for="gambarMobil">Ganti Gambar</label>
                    </div>
                </div>
                <div class="alert error js-car-upload-error d-none" role="alert"></div>

                <label>Status</label>
                <c:choose>
                    <c:when test="${mobil.statusMobil == 'DISEWA'}">
                        <input type="hidden" name="statusMobil" value="DISEWA">
                        <select class="form-select" disabled>
                            <option selected>Disewa</option>
                        </select>
                        <p class="muted status-help">Status Disewa diatur otomatis oleh sistem saat user berhasil booking.</p>
                    </c:when>
                    <c:otherwise>
                        <select class="form-select" name="statusMobil">
                            <option value="TERSEDIA" ${empty mobil || mobil.statusMobil == 'TERSEDIA' ? 'selected' : ''}>Tersedia</option>
                            <option value="DALAM_PERBAIKAN" ${mobil.statusMobil == 'DALAM_PERBAIKAN' ? 'selected' : ''}>Dalam Perbaikan</option>
                        </select>
                    </c:otherwise>
                </c:choose>

                <div class="form-actions mt-4 gap-2 d-flex flex-wrap">
                    <button class="btn-primary" type="submit">Simpan</button>
                    <a class="btn secondary" href="${pageContext.request.contextPath}/admin/mobil">Kembali</a>
                </div>
            </form>
        </section>
    </section>
</main>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
