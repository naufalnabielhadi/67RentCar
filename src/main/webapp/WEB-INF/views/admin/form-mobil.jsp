<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<main class="admin-layout">
    <%@ include file="/WEB-INF/views/layout/sidebar-admin.jsp" %>
    <section class="admin-content">
        <section class="form-card">
            <c:set var="formMerk" value="${requestScope.merkValue != null ? requestScope.merkValue : mobil.merk}" />
            <c:set var="formModel" value="${requestScope.modelValue != null ? requestScope.modelValue : mobil.model}" />
            <c:set var="formPlatNomor" value="${requestScope.platNomorValue != null ? requestScope.platNomorValue : mobil.platNomor}" />
            <c:set var="formHarga" value="${requestScope.hargaSewaPerHariValue != null ? requestScope.hargaSewaPerHariValue : mobil.hargaSewaPerHari}" />
            <c:set var="formTahun" value="${requestScope.tahunValue != null ? requestScope.tahunValue : (empty mobil ? 2024 : mobil.tahun)}" />
            <c:set var="formTransmisi" value="${requestScope.transmisiValue != null ? requestScope.transmisiValue : mobil.transmisi}" />
            <c:set var="formBahanBakar" value="${requestScope.bahanBakarValue != null ? requestScope.bahanBakarValue : (empty mobil ? 'Bensin' : mobil.bahanBakar)}" />
            <c:set var="formKapasitas" value="${requestScope.kapasitasValue != null ? requestScope.kapasitasValue : (empty mobil ? '5 Kursi' : fn:replace(mobil.kapasitas, 'Penumpang', 'Kursi'))}" />
            <c:set var="formStatus" value="${requestScope.statusMobilValue != null ? requestScope.statusMobilValue : mobil.statusMobil}" />
            <span class="eyebrow">Manajemen Armada</span>
            <h1>${empty mobil.idMobil ? 'Tambah Mobil' : 'Edit Mobil'}</h1>

            <c:if test="${not empty error}">
                <div class="alert error">${error}</div>
            </c:if>

            <form method="post" action="${pageContext.request.contextPath}/admin/mobil/save" enctype="multipart/form-data" class="js-car-form" novalidate>
                <input type="hidden" name="idMobil" value="${mobil.idMobil}">
                <input type="hidden" name="gambarLama" value="${mobil.gambar}">

                <label>Merk</label>
                <input class="form-control" type="text" name="merk" value="${formMerk}" required>

                <label>Model</label>
                <input class="form-control" type="text" name="model" value="${formModel}" required>

                <label>Plat Nomor</label>
                <input class="form-control" type="text" name="platNomor" value="${formPlatNomor}" required>

                <label>Harga Sewa Per Hari</label>
                <input class="form-control" type="number" name="hargaSewaPerHari" value="${formHarga}" min="1000" step="1000" required>

                <label>Tahun</label>
                <input class="form-control" type="number" name="tahun" value="${formTahun}" min="2000" max="2035" required>

                <label>Transmisi</label>
                <select class="form-select" name="transmisi" required>
                    <option value="Manual" ${formTransmisi == 'Manual' ? 'selected' : ''}>Manual</option>
                    <option value="Otomatis" ${formTransmisi == 'Otomatis' || formTransmisi == 'Automatic' ? 'selected' : ''}>Otomatis</option>
                </select>

                <label>Bahan Bakar</label>
                <select class="form-select" name="bahanBakar" required>
                    <option value="Bensin" ${formBahanBakar == 'Bensin' ? 'selected' : ''}>Bensin</option>
                    <option value="Diesel" ${formBahanBakar == 'Diesel' ? 'selected' : ''}>Diesel</option>
                    <option value="Hybrid" ${formBahanBakar == 'Hybrid' ? 'selected' : ''}>Hybrid</option>
                    <option value="Listrik" ${formBahanBakar == 'Listrik' ? 'selected' : ''}>Listrik</option>
                </select>

                <label>Kapasitas</label>
                <input class="form-control" type="number" name="kapasitas" value="${fn:replace(fn:replace(formKapasitas, ' Kursi', ''), ' Penumpang', '')}" min="1" required>

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
                <p class="muted upload-help">Unggah gambar JPG, JPEG, PNG, atau GIF maksimal 2MB. Jika tidak ada gambar, sistem memakai gambar default.</p>

                <label>Status</label>
                <select class="form-select" name="statusMobil">
                    <option value="TERSEDIA" ${empty formStatus || formStatus == 'TERSEDIA' ? 'selected' : ''}>Tersedia</option>
                    <option value="TIDAK_TERSEDIA" ${formStatus == 'TIDAK_TERSEDIA' || formStatus == 'DALAM_PERBAIKAN' ? 'selected' : ''}>Tidak Tersedia</option>
                </select>
                <p class="muted status-help">Status Disewa dan Sudah Dikembalikan hanya diatur otomatis oleh proses booking. Setelah mobil sudah dikembalikan, admin bisa mengedit data mobil lalu memilih Tersedia atau Tidak Tersedia.</p>

                <div class="form-actions mt-4 gap-2 d-flex flex-wrap">
                    <button class="btn-primary" type="submit">Simpan</button>
                    <a class="btn secondary" href="${pageContext.request.contextPath}/admin/mobil">Kembali</a>
                </div>
            </form>
        </section>
    </section>
</main>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
