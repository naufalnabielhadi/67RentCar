# User Manual 67 RENT CAR

Dokumen ini adalah template user manual aplikasi 67 RENT CAR. Tambahkan screenshot secara manual pada bagian yang diberi keterangan gambar.

## 1. Gambaran Umum

67 RENT CAR adalah aplikasi rental mobil berbasis Java Web MVC. Aplikasi menyediakan dua role utama:

- Admin: mengelola data mobil, memantau booking, mengonfirmasi atau menolak booking, menyelesaikan booking, dan melihat laporan transaksi.
- Pelanggan: melihat katalog mobil, membuat booking, melakukan pembayaran, melihat riwayat pesanan, melihat riwayat transaksi, menghubungi admin, dan mengatur profil.

[Gambar: Halaman beranda 67 RENT CAR]

## 2. Kebutuhan Sistem

- JDK 17 atau lebih baru.
- Apache Tomcat 11.
- MySQL atau XAMPP.
- Maven.
- Browser modern.

## 3. Akun Default

Admin:

```text
Email    : admin@67rentcar.com
Password : admin123
```

Pelanggan:

```text
Email    : sigitbimantoro@email.com
Password : user123
```

## 4. Instalasi Singkat

1. Jalankan MySQL.
2. Import database dari `database/67rentcar.sql`.
3. Pastikan konfigurasi database di `src/main/java/com/rentcar/config/DatabaseConnection.java` sesuai environment lokal.
4. Buka project di NetBeans.
5. Jalankan project dengan Tomcat 11.
6. Akses aplikasi melalui:

```text
http://localhost:8080/67rentcar
```

[Gambar: Project berhasil berjalan di browser]

## 5. Panduan Pengguna Umum

### 5.1 Registrasi

1. Buka halaman beranda.
2. Klik tombol `Daftar`.
3. Isi nama, email, dan password.
4. Klik `Daftar`.
5. Setelah berhasil, pengguna diarahkan ke halaman login.

[Gambar: Form registrasi]

### 5.2 Login

1. Klik tombol `Masuk`.
2. Masukkan email dan password.
3. Klik `Masuk`.
4. Sistem mengarahkan pengguna sesuai role.

[Gambar: Form login dan pesan error aplikasi]

### 5.3 Melihat Katalog Mobil

1. Login sebagai pelanggan.
2. Buka menu `Katalog Mobil`.
3. Gunakan kolom pencarian untuk mencari mobil berdasarkan nama, model, atau spesifikasi.
4. Klik detail mobil untuk melihat informasi lengkap.

[Gambar: Katalog mobil pelanggan]

### 5.4 Membuat Booking

1. Pilih mobil berstatus `Tersedia`.
2. Klik tombol booking.
3. Isi tanggal sewa dan tanggal kembali.
4. Sistem menghitung durasi dan total biaya.
5. Klik konfirmasi booking.
6. Booking masuk ke riwayat dengan status `MENUNGGU KONFIRMASI`.

[Gambar: Form booking dan ringkasan biaya]

### 5.5 Pembayaran

1. Buka halaman pembayaran dari dashboard atau riwayat transaksi.
2. Pilih metode pembayaran.
3. Unggah bukti pembayaran.
4. Klik tombol submit.
5. Jika valid, status transaksi menjadi `LUNAS`.

[Gambar: Form pembayaran dan bukti pembayaran]

### 5.6 Riwayat Pesanan

1. Buka menu `Riwayat Pesanan`.
2. Pelanggan dapat melihat status booking:
   - `MENUNGGU KONFIRMASI`
   - `DIKONFIRMASI`
   - `DITOLAK`
   - `DIBATALKAN`
   - `SELESAI`
3. Pelanggan dapat membatalkan booking yang masih menunggu konfirmasi.

[Gambar: Tabel riwayat pesanan pelanggan]

### 5.7 Riwayat Transaksi

1. Buka menu `Riwayat Transaksi`.
2. Pelanggan dapat melihat status pembayaran:
   - `LUNAS`
   - `TIDAK LUNAS`
   - `DIKEMBALIKAN`
   - `TIDAK DITERUSKAN`

[Gambar: Tabel riwayat transaksi pelanggan]

### 5.8 Kontak Admin

1. Buka menu `Kontak Admin`.
2. Lihat informasi kontak yang tersedia.

