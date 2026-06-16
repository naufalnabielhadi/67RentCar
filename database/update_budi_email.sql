-- Update email akun pelanggan dummy dari budi@example.com ke sigitbimantoro@email.com.
-- Jalankan pada database 67rentcar jika database lama sudah telanjur di-import.

USE `67rentcar`;

UPDATE `users`
SET `email` = 'sigitbimantoro@email.com'
WHERE `email` = 'budi@example.com';
