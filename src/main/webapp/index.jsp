<%@ include file="/WEB-INF/views/layout/header.jsp" %>
<%@ include file="/WEB-INF/views/layout/navbar.jsp" %>

<main class="hero">
    <section class="hero-left">
        <h1>Rental Mobil Mudah dan Terpercaya</h1>
        <p>Temukan kendaraan yang tepat untuk setiap perjalanan Anda. Kami menyediakan berbagai pilihan mobil berkualitas dengan layanan prima dan harga transparan.</p>
        <div class="actions hero-actions">
            <a class="btn-secondary" href="${pageContext.request.contextPath}/login">Lihat Mobil <span>-></span></a>
            <a class="btn-secondary hero-login-btn" href="${pageContext.request.contextPath}/login">Login</a>
        </div>
    </section>
</main>

<section id="tentang" class="home-section">
    <div class="section-title">
        <h2>Mengapa Memilih Kami?</h2>
        <p class="muted">Layanan profesional yang dirancang untuk kenyamanan dan keamanan mobilitas Anda.</p>
    </div>

    <div class="feature-grid">
        <article class="feature-card">
            <span class="feature-icon icon-badge-ok"></span>
            <h3>Terpercaya</h3>
            <p>Armada terawat dengan baik dan berasuransi penuh untuk ketenangan pikiran Anda selama perjalanan.</p>
        </article>
        <article class="feature-card wide">
            <span class="feature-icon icon-badge-fast"></span>
            <h3>Proses Cepat & Mudah</h3>
            <p>Pemesanan digital yang efisien tanpa dokumen berbelit. Dapatkan kunci mobil Anda dalam waktu kurang dari 15 menit setelah verifikasi.</p>
        </article>
        <article class="feature-card wide">
            <span class="feature-icon icon-headphones"></span>
            <h3>Dukungan 24/7</h3>
            <p>Tim kami siap membantu Anda kapan saja, di mana saja. Karena masalah di jalan tidak mengenal waktu kerja.</p>
            <a class="btn-secondary" href="${pageContext.request.contextPath}/login">Hubungi Kami</a>
        </article>
        <article class="feature-card">
            <span class="feature-icon icon-wallet"></span>
            <h3>Harga Transparan</h3>
            <p>Tidak ada biaya tersembunyi. Apa yang Anda lihat adalah apa yang Anda bayar.</p>
        </article>
    </div>
</section>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
