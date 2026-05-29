<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<main class="auth-page auth-login">
    <section class="auth-card auth-login-card center">
        <a class="nav-brand auth-brand" href="${pageContext.request.contextPath}/">67 RENT CAR</a>
        <h1>Selamat Datang</h1>
        <p class="muted">Silakan masuk untuk melanjutkan</p>

        <c:if test="${param.success == 'register'}">
            <div class="alert success">Registrasi berhasil. Silakan login.</div>
        </c:if>
        <c:if test="${param.success == 'deactivated'}">
            <div class="alert success">Akun berhasil dinonaktifkan. Anda tidak dapat login dengan akun tersebut.</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert error">${error}</div>
        </c:if>

        <form class="auth-form" method="post" action="${pageContext.request.contextPath}/login">
            <label class="auth-label" for="login-email">Email Address</label>
            <div class="input-shell input-email">
                <input class="form-control" id="login-email" type="email" name="email" placeholder="nama@email.com" required>
            </div>

            <div class="label-row">
                <label class="auth-label" for="login-password">Password</label>
                <a href="#" aria-label="Fitur lupa password belum tersedia">Lupa Password?</a>
            </div>
            <div class="input-shell input-lock input-hidden">
                <input class="form-control" id="login-password" type="password" name="password" placeholder="Masukkan password" required>
            </div>

            <button class="btn-primary full auth-submit" type="submit">Masuk <span aria-hidden="true">-></span></button>
        </form>

        <p class="form-note">Belum memiliki akun? <a href="${pageContext.request.contextPath}/register">Daftar sekarang</a></p>
    </section>
</main>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
