-- Reset data transaksi 67 RENT CAR tanpa menghapus tabel users dan mobil.
-- Jalankan pada database 67rentcar.
--
-- Tabel yang dikosongkan:
-- 1. pembayaran
-- 2. detail_booking
-- 3. booking
--
-- Catatan:
-- - Nama tabel user di project ini adalah `users`.
-- - Tabel `users` dan `mobil` tidak dihapus atau di-truncate.
-- - Blok UPDATE mobil di bagian akhir bersifat opsional untuk memperbaiki status
--   armada setelah seluruh booking dihapus.

USE `67rentcar`;

SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS;
SET @OLD_SQL_SAFE_UPDATES = @@SQL_SAFE_UPDATES;
SET FOREIGN_KEY_CHECKS = 0;
SET SQL_SAFE_UPDATES = 0;

START TRANSACTION;

DELETE FROM `pembayaran`;
DELETE FROM `detail_booking`;
DELETE FROM `booking`;

COMMIT;

SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS;
SET SQL_SAFE_UPDATES = @OLD_SQL_SAFE_UPDATES;

-- OPSIONAL:
-- Jika setelah reset transaksi ada mobil yang masih berstatus DISEWA atau
-- SUDAH_DIKEMBALIKAN, jalankan blok ini agar semua armada kembali bisa
-- dikelola dari panel admin.
--
-- UPDATE `mobil`
-- SET `status` = TRUE,
--     `status_mobil` = 'TERSEDIA'
-- WHERE `status_mobil` IN ('DISEWA', 'SUDAH_DIKEMBALIKAN');
