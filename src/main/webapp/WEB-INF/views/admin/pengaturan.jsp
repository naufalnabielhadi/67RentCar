<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<main class="admin-layout">
    <%@ include file="/WEB-INF/views/layout/sidebar-admin.jsp" %>
    <section class="admin-content">
        <div class="page-title">
            <div>
                <h1>Pengaturan Admin</h1>
                <p>Kelola informasi profil administrator dan preferensi panel operasional.</p>
            </div>
        </div>

        <c:if test="${not empty success}">
            <div class="alert success">${success}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert error">${error}</div>
        </c:if>

        <div class="settings-layout admin-settings-layout">
            <section class="settings-card">
                <h2>Profil Administrator</h2>
                <div class="settings-divider"></div>

                <form class="settings-form js-profile-form" method="post" action="${pageContext.request.contextPath}/admin/pengaturan" enctype="multipart/form-data" novalidate>
                    <input type="hidden" name="action" value="profile">
                    <div class="profile-upload">
                        <label class="profile-photo-edit" for="fotoProfilAdmin">
                            <span class="profile-photo profile-avatar large">
                                <c:choose>
                                    <c:when test="${not empty sessionScope.user.fotoProfil}">
                                        <img class="js-profile-preview" src="${pageContext.request.contextPath}/assets/${sessionScope.user.fotoProfil}" alt="Foto profil ${sessionScope.user.username}">
                                    </c:when>
                                    <c:otherwise>
                                        <span class="js-profile-initial">${fn:toUpperCase(fn:substring(sessionScope.user.username, 0, 1))}</span>
                                        <img class="js-profile-preview d-none" src="" alt="Preview foto profil">
                                    </c:otherwise>
                                </c:choose>
                            </span>
                            <span class="profile-edit-badge" aria-hidden="true"><span class="icon-edit"></span></span>
                            <span class="visually-hidden">Pilih foto profil baru</span>
                        </label>
                        <input class="visually-hidden js-profile-image-input" id="fotoProfilAdmin" type="file" name="fotoProfil" accept=".jpg,.jpeg,.png,image/jpeg,image/png">
                        <div>
                            <strong>Unggah foto admin</strong>
                            <p>JPG atau PNG. Maksimal 2MB.</p>
                        </div>
                    </div>
                    <div class="alert error js-profile-upload-error d-none" role="alert"></div>
                    <button class="btn-primary settings-button" type="submit">Simpan Foto Profil <span>></span></button>
                </form>

                <div class="settings-divider"></div>
                <h3>Kartu Identitas Admin</h3>
                <form class="settings-form js-identity-form" method="post" action="${pageContext.request.contextPath}/admin/pengaturan" enctype="multipart/form-data" novalidate>
                    <input type="hidden" name="action" value="identity">
                    <p class="muted">
                        <c:choose>
                            <c:when test="${not empty sessionScope.user.kartuIdentitas}">
                                Kartu identitas sudah diunggah.
                                <a class="file-attachment" href="${pageContext.request.contextPath}/assets/${sessionScope.user.kartuIdentitas}" target="_blank" rel="noopener">
                                    <span class="file-attachment-icon" aria-hidden="true"></span>
                                    <span>Lampiran kartu identitas</span>
                                </a>
                            </c:when>
                            <c:otherwise>
                                Belum ada kartu identitas admin.
                            </c:otherwise>
                        </c:choose>
                    </p>
                    <label for="kartuIdentitasAdmin">Upload KTP / Kartu Identitas</label>
                    <div class="file-picker">
                        <input class="visually-hidden js-identity-input js-file-input" id="kartuIdentitasAdmin" type="file" name="kartuIdentitas" accept=".png,.jpg,.jpeg,image/png,image/jpeg" required>
                        <label class="file-picker-control" for="kartuIdentitasAdmin">
                            <span class="file-picker-button">Pilih File</span>
                            <span class="file-picker-name js-file-name">Belum ada file dipilih</span>
                        </label>
                    </div>
                    <p class="muted upload-help">PNG, JPG, atau JPEG. Maksimal 5MB.</p>
                    <div class="alert error js-identity-upload-error d-none" role="alert"></div>
                    <button class="btn-primary settings-button" type="submit">Simpan Kartu Identitas <span>></span></button>
                </form>

                <div class="settings-divider"></div>
                <dl class="detail-list admin-profile-list">
                    <dt>Nama Admin</dt><dd>${sessionScope.user.username}</dd>
                    <dt>Email</dt><dd>${sessionScope.user.email}</dd>
                    <dt>Nomor Telepon</dt><dd>${empty sessionScope.user.telepon ? '-' : sessionScope.user.telepon}</dd>
                    <dt>Kartu Identitas</dt>
                    <dd>
                        <c:choose>
                            <c:when test="${not empty sessionScope.user.kartuIdentitas}">
                                <a class="file-attachment" href="${pageContext.request.contextPath}/assets/${sessionScope.user.kartuIdentitas}" target="_blank" rel="noopener">
                                    <span class="file-attachment-icon" aria-hidden="true"></span>
                                    <span>Tersimpan</span>
                                </a>
                            </c:when>
                            <c:otherwise>-</c:otherwise>
                        </c:choose>
                    </dd>
                    <dt>Role</dt><dd>${sessionScope.user.role}</dd>
                    <dt>Status</dt><dd>${sessionScope.user.statusAkun}</dd>
                </dl>
            </section>

            <section class="danger-card">
                <h2>Catatan Keamanan</h2>
                <p>Gunakan akun admin hanya untuk pengelolaan armada, pesanan, laporan, dan data operasional.</p>
            </section>
        </div>
    </section>
</main>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
