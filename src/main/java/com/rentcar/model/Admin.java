package com.rentcar.model;

public class Admin extends User implements RiwayatPemesanan {
    public Admin() {
        setRole("ADMIN");
    }

    public Admin(String idUser, String username, String email, String password, String role) {
        super(idUser, username, email, password, role);
    }

    public String tambahMobil() {
        return "Admin menambah data mobil";
    }

    public String editMobil() {
        return "Admin mengedit data mobil";
    }

    public String hapusMobil() {
        return "Admin menghapus data mobil";
    }

    @Override
    public String getKartuIdentitas() {
        return super.getKartuIdentitas();
    }

    @Override
    public String lihatRiwayatPesanan() {
        return "Admin melihat semua riwayat pesanan";
    }

    @Override
    public String laman() {
        return "/admin/dashboard";
    }
}
