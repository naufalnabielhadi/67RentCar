<c:set var="currentPath" value="${requestScope['jakarta.servlet.forward.servlet_path']}" />
<c:if test="${empty currentPath}">
    <c:set var="currentPath" value="${pageContext.request.servletPath}" />
</c:if>
<header class="mobile-appbar">
    <button class="mobile-menu-button js-sidebar-toggle" type="button" aria-label="Buka menu" aria-expanded="false">
        <span></span>
    </button>
    <a class="mobile-brand" href="${pageContext.request.contextPath}/pelanggan/dashboard">67 RENT CAR</a>
    <div class="dropdown mobile-user-dropdown">
        <button class="avatar-button dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false" aria-label="Menu akun">
            <c:choose>
                <c:when test="${not empty sessionScope.user.fotoProfil}">
                    <img src="${pageContext.request.contextPath}/assets/${sessionScope.user.fotoProfil}" alt="Foto profil ${sessionScope.user.username}">
                </c:when>
                <c:otherwise>
                    <span>${fn:toUpperCase(fn:substring(sessionScope.user.username, 0, 1))}</span>
                </c:otherwise>
            </c:choose>
        </button>
        <ul class="dropdown-menu dropdown-menu-end shadow-sm">
            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/pelanggan/pengaturan">Pengaturan Akun</a></li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item text-danger" href="${pageContext.request.contextPath}/logout">Keluar</a></li>
        </ul>
    </div>
</header>
<button class="sidebar-overlay js-sidebar-close" type="button" aria-label="Tutup menu"></button>
<aside class="sidebar user-sidebar">
    <button class="sidebar-close js-sidebar-close" type="button" aria-label="Tutup menu">x</button>
    <a class="sidebar-brand" href="${pageContext.request.contextPath}/pelanggan/dashboard">67 RENT CAR</a>

    <div class="sidebar-profile">
        <span class="avatar sidebar-avatar">
            <c:choose>
                <c:when test="${not empty sessionScope.user.fotoProfil}">
                    <img src="${pageContext.request.contextPath}/assets/${sessionScope.user.fotoProfil}" alt="Foto profil ${sessionScope.user.username}">
                </c:when>
                <c:otherwise>
                    <span>${fn:toUpperCase(fn:substring(sessionScope.user.username, 0, 1))}</span>
                </c:otherwise>
            </c:choose>
        </span>
        <div>
            <strong>${sessionScope.user.username}</strong>
            <span>Pengguna</span>
        </div>
    </div>

    <nav class="sidebar-menu">
        <a class="sidebar-link ${currentPath == '/pelanggan/dashboard' ? 'active' : ''}" href="${pageContext.request.contextPath}/pelanggan/dashboard">
            <span class="sidebar-icon icon-dashboard"></span> Dashboard
        </a>
        <a class="sidebar-link ${currentPath == '/mobil' || currentPath == '/mobil/detail' || currentPath == '/booking' || currentPath == '/pembayaran' ? 'active' : ''}" href="${pageContext.request.contextPath}/mobil">
            <span class="sidebar-icon icon-car"></span> Katalog Mobil
        </a>
        <a class="sidebar-link ${currentPath == '/pelanggan/riwayat' || currentPath == '/riwayat' ? 'active' : ''}" href="${pageContext.request.contextPath}/pelanggan/riwayat">
            <span class="sidebar-icon icon-history"></span> Riwayat Pesanan
        </a>
        <a class="sidebar-link ${currentPath == '/pelanggan/kontak' ? 'active' : ''}" href="${pageContext.request.contextPath}/pelanggan/kontak">
            <span class="sidebar-icon icon-headphones"></span> Kontak Admin
        </a>
        <a class="sidebar-link ${currentPath == '/pelanggan/pengaturan' ? 'active' : ''}" href="${pageContext.request.contextPath}/pelanggan/pengaturan">
            <span class="sidebar-icon icon-settings"></span> Pengaturan
        </a>
    </nav>

    <div class="sidebar-spacer"></div>
    <a class="sidebar-link danger-link" href="${pageContext.request.contextPath}/logout">
        <span class="sidebar-icon icon-logout"></span> Keluar
    </a>
</aside>
