<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<main class="app-shell">
    <%@ include file="/WEB-INF/views/layout/sidebar-user.jsp" %>
    <section class="main-content settings-page">
        <div class="page-title">
            <div>
                <h1>Pengaturan Akun</h1>
                <p>Kelola informasi profil dan preferensi akun Anda.</p>
            </div>
        </div>

        <c:if test="${not empty success}">
            <div class="alert success">${success}</div>
        </c:if>
        <c:if test="${not empty warning}">
            <div class="alert warning">${warning}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert error">${error}</div>
        </c:if>

        <div class="settings-layout">
            <section class="settings-card">
                <h2>Profil Anda</h2>
                <div class="settings-divider"></div>

                <form class="settings-form js-profile-form" method="post" action="${pageContext.request.contextPath}/pelanggan/pengaturan" enctype="multipart/form-data" novalidate>
                    <input type="hidden" name="action" value="profile">
                    <div class="profile-upload">
                        <label class="profile-photo-edit" for="fotoProfil">
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
                        <input class="visually-hidden js-profile-image-input" id="fotoProfil" type="file" name="fotoProfil" accept=".jpg,.jpeg,.png,image/jpeg,image/png">
                        <div>
                            <strong>Unggah foto baru</strong>
                            <p>JPG atau PNG. Maksimal 2MB.</p>
                        </div>
                    </div>
                    <div class="alert error js-profile-upload-error d-none" role="alert"></div>
                    <div class="form-row">
                        <div class="editable-field js-editable-field">
                            <label for="profileUsername">Nama Lengkap</label>
                            <input class="form-control" id="profileUsername" type="text" name="username" value="${sessionScope.user.username}" readonly data-editable-control required>
                            <button class="field-edit-button js-field-edit" type="button" aria-label="Edit nama lengkap">
                                <span class="field-edit-icon icon-edit" aria-hidden="true"></span>
                            </button>
                        </div>
                        <div class="editable-field js-editable-field">
                            <label for="profileEmail">Alamat Email</label>
                            <input class="form-control" id="profileEmail" type="email" name="email" value="${sessionScope.user.email}" readonly data-editable-control required>
                            <button class="field-edit-button js-field-edit" type="button" aria-label="Edit alamat email">
                                <span class="field-edit-icon icon-edit" aria-hidden="true"></span>
                            </button>
                        </div>
                    </div>
                    <div class="editable-field js-editable-field">
                        <label for="profilePhone">Nomor Telepon</label>
                        <input class="form-control" id="profilePhone" type="text" name="telepon" value="${sessionScope.user.telepon}" placeholder="+62 812 3456 7890" readonly data-editable-control>
                        <button class="field-edit-button js-field-edit" type="button" aria-label="Edit nomor telepon">
                            <span class="field-edit-icon icon-edit" aria-hidden="true"></span>
                        </button>
                    </div>
                    <button class="btn-primary settings-button" type="submit">Simpan Perubahan <span>></span></button>
                </form>

                <div class="settings-divider"></div>

                <h3>Ubah Kata Sandi</h3>
                <form class="settings-form" method="post" action="${pageContext.request.contextPath}/pelanggan/pengaturan">
                    <input type="hidden" name="action" value="password">
                    <label>
                        Kata Sandi Saat Ini
                        <input class="form-control" type="password" name="currentPassword" required>
                    </label>
                    <div class="form-row">
                        <label>
                            Kata Sandi Baru
                            <input class="form-control" type="password" name="newPassword" required>
                        </label>
                        <label>
                            Konfirmasi Kata Sandi
                            <input class="form-control" type="password" name="confirmPassword" required>
                        </label>
                    </div>
                    <button class="btn-primary settings-button" type="submit">Ubah Kata Sandi <span>></span></button>
                </form>
            </section>

            <aside class="danger-card">
                <h2>Zona Berbahaya</h2>
                <p>Tindakan di bawah ini akan menghapus akun dan riwayat akun Anda dari database. Akun tidak dapat dihapus saat masih ada booking aktif.</p>
                <form method="post" action="${pageContext.request.contextPath}/pelanggan/pengaturan" class="js-confirm" data-message="Hapus akun ini secara permanen? Data akun dan riwayat akun akan dihapus dari database.">
                    <input type="hidden" name="action" value="deactivate">
                    <button class="btn-danger-outline full" type="submit">Hapus Akun</button>
                </form>
            </aside>
        </div>
    </section>
</main>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
