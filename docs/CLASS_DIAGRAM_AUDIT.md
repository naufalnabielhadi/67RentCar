# Audit Kesesuaian Class Diagram dan Aplikasi

Sumber audit:

- Class diagram rental mobil yang disediakan tim.
- Source code: folder `src/main/java/com/rentcar`.
- Referensi struktur laporan dari laporan tugas besar tahun sebelumnya.

## 1. Ringkasan

Secara umum aplikasi sudah mengikuti konsep utama class diagram:

- `User` sebagai abstract class.
- `Admin` dan `Pelanggan` sebagai turunan `User`.
- `RiwayatPemesanan` sebagai interface.
- `Mobil`, `BookingMobil`, `DetailBooking`, dan `Pembayaran` sebagai model utama rental.

Namun aplikasi sudah berkembang lebih jauh daripada class diagram awal. Banyak fitur praktis web MVC berada di DAO, Servlet, JSP, dan database, sementara class diagram hanya menggambarkan model domain sederhana.

## 2. Class Pada Diagram dan Status di Source

| Class diagram | Ada di source | Catatan |
| --- | --- | --- |
| `User` | Ada: `User.java` | Di source memakai `username`, `role`, `telepon`, `fotoProfil`, `statusAkun`; diagram hanya mencantumkan atribut dasar. |
| `Admin` | Ada: `Admin.java` | Method domain ada, tetapi aksi nyata dilakukan oleh `AdminMobilServlet`, `AdminBookingServlet`, `MobilDAO`, dan `BookingDAO`. |
| `Pelanggan` | Ada: `Pelanggan.java` | Method domain ada, tetapi aksi nyata dilakukan oleh servlet dan DAO. |
| `RiwayatPemesanan` | Ada: `RiwayatPemesanan.java` | Diimplementasikan oleh `Admin` dan `Pelanggan`. |
| `Mobil` | Ada: `Mobil.java` | Source memiliki atribut tambahan: `tahun`, `transmisi`, `bahanBakar`, `kapasitas`, `gambar`, `statusMobil`. |
| `BookingMobil` | Ada: `BookingMobil.java` | Source memiliki `idUser`; diagram belum menampilkan hubungan langsung ke user. |
| `DetailBooking` | Ada: `DetailBooking.java` | Source memiliki `idDetail`, `idBooking`, `idMobil`, `tanggalSewa`, `tanggalKembali`, `subtotal`. |
| `Pembayaran` | Ada: `Pembayaran.java` | Source memiliki `idBooking`, `metodePembayaran`, `buktiPembayaran`; diagram belum mencantumkan semuanya. |
| `Cash`, `TransferBank`, `EWallet` atau variasi pembayaran turunan | Tidak ada | Diagram menampilkan atribut seperti `kembalian`, `nomorKartu`, `namaBank`, `kodeQR`, `provider`, tetapi source saat ini memakai satu class `Pembayaran` dengan field `metodePembayaran`. |

## 3. Fitur Ada di Aplikasi Tetapi Belum Ada di Class Diagram

- Login/register berbasis servlet dan database.
- Status akun aktif/nonaktif.
- Upload foto profil.
- Upload bukti pembayaran.
- Admin dashboard.
- Laporan admin.
- Pencarian dan filter tabel.
- Status booking tambahan:
  - `MENUNGGU_KONFIRMASI`
  - `DIKONFIRMASI`
  - `DITOLAK`
  - `DIBATALKAN`
  - `SELESAI`
- Status pembayaran tambahan:
  - `LUNAS`
  - `TIDAK LUNAS`
  - `DIKEMBALIKAN`
  - `TIDAK DITERUSKAN`
- Status mobil tambahan:
  - `TERSEDIA`
  - `TIDAK_TERSEDIA`
  - `DISEWA`
  - `SUDAH_DIKEMBALIKAN`
- DAO layer:
  - `UserDAO`
  - `MobilDAO`
  - `BookingDAO`
  - `PembayaranDAO`
