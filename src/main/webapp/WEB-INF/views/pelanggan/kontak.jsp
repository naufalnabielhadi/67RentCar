<%@ include file="/WEB-INF/views/layout/header.jsp" %>

<main class="app-shell">
    <%@ include file="/WEB-INF/views/layout/sidebar-user.jsp" %>
    <section class="main-content contact-page">
        <div class="page-title">
            <div>
                <h1>Kontak Admin</h1>
                <p>Butuh bantuan? Tim kami siap membantu Anda kapan saja.</p>
            </div>
        </div>

        <div class="contact-grid">
            <article class="contact-card">
                <span class="contact-icon whatsapp-icon"></span>
                <h2>Hubungi via WhatsApp</h2>
                <p>Respon cepat untuk pertanyaan mendesak dan konfirmasi pesanan instan.</p>
                <a class="btn-whatsapp full" href="https://wa.me/6281200000067" target="_blank" rel="noopener">Chat Sekarang</a>
            </article>

            <article class="contact-card">
                <span class="contact-icon email-icon"></span>
                <h2>Kirim Email</h2>
                <p>Untuk pertanyaan umum, pengajuan kerjasama, atau keluhan resmi tertulis.</p>
                <a class="btn-primary full" href="mailto:admin@67rentcar.com">Kirim Email</a>
            </article>
        </div>
    </section>
</main>

<%@ include file="/WEB-INF/views/layout/footer.jsp" %>
