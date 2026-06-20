CREATE DATABASE IF NOT EXISTS `67rentcar`;
USE `67rentcar`;

DROP TABLE IF EXISTS pembayaran;
DROP TABLE IF EXISTS detail_booking;
DROP TABLE IF EXISTS booking;
DROP TABLE IF EXISTS mobil;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id_user VARCHAR(20) PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    telepon VARCHAR(30) NULL,
    password VARCHAR(100) NOT NULL,
    role ENUM('ADMIN', 'PELANGGAN') NOT NULL,
    status_akun ENUM('AKTIF', 'NONAKTIF') NOT NULL DEFAULT 'AKTIF',
    foto_profil VARCHAR(255) NULL,
    kartu_identitas VARCHAR(255) NULL
);

CREATE TABLE mobil (
    id_mobil VARCHAR(20) PRIMARY KEY,
    merk VARCHAR(80) NOT NULL,
    model VARCHAR(100) NOT NULL,
    plat_nomor VARCHAR(20) NOT NULL UNIQUE,
    harga_sewa_per_hari DECIMAL(12,2) NOT NULL,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    status_mobil VARCHAR(30) NOT NULL DEFAULT 'TERSEDIA',
    tahun INT NOT NULL,
    transmisi VARCHAR(50) NOT NULL,
    bahan_bakar VARCHAR(50) NOT NULL,
    kapasitas VARCHAR(50) NOT NULL,
    gambar VARCHAR(255) NOT NULL
);

CREATE TABLE booking (
    id_booking VARCHAR(20) PRIMARY KEY,
    id_user VARCHAR(20) NOT NULL,
    status VARCHAR(40) NOT NULL,
    total_biaya DECIMAL(12,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_booking_user FOREIGN KEY (id_user) REFERENCES users(id_user)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE detail_booking (
    id_detail VARCHAR(20) PRIMARY KEY,
    id_booking VARCHAR(20) NOT NULL,
    id_mobil VARCHAR(20) NOT NULL,
    tanggal_sewa DATE NOT NULL,
    tanggal_kembali DATE NOT NULL,
    subtotal DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_detail_booking FOREIGN KEY (id_booking) REFERENCES booking(id_booking)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_detail_mobil FOREIGN KEY (id_mobil) REFERENCES mobil(id_mobil)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE pembayaran (
    id_pembayaran VARCHAR(20) PRIMARY KEY,
    id_booking VARCHAR(20) NOT NULL,
    jumlah DECIMAL(12,2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    metode_pembayaran VARCHAR(50) NOT NULL,
    bukti_pembayaran VARCHAR(255) NULL,
    tanggal_pembayaran DATE NOT NULL,
    CONSTRAINT fk_pembayaran_booking FOREIGN KEY (id_booking) REFERENCES booking(id_booking)
        ON UPDATE CASCADE ON DELETE CASCADE
);

INSERT INTO users (id_user, username, email, telepon, password, role, status_akun, foto_profil, kartu_identitas) VALUES
('ADM001', 'Administrator', 'admin@67rentcar.com', '0812-0000-0067', 'admin123', 'ADMIN', 'AKTIF', NULL, NULL),
('PLG001', 'Budi Badindin', 'budi@gmail.com', '0812-3456-7890', 'user123', 'PELANGGAN', 'AKTIF', NULL, NULL);

INSERT INTO mobil (id_mobil, merk, model, plat_nomor, harga_sewa_per_hari, status, status_mobil, tahun, transmisi, bahan_bakar, kapasitas, gambar) VALUES
('MBL001', 'Toyota', 'Avanza', 'B 1234 RCA', 350000, TRUE, 'TERSEDIA', 2022, 'Manual', 'Bensin', '7 Kursi', 'toyota-avanza.svg'),
('MBL002', 'Honda', 'Brio', 'B 5678 RCB', 300000, TRUE, 'TERSEDIA', 2021, 'Otomatis', 'Bensin', '5 Kursi', 'honda-brio.svg'),
('MBL003', 'Mitsubishi', 'Xpander', 'B 9012 RCC', 450000, TRUE, 'TERSEDIA', 2023, 'Manual', 'Bensin', '7 Kursi', 'mitsubishi-xpander.svg'),
('MBL004', 'Daihatsu', 'Terios', 'B 3456 RCD', 400000, TRUE, 'TERSEDIA', 2020, 'Otomatis', 'Bensin', '7 Kursi', 'daihatsu-terios.svg');