- Servlet/controller layer:
  - `AuthServlet`
  - `MobilServlet`
  - `BookingServlet`
  - `PembayaranServlet`
  - `RiwayatServlet`
  - `AdminServlet`
  - `AdminMobilServlet`
  - `AdminBookingServlet`
  - `StaticPageServlet`

## 4. Ada di Class Diagram Tetapi Belum Diimplementasikan Lengkap

- Model enum eksplisit:
  - `StatusBooking`
  - `StatusMobil`
  - `StatusPembayaran`
  Saat ini status masih berupa `String`.
- Class turunan pembayaran seperti cash/transfer/e-wallet belum ada sebagai class terpisah.
- Method domain pada `Admin` dan `Pelanggan` hanya mengembalikan teks, belum benar-benar menjalankan proses bisnis.
- Relasi class belum sepenuhnya kuat di model. Banyak relasi masih berupa ID string, misalnya `idUser`, `idMobil`, `idBooking`.
- Diagram belum mencerminkan pola MVC web, padahal source sudah memakai Servlet + DAO + JSP.

## 5. Rekomendasi Agar Diagram dan Aplikasi Selaras

Pilihan A - Sesuaikan class diagram dengan aplikasi saat ini:

- Tambahkan layer MVC:
  - Servlet/controller.
  - DAO.
  - JSP/view boleh dicatat sebagai boundary/view.
- Tambahkan atribut aktual pada model:
  - `User.telepon`, `User.role`, `User.fotoProfil`, `User.statusAkun`.
  - `Mobil.tahun`, `Mobil.transmisi`, `Mobil.bahanBakar`, `Mobil.kapasitas`, `Mobil.gambar`, `Mobil.statusMobil`.
  - `Pembayaran.idBooking`, `Pembayaran.metodePembayaran`, `Pembayaran.buktiPembayaran`.
  - `BookingMobil.idUser`.
- Tambahkan status baru sebagai enum di diagram walaupun source masih memakai string.
- Tambahkan operasi bisnis aktual:
  - `confirmBooking()`
  - `rejectBooking()`
  - `cancelBooking()`
  - `completeBooking()`
  - `markAsPaid()`
  - `markAsReturned()`

Pilihan B - Sesuaikan source dengan class diagram:

- Buat enum `StatusBooking`, `StatusMobil`, `StatusPembayaran`.
- Buat class turunan pembayaran jika dosen menuntut polymorphism:
  - `PembayaranCash`
  - `PembayaranTransferBank`
  - `PembayaranEWallet`
- Pindahkan sebagian logika bisnis dari DAO ke service/domain class.

Rekomendasi praktis untuk deadline tugas besar:

- Lebih aman menyesuaikan class diagram dengan aplikasi saat ini daripada refactor source besar-besaran.
- Tambahkan penjelasan pada laporan bahwa DAO dan Servlet dipakai sebagai implementasi MVC untuk menghubungkan model dengan database dan tampilan web.

## 6. Catatan Laporan

Mengikuti pola PDF referensi tahun lalu, laporan sebaiknya memuat:

1. Deskripsi aplikasi.
2. Role pengguna.
3. List fitur.
4. Class diagram.
5. Penjelasan class diagram.
6. Use case atau alur fitur utama.
7. Implementasi database.
8. Screenshot antarmuka.
9. Cara menjalankan aplikasi.
10. Pembagian tugas anggota.

## 7. Gap Penting Yang Perlu Diterangkan Di Laporan

- Password masih plain text karena konteks pembelajaran PBO/JDBC. Sebutkan sebagai batasan.
- Status masih `String`, belum enum Java. Jika tidak sempat refactor, jelaskan bahwa status divalidasi melalui konstanta model/DAO.
- Pembayaran belum memisahkan class berdasarkan metode pembayaran. Source memakai satu model `Pembayaran` dengan atribut `metodePembayaran`.
- Tidak ada fitur chat real-time; halaman kontak admin hanya informasi kontak.
- Reset transaksi tersedia lewat SQL terpisah, bukan tombol aplikasi.
