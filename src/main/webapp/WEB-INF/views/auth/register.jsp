<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<main class="auth-page auth-register">
    <section class="auth-left">
        <div class="watermark">67 RENT CAR</div>
        <h2>Kemewahan Dalam Setiap Perjalanan.</h2>
        <p>Bergabunglah bersama 67 RENT CAR dan rasakan pengalaman berkendara kelas dunia dengan armada premium terbaik kami.</p>
    </section>

    <section class="auth-right">
        <div class="auth-card auth-register-card">
            <a class="nav-brand auth-brand" href="${pageContext.request.contextPath}/">67 RENT CAR</a>
            <h1>Buat Akun Baru</h1>
            <p class="muted">Lengkapi detail Anda untuk memulai perjalanan.</p>

            <c:if test="${not empty error}">
                <div class="alert error">${error}</div>
            </c:if>

            <form class="auth-form" method="post" action="${pageContext.request.contextPath}/register">
                <label class="auth-label" for="register-username">NAMA LENGKAP</label>
                <div class="input-shell input-user">
                    <input class="form-control" id="register-username" type="text" name="username" placeholder="John Doe" required>
                </div>

                <label class="auth-label" for="register-email">EMAIL</label>
                <div class="input-shell input-email">
                    <input class="form-control" id="register-email" type="email" name="email" placeholder="nama@email.com" required>
                </div>

                <label class="auth-label" for="register-password">PASSWORD</label>
                <div class="input-shell input-lock input-hidden">
                    <input class="form-control" id="register-password" type="password" name="password" placeholder="Masukkan password" required>
                </div>

                <button class="btn-primary full auth-submit" type="submit">Daftar Sekarang <span aria-hidden="true">-></span></button>
            </form>

            <p class="form-note">Sudah punya akun? <a href="${pageContext.request.contextPath}/login">Masuk sekarang</a></p>
        </div>
    </section>
</main>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