[Gambar: Halaman kontak admin]

### 5.9 Pengaturan Akun Pelanggan

1. Buka menu `Pengaturan`.
2. Ubah profil atau password.
3. Sistem menampilkan pesan berhasil atau gagal sebagai bagian dari UI aplikasi.

[Gambar: Halaman pengaturan pelanggan]

## 6. Panduan Admin

### 6.1 Dashboard Admin

Dashboard menampilkan:

- Total mobil.
- Mobil tersedia.
- Booking aktif.
- Pendapatan.
- Tabel pesanan terbaru yang belum berstatus `SELESAI`.

[Gambar: Dashboard admin]

### 6.2 Kelola Mobil

Admin dapat:

- Menambah mobil baru.
- Mengedit data mobil.
- Menghapus mobil yang belum memiliki riwayat booking dan masih tersedia.
- Mengubah status mobil hanya ke `Tersedia` atau `Tidak Tersedia`.

Catatan status:

- `Disewa` hanya dibuat otomatis saat pelanggan booking mobil.
- `Sudah Dikembalikan` hanya dibuat otomatis saat admin menyelesaikan booking.
- Mobil yang sedang `Disewa` tidak bisa diedit sampai booking selesai.

[Gambar: Tabel kelola mobil admin]
[Gambar: Form tambah/edit mobil]

### 6.3 Riwayat Pesanan Admin

Admin dapat:

- Melihat seluruh booking.
- Mencari booking berdasarkan ID atau nama pelanggan.
- Filter berdasarkan tanggal sewa.
- Melihat detail booking.
- Mengonfirmasi booking.
- Menolak booking.
- Menyelesaikan booking setelah tanggal kembali tercapai.

Aturan penyelesaian booking:

- Jika tanggal kembali belum tercapai, sistem menampilkan pesan error UI.
- Jika tanggal kembali sudah tercapai, booking menjadi `SELESAI`.
- Mobil berubah menjadi `Sudah Dikembalikan`.

[Gambar: Tabel riwayat pesanan admin]
[Gambar: Modal detail booking]
[Gambar: Modal konfirmasi aplikasi]

### 6.4 Laporan

Halaman laporan menampilkan ringkasan transaksi dan status pembayaran. Status pembayaran yang digunakan:

- `LUNAS`
- `TIDAK LUNAS`
- `DIKEMBALIKAN`
- `TIDAK DITERUSKAN`

[Gambar: Halaman laporan admin]

### 6.5 Pengaturan Admin

Admin dapat mengubah data profil dan password.

[Gambar: Halaman pengaturan admin]

## 7. Alur Status

### 7.1 Status Booking

```text
MENUNGGU KONFIRMASI
    -> DIKONFIRMASI
    -> DITOLAK
    -> DIBATALKAN

DIKONFIRMASI
    -> SELESAI setelah tanggal kembali tercapai
```

### 7.2 Status Pembayaran

```text
TIDAK LUNAS
    -> LUNAS setelah pembayaran berhasil

LUNAS + booking ditolak/dibatalkan
    -> DIKEMBALIKAN

TIDAK LUNAS + booking ditolak/dibatalkan
    -> TIDAK DITERUSKAN
```

### 7.3 Status Mobil

```text
TERSEDIA
    -> DISEWA saat booking dibuat
    -> TIDAK TERSEDIA jika admin menonaktifkan mobil

DISEWA
    -> SUDAH DIKEMBALIKAN saat booking diselesaikan

SUDAH DIKEMBALIKAN
    -> TERSEDIA atau TIDAK TERSEDIA setelah admin mengedit data mobil
```

## 8. Reset Data Transaksi

Untuk mengosongkan data booking, detail booking, dan pembayaran tanpa menghapus `users` dan `mobil`, jalankan:

```text
database/reset_except_users_mobil.sql
```

## 9. Catatan Screenshot Untuk Laporan

Screenshot yang disarankan:

- Beranda.
- Login.
- Register.
- Dashboard pelanggan.
- Katalog mobil.
- Detail mobil.
- Form booking.
- Form pembayaran.
- Riwayat pesanan pelanggan.
- Riwayat transaksi pelanggan.
- Dashboard admin.
- Kelola mobil.
- Form tambah/edit mobil.
- Riwayat pesanan admin.
- Laporan admin.
- Pengaturan akun.
