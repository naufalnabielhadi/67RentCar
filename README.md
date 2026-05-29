# 67 RENT CAR

67 RENT CAR adalah aplikasi Java Web MVC untuk rental mobil. Aplikasi ini menangani registrasi dan login pelanggan, katalog mobil, detail mobil, booking dan batal booking, pembayaran, riwayat pemesanan, serta panel admin untuk mengelola armada dan pesanan.

Project ini dibuat untuk tugas besar PBO dan disusun agar bisa dikerjakan berkelompok menggunakan Git branch.

## Tools Yang Dibutuhkan

- JDK 17 atau lebih baru
- Apache NetBeans
- Apache Tomcat 11
- MySQL atau XAMPP
- Maven
- Browser modern
- Git

## Teknologi Project

- Java
- JSP
- Jakarta Servlet
- JDBC
- MySQL
- Maven WAR
- Bootstrap 5 lokal
- HTML, CSS, dan JavaScript

## Struktur Project

```text
src/main/java/com/rentcar/model
src/main/java/com/rentcar/dao
src/main/java/com/rentcar/servlet
src/main/java/com/rentcar/config
src/main/webapp/WEB-INF/views
src/main/webapp/assets
database/67rentcar.sql
pom.xml
README.md
```

Folder penting:

- `model` berisi class data aplikasi.
- `dao` berisi akses database JDBC.
- `servlet` berisi controller.
- `config` berisi konfigurasi koneksi database.
- `WEB-INF/views` berisi halaman JSP.
- `assets` berisi CSS, JavaScript, gambar, dan referensi UI.
- `assets/bootstrap` berisi Bootstrap 5 lokal untuk layout dan komponen UI.
- `database/67rentcar.sql` berisi struktur database dan data awal.

## Catatan Teknis

- Project ini memakai Tomcat 11, sehingga semua Servlet menggunakan `jakarta.servlet`, bukan `javax.servlet`.
- Jangan memakai `javax.servlet` di Tomcat 11 karena namespace tersebut tidak kompatibel dengan Jakarta EE yang dipakai Tomcat 11.
- Project memakai Bootstrap 5 lokal dari `src/main/webapp/assets/bootstrap`.
- Custom CSS di `src/main/webapp/assets/css/style.css` tetap dipakai agar tampilan mengikuti identitas dan desain 67 RENT CAR, bukan template Bootstrap polos.
- Bootstrap dipakai seperlunya untuk layout, form, table, navbar, card, badge, dan modal.

## Cara Import Database

1. Jalankan Apache dan MySQL dari XAMPP.
2. Buka `http://localhost/phpmyadmin`.
3. Buat database dengan nama `67rentcar`, atau langsung import file SQL berikut:

```text
database/67rentcar.sql
```

4. Pastikan konfigurasi database di file berikut sesuai dengan environment lokal:

```text
src/main/java/com/rentcar/config/DatabaseConnection.java
```

Default project:

```text
Database: 67rentcar
User: root
Password: kosong
Port: 3306
```

## Cara Run Di NetBeans + Tomcat 11

1. Buka Apache NetBeans.
2. Pilih `File > Open Project`.
3. Pilih folder project `67rentcar`.
4. Pastikan JDK yang digunakan minimal Java 17.
5. Pastikan server yang dipilih adalah Apache Tomcat 11.
6. Jalankan `Clean and Build`.
7. Jalankan project.
8. Buka aplikasi di browser:

```text
http://localhost:8080/67rentcar
```

Build manual jika Maven tersedia di PATH:

```bash
mvn clean package
```

## Akun Dummy

Admin:

```text
Email: admin@67rentcar.com
Password: admin123
```

Pelanggan:

```text
Email: budi@example.com
Password: user123
```

Catatan: password masih plain text karena project ini dibuat untuk pembelajaran Java Web MVC dan JDBC.

## Pembagian Tugas Anggota

Naufal Nabiel Hadi:

- Booking dan Batal Booking Mobil
- `BookingServlet.java`
- `BookingDAO.java`
- `booking.jsp`

Audrey Fidelya:

- Login dan Buat Akun
- `AuthServlet.java`
- `UserDAO.java`
- `login.jsp`
- `register.jsp`

Muhamad Indal Fauzan Azima:

- Kelola Mobil
- `AdminMobilServlet.java`
- `MobilDAO.java`
- `kelola-mobil.jsp`
- `form-mobil.jsp`

Sigit Bimantoro:

- Pembayaran
- `PembayaranServlet.java`
- `PembayaranDAO.java`
- `pembayaran.jsp`

Anggelina Putri Kristierry:

- Riwayat Pemesanan
- `RiwayatServlet.java`
- `riwayat.jsp`
- `riwayat-pesanan.jsp`

Alyaa Nur Hanifah:

- Lihat Daftar Mobil
- `MobilServlet.java`
- `MobilDAO.java`
- `daftar-mobil.jsp`
- `detail-mobil.jsp`

## Aturan Branch Git

Branch utama:

- `main` = versi stabil
- `dev` = tempat gabung fitur sebelum masuk ke `main`

Branch fitur anggota:

- `feature/auth-audrey`
- `feature/mobil-list-alyaa`
- `feature/mobil-admin-indal`
- `feature/booking-naufal`
- `feature/payment-sigit`
- `feature/history-anggelina`

Aturan kerja:

- Jangan langsung mengerjakan fitur di branch `main`.
- Ambil update terbaru dari `dev` sebelum mulai kerja.
- Kerjakan fitur di branch masing-masing.
- Push branch fitur ke GitHub.
- Merge ke `dev` setelah fitur dicek.
- Merge `dev` ke `main` hanya jika versi sudah stabil.

## Instruksi Git

Untuk push awal:

```bash
git init
git branch -M main
git remote add origin https://github.com/naufalnabielhadi/67RentCar.git
git add .
git commit -m "Initial Java Web MVC project"
git push -u origin main
```

Untuk membuat branch `dev`:

```bash
git checkout -b dev
git push -u origin dev
```

Untuk anggota:

```bash
git clone https://github.com/naufalnabielhadi/67RentCar.git
cd 67RentCar
git checkout dev
git checkout -b feature/nama-fitur
```

Contoh push branch fitur:

```bash
git add .
git commit -m "Implement fitur pembayaran"
git push -u origin feature/payment-sigit
```

## File Yang Tidak Dipush

Folder dan file hasil build tidak perlu masuk repository. File tersebut sudah diatur di `.gitignore`, terutama:

```text
target/
*.class
*.war
.DS_Store
.vscode/
.idea/
nbproject/private/
```
