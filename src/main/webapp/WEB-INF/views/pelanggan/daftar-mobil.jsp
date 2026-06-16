<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<main class="app-shell">
    <%@ include file="/WEB-INF/views/layout/sidebar-user.jsp" %>
    <section class="main-content">
        <div class="page-title">
            <div>
                <h1>Katalog Mobil</h1>
                <p>Temukan kendaraan yang tepat untuk perjalanan Anda.</p>
            </div>
        </div>

        <form class="history-toolbar catalog-toolbar mb-4" method="get" action="${pageContext.request.contextPath}/mobil">
            <div class="input-group catalog-search">
                <button class="input-group-text catalog-search-button" type="submit" aria-label="Cari mobil">
                    <span class="search-icon" aria-hidden="true"></span>
                </button>
                <input class="form-control search-control" type="search" name="q" value="${fn:escapeXml(query)}" placeholder="Cari merk atau model...">
            </div>
        </form>

        <c:if test="${not empty error}">
            <div class="alert error">${error}</div>
        </c:if>

        <c:choose>
            <c:when test="${empty mobilList}">
                <div class="alert">
                    <c:choose>
                        <c:when test="${not empty query}">
                            Hasil pencarian tidak ditemukan.
                        </c:when>
                        <c:otherwise>
                            Tidak ada mobil tersedia saat ini.
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:when>
            <c:otherwise>
                <div class="car-grid">
                    <c:forEach var="mobil" items="${mobilList}">
                        <article class="card car-card">
                            <img class="car-image" src="${pageContext.request.contextPath}/assets/${mobil.gambarPath}" alt="${mobil.merk} ${mobil.model}" onerror="this.onerror=null;this.src='${pageContext.request.contextPath}/assets/img/default-car.svg';">
                            <div class="car-card-body">
                                <h3>${mobil.merk} ${mobil.model}</h3>
                                <p class="meta">${mobil.platNomor}</p>
                                <p class="price"><fmt:formatNumber value="${mobil.hargaSewaPerHari}" type="currency" currencySymbol="Rp " maxFractionDigits="0"/> <small>/ Hari</small></p>
                                <div class="form-actions">
                                    <a class="btn-secondary" href="${pageContext.request.contextPath}/mobil/detail?idMobil=${mobil.idMobil}">Lihat Detail</a>
                                    <c:if test="${mobil.statusMobil == 'TERSEDIA'}">
                                        <a class="btn-primary" href="${pageContext.request.contextPath}/booking?idMobil=${mobil.idMobil}">Booking Sekarang</a>
                                    </c:if>
                                </div>
                            </div>
                        </article>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</main>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
